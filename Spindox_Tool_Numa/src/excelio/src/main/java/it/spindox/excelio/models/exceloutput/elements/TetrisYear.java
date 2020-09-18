package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.ArrayList;
import java.util.List;

public class TetrisYear {
    //Lista delle row di un tetris sheet
    private List<TetrisBlade> tetrisBladeList = new ArrayList<TetrisBlade>();

    public void addBlade(TetrisBlade tb) {
        tetrisBladeList.add(tb);
    }

    public int getMaxNumOfVms() { // max number of VMs in a blade, for all the blades of this year
        int max = -1;
        for (TetrisBlade tb : tetrisBladeList) {
            if (tb.getVmBladeList().size() > max) {
                max = tb.getVmBladeList().size();
            }
        }
        return max;
    }

    public List<TetrisBlade> getTetrisBladeList() {
        return tetrisBladeList;
    }

    public void setTetrisBladeList(List<TetrisBlade> tetrisBladeList) {
        this.tetrisBladeList = tetrisBladeList;
    }
}