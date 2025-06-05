package josie.dialog.api;

import java.nio.file.Path;

public interface JosieDialog {
    DialogManager createDialogManager(String namespace, Path path);
}
