package nyc.getcityhub.models;

import java.util.Date;

/**
 * Created by stephanie on 2/12/17.
 */
public class Politician {

    private int id;
    private String name;
    private int[] zipcodes;
    private String position;
    private String party;
    private String email;
    private String phone;
    private String website;
    private String facebook;
    private String googleplus;
    private String twitter;
    private String youtube;
    private Date createdAt;
    private Date updatedAt;

    public Politician(int id, String name, int[] zipcodes, String position, String party, String email, String phone, String website, String facebook, String googleplus, String twitter, String youtube, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.zipcodes = zipcodes;
        this.position = position;
        this.party = party;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.facebook = facebook;
        this.googleplus = googleplus;
        this.twitter = twitter;
        this.youtube = youtube;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int[] getZipcode() {
        return zipcodes;
    }

    public String getPosition() {
        return position;
    }

    public String getParty() {
        return party;
    }

    public String getEmailAddress() {
        return email;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public String getWebsiteURL() {
        return website;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getGooglePlus() {
        return googleplus;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}


