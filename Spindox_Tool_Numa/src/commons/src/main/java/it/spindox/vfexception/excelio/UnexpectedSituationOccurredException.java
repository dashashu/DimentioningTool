package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

public class UnexpectedSituationOccurredException extends VfException {

	private static final long serialVersionUID = -9118151223697222187L;

	public UnexpectedSituationOccurredException(String message) {
        super(message);
    }

    public UnexpectedSituationOccurredException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedSituationOccurredException(Throwable cause) {
        super(cause);
    }

    public UnexpectedSituationOccurredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UnexpectedSituationOccurredException() {
    }

}
