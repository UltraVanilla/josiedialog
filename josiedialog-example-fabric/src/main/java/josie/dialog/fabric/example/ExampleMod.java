package josie.dialog.fabric.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.UUID;
import josie.dialog.api.DialogManager;
import josie.dialog.api.JosieDialogHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "josiedialogexample";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Nullable
    private DialogManager dialogManager;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing JosieDialogExample");

        final var configDir = createConfigs();

        ServerLifecycleEvents.SERVER_STARTING.register((final MinecraftServer server) -> {
            final var api = JosieDialogHolder.instance();

            dialogManager = api.createDialogManager(configDir.resolve("templates.js"));

            dialogManager.registerForm(new ReportForm());
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("report").executes(context -> {
                final var player = context.getSource().getPlayer();
                if (player == null) return 0;

                final var rotationVector = player.getRotationVector();
                final var position = player.getEyePosition();

                dialogManager.sendForm(
                        player.getUUID(),
                        "report",
                        new ReportForm.Parameters(
                                Map.of(UUID.fromString("61699b2e-d327-4a01-9f1e-0ea8c3f06bc6"), "Dinnerbone"),
                                player.getBlockX(),
                                player.getBlockZ()),
                        new ReportForm.HiddenState(position, rotationVector));
                return 1;
            }));
        });
    }

    public Path createConfigs() {
        final var configDir = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);
        final var targetFile = configDir.resolve("templates.js");

        if (!Files.exists(targetFile)) {
            try (final var in =
                    ExampleMod.class.getClassLoader().getResourceAsStream("assets/%s/templates.js".formatted(MOD_ID))) {
                if (in == null) {
                    throw new IllegalStateException("templates.js does not exist in mod resources");
                }
                Files.createDirectories(configDir);
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }

        return configDir;
    }
}
