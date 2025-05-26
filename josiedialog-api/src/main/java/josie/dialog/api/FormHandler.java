package josie.dialog.api;

import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface FormHandler {
    @Nullable
    Class<?> parametersType();

    @Nullable
    Class<?> hiddenStateType();

    Class<?> submissionDataType();

    String formId();

    void handleSubmit(UUID playerUUID, Object submissionData, @Nullable Object hiddenState);
}
