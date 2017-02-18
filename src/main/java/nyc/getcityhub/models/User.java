package nyc.getcityhub.models;

import java.util.Date;

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private boolean anonymous;
    private short zipcode;
    private String[] languages;
    private String emailAddress;
    private String uniqueCode;
    private Date createdAt;
    private Date updatedAt;

    public User(int id, String firstName, String lastName, boolean anonymous, short zipcode, String[] languages, String emailAddress, String uniqueCode, Date createdAt, Date updatedAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.anonymous = anonymous;
        this.languages = languages;
        this.emailAddress = emailAddress;
        this.uniqueCode = uniqueCode;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.zipcode = zipcode;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public short getZipcode() {
        return zipcode;
    }

    public String[] getLanguages()  {
        return languages;
    }

    public String getEmailAddress() { return emailAddress; }

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
