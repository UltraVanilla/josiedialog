package josie.dialog.api;

public class RenderException extends Exception {
    public RenderException(final Throwable err) {
        super("Error rendering dialog", err);
    }
}
