package josie.dialog.fabric;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import josie.dialog.api.ClickActionHandler;
import josie.dialog.api.Platform;
import josie.dialog.api.SendDialogException;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.common.ClientboundShowDialogPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dialog.Dialog;
import org.jspecify.annotations.Nullable;

public class PlatformFabric implements Platform {
    private static final List<PlatformFabric> instances = new ArrayList<>();
    private MinecraftServer server;
    private final List<ClickActionHandler> clickActionHandlers = new ArrayList<>();

    private final MiniMessage mm = MiniMessage.miniMessage();

    public PlatformFabric(final MinecraftServer server) {
        this.server = server;
        instances.add(this);
    }

    @Override
    public void sendDialog(final UUID playerUuid, final JsonElement jsonForm) throws SendDialogException {
        try {
            final var player = server.getPlayerList().getPlayer(playerUuid);
            if (player == null) return;

            final var dialog = Dialog.CODEC.parse(JsonOps.INSTANCE, jsonForm).getOrThrow();
            final var responsePacket = new ClientboundShowDialogPacket(dialog);

            final var connection = player.connection;
            connection.send(responsePacket);
        } catch (final Exception err) {
            throw new SendDialogException(err);
        }
    }

    @Override
    public void registerClickActionHandler(final ClickActionHandler handler) {
        clickActionHandlers.add(handler);
    }

    public static void receiveClickAction(final UUID uuid, final String id, @Nullable final Tag rawForm) {
        final JsonObject parsedForm;
        if (rawForm == null) {
            parsedForm = new JsonObject();
        } else if (rawForm instanceof final CompoundTag compound) {
            final var booleanKeys = new ArrayList<String>();

            for (final var entry : compound.entrySet()) {
                final var type = entry.getValue().getId();
                // disallow garbage ENTIRELY. No garbage allowed!
                if (type != Tag.TAG_STRING && type != Tag.TAG_BYTE && type != Tag.TAG_FLOAT) {
                    return;
                } else if (type == Tag.TAG_BYTE) {
                    booleanKeys.add(entry.getKey());
                }
            }
            final Dynamic<Tag> dynamic = new Dynamic<>(NbtOps.INSTANCE, rawForm);
            parsedForm = dynamic.convert(JsonOps.INSTANCE).getValue().getAsJsonObject();

            // replace bytes with booleans
            for (final var booleanKey : booleanKeys) {
                parsedForm.addProperty(booleanKey, parsedForm.get(booleanKey).getAsByte() == 1);
            }
        } else {
            return;
        }

        for (final var instance : instances) {
            for (final var handler : instance.clickActionHandlers) {
                handler.apply(uuid, id, parsedForm);
            }
        }
    }

    @Override
    public boolean isServerThread() {
        return server.isSameThread();
    }

    @Override
    public JsonElement minimessage(final String input) {
        return GsonComponentSerializer.gson().serializeToTree(mm.deserialize(input));
    }
}
