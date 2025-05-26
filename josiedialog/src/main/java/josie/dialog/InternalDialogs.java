package josie.dialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InternalDialogs {
    public static JsonElement expired() {
        // TODO: allow the user to translate this
        final var root = new JsonObject();
        root.addProperty("type", "minecraft:notice");
        root.addProperty("title", "Expired");

        final var bodyArray = new JsonArray();

        final var bodyItem = new JsonObject();
        bodyItem.addProperty("type", "minecraft:plain_message");
        bodyItem.addProperty("contents", "This form has expired.");

        bodyArray.add(bodyItem);

        root.add("body", bodyArray);

        return root;
    }
}
