package commons.src.main.java.it.spindox.vfexception.excelio;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */
public class InvalidVirtualMachineException extends VfException {

	private static final long serialVersionUID = -2603176221035578144L;

	public InvalidVirtualMachineException(String message) {
        super(message);
    }
}
