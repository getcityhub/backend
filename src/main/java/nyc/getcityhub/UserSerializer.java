package nyc.getcityhub;

import com.google.gson.*;
import nyc.getcityhub.models.User;

import java.lang.reflect.Type;

/**
 * Created by v-jacco on 2/25/17.
 */
public class UserSerializer implements JsonSerializer<User> {

    @Override
    public JsonElement serialize(User user, Type type, JsonSerializationContext jsonSerializationContext) {
        Gson gson = new Gson();
        JsonObject object = (JsonObject) gson.toJsonTree(user);

        object.remove("anonymous");
        object.remove("emailAddress");
        object.remove("uniqueCode");

        if (user.isAnonymous()) {
            object.remove("firstName");
            object.remove("lastName");
        }

        return object;
    }
}
