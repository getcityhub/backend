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
    private Date createdAt;
    private Date updatedAt;
    public Politician(int id, String firstName, String lastName, short zipCode, Date createdAt, Date updatedAt){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.zipCode = zipCode;
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
}


