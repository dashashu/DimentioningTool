package excelio.src.main.java.it.spindox.excelio.models.exceloutput;

import java.util.*;

import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TetrisYear;



public class Tetris {
    //<Nome sheet tetris, contenuto singolo sheet>
    Map<String, excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TetrisYear> tetrisYearList = new HashMap<String, TetrisYear>();
    protected List<String> sortedTetrisYearNames;  // contains the "keys" of Map, in sorted manner. sorting is done in Getter

    // Ashraf
    public List<String> getSortedYearNames() {
        sortedTetrisYearNames = new ArrayList<>(tetrisYearList.keySet());
        Collections.sort(sortedTetrisYearNames);

        // ensuring "Foundation" is always at the top of the sorted arrayList (if exists)
        if (sortedTetrisYearNames.contains("Foundation")) {
            sortedTetrisYearNames.remove("Foundation");
            sortedTetrisYearNames.add(0, "Foundation");
        }
        return sortedTetrisYearNames;
    }

    public Map<String, TetrisYear> getTetrisYearList() {
        return tetrisYearList;
    }

    public void setTetrisYearList(Map<String, TetrisYear> tetrisYearList) {
        this.tetrisYearList = tetrisYearList;
    }
}
