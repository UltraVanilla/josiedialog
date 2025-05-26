package josie.dialog;

import java.nio.file.Path;
import josie.dialog.api.DialogManager;
import josie.dialog.api.JosieDialog;

public class JosieDialogImpl implements JosieDialog {
    @Override
    public DialogManager createDialogManager(final Path path) {
        return new DialogManagerImpl(path);
    }
}
