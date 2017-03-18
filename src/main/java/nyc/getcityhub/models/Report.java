package nyc.getcityhub.models;

import java.sql.*;
import java.util.Date;

import static nyc.getcityhub.Constants.*;

/**
 * Created by carol on 3/10/17.
 */
public class Report {

    private int id;
    private int reporterId;
    private Date createdAt;
    private Date updatedAt;
    private String language;
    private String text;
    private int reasonId;
    private User reporter;

    public Report(int id, int reporterId, Date createdAt, Date updatedAt, String language, String text, int reasonId) {
        this.id = id;
        this.reporterId = reporterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.language = language;
        this.text = text;
        this.reasonId = reasonId;
    }

    public void setReporter(User reporter){
        this.reporter = reporter;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return reporterId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getLanguage() {
        return language;
    }

    public String getText() {
        return text;
    }

    public int getReasonId() {
        return reasonId;
    }

    public static Report getReportById(int id) {
        String command = "SELECT * FROM reports WHERE id = " + id;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                int reporterId = resultSet.getInt(2);
                String language = resultSet.getString(3);
                String text = resultSet.getString(4);
                int reasonId = ReportReason.fromId(resultSet.getInt(5)).getId();
                Date createdAt = new Date(resultSet.getTimestamp(6).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(7).getTime());

                return new Report(id, reporterId, createdAt, updatedAt, language, text, reasonId);
            }
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

        return null;
    }
}
