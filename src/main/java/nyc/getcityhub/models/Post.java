package nyc.getcityhub.models;

import java.util.Date;

/**
 * Created by carol on 2/5/17.
 */
public class Post {

    private int id;
    private Date createdAt;
    private Date updatedAt;
    private int authorId;
    private String title;
    private String text;
    private int categoryId;
    private String language;

    public Post(int id, Date createdAt, Date updatedAt, int authorId, String title, String text, int categoryId, String language) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authorId = authorId;
        this.title = title;
        this.text = text;
        this.categoryId = categoryId;
        this.language = language;
    }

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() { return updatedAt; }

    public int getAuthorId() {
        return authorId;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getLanguages()  {
        return language;
    }

    public int getCategoryId() {
        return categoryId;
    }
}
