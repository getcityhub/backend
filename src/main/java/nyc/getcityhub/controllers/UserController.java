package nyc.getcityhub.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.BadRequestException;
import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.User;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.Random;

/**
 * Created by jackcook on 06/02/2017.
 */
public class UserController {

    public static User createUser(Request request) throws BadRequestException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'firstName', 'lastName', 'anonymous', 'zipcode', 'languages', and 'email' keys must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject userObject = (JsonObject) parser.parse(request.body());

        if (!userObject.has("firstName")
                || !userObject.has("lastName")
                || !userObject.has("anonymous")
                || !userObject.has("zipcode")
                || !userObject.has("languages")
                || !userObject.has("email")) {
            throw new BadRequestException("The 'firstName', 'lastName', 'anonymous', 'zipcode', 'languages', and 'email' keys must be included in your request body.");
        }

        String firstName = userObject.get("firstName").getAsString();
        String lastName = userObject.get("lastName").getAsString();
        boolean anonymous = userObject.get("anonymous").getAsBoolean();
        short zipcode = userObject.get("zipcode").getAsShort();
        String emailAddress = userObject.get("email").getAsString();

        JsonArray languages = userObject.get("languages").getAsJsonArray();
        String languagesString = "";

        for (JsonElement element : languages) {
            String id = element.getAsString();

            if (!Language.isLanguageSupported(id)) {
                throw new BadRequestException("The language '" + id + "' is not supported at this time.");
            }

            languagesString += id + ",";
        }

        languagesString = languagesString.substring(0, languagesString.length() - 1);

        String randomCode = generateRandomCode();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement userStatement = null;
        ResultSet userResultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");

            String query = "INSERT INTO users (first_name, last_name, anonymous, zipcode, languages, email, unique_code) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setBoolean(3, anonymous);
            statement.setShort(4, zipcode);
            statement.setString(5, languagesString);
            statement.setString(6, emailAddress);
            statement.setString(7, randomCode);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                return User.getUserById(id);
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The users table doesn't exist in the database");

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

    public static User retrieveCurrentUser(Request request) throws UnauthorizedException {
        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You are not currently logged in");
        } else {
            return user;
        }
    }

    public static User loginUser(Request request) throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' and 'password' keys must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject loginObject = (JsonObject) parser.parse(request.body());

        if (!loginObject.has("email")
                || !loginObject.has("password")) {
            throw new BadRequestException("The 'email' and 'password' keys must be included in your request body.");
        }

        String email = loginObject.get("email").getAsString();
        String password = loginObject.get("password").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");

            String query = "SELECT * FROM users WHERE email = ? AND unique_code = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, password);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                User user = User.getUserById(id);

                request.session(true).attribute("user", user);
                return user;
            } else {
                throw new UnauthorizedException("Invalid email or password");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The users table doesn't exist in the database");

            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } finally {
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

    public static int logoutUser(Request request, Response response) {
        request.session().removeAttribute("user");
        response.status(204);

        return 0;
    }
}
