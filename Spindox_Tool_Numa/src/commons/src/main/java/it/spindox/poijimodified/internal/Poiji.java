package commons.src.main.java.it.spindox.poijimodified.internal;

import commons.src.main.java.it.spindox.poijimodified.internal.PoijiOptions.PoijiOptionsBuilder;
import commons.src.main.java.it.spindox.poijimodified.internal.marshaller.Deserializer;
import commons.src.main.java.it.spindox.poijimodified.util.Files;


import java.io.*;
import java.util.List;

/**
 * The main entry point of mapping excel data to Java classes
 * Created by hakan on 16/01/2017.
 * https://github.com/ozlerhakan/poiji
 */
public final class Poiji {

    protected Poiji() {
    }

    public static <T> List<T> readExcelSheet(File file, Class<T> clazz, int sheetPosition) throws FileNotFoundException {
        final Deserializer unmarshaller = deserializer(file);
        return deserialize(clazz, unmarshaller, sheetPosition);
    }

    public static <T> List<T> readExcelSheet(File file, Class<T> clazz, PoijiOptions options, int sheetPosition) throws FileNotFoundException {
        final Deserializer unmarshaller = deserializer(file);
        return deserialize(clazz, unmarshaller, options, sheetPosition);
    }

    @SuppressWarnings("unchecked")
    private static Deserializer deserializer(File file) throws FileNotFoundException {
        final PoijiStream poiParser = new PoijiStream(fileInputStream(file));
        final PoiWorkbook workbook = PoiWorkbook.workbook(Files.getExtension(file.getName()), poiParser);
        return Deserializer.instance(workbook);
    }

    private static <T> List<T> deserialize(final Class<T> type, final Deserializer unmarshaller, int sheetPosition) {
        return unmarshaller.deserialize(type, PoijiOptionsBuilder.settings().build(),sheetPosition);
    }

    private static <T> List<T> deserialize(final Class<T> type, final Deserializer unmarshaller, PoijiOptions options, int sheetPosition) {
        return unmarshaller.deserialize(type, options,sheetPosition);
    }

    private static FileInputStream fileInputStream(File file) throws FileNotFoundException {
        return new FileInputStream(file);
    }

}
