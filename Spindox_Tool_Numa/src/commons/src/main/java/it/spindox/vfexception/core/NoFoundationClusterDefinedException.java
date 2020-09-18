package commons.src.main.java.it.spindox.vfexception.core;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by fabrizio.sanfilippo on 12/05/2017.
 */
public class NoFoundationClusterDefinedException extends VfException {
    public NoFoundationClusterDefinedException(String message) {
        super(message);
    }

    public NoFoundationClusterDefinedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoFoundationClusterDefinedException(Throwable cause) {
        super(cause);
    }

    public NoFoundationClusterDefinedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NoFoundationClusterDefinedException() {
    }
}
