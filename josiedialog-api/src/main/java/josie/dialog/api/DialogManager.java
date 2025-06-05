package josie.dialog.api;

import com.google.gson.JsonElement;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface DialogManager {
    DialogManager registerDialog(ParametizableDialog form);

    default void sendLifecycledForm(final UUID user, final String id) throws SendDialogException, RenderException {
        sendLifecycledForm(user, id, null, null);
    }

    default void sendLifecycledForm(final UUID user, final String id, final Object parameters)
            throws SendDialogException, RenderException {
        sendLifecycledForm(user, id, parameters, null);
    }

    void sendLifecycledForm(UUID user, String id, @Nullable Object parameters, @Nullable Object hiddenState)
            throws SendDialogException, RenderException;

    default void sendDialog(final UUID user, final String id) throws SendDialogException, RenderException {
        sendDialog(user, id, null);
    }

    void sendDialog(UUID user, String id, @Nullable Object parameters) throws SendDialogException, RenderException;

    JsonElement renderDialog(String id, @Nullable Object parameters, @Nullable UUID stateId) throws RenderException;

    default JsonElement renderDialog(final String id, @Nullable final Object parameters) throws RenderException {
        return renderDialog(id, parameters, null);
    }
}
