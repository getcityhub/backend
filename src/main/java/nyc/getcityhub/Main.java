package nyc.getcityhub;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import nyc.getcityhub.controllers.*;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import static nyc.getcityhub.Credentials.*;
import static spark.Spark.*;

public class Main {

    final private static Logger logger = LoggerFactory.getLogger(Main.class);
    final private static JsonTransformer transformer = new JsonTransformer();

    public static boolean PRODUCTION = false;
    public static Configuration FTL_CONFIG;

    public static void main(String[] args) {
        Credentials.loadCredentials();

        // -p = production
        if (Arrays.asList(args).contains("-p")) {
            PRODUCTION = true;

            port(443);
            secure("keystore.jks", SSL_KEYPASS, null, null);
        }

        FTL_CONFIG = new Configuration(Configuration.VERSION_2_3_25);

        try {
            FTL_CONFIG.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/emails"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FTL_CONFIG.setDefaultEncoding("UTF-8");
        FTL_CONFIG.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        FTL_CONFIG.setLogTemplateExceptions(true);

        before((request, response) -> response.type("application/json; charset=utf-8"));

        before((request, response) -> {
            DateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
            String reportDate = format.format(new Date());
            logger.info(request.ip() + " [" + reportDate + "] \"" + request.requestMethod() + " " + request.uri() + " " + request.protocol() + "\"");
        });

        path("/posts", () -> {
            get("", PostController::retrievePosts, transformer);
            get("/:id", PostController::retrievePost, transformer);
            post("", PostController::createPost, transformer);
            post("/:id/like", PostController::likePost);
        });

        path("/reports", () -> {
           get("/:id", ReportController::retrieveReport, transformer);
           post("", ReportController::createReport, transformer);
        });

        path("/users", () -> {
            delete("/current", UserController::logoutUser);
            get("/:id", UserController::retrieveUser, transformer);
            get("/current", UserController::retrieveCurrentUser, transformer);
            patch("/reset", UserController::updatePassword);
            post("", UserController::createUser, transformer);
            post("/login", UserController::loginUser, transformer);
            post("/reset", UserController::forgotPassword);
        });

        path("/politicians", () -> {
            get("", PoliticianController::retrievePoliticians, transformer);
            get("/:id", PoliticianController::retrievePolitician, transformer);
        });

        path("/topics", () -> {
            get("", TopicController::retrieveTopics, transformer);
            get("/:id",TopicController::retrieveTopic, transformer);
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
