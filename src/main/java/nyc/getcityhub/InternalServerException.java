package nyc.getcityhub;

/**
 * Created by carol on 2/12/17.
 */
public class InternalServerException extends Exception {

    private String message;

    public InternalServerException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
