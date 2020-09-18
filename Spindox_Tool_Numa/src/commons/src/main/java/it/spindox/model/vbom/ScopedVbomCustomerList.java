package commons.src.main.java.it.spindox.model.vbom;

import java.util.ArrayList;
import java.util.List;

/**
 * An object containing a list of customer vBoms along with the sheet name (i.e. scope)
 * Created by Ashraf Uz Zaman on  September 2017.
 */
public class ScopedVbomCustomerList {
    private String sheetName;
    List<VBom> vBomArrayList;


    public ScopedVbomCustomerList() {
        sheetName = "";
        vBomArrayList = new ArrayList<>();
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<VBom> getvBomCustomerList() {
        return vBomArrayList;
    }
}

