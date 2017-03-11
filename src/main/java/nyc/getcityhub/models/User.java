package nyc.getcityhub.models;

import java.sql.*;
import java.util.Date;

import static nyc.getcityhub.Constants.*;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private boolean anonymous;
    private int zipcode;
    private String[] languages;
    private String emailAddress;
    private Date createdAt;
    private Date updatedAt;

    public User(int id, String firstName, String lastName, boolean anonymous, int zipcode, String[] languages, String emailAddress, Date createdAt, Date updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.anonymous = anonymous;
        this.languages = languages;
        this.emailAddress = emailAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.zipcode = zipcode;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public int getZipcode() {
        return zipcode;
    }

    public String[] getLanguages()  {
        return languages;
    }

    public String getEmailAddress() { return emailAddress; }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public static User getUserById(int id) {
        String command = "SELECT * FROM users WHERE id = " + id;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                String firstName = resultSet.getString(2);
                String lastName = resultSet.getString(3);
                boolean anonymous = resultSet.getBoolean(4);
                int zipcode = resultSet.getInt(5);

                String languages = resultSet.getString(6);
                String[] languagesArray = languages.split(",");

                String emailAddress = resultSet.getString(7);
                Date createdAt = new Date(resultSet.getTimestamp(9).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(10).getTime());

                return new User(id, firstName, lastName, anonymous, zipcode, languagesArray, emailAddress, createdAt, updatedAt);
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

    public static boolean userExistsWithEmail(String emailAddress) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String command = "SELECT * FROM users WHERE email = ?";
            statement = connection.prepareStatement(command);
            statement.setString(1, emailAddress);

            resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            return true;
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