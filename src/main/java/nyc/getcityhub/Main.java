package nyc.getcityhub;

import nyc.getcityhub.controllers.TopicController;
import nyc.getcityhub.controllers.PoliticianController;
import nyc.getcityhub.controllers.PostController;
import nyc.getcityhub.controllers.UserController;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static spark.Spark.*;

public class Main {

    final private static Logger logger = LoggerFactory.getLogger(Main.class);
    final private static JsonTransformer transformer = new JsonTransformer();

    public static boolean PRODUCTION = false;

    public static void main(String[] args) {
        // -p = production
        if (Arrays.asList(args).contains("-p")) {
            PRODUCTION = true;

            port(443);

            try {
                String keypass = new String(Files.readAllBytes(Paths.get("keypass.txt"))).replace("\n", "");
                secure("keystore.jks", keypass, null, null);
            } catch (IOException e) {
                System.out.println("Error reading keystore password:");
                e.printStackTrace();
            }
        }

        before((request, response) -> response.type("application/json; charset=utf-8"));

        before((request, response) -> {
            DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            String reportDate = format.format(new Date());
            logger.info(request.ip() + " [" + reportDate + "] \"" + request.requestMethod() + " " + request.uri() + " " + request.protocol() + "\"");
        });

        path("/posts", () -> {
            get("", (req, res) -> PostController.retrievePosts(req), transformer);
            get(":id", (req, res) -> PostController.retrievePost(req), transformer);
            post("", (req, res) -> PostController.createPost(req), transformer);
        });

        path("/users", () -> {
            delete("/current", UserController::logoutUser);
            get(":id", (req, res) -> UserController.retrieveUser(req), transformer);
            get("/current", (req, res) -> UserController.retrieveCurrentUser(req), transformer);
            post("", (req, res) -> UserController.createUser(req), transformer);
            post("/login", (req, res) -> UserController.loginUser(req), transformer);
        });

        path("/politicians", () -> {
            get("", (req, res) -> PoliticianController.retrievePoliticians(req), transformer);
            get(":id", (req,res) -> PoliticianController.retrievePolitician(req), transformer);
        });

        path("/topics", () -> {
            get("", (req, res) -> TopicController.retrieveTopics(req), transformer);
            get(":id", (req, res) -> TopicController.retrieveTopic(req), transformer);
        });

        exception(BadRequestException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(400, exception.getMessage());
            response.status(error.getStatusCode());
            response.body(transformer.render(error));
        });

        exception(UnauthorizedException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(401, exception.getMessage());
            response.status(error.getStatusCode());
            response.body(transformer.render(error));
        });

        exception(NotFoundException.class, (exception, request, response) -> {
            ResponseError error = new ResponseError(404, exception.getMessage());
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
