import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        get("/categories", (req, res) -> retrieveCategories(req), new JsonTransformer());
        post("/posts", (req, res) -> createPost(req), new JsonTransformer());
        post("/users", (req, res) -> createUser(req), new JsonTransformer());

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

    private static User createUser(Request request) {
        JsonParser parser = new JsonParser();
        JsonObject userObject = (JsonObject) parser.parse(request.body());

        String firstName = userObject.get("firstName").getAsString();
        String lastName = userObject.get("lastName").getAsString();
        boolean anonymous = userObject.get("anonymous").getAsBoolean();

        JsonArray languages = userObject.get("languages").getAsJsonArray();
        String languagesString = "";

        for (JsonElement element : languages) {
            languagesString += element.getAsString() + ",";
        }

        languagesString = languagesString.substring(0, languagesString.length() - 1);

        JsonArray topics = userObject.get("topics").getAsJsonArray();
        String topicsString = "";

        for (JsonElement element : topics) {
            topicsString += element.getAsString() + ",";
        }

        topicsString = topicsString.substring(0, topicsString.length() - 1);

        String randomCode = generateRandomCode();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement userStatement = null;
        ResultSet userResultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub");

            String query = "INSERT INTO users (first_name, last_name, anonymous, languages, topics, unique_code) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setBoolean(3, anonymous);
            statement.setString(4, languagesString);
            statement.setString(5, topicsString);
            statement.setString(6, randomCode);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                userStatement = connection.createStatement();
                userResultSet = userStatement.executeQuery("SELECT * FROM users WHERE id = " + id);

                if (userResultSet.next()) {
                    String userFirstName = userResultSet.getString(2);
                    String userLastName = userResultSet.getString(3);
                    boolean userAnonymous = userResultSet.getBoolean(4);

                    String userLanguages = userResultSet.getString(5);
                    String[] userLanguagesArray = userLanguages.split(",");

                    String userTopics = userResultSet.getString(6);
                    String[] userTopicsArray = userTopics.split(",");

                    String userUniqueCode = userResultSet.getString(7);
                    Date userCreatedAt = userResultSet.getDate(8);
                    Date userUpdatedAt = userResultSet.getDate(9);

                    return new User(id, userFirstName, userLastName, userAnonymous, userLanguagesArray, userTopicsArray, userUniqueCode, userCreatedAt, userUpdatedAt);
                }
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } finally {
            if (userResultSet != null) {
                try {
                    userResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                userResultSet = null;
            }

            if (userStatement != null) {
                try {
                    userStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                userStatement = null;
            }

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

    private static String generateRandomCode() {
        String base = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";
        //deleted O, 0, I, l to avoid confusion
        String s = "";
        Random random = new Random();
        for (int i = 0; i < 8; i++)
            s += Character.toString(base.charAt(random.nextInt(base.length())));
        return s;
    }

    private static Post createPost(Request request){
        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        int authorId = postObject.get("authorId").getAsInt();
        String title = postObject.get("title").getAsString();
        String topic = postObject.get("topic").getAsString();
        String language = postObject.get("language").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement postStatement = null;
        ResultSet postResultSet = null;

        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub");

            String query = "INSERT INTO posts (author_id, title, topic, language) VALUES (?, ?, ?, ?)"; //topic needs to be linked to categories
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, authorId);
            statement.setString(2, title);
            statement.setString(3, topic);
            statement.setString(4, language);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                postStatement = connection.createStatement();
                postResultSet = postStatement.executeQuery("SELECT * FROM posts WHERE id = " + id);

                if (postResultSet.next()) {
                    int postAuthorId = postResultSet.getInt(2);
                    Date postCreatedAt = postResultSet.getDate(3);
                    Date postUpdatedAt = postResultSet.getDate(4);
                    String postTitle = postResultSet.getString(5);
                    String postTopic = postResultSet.getString(6);
                    String postLanguage = postResultSet.getString(7);

                    return new Post(id, postCreatedAt, postUpdatedAt, postAuthorId, postTitle, postLanguage, postTopic);
                }
            }
        }catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        } finally {
            if (postResultSet != null) {
                try {
                    postResultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                postResultSet = null;
            }

            if (postStatement != null) {
                try {
                    postStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                postStatement = null;
            }

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
