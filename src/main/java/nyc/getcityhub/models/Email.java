package nyc.getcityhub.models;

import java.util.HashMap;

/**
 * Created by jackcook on 3/15/17.
 */
public class Email {

    private String fileName;
    private HashMap<String, String> data;
    private String subject;

    public Email(String fileName, HashMap<String, String> data, String subject) {
        this.fileName = fileName;
        this.data = data;
        this.subject = subject;
    }

    public String getFileName() {
        return fileName + ".email";
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public String getSubject() {
        return subject;
    }
}
