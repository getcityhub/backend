package nyc.getcityhub.exceptions;

/**
 * Created by v-jacco on 2/24/17.
 */
public class NotFoundException extends Exception {

    private String message;

    public NotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
