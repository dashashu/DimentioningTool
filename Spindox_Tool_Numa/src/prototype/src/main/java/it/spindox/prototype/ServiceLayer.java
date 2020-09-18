package prototype.src.main.java.it.spindox.prototype;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import core.src.main.java.it.spindox.estimation.EstimationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.catalog.CatalogConstants;
import commons.src.main.java.it.spindox.model.catalog.CatalogFromExcel;
import commons.src.main.java.it.spindox.model.catalog.Compute;
import commons.src.main.java.it.spindox.model.catalog.Container;
import commons.src.main.java.it.spindox.model.catalog.Storage;
import commons.src.main.java.it.spindox.model.configurations.ClusterConfiguration;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.configurations.VBomRules;
import commons.src.main.java.it.spindox.model.placementAndEstimation.*;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import core.src.main.java.it.spindox.placement.PlacementManagement;
import commons.src.main.java.it.spindox.utils.RulesUtil;
import commons.src.main.java.it.spindox.vfexception.RulesValidationException;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.core.*;
import commons.src.main.java.it.spindox.vfexception.excelio.*;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * Created by fabrizio.sanfilippo on 27/03/2017.
 */
public class ServiceLayer {
    //private static final Logger logger = LogManager.getLogger(ServiceLayer.class);
    final static Logger logger = Logger.getLogger(ServiceLayer.class);
    Catalog catalog;
    InputConfiguration inputConfig;
    VBomRules vBomRules;
    Estimation estimation;

    List<Cluster> clusterList;
    List<Averages> averagesList;
    public static Map<Integer, String> yearsOrder = new HashMap<Integer, String>();

    public ServiceLayer( File file,File file2) throws VfException {
        this();

       
        fillInputConfigFile(file);
        fillRulesFile(file2);
    }

    public ServiceLayer() {
        clusterList = new ArrayList<>();
        catalog = new Catalog();
        inputConfig = new InputConfiguration();
        vBomRules = new VBomRules();
        averagesList = new ArrayList<>();
        
   	 
    }

    public void fillInputConfigFile(File file1) throws VfException {
    	
        try {
            Gson gson = new Gson();
            StringBuilder sb = new StringBuilder();
            System.out.println(Paths.get(file1.getAbsolutePath()));
            
            Files.lines(Paths.get(file1.getAbsolutePath())).forEach(s -> sb.append(s));

            inputConfig = gson.fromJson(sb.toString().replace("\\", "\\\\"), InputConfiguration.class);
            logger.info("Found:- " + inputConfig);

        } catch (FileNotFoundException e) {
            throw new VfException("Error: input config file not found at path: " + file1.getAbsolutePath());
        } catch (IOException e) {
            throw new VfException("Error:  input config file not found at path: " + file1.getAbsolutePath());
        }
    }

