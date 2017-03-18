package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Language;
import nyc.getcityhub.models.Post;
import nyc.getcityhub.models.Topic;
import nyc.getcityhub.models.User;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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

        String language = postObject.get("language").getAsString();

        if (!Language.isLanguageSupported(language)) {
            throw new BadRequestException("The language '" + language + "' is not supported at this time.");
        }

        Language lang = Language.fromId(language);

        String title = postObject.get("title").getAsString();

        int minTitleLength = 10;
        int maxTitleLength = 50;

        if (lang == Language.CHINESE_SIMPLIFIED){
            minTitleLength = 2;
            maxTitleLength = 20;
        }

        if(title.length() < minTitleLength) {
            throw new BadRequestException("The title has to be at least " + minTitleLength + " characters long");
        } else if (title.length() > maxTitleLength){
            throw new BadRequestException("The title has to be at most " + maxTitleLength + " characters long");
        }

        int topicId = postObject.get("topicId").getAsInt();
        Topic topic = Topic.getTopicById(topicId, Language.ENGLISH);

        if (topic == null) {
            throw new BadRequestException("The topic ID must be valid.");
        }


        String text = postObject.get("text").getAsString();

        int minPostLength = 50;
        int maxPostLength = 5000;

        if (lang == Language.CHINESE_SIMPLIFIED){
            minPostLength = 15;
            maxPostLength = 1250;
        }

        if (text.length() < minPostLength){
            throw new BadRequestException("The post has to be at least " + minPostLength + " characters long.");
        } else if (text.length() > maxPostLength){
            throw new BadRequestException("The post has to be at most " + maxPostLength + " characters long.");
        }

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement likeStatement = null;
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

                likeStatement = connection.prepareStatement("UPDATE users SET liked = IF(CHAR_LENGTH(liked) = 0, '" + id + "', CONCAT(liked, '," + id + "')) WHERE id = " + authorId);
                likeStatement.executeUpdate();

                Post post = Post.getPostById(id);

                if (post != null) {
                    post.setAuthor(User.getUserById(authorId));
                    return post;
                } else {
                    throw new InternalServerException("An unknown error occurred.");
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        } finally {
            if (likeStatement != null) {
                try {
                    likeStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

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
            System.out.println(e.getMessage());
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

    public static int likePost(Request request, Response response, Boolean like) throws BadRequestException, InternalServerException, UnauthorizedException {
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
            throw new UnauthorizedException("You must be logged in to " + (like ? "like" : "unlike") + " posts.");
        }

        int userId = user.getId();
        User updatedUser = User.getUserById(userId);
        Post post = Post.getPostById(postId);

        Connection connection = null;
        PreparedStatement statement = null;
        PreparedStatement userStatement = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            ArrayList<Integer> likedPostIds = new ArrayList<>();

            for (int likedPostId : updatedUser.getLiked()) {
                likedPostIds.add(likedPostId);
            }

            if (like == !likedPostIds.contains(postId) && post.getAuthorId() != userId) {
                statement = connection.prepareStatement("UPDATE posts SET likes = likes " + (like ? "+" : "-") + " 1 where id = " + postId);
                statement.executeUpdate();

                if (like) {
                    likedPostIds.add(postId);
                } else {
                    likedPostIds.remove(new Integer(postId));
                }

                String likedPostIdsString = likedPostIds.stream().map(Object::toString).collect(Collectors.joining(","));

                userStatement = connection.prepareStatement("UPDATE users SET liked = ? WHERE id = ?");
                userStatement.setString(1, likedPostIdsString);
                userStatement.setInt(2, userId);
                userStatement.executeUpdate();
            }

            response.status(204);
            return 0;
        } catch (SQLException e) {
            throw new InternalServerException(e);
        } finally {
            if (userStatement != null) {
                try {
                    userStatement.close();
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
