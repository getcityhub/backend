package nyc.getcityhub;

import nyc.getcityhub.controllers.TopicController;
import nyc.getcityhub.controllers.PoliticianController;
import nyc.getcityhub.controllers.PostController;
import nyc.getcityhub.controllers.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static spark.Spark.*;

public class Main {

    final private static Logger logger = LoggerFactory.getLogger(Main.class);
    final private static JsonTransformer transformer = new JsonTransformer();

    public static void main(String[] args) {
        // -p = production
        if (Arrays.asList(args).contains("-p")) {
            port(80);
        }

        before((request, response) -> response.type("application/json; charset=utf-8"));

        before((request, response) -> {
            DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            String reportDate = format.format(new Date());
            logger.info(request.ip() + " [" + reportDate + "] \"" + request.requestMethod() + " " + request.uri() + " " + request.protocol() + "\"");
        });

        post("/posts", (req, res) -> PostController.createPost(req), transformer);
        post("/users", (req, res) -> UserController.createUser(req), transformer);

        get("/topics", (req, res) -> TopicController.retrieveTopics(req), transformer);
        get("/posts", (req, res) -> PostController.retrievePosts(req), transformer);
        get("/politicians", (req, res) -> PoliticianController.retrievePolitician(req), transformer);

        exception(BadRequestException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(400, exception.getMessage());
            response.status(error.getStatusCode());
            response.body(transformer.render(error));
        });

        exception(InternalServerException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(500, exception.getMessage());
            response.status(error.getStatusCode());
            response.body(transformer.render(error));
        });

        notFound((request, response) -> {
            ResponseError error = new ResponseError(404, "Not found");
            return transformer.render(error);
        });

        internalServerError((request, response) -> {
            ResponseError error = new ResponseError(500, "Internal server error");
            return transformer.render(error);
        });
    }
}
