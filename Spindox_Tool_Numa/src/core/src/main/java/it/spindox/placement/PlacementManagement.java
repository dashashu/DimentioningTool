package core.src.main.java.it.spindox.placement;

import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.*;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import core.src.main.java.it.spindox.utils.VbomManagement;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.core.InconsistentConstraintsException;
import commons.src.main.java.it.spindox.vfexception.core.NotEnoughResourceAvailableException;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabrizio.sanfilippo on 20/04/2017.
 */
public class PlacementManagement {


    //private static final Logger logger = LogManager.getLogger(PlacementManagement.class);
	final static Logger logger = Logger.getLogger(PlacementManagement.class);

    @Deprecated
    private BladeFactory bladeFactory;
    private InputConfiguration inputConfig;
    private Cluster cluster;

    @Deprecated
    public PlacementManagement(InputConfiguration inputConfig, BladeFactory bladeFactory) {
        this.inputConfig = inputConfig;
        this.bladeFactory = bladeFactory;
    }


//    public PlacementManagement(InputConfiguration inputConfig, Cluster cluster) {
//        this.inputConfig = inputConfig;
//        this.cluster = cluster;
//    }

    public PlacementManagement(Cluster cluster) {
        this.inputConfig = cluster.getInputConfiguration();
        this.cluster = cluster;
    }

    public void placeCluster() throws VfException {
        cluster.setPlacementTable(placeVbom(cluster));
    }
    
    public void placeClusterForTest(Site site, Year year) throws VfException {
        cluster.setPlacementTable(placeVbomForTest(cluster, site, year));
    }

    public PlacementTable placeVbom(Cluster cluster) throws VfException {
        VbomManagement vbomManagement = new VbomManagement(cluster);
        PlacementTable placementTable = new PlacementTable();

        vbomManagement.extractDataFromVbom(); //TODO foundation site does not fill up

        vbomManagement.applyConstraints(inputConfig.isAffinityOnlyPerTypePerInstance());
        //NUMA here all the aff/aaf/ams value must get set: debug it for value
        try {
            List<String> sites = new ArrayList<String>();
          //check whats hapening here
            vbomManagement.getYearList().forEach(year -> {
                year.getSiteList().forEach(site -> {
                    try {
                        sites.add(site.getSiteName());
                        placementTable.getPlacementResultList().add(placement(site, year, cluster)); //year site and blade-sockets details 
                    } catch (InconsistentConstraintsException | NotEnoughResourceAvailableException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (RuntimeException e) {
        	logger.error("Error during placement for cluster "+cluster.getSheetLabel());
            if (e.getCause() != null && e.getCause() instanceof InconsistentConstraintsException) {
                throw (InconsistentConstraintsException) e.getCause();
            }

            if (e.getCause() != null && e.getCause() instanceof NotEnoughResourceAvailableException) {
                throw (NotEnoughResourceAvailableException) e.getCause();
            }else {
                throw new VfException(e);
            }
        }
        return placementTable;
    }
    
    public PlacementTable placeVbomForTest(Cluster cluster, Site site, Year year) throws VfException {
        VbomManagement vbomManagement = new VbomManagement(cluster);
        PlacementTable placementTable = new PlacementTable();

        vbomManagement.extractDataFromVbom();

        vbomManagement.applyConstraints(inputConfig.isAffinityOnlyPerTypePerInstance());

        try {
        	placementTable.getPlacementResultList().add(placement(site, year, cluster));
        } catch (RuntimeException e) {
        	logger.error("Error during placement for cluster "+cluster.getSheetLabel());
            if (e.getCause() != null && e.getCause() instanceof InconsistentConstraintsException) {
                throw (InconsistentConstraintsException) e.getCause();
            }

            if (e.getCause() != null && e.getCause() instanceof NotEnoughResourceAvailableException) {
                throw (NotEnoughResourceAvailableException) e.getCause();
            }else {
                throw new VfException(e);
            }
        }
        return placementTable;
    }


    public PlacementResult placement(Site site, Year year, Cluster cluster) throws InconsistentConstraintsException, NotEnoughResourceAvailableException {

        Placement placement;
        PlacementResult placementResult = new PlacementResult(year.getYearName(), site.getSiteName(), new ArrayList<Blade>());

        if (inputConfig.isExtrasocketEnabled() && !cluster.getClusterConfiguration().isNumaFlag()) {
            placement = new PlacementExtraSocket(site, cluster);
        } else {
            placement = new PlacementSingleSocket(site, cluster);
        }

        logger.debug("\n\nDefined " + placement.getGroupList().size() + " groups for: " + site.getSiteName() + "/" + year.getYearName());

        logger.debug("Placement on " + site.getSiteName() + "/" + year.getYearName());
        placement.place();
        logger.debug("Blade Compression on " + site.getSiteName() + "/" + year.getYearName());
        placement.bladeCompression();
    	//Ashutosh Flag bladeBufferPercentage
        List<Blade> newActiveBlades = new ArrayList<Blade>();
        int newBladeNeedToAdd = ((int)Math.ceil(placement.bladeList.size()*(inputConfig.getBladeBufferPercentage()/100.0f)));
        for (int i = 0; i < newBladeNeedToAdd; i++)
        	newActiveBlades.add(cluster.getBladeFactory().createActiveBlade());
        placement.bladeList.addAll(newActiveBlades);
        if (inputConfig.getSpareNumber() != 0) {
            logger.debug("Adding Spare Blade on " + site.getSiteName() + "/" + year.getYearName());
            placement.addSpareBlades(inputConfig.getSpareNumber());
        }

        logger.debug("Setting Placement Result on " + site.getSiteName() + "/" + year.getYearName());
        placementResult.setPlacement(placement.getBladeList());
        return placementResult;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }


}
