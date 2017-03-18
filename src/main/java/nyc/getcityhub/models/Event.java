package nyc.getcityhub.models;

import java.sql.*;
import java.util.Date;

import static nyc.getcityhub.Constants.JDBC_URL;

/**
 * Created by carol on 3/18/17.
 */
public class Event {
    private int id;
    private String host;
    private String name;
    private String description;
    private Date startDate;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;

    public Event(int id, String host, String name, String description, Date startDate, Date endDate, Date createdAt, Date updatedAt){
        this.id = id;
        this.host = host;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId(){
        return id;
    }

    public String getHost(){
        return host;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Date startDate(){
        return startDate;
    }

    public Date getEndDate(){
        return endDate;
    }

    public Date getCreatedAt(){
        return createdAt;
    }

    public Date getUpdatedAt(){
        return updatedAt;
    }

    public static Event getEventById(int id) {
        String command = "SELECT * FROM events WHERE id = " + id;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                String host = resultSet.getString(2);
                String name = resultSet.getString(3);
                String description = resultSet.getString(4);
                Date startDate = new Date(resultSet.getTimestamp(5).getTime());
                Date endDate = new Date(resultSet.getTimestamp(6).getTime());
                Date createdAt = new Date(resultSet.getTimestamp(7).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(8).getTime());

                return new Event(id, host, name, description, startDate, endDate, createdAt, updatedAt);
            }

            return null;
        } catch (SQLException e) {
            return null;
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

