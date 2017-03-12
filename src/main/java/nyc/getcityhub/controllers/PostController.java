package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.User;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;

import static nyc.getcityhub.Constants.*;

/**
 * Created by jackcook on 06/02/2017.
 */
public class PostController {

    public static Post createPost(Request request, Response response) throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'title', 'topicId', 'language', and 'text' keys must be included in your request body.");
        }

        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You must be logged in to create posts.");
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

        try {
            connection = DriverManager.getConnection(JDBC_URL);

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
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            throw new InternalServerException(e);
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

    public static Post retrievePost(Request request, Response response) throws BadRequestException, NotFoundException {
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        Post post = Post.getPostById(id);

        if (post == null) {
            throw new NotFoundException("The post requested does not exist");
        } else {
            return post;
        }
    }

    public static Post[] retrievePosts(Request request, Response response) throws InternalServerException {
        String language = Language.fromId(request.headers("Accept-Language")).getId();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "SELECT *, ((likes - 1) / power(time_to_sec(timediff(NOW(), created_at)) / 3600 + 2, 1.8)) AS score FROM posts ORDER BY score DESC";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            ArrayList<Post> posts = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                int authorId = resultSet.getInt(2);
                String title = resultSet.getString(3);
                String text = resultSet.getString(4);
                int postTopicId = resultSet.getInt(5);
                String postLanguage = resultSet.getString(6);
                int likes = resultSet.getInt(7);
                Date createdAt = new Date(resultSet.getTimestamp(8).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(9).getTime());

                Post post = new Post(id, createdAt, updatedAt, authorId, title, text, postTopicId, postLanguage, likes);
                post.setAuthor(User.getUserById(authorId));
                posts.add(post);
            }

            Post[] postsArray = new Post[posts.size()];
            postsArray = posts.toArray(postsArray);

            return postsArray;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            throw new InternalServerException(e);
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

    public static int likePost(Request request, Response response) throws BadRequestException, InternalServerException, UnauthorizedException {
        String idString = request.params(":id");
        int postId;

        try {
            postId = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        if (postId <= 0) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        User user = request.session().attribute("user");

        if (user == null) {
            throw new UnauthorizedException("You must be logged in to like posts.");
        }

        int userId = user.getId();

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement userStatement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            statement = connection.prepareStatement("UPDATE posts SET likes = likes + 1 where id = " + postId);
            statement.executeUpdate();

            userStatement = connection.prepareStatement("UPDATE users SET liked = IF(CHAR_LENGTH(liked) = 0, '" + postId + "', CONCAT(liked, '," + postId + "')) WHERE id = " + userId);
            userStatement.executeUpdate();

            response.status(204);
            return 0;
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());

            throw new InternalServerException(e);
        } finally {
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
