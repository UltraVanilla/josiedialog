package josie.dialog.fabric;

import josie.dialog.DialogTemplateManager;
import josie.dialog.JosieDialogImpl;
import josie.dialog.api.JosieDialogHolder;
import josie.dialog.api.PlatformHolder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JosieDialogMod implements ModInitializer {
    public static final String MOD_ID = "josiedialog";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing JosieDialog");

        JosieDialogHolder.setInstance(new JosieDialogImpl());

        ServerLifecycleEvents.SERVER_STARTING.register((final MinecraftServer server) -> {
            PlatformHolder.registerPlatform(new PlatformFabric(server));
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("reloaddialogs")
                    .requires(source -> source.hasPermission(3))
                    .executes(context -> {
                        try {
                            DialogTemplateManager.reloadAll();
                        } catch (final Exception err) {
                            err.printStackTrace();
                            throw err;
                        }
                        return 1;
                    }));
        });
    }
}
