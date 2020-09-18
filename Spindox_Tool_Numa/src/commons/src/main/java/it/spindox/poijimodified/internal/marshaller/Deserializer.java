package commons.src.main.java.it.spindox.poijimodified.internal.marshaller;

import commons.src.main.java.it.spindox.poijimodified.internal.PoiWorkbook;
import commons.src.main.java.it.spindox.poijimodified.internal.PoijiOptions;

import java.util.List;

/**
 * Created by hakan on 17/01/2017.
 */
public interface Deserializer {

    <T> List<T> deserialize(Class<T> type, PoijiOptions options,int sheetPosition);

    static Deserializer instance(PoiWorkbook workbook) {
        return new Unmarshaller(workbook);
    }
}
