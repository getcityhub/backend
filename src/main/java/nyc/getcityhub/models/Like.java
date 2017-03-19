package nyc.getcityhub.models;

import nyc.getcityhub.exceptions.InternalServerException;

import java.sql.*;

import static nyc.getcityhub.Constants.JDBC_URL;

/**
 * Created by carol on 3/18/17.
 */
public class Like {

    private int userId;
    private int postId;

    public Like(int userId,int postId){
        this.userId = userId;
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public int getPostId() {
        return postId;
    }

    public static boolean doesUserLikePost(int userId, int postId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            statement = connection.prepareStatement("SELECT * FROM likes WHERE author_id = " + userId + " AND post_id = " + postId);
            resultSet = statement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            return false;
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

    public static int likesOnPost(int postId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            statement = connection.prepareStatement("SELECT COUNT(*) FROM likes WHERE post_id = " + postId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            return 0;
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
}
