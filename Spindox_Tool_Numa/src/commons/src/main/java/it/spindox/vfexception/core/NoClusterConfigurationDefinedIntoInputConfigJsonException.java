package commons.src.main.java.it.spindox.vfexception.core;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by fabrizio.sanfilippo on 11/05/2017.
 */
public class NoClusterConfigurationDefinedIntoInputConfigJsonException extends VfException {
    public NoClusterConfigurationDefinedIntoInputConfigJsonException(String message) {
        super(message);
    }

    public NoClusterConfigurationDefinedIntoInputConfigJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoClusterConfigurationDefinedIntoInputConfigJsonException(Throwable cause) {
        super(cause);
    }

    public NoClusterConfigurationDefinedIntoInputConfigJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public NoClusterConfigurationDefinedIntoInputConfigJsonException() {
    }
}
