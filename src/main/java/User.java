import java.util.Date;
import java.util.Random;

public class User {
    private String lastName;
    private String firstName;
    private boolean anonymous;
    private String uniqueC;
    private String[] languages;
    private String[] topics;
    private int id;
    private Date update;
    private Date createdAt;

    public User(String lastName, String firstName, boolean anonymous, String[] languages, String[] topics, int id){
        this.lastName = lastName;
        this.firstName = firstName;
        this.anonymous = anonymous;
        this.languages = languages;
        this.topics = topics;
        this.id = id;
        update = new Date();
        createdAt = new Date();
        uniqueC = generateRandom();
    }

    public static String generateRandom(){
        String base = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz123456789";
        //deleted O, 0, I, l to avoid confusion
        String s = "";
        Random random = new Random();
        for(int i = 0; i < 8; i++)
            s += Character.toString(base.charAt(random.nextInt(base.length())));
        return s;
    }

    public String getLastName(){
        return lastName;
    }

    public String getFirstName(){
        return firstName;
    }

    public boolean isAnonymous(){
        return anonymous;
    }

    public String[] getLanguages(){
        return languages;
    }

    public String[] getTopics(){
        return topics;
    }

    public String getUniqueC(){
        return uniqueC;
    }
}
