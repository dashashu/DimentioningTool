package core.src.main.java.it.spindox.estimation;

import java.util.List;

import org.apache.log4j.Logger;

import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Averages;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;



public class EstimationManagement {


    private Estimation estimation;

    //private static final Logger logger = (Logger) LogManager.getLogger(EstimationManagement.class);
    final static Logger logger = Logger.getLogger(EstimationManagement.class);

//
//    @Deprecated
//    public EstimationManagement(PlacementTable serviceCluster, PlacementTable managementCluster, PlacementTable infrastructureManagement, boolean foundation, boolean management, Catalog catalog, String threeparCharacterizationPath, List<Averages> averagesList) {
//        this.estimation = new Estimation(serviceCluster, managementCluster, infrastructureManagement, foundation, management, catalog, threeparCharacterizationPath, averagesList);
//    }


    public EstimationManagement(boolean foundation, boolean management, Double defaultAvgReadWrite, Double defaultAvgBlockSize, Double defaultAvgIops, Double defaultFoundationSpace, Catalog catalog, String threeparCharacterizationPath, List<Averages> averagesList, List<Cluster> clusterList) {
        this.estimation = new Estimation(management, foundation, defaultAvgReadWrite, defaultAvgBlockSize, defaultAvgIops, defaultFoundationSpace, catalog, threeparCharacterizationPath, clusterList, averagesList);
    }


    public ComponentEstimation getCorrectInstance(String vendor, String className) throws UnexpectedSituationOccurredException {
        String estimationClass = "core.src.main.java.it.spindox.estimation.";
        if (!vendor.isEmpty()) {
            estimationClass = estimationClass + vendor + ".";
        }
        try {
            return (ComponentEstimation) Class.forName(estimationClass + className).newInstance();
        } catch (InstantiationException e) {
            throw new UnexpectedSituationOccurredException(e);
        } catch (IllegalAccessException e) {
            throw new UnexpectedSituationOccurredException(e);
        } catch (ClassNotFoundException e) {
            if (!vendor.isEmpty())
                return getCorrectInstance("", className);
            else
                throw new UnexpectedSituationOccurredException(e);
        }
    }

    public void estimate() throws VfException {


        logger.debug("Blade Estimation");
        ComponentEstimation bladeEstimation = getCorrectInstance("", "BladeEstimation");
        bladeEstimation.setEstimation(estimation);
        bladeEstimation.estimate();

        logger.debug("Enclosure Estimation");
        ComponentEstimation enclosureEstimation = getCorrectInstance("", "EnclosureEstimation");
        enclosureEstimation.setEstimation(bladeEstimation.getEstimation());
        enclosureEstimation.estimate();

        logger.debug("San Area Estimation");
        ComponentEstimation sanAreaEstimation = getCorrectInstance("hp", "SanAreaEstimation");
        sanAreaEstimation.setEstimation(enclosureEstimation.getEstimation());
        sanAreaEstimation.estimate();
    }

    public Estimation getEstimation() {
        return estimation;
    }

    public void setEstimation(Estimation estimation) {
        this.estimation = estimation;
    }

    public static Logger getLogger() {
        return logger;
    }
}
