package josie.dialog.api;

import com.google.gson.JsonElement;
import java.util.UUID;

public interface Platform {
    void sendDialog(UUID player, JsonElement jsonForm);

    void registerClickActionHandler(ClickActionHandler handler);
}
