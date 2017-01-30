import spark.Request;

import java.sql.*;
import java.util.ArrayList;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        get("/categories", (req, res) -> retrieveCategories(req), new JsonTransformer());
        System.out.println("Base URL: http://localhost:4567");
    }

    private static Category[] retrieveCategories(Request request) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub");
            statement = connection.createStatement();
            resultset = statement.executeQuery("SElECT * FROM categories");

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
