package commons.src.main.java.it.spindox.vfexception;

/**
 * Created by fabrizio.sanfilippo on 09/05/2017.
 */
public class NoValidPathException extends VfException {
    public NoValidPathException(String message) {
        super(message);
    }

    public NoValidPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoValidPathException(Throwable cause) {
        super(cause);
    }

    public NoValidPathException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NoValidPathException() {
    }
}
