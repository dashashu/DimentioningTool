package commons.src.main.java.it.spindox.vfexception;

public class RulesValidationException extends VfException {
    public RulesValidationException(String message) {
        super(message);
    }

    public RulesValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RulesValidationException(Throwable cause) {
        super(cause);
    }

    public RulesValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RulesValidationException() {
    }
}
