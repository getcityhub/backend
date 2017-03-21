package nyc.getcityhub.models;

import java.util.HashMap;

/**
 * Created by jackcook on 3/15/17.
 */
public class Email {

    private String fileName;
    private HashMap<String, String> data;
    private Language language;

    public Email(String fileName, HashMap<String, String> data, Language language) {
        this.fileName = fileName;
        this.data = data;
        this.language = language;
    }

    public String getFileName() {
        return fileName + ".email";
    }

    public HashMap<String, String> getData() {
        return data;
    }

    public Language getLanguage() {
        return language;
    }
}
