package commons.src.main.java.it.spindox.vfexception;

/**
 * Created by Ashraf on 15/02/2017.
 */
public class VfException extends Exception {

	private static final long serialVersionUID = 8486622037622568007L;

	public VfException(String message) {
        super(message);
    }

    public VfException(String message, Throwable cause) {
        super(message, cause);
    }

    public VfException(Throwable cause) {
        super(cause);
    }

    public VfException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public VfException() {
    }
}