    public void fillRulesFile(File file) throws VfException {

        try {
            Gson gson = new Gson();
            StringBuilder sb = new StringBuilder();
            logger.info("PATH-----> "+file.getAbsolutePath());
            Files.lines(Paths.get(file.getAbsolutePath())).forEach(s -> sb.append(s));

            vBomRules = gson.fromJson(sb.toString().replace("\\", "\\\\"), VBomRules.class);
            logger.info("Found:- " + vBomRules);
            RulesUtil.validateRules(vBomRules, inputConfig.isVbomCustomer());

        } catch (FileNotFoundException e) {
            throw new VfException("Error: VBomRules file not found at path: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new VfException("Error: VBomRules file not found at path: " + file.getAbsolutePath());
        } catch (RulesValidationException e) {
            throw new VfException("Error: the validation of VBomRules file failed: " + e);
        } catch (JsonSyntaxException e) {
            throw new VfException("Error: the Json file is not properly formatted: " + e);
        }
    }

    public void fillClusterList() throws VfException {
    	ServiceLayer.yearsOrder = Util.getYearOrder(inputConfig);
    	
        if (inputConfig.isVbomCustomer()) {
            List<VBom> vBomCustomerList = new ArrayList<VBom>();
            vBomCustomerList = Util.readCustomerSheet(inputConfig);
            vBomCustomerList = RulesUtil.applyPreProcessingRules(vBomCustomerList, vBomRules.getPreProcessRules());

            clusterList.addAll(RulesUtil.dividePerCluster(vBomCustomerList, vBomRules, inputConfig, catalog));
        } else {
            List<String> sheetLabelList = Util.readSheetLabel(inputConfig.getvBomFilePath());

            for (String clusterLabel : sheetLabelList) {

                if ((!clusterLabel.equalsIgnoreCase(ConfigurationManagement.getVbomConfiguration().getString("foundationSheetLabel"))) ||
                        (clusterLabel.equalsIgnoreCase(ConfigurationManagement.getVbomConfiguration().getString("foundationSheetLabel"))
                                && inputConfig.isFoundationFlag()
                                && inputConfig.isManagementFlag()
                        )) {

                    List<ClusterConfiguration> tmpClusterConfigurationList = inputConfig.getClusterConfiguration().stream().filter(clusterConfiguration -> clusterConfiguration.getSheetLabel().equalsIgnoreCase(clusterLabel)).collect(Collectors.toList());
                    if (tmpClusterConfigurationList.size() == 1) {
                        Compute blade;

                        ClusterConfiguration clusterConfiguration = tmpClusterConfigurationList.get(0);
                        if (!clusterConfiguration.isHighPerformanceBladeFlag()) {
                            blade = catalog.getBlade();
                        } else {
                            blade = catalog.getBladeHighPerformance();
                        }
                        BladeFactory bladeFactory = new BladeFactory(blade, clusterConfiguration, inputConfig.getEsxiCores(), inputConfig.getTxrxCores());

                        clusterList.add(new Cluster(clusterLabel, Util.readSheet(inputConfig.getvBomFilePath(), clusterLabel, vBomRules.getPreProcessRules()), bladeFactory, inputConfig));
                    } else {
                        if (tmpClusterConfigurationList.size() == 0) {
                        	logger.error("Cluster are not configured in the input Json file");
                            throw new NoClusterConfigurationDefinedIntoInputConfigJsonException(clusterLabel);
                            
                        } else {
                        	logger.error("Multiple Clusters with the same name are configured in the input Json file");
                            throw new MultipleClusterConfigurationDefinedIntoInputConfigJsonException(clusterLabel);
                        }
                    }
                } else {
                    logger.info("Found a foundation sheet, but management is disabled");
                }
            }
        }
    }

    public void placeCluster(Cluster cluster) throws VfException {
        PlacementManagement placementManagement = new PlacementManagement(cluster);
        placementManagement.placeCluster();
    }

    public List<Cluster> placeAllCluster() throws VfException {
        if (inputConfig.isFoundationFlag() && inputConfig.isManagementFlag()) {
            if (clusterList.stream().filter(cluster -> cluster.isFoundation()).count() == 0) {
                throw new NoFoundationClusterDefinedException();
            }
        }

        Set<String> sites = new HashSet<String>();

        for (Cluster cluster : clusterList) {
            logger.info("***************************************************");
            logger.info("\tPLACEMENT " + cluster.getSheetLabel());
            logger.info("***************************************************");


            placeCluster(cluster);

            logger.info(cluster.getPlacementTable().printSummaryPlacement());

            sites.addAll(cluster.getPlacementTable().getSite());
        }

        Set<String> yearNames = Util.checkYearsConsistency(inputConfig.getvBomFilePath(), inputConfig.isVbomCustomer());
        for (String site : sites) {
            Averages average = new Averages(site, yearNames, clusterList);

            averagesList.add(average);
        }

        return clusterList;
    }


    public void logPlacementDetailedForCluster(Cluster cluster) {
        logger.info("***************************************************");
        logger.info("\tPLACEMENT " + cluster.getSheetLabel());
        logger.info("***************************************************");

        cluster.getPlacementTable().getPlacementResultList().forEach(placementResult -> {
            logger.info(placementResult.print());
        });

    }

    public void logPlacementDetailedForCluster() {
        logger.info("\n\n************************ PLACEMENT BLADE LOG ************************\n\n");
        for (Cluster cluster : clusterList) {
            logPlacementDetailedForCluster(cluster);
        }
    }


    public void estimateWithCluster() throws VfException {
        logger.info("\n\n************************ ESTIMATION ************************");

        EstimationManagement estimationManagement
                = new EstimationManagement(inputConfig.isFoundationFlag(),
                inputConfig.isManagementFlag(),
                inputConfig.getDefaultAvgReadWrite(),
                inputConfig.getDefaultAvgBlockSize(),
                inputConfig.getDefaultAvgIOPS(),
                inputConfig.getDefaultFoundationSpace(),
                catalog,
                inputConfig.getThreeparCharacterizationPath(),
                averagesList,
                clusterList);
        estimationManagement.estimate();

        estimation = estimationManagement.getEstimation();
        logger.info(estimation.print());
        logger.info("\n\n");
    }


    /**
     * Loads the catalog file but does not fill the catalog object [for that scope call pickFromCatalog]
     *
     * @throws UnexpectedSituationOccurredException
     * @throws InvalidInputFileException
     * @throws IllegalEmptyCellException
     * @throws IllegalCellFormatException
     */
    public void loadCatalog() throws UnexpectedSituationOccurredException, InvalidInputFileException, IllegalEmptyCellException, IllegalCellFormatException {
    	
         
        String catalogFilePath = inputConfig.getCatalogFilePath();

        try {
            Util.readCatalogSheet(catalogFilePath, CatalogConstants.COMPUTE);

        } catch (UnexpectedSituationOccurredException e) {
            throw new UnexpectedSituationOccurredException("Error during the Analysis of Catalog File - Sheet COMPUTE: " + e.getMessage(), e);
        } catch (InvalidInputFileException e) {
            throw new InvalidInputFileException("Error during the Analysis of Catalog File - Sheet COMPUTE: " + e.getMessage(), e);
        } catch (IllegalEmptyCellException e) {
            throw new IllegalEmptyCellException("Error during the Analysis of Catalog File - Sheet COMPUTE: " + e.getMessage(), e);
        } catch (IllegalCellFormatException e) {
            throw new IllegalCellFormatException("Error during the Analysis of Catalog File - Sheet COMPUTE: " + e.getMessage(), e);
        }
        try {
            Util.readCatalogSheet(catalogFilePath, CatalogConstants.CONTAINER);
        } catch (UnexpectedSituationOccurredException e) {
            throw new UnexpectedSituationOccurredException("Error during the Analysis of Catalog File - Sheet CONTAINER: " + e.getMessage(), e);
        } catch (InvalidInputFileException e) {
            throw new InvalidInputFileException("Error during the Analysis of Catalog File - Sheet CONTAINER: " + e.getMessage(), e);
        } catch (IllegalEmptyCellException e) {
            throw new IllegalEmptyCellException("Error during the Analysis of Catalog File - Sheet CONTAINER: " + e.getMessage(), e);
        } catch (IllegalCellFormatException e) {
            throw new IllegalCellFormatException("Error during the Analysis of Catalog File - Sheet CONTAINER: " + e.getMessage(), e);
        }

        try {
            Util.readCatalogSheet(catalogFilePath, CatalogConstants.STORAGE);
        } catch (UnexpectedSituationOccurredException e) {
            throw new UnexpectedSituationOccurredException("Error during the Analysis of Catalog File - Sheet STORAGE: " + e.getMessage(), e);
        } catch (InvalidInputFileException e) {
            throw new InvalidInputFileException("Error during the Analysis of Catalog File - Sheet STORAGE: " + e.getMessage(), e);
        } catch (IllegalEmptyCellException e) {
            throw new IllegalEmptyCellException("Error during the Analysis of Catalog File - Sheet STORAGE: " + e.getMessage(), e);
        } catch (IllegalCellFormatException e) {
            throw new IllegalCellFormatException("Error during the Analysis of Catalog File - Sheet STORAGE: " + e.getMessage(), e);
        }
        try {
            Util.readCatalogSheet(catalogFilePath, CatalogConstants.SWITCH);
        } catch (UnexpectedSituationOccurredException e) {
            throw new UnexpectedSituationOccurredException("Error during the Analysis of Catalog File - Sheet SWITCH: " + e.getMessage(), e);
        } catch (InvalidInputFileException e) {
            throw new InvalidInputFileException("Error during the Analysis of Catalog File - Sheet SWITCH: " + e.getMessage(), e);
        } catch (IllegalEmptyCellException e) {
            throw new IllegalEmptyCellException("Error during the Analysis of Catalog File - Sheet SWITCH: " + e.getMessage(), e);
        } catch (IllegalCellFormatException e) {
            throw new IllegalCellFormatException("Error during the Analysis of Catalog File - Sheet SWITCH: " + e.getMessage(), e);
        }

    }


    public void pickFromCatalog() throws VfException {
        catalog.setBlade((Compute) (CatalogFromExcel.getCatalogEntry(CatalogConstants.COMPUTE, inputConfig.getCatalogChoices().getBlade(), CatalogConstants.BLADE)));
        catalog.setBladeHighPerformance((Compute) (CatalogFromExcel.getCatalogEntry(CatalogConstants.COMPUTE, inputConfig.getCatalogChoices().getHighPerformanceBlade(), CatalogConstants.HIGH_PERFORMANCE_BLADE)));
        catalog.setEnclosure((Container) CatalogFromExcel.getCatalogEntry(CatalogConstants.CONTAINER, inputConfig.getCatalogChoices().getEnclosure(), CatalogConstants.ENCLOSURE));
        catalog.setEnclosureHighPerformance((Container) CatalogFromExcel.getCatalogEntry(CatalogConstants.CONTAINER, inputConfig.getCatalogChoices().getHighPerformanceEnclosure(), CatalogConstants.HIGH_PERFORMANCE_ENCLOSURE));
        catalog.setThreePar((Storage) CatalogFromExcel.getCatalogEntry(CatalogConstants.STORAGE, inputConfig.getCatalogChoices().getThreePar(), CatalogConstants.THREEPAR));
        catalog.setThreeParExpansion((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.THREEPAR_EXPANSION, inputConfig.getCatalogChoices().getThreePar(), CatalogConstants.THREEPAR_EXPANSION));
        catalog.setDisk((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.DISK, inputConfig.getCatalogChoices().getThreePar(), CatalogConstants.DISK));
        catalog.setDriveEnclosureDisk((Storage) CatalogFromExcel.getThreeParDependency(CatalogConstants.DRIVE_ENCLOSURE_DISK, inputConfig.getCatalogChoices().getThreePar(), CatalogConstants.DRIVE_ENCLOSURE_DISK));
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    public Estimation getEstimation() {
        return estimation;
    }

    public void setEstimation(Estimation estimation) {
        this.estimation = estimation;
    }

    public InputConfiguration getInputConfig() {
        return inputConfig;
    }

    public void setInputConfig(InputConfiguration inputConfig) {
        this.inputConfig = inputConfig;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    public void setClusterList(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }

    public List<Averages> getAveragesList() {
        return averagesList;
    }

    public void setAveragesList(List<Averages> averagesList) {
        this.averagesList = averagesList;
    }

}

