package commons.src.main.java.it.spindox.poijimodified.internal;

import java.io.InputStream;

class PoijiStream<T extends InputStream> {

    private final T t;

    PoijiStream(T t) {
        this.t = t;
    }

    /**
     * the T derived from {@link InputStream}
     * @return T
     */
    public T get() {
        return t;
    }
}
