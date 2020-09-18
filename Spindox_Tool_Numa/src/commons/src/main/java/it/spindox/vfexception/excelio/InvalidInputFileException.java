package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by Ashraf on 08/03/2017.
 */
public class InvalidInputFileException extends VfException {

	private static final long serialVersionUID = 7238408710252195214L;

	public InvalidInputFileException(String message) {
        super(message);
    }

    public InvalidInputFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidInputFileException(Throwable cause) {
        super(cause);
    }

    public InvalidInputFileException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidInputFileException() {
    }

}
