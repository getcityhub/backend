package nyc.getcityhub;

/**
 * Created by carol on 2/5/17.
 */
public class ResponseError {

    private int statusCode;
    private String message;

    public ResponseError(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
