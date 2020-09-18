package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by Ashraf on 15/02/2017.
 */
public class IllegalCellFormatException extends VfException {

	private static final long serialVersionUID = -2219236360060488472L;

	public IllegalCellFormatException(String message) {
		super(message);
	}

	public IllegalCellFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalCellFormatException(Throwable cause) {
		super(cause);
	}

	public IllegalCellFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public IllegalCellFormatException() {
	}
}
