package core.src.main.java.it.spindox.estimation;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

/**
 * Created by fabrizio.sanfilippo on 13/04/2017.
 */
public class SanAreaEstimation extends ComponentEstimation {

    public SanAreaEstimation() {
    }

    public SanAreaEstimation(Estimation estimation) {
        super(estimation);
    }

    @Override
    protected void estimateFoundation(String site) throws UnexpectedSituationOccurredException {
        System.out.println("LE BANANE SONO BUONE in GENERALE");
    }

    @Override
    protected void estimateInitiative(String site) throws UnexpectedSituationOccurredException {
        System.out.println("LE BANANE SONO BUONE in GENERALE");        
    }

	@Override
	public void executeBeforeEstimate() throws VfException {

	}
}
