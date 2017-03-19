package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.User;
import spark.Response;
import spark.Request;

import java.sql.*;
import java.util.Random;

import static nyc.getcityhub.Constants.JDBC_URL;

/**
 * Created by stephanie on 3/19/17.
 */
public class EmailController {

    public static int registerEmail(Request request, Response response)throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'email' keys must be included in your request body.");
        }
        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        if (!postObject.has("email")) {
            throw new BadRequestException("The 'email' keys must be included in your request body.");
        }

        String email = postObject.get("email").getAsString();
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
}
