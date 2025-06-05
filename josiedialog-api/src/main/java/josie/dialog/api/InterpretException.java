package josie.dialog.api;

public class InterpretException extends Exception {
    public InterpretException(final Throwable err) {
        super("Error interpreting form submission", err);
    }
}
