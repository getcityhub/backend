package nyc.getcityhub.controllers;

import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.models.Category;
import spark.Request;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class CategoryController {

    public static Category[] retrieveCategories(Request request) throws InternalServerException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub");
            statement = connection.createStatement();
            resultset = statement.executeQuery("SELECT * FROM categories");

            ArrayList<Category> categories = new ArrayList<Category>();

            while (resultset.next()) {
                int id = resultset.getInt(1);
                String name = resultset.getString(2);

                Category category = new Category(id, name);
                categories.add(category);
            }

            Category[] categoriesArray = new Category[categories.size()];
            categoriesArray = categories.toArray(categoriesArray);

            return categoriesArray;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist.");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The categories table doesn't exist in the database.");

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
