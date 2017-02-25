package nyc.getcityhub;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nyc.getcityhub.models.User;
import spark.ResponseTransformer;

public class JsonTransformer implements ResponseTransformer {

    private Gson gson = new GsonBuilder()
            .disableHtmlEscaping()
            .registerTypeAdapter(User.class, new UserSerializer())
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
            .create();

    @Override
    public String render(Object model) {
        return gson.toJson(model);
    }
}
