package commons.src.main.java.it.spindox.vfexception.core;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by fabrizio.sanfilippo on 11/05/2017.
 */
public class MultipleClusterConfigurationDefinedIntoInputConfigJsonException extends VfException {
    public MultipleClusterConfigurationDefinedIntoInputConfigJsonException(String message) {
        super(message);
    }

    public MultipleClusterConfigurationDefinedIntoInputConfigJsonException(String message, Throwable cause) {
        super(message, cause);
    }

    public MultipleClusterConfigurationDefinedIntoInputConfigJsonException(Throwable cause) {
        super(cause);
    }

    public MultipleClusterConfigurationDefinedIntoInputConfigJsonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MultipleClusterConfigurationDefinedIntoInputConfigJsonException() {
    }
}
