package nyc.getcityhub.models;

import nyc.getcityhub.Main;

import java.sql.*;
import java.util.Date;

/**
 * Created by carol on 2/5/17.
 */
public class Post {

    private int id;
    private Date createdAt;
    private Date updatedAt;
    private transient int authorId;
    private String title;
    private String text;
    private int topicId;
    private String language;
    private User author;

    public Post(int id, Date createdAt, Date updatedAt, int authorId, String title, String text, int topicId, String language) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authorId = authorId;
        this.title = title;
        this.text = text;
        this.topicId = topicId;
        this.language = language;
    }

    public void setAuthor(User author){ this.author = author; }

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() { return updatedAt; }

    public int getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLanguages()  {
        return language;
    }

    public int getTopicId() {
        return topicId;
    }

    public User getAuthor(){ return author; }

    public static Post getPostById(int id) {
        String command = "SELECT * FROM posts WHERE id = " + id;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                int authorId = resultSet.getInt(2);
                String title = resultSet.getString(3);
                String text = resultSet.getString(4);
                int topicId = resultSet.getInt(5);
                String language = resultSet.getString(6);
                Date createdAt = new Date(resultSet.getTimestamp(7).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(8).getTime());

                return new Post(id, createdAt, updatedAt, authorId, title, text, topicId, language);
            }
        }catch (SQLException e) {
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
