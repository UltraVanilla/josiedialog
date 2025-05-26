package josie.dialog.api;

import java.util.UUID;

@FunctionalInterface
public interface ClickActionHandler {
    void apply(UUID playerUUID, String id, String payload);
}
