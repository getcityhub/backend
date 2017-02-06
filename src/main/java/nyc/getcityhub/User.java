package nyc.getcityhub;

import java.util.Date;
import java.util.Random;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private boolean anonymous;
    private short zipCode;
    private String[] languages;
    private String[] topics;
    private String uniqueCode;
    private Date createdAt;
    private Date updatedAt;

    public User(int id, String firstName, String lastName, boolean anonymous, short zipCode, String[] languages, String[] topics, String uniqueCode, Date createdAt, Date updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.anonymous = anonymous;
        this.languages = languages;
        this.topics = topics;
        this.uniqueCode = uniqueCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.zipCode = zipCode;
    }
    public int getId() {return id; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public short getZipCode(){ return zipCode; }

    public String[] getLanguages()  {
        return languages;
    }

    public String[] getTopics() {
        return topics;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

}
