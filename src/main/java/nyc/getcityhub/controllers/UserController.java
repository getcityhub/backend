package nyc.getcityhub.controllers;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nulabinc.zxcvbn.Feedback;
import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import nyc.getcityhub.Constants;
import nyc.getcityhub.Main;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.User;
import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by jackcook on 06/02/2017.
 */
public class UserController {

    public static User createUser(Request request, Response response) throws BadRequestException, InternalServerException {
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

        if (firstName.length() < 2) {
            throw new BadRequestException("First name must be at least two characters long.");
        } else if (firstName.length() > 20) {
            throw new BadRequestException("First name must be at most 20 characters long.");
        } else if (!firstName.matches("([A-zÀ-ÿ]){2,20}")) {
            // regex checks for letters only (diacritics are allowed)
            throw new BadRequestException("First name must only have letters.");
        }

        String lastName = userObject.get("lastName").getAsString();

        if (lastName.length() < 2) {
            throw new BadRequestException("Last name must be at least two characters long.");
        } else if (lastName.length() > 20) {
            throw new BadRequestException("Last name must be at most 20 characters long.");
        } else if (!lastName.matches("([A-zÀ-ÿ]){2,20}")) {
            // regex checks for letters only (diacritics are allowed)
            throw new BadRequestException("Last name must only have letters.");
        }

        boolean anonymous = userObject.get("anonymous").getAsBoolean();
        int zipcode = userObject.get("zipcode").getAsInt();

        if (Arrays.asList(Constants.NYC_ZIPCODES).contains(zipcode)) {
            throw new BadRequestException("Zipcode must be a valid NYC zipcode.");
        }

        String password = userObject.get("password").getAsString();

        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();

        if (strength.getScore() < 3) {
            throw new BadRequestException("Password is too weak. " + feedback.getWarning());
        }

        String emailAddress = userObject.get("email").getAsString();

        if (!EmailValidator.getInstance().isValid(emailAddress)) {
            throw new BadRequestException("Email address is invalid.");
        }

        if (User.userExistsWithEmail(emailAddress)) {
            throw new BadRequestException("Email address has already been registered.");
        }

        JsonArray languages = userObject.get("languages").getAsJsonArray();

        if (languages.size() == 0) {
            throw new BadRequestException("At least one language must be selected.");
        }

        String languagesString = "";

        for (JsonElement element : languages) {
            String id = element.getAsString();

            if (!Language.isLanguageSupported(id)) {
                throw new BadRequestException("The language '" + id + "' is not supported.");
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
            statement.setInt(4, zipcode);
            statement.setString(5, languagesString);
            statement.setString(6, emailAddress);
            statement.setString(7, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                Map root = new HashMap();
                root.put("firstName", firstName);
                root.put("lastName", lastName);
                root.put("email", emailAddress);

                try {
                    Template temp = Main.FTL_CONFIG.getTemplate("registration.ftl");
                    StringWriter writer = new StringWriter();
                    temp.process(root, writer);

                    Destination destination = new Destination().withToAddresses(emailAddress);

                    Content subject = new Content().withCharset("UTF-8").withData("This is my subject");
                    Content textBody = new Content().withCharset("UTF-8").withData(writer.toString());
                    Body body = new Body().withHtml(textBody);

                    Message message = new Message().withSubject(subject).withBody(body);
                    SendEmailRequest emailRequest = new SendEmailRequest().withSource("Team CityHub <no_reply@getcityhub.org>").withDestination(destination).withMessage(message);

                    AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();
                    Region region = Region.getRegion(Regions.US_EAST_1);
                    client.setRegion(region);
                    client.sendEmail(emailRequest);
                } catch (IOException | TemplateException e) {
                    e.printStackTrace();
                }

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

    public static User retrieveCurrentUser(Request request, Response response) throws UnauthorizedException {
        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You are not currently logged in");
        } else {
            return user;
        }
    }

    public static User retrieveUser(Request request, Response response) throws BadRequestException, NotFoundException {
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

    public static User loginUser(Request request, Response response) throws BadRequestException, UnauthorizedException, InternalServerException {
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

    public static int logoutUser(Request request, Response response) {
        request.session().removeAttribute("user");
        response.status(204);

        return 0;
    }

    public static int forgotPassword(Request request, Response response) throws BadRequestException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject bodyObject = (JsonObject) parser.parse(request.body());

        if (!bodyObject.has("email")) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }

        String email = bodyObject.get("email").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement resetStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);

            String query = "SELECT * FROM users WHERE email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int userId = resultSet.getInt(1);
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);

                Map root = new HashMap();
                root.put("firstName", firstName);
                root.put("lastName", lastName);

                try {
                    Template temp = Main.FTL_CONFIG.getTemplate("forgot-password.ftl");
                    StringWriter writer = new StringWriter();
                    temp.process(root, writer);

                    Destination destination = new Destination().withToAddresses(email);

                    Content subject = new Content().withCharset("UTF-8").withData("This is my subject");
                    Content textBody = new Content().withCharset("UTF-8").withData(writer.toString());
                    Body body = new Body().withHtml(textBody);

                    Message message = new Message().withSubject(subject).withBody(body);
                    SendEmailRequest emailRequest = new SendEmailRequest().withSource("Team CityHub <no_reply@getcityhub.org>").withDestination(destination).withMessage(message);

                    AmazonSimpleEmailServiceClient client = new AmazonSimpleEmailServiceClient();
                    Region region = Region.getRegion(Regions.US_EAST_1);
                    client.setRegion(region);
                    client.sendEmail(emailRequest);
                } catch (IOException | TemplateException e) {
                    e.printStackTrace();
                }

                String resetQuery = "INSERT INTO password_reset_requests (user_id, code) VALUES (?, ?)";
                resetStatement = connection.prepareStatement(resetQuery);
                resetStatement.setInt(1, userId);
                resetStatement.setString(2, UUID.randomUUID().toString());
                resetStatement.execute();
            }

            response.status(204);

            return 0;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            throw new InternalServerException(e);
        } finally {
            if (resetStatement != null) {
                try {
                    resetStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

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

    public static int updatePassword(Request request, Response response) throws BadRequestException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'password' and 'token' keys must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject bodyObject = (JsonObject) parser.parse(request.body());

        if (!bodyObject.has("password")
                || !bodyObject.has("code")) {
            throw new BadRequestException("The 'password' and 'code' keys must be included in your request body.");
        }

        String password = bodyObject.get("password").getAsString();

        Zxcvbn zxcvbn = new Zxcvbn();
        Strength strength = zxcvbn.measure(password);
        Feedback feedback = strength.getFeedback();

        if (strength.getScore() < 3) {
            throw new BadRequestException("Password is too weak. " + feedback.getWarning());
        }

        String code = bodyObject.get("code").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement deleteStatement = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);

            String query = "UPDATE users SET password = ? WHERE id = (SELECT user_id FROM password_reset_requests WHERE code = ?);";
            statement = connection.prepareStatement(query);
            statement.setString(1, BCrypt.hashpw(password, BCrypt.gensalt(10)));
            statement.setString(2, code);
            statement.executeUpdate();

            String deleteQuery = "DELETE FROM password_reset_requests WHERE code = ?";
            deleteStatement = connection.prepareStatement(deleteQuery);
            deleteStatement.setString(1, code);
            deleteStatement.execute();

            response.status(204);
            return 0;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            throw new InternalServerException(e);
        } finally {
            if (deleteStatement != null) {
                try {
                    deleteStatement.close();
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
