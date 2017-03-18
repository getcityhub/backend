package nyc.getcityhub.models;

/**
 * Created by carol on 3/18/17.
 */
public class Like {
    private int userId;
    private int postId;

    public Like(int userId,int postId){
        this.userId = userId;
        this.postId = postId;
    }

    public int getUserId(){
        return userId;
    }

    public int getPostId(){
        return postId;
    }
}
