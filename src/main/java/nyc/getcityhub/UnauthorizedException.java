package nyc.getcityhub;

/**
 * Created by v-jacco on 2/25/17.
 */
public class UnauthorizedException extends Exception {

    private String message;

    public UnauthorizedException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
