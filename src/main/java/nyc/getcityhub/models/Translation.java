package nyc.getcityhub.models;

import java.sql.*;
import java.util.Date;

/**
 * Created by v-jacco on 2/25/17.
 */
public class Translation {

    private String english;
    private String spanish;
    private String french;
    private String simplified;
    private String traditional;

    public Translation(String english, String spanish, String french, String simplified, String traditional) {
        this.english = english;
        this.spanish = spanish;
        this.french = french;
        this.simplified = simplified;
        this.traditional = traditional;
    }

    public String getEnglish() {
        return english;
    }

    public String getSpanish() {
        return spanish;
    }

    public String getFrench() {
        return french;
    }

    public String getSimplified() {
        return simplified;
    }

    public String getTraditional() {
        return traditional;
    }

    public static String getTranslation(String english, Language target) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");

            String command = "SELECT * FROM translations WHERE english = ?";
            statement = connection.prepareStatement(command);
            statement.setString(1, english);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                switch (target) {
                    case SPANISH:
                        return resultSet.getString(2);
                    case FRENCH:
                        return resultSet.getString(3);
                    case CHINESE_SIMPLIFIED:
                        return resultSet.getString(4);
                    case CHINESE_TRADITIONAL:
                        return resultSet.getString(5);
                }
            }

            return english;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            return english;
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
