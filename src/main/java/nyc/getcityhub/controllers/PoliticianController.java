package nyc.getcityhub.controllers;

import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.models.Politician;
import spark.Request;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;

/**
 * Created by stephanie on 2/12/17.
 */
public class PoliticianController {

    public static Politician[] retrievePolitician(Request request) throws InternalServerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();
            resultset = statement.executeQuery("SELECT * FROM politicians");

            ArrayList<Politician> Politicians = new ArrayList<Politician>();

            while (resultset.next()) {
                int id = resultset.getInt(1);
                String name = resultset.getString(2);

                String[] zipcodesArray = resultset.getString(3).split(",");
                int[] zipcodes = new int[zipcodesArray.length];

                for (int i = 0; i < zipcodesArray.length; i++) {
                    zipcodes[i] = Integer.parseInt(zipcodesArray[i]);
                }

                String position = resultset.getString(4);
                String party = resultset.getString(5);
                String email = resultset.getString(6);
                String phone = resultset.getString(7);
                String website = resultset.getString(8);
                String facebook = resultset.getString(9);
                String googleplus = resultset.getString(10);
                String twitter = resultset.getString(11);
                String youtube = resultset.getString(12);
                Date createdAt = resultset.getDate(13);
                Date updatedAt = resultset.getDate(14);

                Politician politician = new Politician(id, name, zipcodes, position, party, email, phone, website, facebook, googleplus, twitter, youtube, createdAt, updatedAt);
                Politicians.add(politician);
            }

            Politician[] politicianArray = new Politician[Politicians.size()];
            politicianArray = Politicians.toArray(politicianArray);

            return politicianArray;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist.");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The politicians table doesn't exist in the database.");

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
