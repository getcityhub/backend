package nyc.getcityhub.models;

import java.sql.*;
import java.util.Date;

import static nyc.getcityhub.Constants.*;

/**
 * Created by stephanie on 2/12/17.
 */
public class Politician {

    private int id;
    private String name;
    private transient int[] zipcodes;
    private String position;
    private String party;
    private String photoUrl;
    private String email;
    private String phone;
    private String website;
    private String facebook;
    private String googleplus;
    private String twitter;
    private String youtube;
    private Date createdAt;
    private Date updatedAt;

    public Politician(int id, String name, int[] zipcodes, String position, String party, String photoUrl, String email, String phone, String website, String facebook, String googleplus, String twitter, String youtube, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.zipcodes = zipcodes;
        this.position = position;
        this.party = party;
        this.photoUrl = photoUrl;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.facebook = facebook;
        this.googleplus = googleplus;
        this.twitter = twitter;
        this.youtube = youtube;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int[] getZipcode() {
        return zipcodes;
    }

    public String getPosition() {
        return position;
    }

    public String getParty() {
        return party;
    }

    public String getPhotoURL() {
        return photoUrl;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getWebsiteURL() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getGooglePlus() {
        return googleplus;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public static Politician getPoliticianById(int id, Language language) {
        String command = "SELECT * FROM politicians WHERE id = " + id;

        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                String name = Translation.getTranslation(resultSet.getString(2), language);
                String[] zipcodesArray = resultSet.getString(3).split(",");
                int[] zipcodes = new int[zipcodesArray.length];

                for (int i = 0; i < zipcodesArray.length; i++) {
                    zipcodes[i] = Integer.parseInt(zipcodesArray[i]);
                }

                String position = Translation.getTranslation(resultSet.getString(4), language);
                String party = Translation.getTranslation(resultSet.getString(5), language);
                String photo = resultSet.getString(6);
                String email = resultSet.getString(7);
                String phone = resultSet.getString(8);
                String website = resultSet.getString(9);
                String facebook = resultSet.getString(10);
                String googleplus = resultSet.getString(11);
                String twitter = resultSet.getString(12);
                String youtube = resultSet.getString(13);
                java.sql.Date createdAt = new java.sql.Date(resultSet.getTimestamp(14).getTime());
                java.sql.Date updatedAt = new java.sql.Date(resultSet.getTimestamp(15).getTime());

                return new Politician(id, name, zipcodes, position, party, photo, email, phone, website, facebook, googleplus, twitter, youtube, createdAt, updatedAt);

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


