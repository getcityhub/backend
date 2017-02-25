package nyc.getcityhub.controllers;

import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Topic;
import nyc.getcityhub.models.Translation;
import spark.Request;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class TopicController {

    public static Topic[] retrieveTopics(Request request) throws InternalServerException {
        Language language = Language.fromId(request.headers("Accept-Language"));

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM topics");

            ArrayList<Topic> topics = new ArrayList<Topic>();

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
}
