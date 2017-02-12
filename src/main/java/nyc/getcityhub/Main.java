package nyc.getcityhub;

import nyc.getcityhub.controllers.CategoryController;
import nyc.getcityhub.controllers.PoliticianController;
import nyc.getcityhub.controllers.PostController;
import nyc.getcityhub.controllers.UserController;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static spark.Spark.*;

public class Main {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        before((request, response) -> response.type("application/json; charset=utf-8"));

        before((request, response) -> {
            DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            Date date = Calendar.getInstance().getTime();
            String reportDate = df.format(date);
            logger.info(request.ip() + " [" + reportDate + "] \"" + request.requestMethod() + " " + request.uri() + " " + request.protocol() + "\"");
            //need to add response
        });

        post("/posts", (req, res) -> PostController.createPost(req), new JsonTransformer());
        post("/users", (req, res) -> UserController.createUser(req), new JsonTransformer());

        get("/categories", (req, res) -> CategoryController.retrieveCategories(req), new JsonTransformer());
        get("/posts", (req, res) -> PostController.retrievePosts(req), new JsonTransformer());
        get("/politicians", (req, res) -> PoliticianController.retrievePolitician(req), new JsonTransformer());

        exception(BadRequestException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(400, exception.getMessage());
            response.status(error.getStatusCode());

            JsonTransformer transformer = new JsonTransformer();
            response.body(transformer.render(error));
        });

        exception(InternalServerException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(500, exception.getMessage());
            response.status(error.getStatusCode());

            JsonTransformer transformer = new JsonTransformer();
            response.body(transformer.render(error));
        });

        logger.info("Base URL: http://localhost:4567");
    }
}
