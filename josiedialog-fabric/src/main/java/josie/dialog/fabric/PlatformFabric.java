package josie.dialog.fabric;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import josie.dialog.api.ClickActionHandler;
import josie.dialog.api.Platform;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dialog.Dialog;

public class PlatformFabric implements Platform {
    private static final List<PlatformFabric> instances = new ArrayList<>();
    private MinecraftServer server;
    private final List<ClickActionHandler> clickActionHandlers = new ArrayList<>();

    public PlatformFabric(final MinecraftServer server) {
        this.server = server;
        instances.add(this);
    }

    @Override
    public void sendDialog(final UUID playerUuid, final JsonElement jsonForm) {
        final var player = server.getPlayerList().getPlayer(playerUuid);
        if (player == null) return;

        final var dialog = Dialog.CODEC.parse(JsonOps.INSTANCE, jsonForm).getOrThrow();
        final var responsePacket = new ClientboundShowDialogPacket(dialog);

        final var connection = player.connection;
        connection.send(responsePacket);
    }

    @Override
    public void registerClickActionHandler(final ClickActionHandler handler) {
        clickActionHandlers.add(handler);
    }

    public static void receiveClickAction(final UUID uuid, final String id, final String rawForm) {
        for (final var instance : instances) {
            for (final var handler : instance.clickActionHandlers) {
                handler.apply(uuid, id, rawForm);
            }
        }
    }

    public boolean isServerThread() {
        return server.isSameThread();
    }
}
