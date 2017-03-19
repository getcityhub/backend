package nyc.getcityhub.models;

import java.sql.*;

import static nyc.getcityhub.Constants.*;

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

    public static String getTranslation(String english, Language target, boolean male) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String command = "SELECT * FROM translations WHERE english = ?";
            statement = connection.prepareStatement(command);
            statement.setString(1, english);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                switch (target) {
                    case SPANISH:
                        return adaptToGender(resultSet.getString(2), male);
                    case FRENCH:
                        return adaptToGender(resultSet.getString(3), male);
                    case CHINESE_SIMPLIFIED:
                        return resultSet.getString(4);
                    case CHINESE_TRADITIONAL:
                        return resultSet.getString(5);
                }
            }

            return english;
        } catch (SQLException e) {
            return english;
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

    private static String adaptToGender(String translation, boolean male) {
        while (translation.contains("(")) {
            int beginning = translation.indexOf('(');
            int end = translation.indexOf(')') + 1;

            String group = translation.substring(beginning, end).replace("(", "\\(").replace(")", "\\)");
            String content = group.replace("\\(", "").replace("\\)", "");

            if (content.contains("/")) {
                translation = translation.replaceFirst(group, content.split("/")[male ? 0 : 1]);
            } else {
                if (male) {
                    translation = translation.replaceFirst(group, "");
                } else {
                    translation = translation.replaceFirst(group, content);
                }
            }
        }

        return translation;
    }
}