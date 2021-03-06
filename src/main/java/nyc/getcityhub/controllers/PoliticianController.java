package nyc.getcityhub.controllers;

import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Politician;
import nyc.getcityhub.models.Translation;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;

import static nyc.getcityhub.Constants.*;

/**
 * Created by stephanie on 2/12/17.
 */
public class PoliticianController {

    public static Politician retrievePolitician(Request request, Response response) throws BadRequestException, NotFoundException {
        Language language = Language.fromId(request.headers("Accept-Language"));
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid politician id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid politician id.");
        }

        Politician politician = Politician.getPoliticianById(id, language);

        if (politician == null) {
            throw new NotFoundException("The politician requested does not exist");
        } else {
            return politician;
        }
    }

    public static Politician[] retrievePoliticians(Request request, Response response) throws InternalServerException {
        Language language = Language.fromId(request.headers("Accept-Language"));
        String zipcode = request.queryParams("zip");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            String command = "SELECT * FROM politicians";

            if (zipcode != null && zipcode.matches("[0-9]{5}")) {
                command = "SELECT * FROM politicians WHERE zipcodes LIKE '%" + zipcode + "%'";
            }

            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            ArrayList<Politician> Politicians = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                boolean male = resultSet.getBoolean(3);

                String[] zipcodesArray = resultSet.getString(4).split(",");
                int[] zipcodes = new int[zipcodesArray.length];

                for (int i = 0; i < zipcodesArray.length; i++) {
                    zipcodes[i] = Integer.parseInt(zipcodesArray[i]);
                }

                String position = Translation.getTranslation(resultSet.getString(5), language, male);
                String party = Translation.getTranslation(resultSet.getString(6), language, male);
                String photo = resultSet.getString(7);
                String email = resultSet.getString(8);
                String phone = resultSet.getString(9);
                String website = resultSet.getString(10);
                String facebook = resultSet.getString(11);
                String googleplus = resultSet.getString(12);
                String twitter = resultSet.getString(13);
                String youtube = resultSet.getString(14);
                Date createdAt = new Date(resultSet.getTimestamp(15).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(16).getTime());

                Politician politician = new Politician(id, name, male, zipcodes, position, party, photo, email, phone, website, facebook, googleplus, twitter, youtube, createdAt, updatedAt);
                Politicians.add(politician);
            }

            Politician[] politicianArray = new Politician[Politicians.size()];
            politicianArray = Politicians.toArray(politicianArray);

            return politicianArray;
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
    }
}
