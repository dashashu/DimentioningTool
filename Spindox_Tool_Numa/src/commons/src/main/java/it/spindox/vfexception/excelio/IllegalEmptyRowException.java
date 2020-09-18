package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by Ashraf on 15/02/2017.
 */
public class IllegalEmptyRowException extends VfException {

	private static final long serialVersionUID = -3036401708592334014L;

	public IllegalEmptyRowException(String message) {
        super(message);
    }
}
