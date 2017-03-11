package nyc.getcityhub.controllers;

import nyc.getcityhub.Main;
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

            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            ArrayList<Politician> Politicians = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);

                String[] zipcodesArray = resultSet.getString(3).split(",");
                int[] zipcodes = new int[zipcodesArray.length];

                for (int i = 0; i < zipcodesArray.length; i++) {
                    zipcodes[i] = Integer.parseInt(zipcodesArray[i]);
                }

                String position = Translation.getTranslation(resultSet.getString(4), language);
                String party = Translation.getTranslation(resultSet.getString(5), language);
                String photo = resultSet.getString(6);
                String email = resultSet.getString(7);
                String phone = resultSet.getString(8);
                String website = resultSet.getString(9);
                String facebook = resultSet.getString(10);
                String googleplus = resultSet.getString(11);
                String twitter = resultSet.getString(12);
                String youtube = resultSet.getString(13);
                Date createdAt = new Date(resultSet.getTimestamp(14).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(15).getTime());

                Politician politician = new Politician(id, name, zipcodes, position, party, photo, email, phone, website, facebook, googleplus, twitter, youtube, createdAt, updatedAt);
                Politicians.add(politician);
            }

            Politician[] politicianArray = new Politician[Politicians.size()];
            politicianArray = Politicians.toArray(politicianArray);

            return politicianArray;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

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
