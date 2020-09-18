package commons.src.main.java.it.spindox.model.vbom;

import java.util.ArrayList;
import java.util.List;

/**
 * An object containing a list of VBoms along with the sheet name (i.e. scope)
 * Created by Ashraf Uz Zaman on 03/03/2017.
 */
public class ScopedVbomList {
    private String sheetName; // Service Cluster - Management Cluster - Infrastructure Management
    List<VBom> vBomArrayList;


    public ScopedVbomList() {
        sheetName = "";
        vBomArrayList = new ArrayList<>();
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<VBom> getvBomList() {
        return vBomArrayList;
    }
}

