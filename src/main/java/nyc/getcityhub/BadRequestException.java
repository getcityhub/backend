package nyc.getcityhub;

/**
 * Created by jackcook on 06/02/2017.
 */
public class BadRequestException extends Exception {

    private String message;

    public BadRequestException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
