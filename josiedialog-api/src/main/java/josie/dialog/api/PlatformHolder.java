package josie.dialog.api;

import org.jspecify.annotations.Nullable;

public class PlatformHolder {
    @Nullable
    private static Platform platform;

    public static void registerPlatform(final Platform platform) {
        PlatformHolder.platform = platform;
    }

    @Nullable
    public static Platform platform() {
        return platform;
    }
}
