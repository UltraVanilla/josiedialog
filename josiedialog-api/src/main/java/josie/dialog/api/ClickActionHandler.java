package josie.dialog.api;

import com.google.gson.JsonObject;
import java.util.UUID;

@FunctionalInterface
public interface ClickActionHandler {
    void apply(UUID playerUUID, String id, JsonObject payload);
}
