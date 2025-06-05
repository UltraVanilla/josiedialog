package josie.dialog;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.Objects;
import java.util.UUID;
import josie.dialog.api.PlatformHolder;
import org.jspecify.annotations.Nullable;

class DialogPostprocessingUtils {
    protected static JsonElement postProcess(
            final JsonElement input, final String submitId, @Nullable final UUID stateId) {
        if (input instanceof final JsonObject obj) {
            if (obj.get("type") instanceof final JsonPrimitive type) {
                final var typeString = type.getAsString();

                if (Objects.equals(typeString, "josiedialog_form")) {
                    obj.addProperty("type", "dynamic/custom");
                    obj.addProperty("id", submitId);
                    if (stateId != null) {
                        var additions = obj.getAsJsonObject("additions");
                        if (additions == null) {
                            additions = new JsonObject();
                        }
                        additions.addProperty("stateId", stateId.toString());
                        obj.add("additions", additions);
                    }
                } else if (Objects.equals(typeString, "josiedialog_minimessage")
                        && obj.get("text") instanceof final JsonPrimitive textPrim
                        && textPrim.getAsString() instanceof final String mmString) {
                    return PlatformHolder.platform().minimessage(mmString);
                }
            }

            final JsonObject result = new JsonObject();
            for (final String key : obj.keySet()) {
                result.add(key, postProcess(obj.get(key), submitId, stateId));
            }
            return result;
        } else if (input instanceof final JsonArray array) {
            final JsonArray result = new JsonArray();
            for (final JsonElement item : array) {
                result.add(postProcess(item, submitId, stateId));
            }
            return result;
        } else {
            return input;
        }
    }
}
