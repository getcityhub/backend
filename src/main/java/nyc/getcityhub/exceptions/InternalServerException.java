package nyc.getcityhub.exceptions;

import java.sql.SQLException;

/**
 * Created by carol on 2/12/17.
 */
public class InternalServerException extends Exception {

    private String message;

    public InternalServerException(String message) {
        this.message = message;
    }

    public InternalServerException(SQLException e) {
        if (e.getErrorCode() == 1049)
            message = "The database doesn't exist";
        else if (e.getErrorCode() == 1146)
            message = "The topics table doesn't exist in the database";
        else
            message = "An unknown exception occurred when accessing the database";
    }

    public String getMessage() {
        return message;
    }
}
