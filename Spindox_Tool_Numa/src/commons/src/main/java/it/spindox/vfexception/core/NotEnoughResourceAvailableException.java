package commons.src.main.java.it.spindox.vfexception.core;

import commons.src.main.java.it.spindox.vfexception.VfException;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */
public class NotEnoughResourceAvailableException extends VfException {
    public NotEnoughResourceAvailableException(String message) {
        super(message);
    }
}
