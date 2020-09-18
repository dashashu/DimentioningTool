package core.src.main.java.it.spindox.estimation;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalCellFormatException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalEmptyCellException;
import commons.src.main.java.it.spindox.vfexception.excelio.InvalidInputFileException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import java.util.Set;

//import it.spindox.model.placementAndEstimation.ComponentCooq;

public abstract class ComponentEstimation {


    protected Estimation estimation;
    protected Set<String> siteAvailable;

    public ComponentEstimation() {
    }

    public ComponentEstimation(Estimation estimation) {
        this.estimation = estimation;
        siteAvailable = estimation.getEstimationTable().keySet();
    }

    public Estimation getEstimation() {
        return estimation;
    }

    public void setEstimation(Estimation estimation) {
        this.estimation = estimation;
        siteAvailable = estimation.getEstimationTable().keySet();
    }
    
    public abstract void executeBeforeEstimate() throws VfException;

    public void estimate() throws VfException {
    	executeBeforeEstimate();
    	
        if (siteAvailable != null)
            for (String s : siteAvailable) {
                if (estimation.isFoundation())
                    estimateFoundation(s);
                estimateInitiative(s);
            }
    }

    protected abstract void estimateFoundation(String site) throws UnexpectedSituationOccurredException;

    protected abstract void estimateInitiative(String site) throws UnexpectedSituationOccurredException, IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException;

}
