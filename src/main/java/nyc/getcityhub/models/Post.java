package nyc.getcityhub.models;

import java.util.Date;

/**
 * Created by carol on 2/5/17.
 */
public class Post {

    private int id;
    private Date createdAt;
    private Date updatedAt;
    private transient int authorId;
    private String title;
    private String text;
    private int topicId;
    private String language;
    private User author;

    public Post(int id, Date createdAt, Date updatedAt, int authorId, String title, String text, int topicId, String language) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.authorId = authorId;
        this.title = title;
        this.text = text;
        this.topicId = topicId;
        this.language = language;
    }

    public void setAuthor(User author){ this.author = author; }

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

    public int getTopicId() {
        return topicId;
    }

    public User getAuthor(){ return author; }
}
