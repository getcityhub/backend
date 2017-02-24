package nyc.getcityhub.models;

import java.sql.*;

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

    public Topic getTopicById(int id, Language lang) {
        String command = "SELECT * FROM topics WHERE id = " + id;
        int column = 2;

        switch(lang) {
            case SPANISH:
                column = 3;
                break;
            case FRENCH:
                column = 4;
                break;
            case CHINESE_SIMPLIFIED:
                column = 5;
                break;
            case CHINESE_TRADITIONAL:
                column = 6;
                break;
        }
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                String name = resultSet.getString(column);

                return new Topic(id, name);
            }
        }catch (SQLException e) {
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
}
