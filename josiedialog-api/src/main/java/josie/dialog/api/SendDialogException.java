package josie.dialog.api;

public class SendDialogException extends Exception {
    public SendDialogException(final Throwable err) {
        super("Error sending dialog", err);
    }
}
