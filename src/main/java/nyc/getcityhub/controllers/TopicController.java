package nyc.getcityhub.controllers;

import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.models.Topic;
import spark.Request;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class TopicController {

    public static Topic[] retrieveTopics(Request request) throws InternalServerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();
            resultset = statement.executeQuery("SELECT * FROM topics");

            ArrayList<Topic> topics = new ArrayList<Topic>();

            while (resultset.next()) {
                int id = resultset.getInt(1);
                String name = resultset.getString(2);

                Topic topic = new Topic(id, name);
                topics.add(topic);
            }

            Topic[] topicsArray = new Topic[topics.size()];
            topicsArray = topics.toArray(topicsArray);

            return topicsArray;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist.");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The topics table doesn't exist in the database.");

            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } finally {
            if (resultset != null) {
                try {
                    resultset.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                resultset = null;
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
}
