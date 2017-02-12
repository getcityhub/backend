package nyc.getcityhub.models;

import java.util.Date;

/**
 * Created by stephanie on 2/12/17.
 */
public class Politician {
    private int id;
    private String firstName;
    private String lastName;
    private short zipCode;
    private String email;
    private String phoneNumber;
    private String facebook;
    private String google;
    private String twitter;
    private Date createdAt;
    private Date updatedAt;

    public Politician(int id, String firstName, String lastName, short zipCode, String email, String phoneNumber, String facebook, String google, String twitter, Date createdAt, Date updatedAt){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.zipCode = zipCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.facebook = facebook;
        this.google = google;
        this.twitter = twitter;
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

    public short getZipCode() {
        return zipCode;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getFacebook() { return facebook; }

    public String getGoogle() { return google; }

    public String getTwitter() { return twitter;}

}


