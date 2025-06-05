package josie.dialog.api;

import org.jspecify.annotations.Nullable;

public interface ParametizableDialog {
    @Nullable
    default Class<?> parametersType() {
        return null;
    }

    String formId();
}
