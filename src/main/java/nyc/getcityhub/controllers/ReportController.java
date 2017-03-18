package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Report;
import nyc.getcityhub.models.ReportReason;
import nyc.getcityhub.models.User;
import spark.Request;
import spark.Response;

import java.security.cert.CertPathValidatorException;
import java.sql.*;

import static nyc.getcityhub.Constants.*;

/**
 * Created by carol on 3/10/17.
 */
public class ReportController {

    public static Report createReport(Request request, Response response) throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'language', 'text' and 'reasonId' keys must be included in your request body.");
        }

        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You must be logged in to create reports.");
        }

        JsonParser parser = new JsonParser();
        JsonObject reportObject =  (JsonObject) parser.parse(request.body());

        if (!reportObject.has("language")
                || !reportObject.has("text")
                || !reportObject.has("reasonId")) {
            throw new BadRequestException("The 'language', 'text' and 'reasonId' keys must be included in your request body.");
        }

        int reporterId = user.getId();
        String language = reportObject.get("language").getAsString();

        if (!Language.isLanguageSupported(language)) {
            throw new BadRequestException("The language '" + language + "' is not supported at this time.");
        }

        Language lang = Language.fromId(language);

        String text = reportObject.get("text").getAsString();

        int minReportLength = 50;
        int maxReportLength = 5000;

        if (lang == Language.CHINESE_SIMPLIFIED){
            minReportLength = 15;
            maxReportLength = 1250;
        }

        if (text.length() < minReportLength){
            throw new BadRequestException("The report has to be as least " + minReportLength + " characters long.");
        } else if (text.length() > maxReportLength){
            throw new BadRequestException("The report has to be at most " + maxReportLength + " characters long.");
        }

        int reasonId = reportObject.get("reasonId").getAsInt();
        ReportReason reason = ReportReason.fromId(reasonId);

        if (reason == ReportReason.OTHER) {
            reasonId = 0;
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "INSERT INTO reports (reporter_id, language, text, reason_id) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, reporterId);
            statement.setString(2, language);
            statement.setString(3, text);
            statement.setInt(4, reasonId);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                Report report = Report.getReportById(id);
                report.setReporter(User.getUserById(reporterId));

                return report;
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static Report retrieveReport(Request request, Response response) throws BadRequestException, NotFoundException {
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid report id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid report id.");
        }

        Report report = Report.getReportById(id);

        if (report == null) {
            throw new NotFoundException("The report requested does not exist");
        } else {
            return report;
        }
    }
}
