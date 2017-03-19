package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.cj.api.x.InsertStatement;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.User;
import org.apache.commons.validator.routines.EmailValidator;
import spark.Response;
import spark.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.*;
import java.util.Random;
import java.util.ResourceBundle;

import static nyc.getcityhub.Constants.JDBC_URL;

/**
 * Created by stephanie on 3/19/17.
 */
public class EmailController {

    public static int registerEmail(Request request, Response response)throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        if (!postObject.has("email")) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }

        String email = postObject.get("email").getAsString();
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException("Email address is invalid.");
        }

        if (emailExists(email)) {
            throw new BadRequestException("Email address has already been registered.");
        }

        String code = createCode();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "INSERT INTO mailing_list_unconfirmed (email, code) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, code);
            statement.executeUpdate();

            response.status(204);
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new InternalServerException(e);
        } finally {
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

    public static int registerFromForm(Request request, Response response) throws BadRequestException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("Your email address must be included in the request body.");
        }

        String email;

        try {
            email = URLDecoder.decode(request.body().split("=")[1], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new BadRequestException("An error occurred while adding you to the mailing list.");
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException("Email address is invalid.");
        }

        if (emailExists(email)) {
            throw new BadRequestException("Email address has already been registered.");
        }

        String code = createCode();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "INSERT INTO mailing_list_unconfirmed (email, code) VALUES (?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, code);
            statement.executeUpdate();

            response.status(204);
            return 0;
        } catch (SQLException e) {
            throw new InternalServerException(e);
        } finally {
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

    public static int confirmEmail(Request request, Response response)throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' and 'code' keys must be included in your request body.");
        }
        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        if (!postObject.has("email") || !postObject.has("code")) {
            throw new BadRequestException("The 'email' and 'code' keys must be included in your request body.");
        }

        String email = postObject.get("email").getAsString();
        String code = postObject.get("code").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement insertStatement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "SELECT * FROM mailing_list_unconfirmed WHERE email = ? AND code = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.setString(2, code);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String deleteQuery = "DELETE FROM mailing_list_unconfirmed WHERE email = ? AND code = ?";
                deleteStatement = connection.prepareStatement(deleteQuery);
                deleteStatement.setString(1, email);
                deleteStatement.setString(2, code);
                deleteStatement.executeUpdate();

                String insertQuery = "INSERT INTO mailing_list VALUES (?)";
                insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, email);
                insertStatement.executeUpdate();
            } else {
                throw new BadRequestException("The email address or the code was incorrect.");
            }

            response.status(204);
            return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new InternalServerException(e);
        } finally {
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (deleteStatement != null) {
                try {
                    deleteStatement.close();
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

    public static int deleteEmail(Request request, Response response)throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }
        JsonParser parser = new JsonParser();
        JsonObject postObject = (JsonObject) parser.parse(request.body());

        if (!postObject.has("email")) {
            throw new BadRequestException("The 'email' key must be included in your request body.");
        }

        String email = postObject.get("email").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "DELETE FROM mailing_list WHERE email = ?";
            statement = connection.prepareStatement(query);
            statement.setString(1, email);
            statement.executeUpdate();
            response.status(204);
            return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            throw new InternalServerException(e);
        } finally {
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
    private static String createCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder code = new StringBuilder();
        Random rnd = new Random();

        while (code.length() < 16) {
            int index = (int) (rnd.nextFloat() * chars.length());
            code.append(chars.charAt(index));
        }

        return code.toString();
    }

    private static boolean emailExists(String emailAddress) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String command = "SELECT * FROM mailing_list WHERE email = ?";
            statement = connection.prepareStatement(command);
            statement.setString(1, emailAddress);

            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            return false;
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
