package nyc.getcityhub.models;

import nyc.getcityhub.Main;

import java.sql.*;
import java.util.Date;

/**
 * Created by carol on 3/10/17.
 */
public class Report {
    private int id;
    private int reporterId;
    private Date createdAt;
    private Date updatedAt;
    private String text;
    private ReportReason reason;
    private User reporter;

    public Report(int id, int reporterId, Date createdAt, Date updatedAt, String text, ReportReason reason){
        this.id = id;
        this.reporterId = reporterId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.text = text;
        this.reason = reason;
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

    public String getText() {
        return text;
    }

    public ReportReason getReason() {
        return reason;
    }

    public static Report getReportById(int id) {
        String command = "SELECT * FROM reports WHERE id = " + id;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/cityhub?user=root&password=cityhub&useSSL=" + Main.PRODUCTION);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(command);

            if (resultSet.next()) {
                int reporterId = resultSet.getInt(2);
                String text = resultSet.getString(3);
                ReportReason reason = ReportReason.fromId(resultSet.getInt(4));
                Date createdAt = new Date(resultSet.getTimestamp(5).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(6).getTime());

                return new Report(id, reporterId, createdAt, updatedAt, text, reason);
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
