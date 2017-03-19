package nyc.getcityhub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nyc.getcityhub.models.User;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .registerTypeAdapter(User.class, new UserSerializer())
            .create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
