package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.BadRequestException;
import nyc.getcityhub.InternalServerException;
import nyc.getcityhub.NotFoundException;
import nyc.getcityhub.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.User;
import spark.Request;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by jackcook on 06/02/2017.
 */
public class PostController {

    public static Post createPost(Request request) throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'title', 'topicId', 'language', and 'text' keys must be included in your request body.");
        }

        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You must be logged in to create posts");
        }

        JsonParser parser = new JsonParser();
        JsonObject postObject =  (JsonObject) parser.parse(request.body());

        if (!postObject.has("title")
                || !postObject.has("topicId")
                || !postObject.has("text")
                || !postObject.has("language")) {
            throw new BadRequestException("The 'title', 'topicId', 'language', and 'text' keys must be included in your request body.");
        }

        int authorId = user.getId();
        String title = postObject.get("title").getAsString();
        int topicId = postObject.get("topicId").getAsInt();
        String text = postObject.get("text").getAsString();
        String language = postObject.get("language").getAsString();

        if (!Language.isLanguageSupported(language)) {
            throw new BadRequestException("The language '" + language + "' is not supported at this time.");
        }

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        Statement postStatement = null;
        ResultSet postResultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");

            String query = "INSERT INTO posts (author_id, title, topic_id, text, language) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, authorId);
            statement.setString(2, title);
            statement.setInt(3, topicId);
            statement.setString(4, text);
            statement.setString(5, language);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);
                Post post = Post.getPostById(id);
                post.setAuthor(User.getUserById(authorId));

                return post;
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

    public static Post retrievePost(Request request) throws BadRequestException, NotFoundException {
        String idString = request.params(":id");
        int id = 0;
        try{
            id = Integer.parseInt(idString);
        }
        catch( NumberFormatException e ) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        if (id<=0){
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        Post post = Post.getPostById(id);

        if (post == null) {
            throw new NotFoundException("The post requested does not exist");
        }
        else {
             return post;
        }
    }

    public static Post[] retrievePosts(Request request) throws InternalServerException {
        String topicId = request.queryParams("cid");
        String language = request.queryParams("lang");
        String zipcode = request.queryParams("zip");

        Connection connection = null;
        Statement statement = null;
        ResultSet resultset = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=false");
            statement = connection.createStatement();

            ArrayList<String> parts = new ArrayList<>();

            if (topicId != null) parts.add("topic_id = " + topicId);
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
                int postTopicId = resultset.getInt(5);
                String postLanguage = resultset.getString(6);
                Date createdAt = resultset.getDate(7);
                Date updatedAt = resultset.getDate(8);

                Post post = new Post(id, createdAt, updatedAt, authorId, title, text, postTopicId, postLanguage);
                post.setAuthor(User.getUserById(authorId));
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
