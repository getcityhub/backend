package nyc.getcityhub.controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.Main;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.User;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;

import java.sql.*;

/**
 * Created by jackcook on 06/02/2017.
 */
public class UserController {

    public static User createUser(Request request) throws BadRequestException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'firstName', 'lastName', 'anonymous', 'zipcode', 'languages', 'password', and 'email' keys must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject userObject = (JsonObject) parser.parse(request.body());

        if (!userObject.has("firstName")
                || !userObject.has("lastName")
                || !userObject.has("anonymous")
                || !userObject.has("zipcode")
                || !userObject.has("languages")
                || !userObject.has("password")
                || !userObject.has("email")) {
            throw new BadRequestException("The 'firstName', 'lastName', 'anonymous', 'zipcode', 'languages', 'password', and 'email' keys must be included in your request body.");
        }

        String firstName = userObject.get("firstName").getAsString();
        String lastName = userObject.get("lastName").getAsString();
        boolean anonymous = userObject.get("anonymous").getAsBoolean();
        short zipcode = userObject.get("zipcode").getAsShort();
        String password = userObject.get("password").getAsString();
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

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);

            String query = "INSERT INTO users (first_name, last_name, anonymous, zipcode, languages, email, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setBoolean(3, anonymous);
            statement.setShort(4, zipcode);
            statement.setString(5, languagesString);
            statement.setString(6, emailAddress);
            statement.setString(7, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                User user = User.getUserById(id);

                request.session(true).attribute("user", user);
                return user;
            }
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

    public static User retrieveCurrentUser(Request request) throws UnauthorizedException {
        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You are not currently logged in");
        } else {
            return user;
        }
    }

    public static User retrieveUser(Request request) throws BadRequestException, NotFoundException {
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid user id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid user id.");
        }

        User user = User.getUserById(id);

        if (user == null) {
            throw new NotFoundException("The user requested does not exist");
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
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);

            String query = "SELECT * FROM users WHERE email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String passwordHash = resultSet.getString(8);

                if (BCrypt.checkpw(password, passwordHash)) {
                    int id = resultSet.getInt(1);
                    User user = User.getUserById(id);

                    request.session(true).attribute("user", user);
                    return user;
                }
            }

            throw new UnauthorizedException("Invalid email or password");
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
    }

    public static int logoutUser(Request request, Response response) {
        request.session().removeAttribute("user");
        response.status(204);

        return 0;
    }
}
