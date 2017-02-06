package nyc.getcityhub;

import nyc.getcityhub.controllers.CategoryController;
import nyc.getcityhub.controllers.PostController;
import nyc.getcityhub.controllers.UserController;

import static spark.Spark.*;

public class Main {

    public static void main(String[] args) {
        before((request, response) -> response.type("application/json; charset=utf-8"));

        post("/posts", (req, res) -> PostController.createPost(req), new JsonTransformer());
        post("/users", (req, res) -> UserController.createUser(req), new JsonTransformer());

        get("/categories", (req, res) -> CategoryController.retrieveCategories(req), new JsonTransformer());
        get("/posts",(req, res) -> PostController.retrievePosts(req), new JsonTransformer());

        exception(BadRequestException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(400, exception.getMessage());
            response.status(error.getStatusCode());

            JsonTransformer transformer = new JsonTransformer();
            response.body(transformer.render(error));
        });

        System.out.println("Base URL: http://localhost:4567");
    }
}
