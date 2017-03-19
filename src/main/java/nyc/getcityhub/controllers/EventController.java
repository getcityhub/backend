package nyc.getcityhub.controllers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nyc.getcityhub.exceptions.BadRequestException;
import nyc.getcityhub.exceptions.InternalServerException;
import nyc.getcityhub.exceptions.NotFoundException;
import nyc.getcityhub.exceptions.UnauthorizedException;
import nyc.getcityhub.models.Event;
import spark.Request;
import spark.Response;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import static nyc.getcityhub.Constants.*;

/**
 * Created by jackcook on 06/02/2017.
 */
public class EventController {

    public static Event createEvent(Request request, Response response) throws BadRequestException, UnauthorizedException, InternalServerException {
        if (request.body().length() == 0) {
            throw new BadRequestException("The 'host', 'name', 'description', 'startDate', and 'endDate' keys must be included in your request body.");
        }

        JsonParser parser = new JsonParser();
        JsonObject eventObject =  (JsonObject) parser.parse(request.body());

        if (!eventObject.has("host")
                || !eventObject.has("name")
                || !eventObject.has("description")
                || !eventObject.has("startDate")
                || !eventObject.has("endDate")) {
            throw new BadRequestException("The 'host', 'name', 'description', 'startDate', and 'endDate' keys must be included in your request body.");
        }

        String host = eventObject.get("host").getAsString();
        String name = eventObject.get("name").getAsString();
        String description = eventObject.get("description").getAsString();
        Date startDate = new Date(eventObject.get("startDate").getAsInt());
        Date endDate = new Date(eventObject.get("endDate").getAsInt());

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "INSERT INTO events (host, name, description, start_date, end_date) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, host);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setDate(4, startDate);
            statement.setDate(5, endDate);
            statement.executeUpdate();

            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                int id = resultSet.getInt(1);

                Event event = Event.getEventById(id);

                if (event != null) {
                    return event;
                } else {
                    throw new InternalServerException("An unknown error occurred.");
                }
            }
        } catch (SQLException e) {
            throw new InternalServerException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    public static Event retrieveEvent(Request request, Response response) throws BadRequestException, NotFoundException {
        String idString = request.params(":id");
        int id;

        try {
            id = Integer.parseInt(idString);
        } catch(NumberFormatException e) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        if (id <= 0) {
            throw new BadRequestException(idString + " is not a valid post id.");
        }

        Event event = Event.getEventById(id);

        if (event == null) {
            throw new NotFoundException("The post requested does not exist");
        } else {
            return event;
        }
    }

    public static Event[] retrieveEvents(Request request, Response response) throws InternalServerException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(JDBC_URL);

            String query = "SELECT * FROM events";
            statement = connection.prepareStatement(query);
            resultSet = statement.executeQuery();

            ArrayList<Event> events = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String host = resultSet.getString(2);
                String name = resultSet.getString(3);
                String description = resultSet.getString(4);
                Date startDate = new Date(resultSet.getTimestamp(5).getTime());
                Date endDate = new Date(resultSet.getTimestamp(6).getTime());
                Date createdAt = new Date(resultSet.getTimestamp(7).getTime());
                Date updatedAt = new Date(resultSet.getTimestamp(8).getTime());

                Event event = new Event(id, host, name, description, startDate, endDate, createdAt, updatedAt);
                events.add(event);
            }

            Event[] eventsArray = new Event[events.size()];
            eventsArray = events.toArray(eventsArray);

            return eventsArray;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new InternalServerException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
