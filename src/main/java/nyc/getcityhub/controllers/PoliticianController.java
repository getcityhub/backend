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
    public static Politician[] retrievePolitician(Request request) throws InternalServerException{

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
                String firstName = resultset.getString(2);
                String lastName = resultset.getString(3);
                short zipCode = resultset.getShort(4);
                Date CreatedAt = resultset.getDate(5);
                Date UpdatedAt = resultset.getDate(6);

                Politician politician = new Politician(id, firstName, lastName, zipCode, CreatedAt, UpdatedAt);
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

