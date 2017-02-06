package nyc.getcityhub.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.BadRequestException;
import nyc.getcityhub.User;
import spark.Request;

import java.sql.*;
import java.util.Random;

/**
 * Created by jackcook on 06/02/2017.
 */
public class UserController {

    public static User createUser(Request request) throws BadRequestException {
        JsonParser parser = new JsonParser();
        JsonObject userObject = (JsonObject) parser.parse(request.body());

        if(!userObject.has("firstName")
                || !userObject.has("lastName")
                || !userObject.has("anonymous")
                || !userObject.has("zipCode")
                || !userObject.has("languages")
                || !userObject.has("topics")) {
            throw new BadRequestException("The 'firstName', 'lastName', 'anonymous', 'languages', and 'topics' keys must be included in your request body.");
        }

        String firstName = userObject.get("firstName").getAsString();
        String lastName = userObject.get("lastName").getAsString();
        boolean anonymous = userObject.get("anonymous").getAsBoolean();
        short zipCode = userObject.get("zipCode").getAsShort();

        JsonArray languages = userObject.get("languages").getAsJsonArray();
        String languagesString = "";

        for (JsonElement element : languages) {
            languagesString += element.getAsString() + ",";
        }

        languagesString = languagesString.substring(0, languagesString.length() - 1);

        JsonArray topics = userObject.get("topics").getAsJsonArray();
        String topicsString = "";

        for (JsonElement element : topics) {
            topicsString += element.getAsString() + ",";
        }

        topicsString = topicsString.substring(0, topicsString.length() - 1);

        String randomCode = generateRandomCode();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement userStatement = null;
        ResultSet userResultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub");

            String query = "INSERT INTO users (first_name, last_name, anonymous, zip_code, languages, topics, unique_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setBoolean(3, anonymous);
            statement.setShort(4, zipCode);
            statement.setString(5, languagesString);
            statement.setString(6, topicsString);
            statement.setString(7, randomCode);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                userStatement = connection.createStatement();
                userResultSet = userStatement.executeQuery("SELECT * FROM users WHERE id = " + id);

                if (userResultSet.next()) {
                    String userFirstName = userResultSet.getString(2);
                    String userLastName = userResultSet.getString(3);
                    boolean userAnonymous = userResultSet.getBoolean(4);
                    short userZipCode = userResultSet.getShort(5);

                    String userLanguages = userResultSet.getString(6);
                    String[] userLanguagesArray = userLanguages.split(",");

                    String userTopics = userResultSet.getString(7);
                    String[] userTopicsArray = userTopics.split(",");

                    String userUniqueCode = userResultSet.getString(8);
                    Date userCreatedAt = userResultSet.getDate(9);
                    Date userUpdatedAt = userResultSet.getDate(10);

                    return new User(id, userFirstName, userLastName, userAnonymous, userZipCode, userLanguagesArray, userTopicsArray, userUniqueCode, userCreatedAt, userUpdatedAt);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } finally {
            if (userResultSet != null) {
                try {
                    userResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                userResultSet = null;
            }

            if (userStatement != null) {
                try {
                    userStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                userStatement = null;
            }

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                resultSet = null;
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                statement = null;
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                connection = null;
            }
        }

        return null;
    }

    private static String generateRandomCode() {
        String base = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";
        //deleted O, 0, I, l to avoid confusion
        String s = "";
        Random random = new Random();
        for (int i = 0; i < 8; i++)
            s += Character.toString(base.charAt(random.nextInt(base.length())));
        return s;
    }
}
