package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by Ashraf on 15/02/2017.
 */
public class IllegalEmptyCellException extends VfException {

	private static final long serialVersionUID = -2852025996013545969L;

    public IllegalEmptyCellException(String message) {
        super(message);
    }

    public IllegalEmptyCellException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalEmptyCellException(Throwable cause) {
        super(cause);
    }

    public IllegalEmptyCellException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public IllegalEmptyCellException() {
    }
}
