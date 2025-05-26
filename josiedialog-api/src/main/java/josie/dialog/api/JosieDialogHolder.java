package josie.dialog.api;

import org.jspecify.annotations.Nullable;

public class JosieDialogHolder {
    @Nullable
    private static JosieDialog instance = null;

    public static void setInstance(final JosieDialog impl) {
        instance = impl;
    }

    @Nullable
    public static JosieDialog instance() {
        return instance;
    }
}
