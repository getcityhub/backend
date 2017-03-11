package nyc.getcityhub.models;

import java.sql.*;

import static nyc.getcityhub.Constants.*;

public class Topic {

    private int id;
    private String name;

    public Topic(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Topic getTopicById(int id, Language language) {
        String command = "SELECT * FROM topics WHERE id = " + id;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                String name = Translation.getTranslation(resultSet.getString(2), language);
                return new Topic(id, name);
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            return null;
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
}
