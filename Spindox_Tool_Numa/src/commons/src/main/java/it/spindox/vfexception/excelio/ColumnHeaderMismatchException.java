package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by Ashraf on 15/02/2017.
 */
public class ColumnHeaderMismatchException extends VfException {

	private static final long serialVersionUID = -1547231179911991149L;

	public ColumnHeaderMismatchException(String message) {
        super(message);
    }
}
