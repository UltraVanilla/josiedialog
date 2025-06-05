package josie.dialog.api;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface ParametizableFormLifecycleHandler extends ParametizableDialog {
    @Nullable
    Class<?> hiddenStateType();

    Class<?> submissionDataType();

    void handleSubmit(UUID playerUUID, Object submissionData, @Nullable Object hiddenState);
}
