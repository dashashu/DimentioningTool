package excelio.src.main.java.it.spindox.excelio;

import org.apache.commons.configuration2.Configuration;
import org.apache.poi.ss.usermodel.Sheet;

public abstract class WorksheetManagement {
    protected Sheet sheet;
    protected String sheetName;
    protected Configuration config;

    public WorksheetManagement(Sheet sheet, String sheetName) {
        this.sheet = sheet;
        this.sheetName = sheetName;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }
}
