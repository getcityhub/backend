import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        get("/hello", (req, res) -> "Hello World!");
        // base url: http://localhost:4567
    }
}
