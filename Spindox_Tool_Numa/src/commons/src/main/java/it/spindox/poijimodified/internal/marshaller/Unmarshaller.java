package commons.src.main.java.it.spindox.poijimodified.internal.marshaller;

import commons.src.main.java.it.spindox.poijimodified.exception.IllegalCastException;
import commons.src.main.java.it.spindox.poijimodified.exception.PoijiInstantiationException;
import commons.src.main.java.it.spindox.poijimodified.internal.PoiWorkbook;
import commons.src.main.java.it.spindox.poijimodified.internal.PoijiOptions;
import commons.src.main.java.it.spindox.poijimodified.internal.annotation.ExcelCell;
import commons.src.main.java.it.spindox.poijimodified.util.Casting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This is the main class that converts the excel sheet fromExcel Java object
 * Created by hakan on 16/01/2017.
 * https://github.com/ozlerhakan/poiji
 */
final class Unmarshaller implements Deserializer {

    private final PoiWorkbook poiWorkbook;
    private final DataFormatter df = new DataFormatter();

    Unmarshaller(final PoiWorkbook poiWorkbook) {
        this.poiWorkbook = poiWorkbook;
    }


    public <T> List<T> deserialize(Class<T> type, PoijiOptions options, int sheetPosition) {

        Sheet sheet = poiWorkbook.workbook().getSheetAt(sheetPosition);
        int skip = options.skip();
        int maxPhysicalNumberOfRows = sheet.getPhysicalNumberOfRows() + 1 - skip;
        List<T> list = new ArrayList<>(maxPhysicalNumberOfRows);

        for (Row currentRow : sheet) {

            if (skip(currentRow, skip))
                continue;

            if (maxPhysicalNumberOfRows > list.size()) {
                T t = deserialize0(currentRow, type);
                list.add(t);
            }
        }

        return list;
    }

    private <T> T deserialize0(Row currentRow, Class<T> type) {

        T instance;
        try {
            instance = type.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new PoijiInstantiationException("Cannot create a new instance of " + type.getName());
        }

        return setFieldValue(currentRow, type, instance);

    }

    private <T> T tailSetFieldValue(Row currentRow, Class<? super T> type, T instance) {
        for (Field field : type.getDeclaredFields()) {

            ExcelCell index = field.getAnnotation(ExcelCell.class);
            if (index != null) {
                Class<?> fieldType = field.getType();
                Cell cell = currentRow.getCell(index.value());
                Object o;

                if (!field.isAccessible())
                    field.setAccessible(true);

                if (cell != null) {
                    String value = df.formatCellValue(cell);
                    o = castValue(fieldType, value);
                } else {
                    o = castValue(fieldType, "");
                }
                try {
                    field.set(instance, o);
                } catch (IllegalAccessException e) {
                    throw new IllegalCastException("Unexpected cast type {" + o + "} of field" + field.getName());
                }
            }
        }
        return instance;
    }

    private <T> T setFieldValue(Row currentRow, Class<? super T> subclass, T instance) {
        return subclass == null
                ? instance
                : tailSetFieldValue(currentRow, subclass, setFieldValue(currentRow, subclass.getSuperclass(), instance));
    }

    private Object castValue(Class<?> fieldType, String value) {
        Object o;
        if (fieldType.getName().equals("int")) {
            o = Casting.integerValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("long")) {
            o = Casting.longValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("double")) {
            o = Casting.doubleValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("float")) {
            o = Casting.floatValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("boolean")) {
            o = Boolean.valueOf(value);

        } else if (fieldType.getName().equals("byte")) {
            o = Casting.byteValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("short")) {
            o = Casting.shortValue(Objects.equals(value, "") ? "0" : value);

        } else if (fieldType.getName().equals("char")) {
            value = Objects.equals(value, "") ? " " : value;
            o = value.charAt(0);

        } else
            o = value;
        return o;
    }

    private boolean skip(final Row currentRow, int skip) {
        return currentRow.getRowNum() + 1 <= skip;
    }
}
