package josie.dialog.api;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface DialogManager {
    void registerForm(FormHandler form);

    default void sendForm(final UUID user, final String id) {
        sendForm(user, id, null, null);
    }

    default void sendForm(final UUID user, final String id, final Object parameters) {
        sendForm(user, id, parameters, null);
    }

    void sendForm(UUID user, String id, @Nullable Object parameters, @Nullable Object hiddenState);
}
