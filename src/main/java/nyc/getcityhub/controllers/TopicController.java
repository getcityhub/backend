package nyc.getcityhub.controllers;

import nyc.getcityhub.Main;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Topic;
import nyc.getcityhub.models.Translation;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class TopicController {

    public static Topic retrieveTopic(Request request, Response response) throws BadRequestException, NotFoundException {
        Language language = Language.fromId(request.headers("Accept-Language"));
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid topic id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid topic id.");
        }

        Topic topic= Topic.getTopicById(id, language);

        if (topic == null) {
            throw new NotFoundException("The topic requested does not exist");
        } else {
            return topic;
        }
    }

    public static Topic[] retrieveTopics(Request request, Response response) throws InternalServerException {
        Language language = Language.fromId(request.headers("Accept-Language"));

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM topics");

            ArrayList<Topic> topics = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = Translation.getTranslation(resultSet.getString(2), language);

                Topic topic = new Topic(id, name);
                topics.add(topic);
            }

            Topic[] topicsArray = new Topic[topics.size()];
            topicsArray = topics.toArray(topicsArray);

            return topicsArray;
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
