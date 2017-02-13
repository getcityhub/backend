package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.BadRequestException;
import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.Main;
import nyc.getcityhub.models.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class PostController {

    public static Post createPost(Request request) throws BadRequestException, InternalServerException {
        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        if (!postObject.has("authorId")
                || !postObject.has("title")
                || !postObject.has("categoryId")
                || !postObject.has("text")
                || !postObject.has("language")) {
            throw new BadRequestException("The 'authorId', 'title', 'categoryId', 'language', and 'text' keys must be included in your request body.");
        }

        int authorId = postObject.get("authorId").getAsInt();
        String title = postObject.get("title").getAsString();
        int categoryId = postObject.get("categoryId").getAsInt();
        String text = postObject.get("text").getAsString();
        String language = postObject.get("language").getAsString();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement postStatement = null;
        ResultSet postResultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");

            String query = "INSERT INTO posts (author_id, title, category_id, text, language) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, authorId);
            statement.setString(2, title);
            statement.setInt(3, categoryId);
            statement.setString(4, text);
            statement.setString(5, language);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                postStatement = connection.createStatement();
                postResultSet = postStatement.executeQuery("SELECT * FROM posts WHERE id = " + id);

                if (postResultSet.next()) {
                    int postAuthorId = postResultSet.getInt(2);
                    String postTitle = postResultSet.getString(3);
                    String postText = postResultSet.getString(4);
                    int postCategoryId = postResultSet.getInt(5);
                    String postLanguage = postResultSet.getString(6);
                    Date postCreatedAt = postResultSet.getDate(7);
                    Date postUpdatedAt = postResultSet.getDate(8);

                    return new Post(id, postCreatedAt, postUpdatedAt, postAuthorId, postTitle, postText, postCategoryId, postLanguage);
                }
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The posts table doesn't exist in the database");

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

    public static Post[] retrievePosts(Request request) throws InternalServerException {
        String categoryId = request.queryParams("cid");
        String language = request.queryParams("lang");
        String zipCode = request.queryParams("zip");

        Logger logger = LoggerFactory.getLogger(PostController.class);
        logger.debug("category id: " + categoryId);

        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();

            ArrayList<String> parts = new ArrayList<>();

            if (categoryId != null) parts.add("category_id = " + categoryId);
            if (language != null) parts.add("language = '" + language + "'");
            //if (zipCode != null) parts.add("zip_code = " + zipCode);

            String command = "SELECT * FROM posts";

            if (parts.size() > 0) {
                command += " WHERE ";

                for (String part : parts) {
                    command += part;

                    if (!part.equals(parts.get(parts.size() - 1))) {
                        command += " AND ";
                    }
                }
            }

            resultset = statement.executeQuery(command);
            ArrayList<Post> posts = new ArrayList<>();

            while (resultset.next()) {
                int id = resultset.getInt(1);
                int authorId = resultset.getInt(2);
                String title = resultset.getString(3);
                String text = resultset.getString(4);
                int postCategoryId = resultset.getInt(5);
                String postLanguage = resultset.getString(6);
                Date createdAt = resultset.getDate(7);
                Date updatedAt = resultset.getDate(8);

                Post post = new Post(id, createdAt, updatedAt, authorId, title, text, postCategoryId, postLanguage);
                posts.add(post);
            }

            Post[] postsArray = new Post[posts.size()];
            postsArray = posts.toArray(postsArray);

            return postsArray;
        } catch (SQLException e) {
            if (e.getErrorCode() == 1049)
                throw new InternalServerException("The MySQL database doesn't exist");
            else if (e.getErrorCode() == 1146)
                throw new InternalServerException("The posts table doesn't exist in the database");

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
