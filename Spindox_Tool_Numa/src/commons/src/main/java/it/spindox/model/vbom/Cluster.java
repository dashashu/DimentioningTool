package commons.src.main.java.it.spindox.model.vbom;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import commons.src.main.java.it.spindox.model.configurations.ClusterConfiguration;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.BladeFactory;
import commons.src.main.java.it.spindox.model.placementAndEstimation.PlacementTable;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import java.util.List;

/**
 * Created by fabrizio.sanfilippo on 09/05/2017.
 */
public class Cluster {

	private String sheetLabel;
	private List<VBom> vbom;
	private BladeFactory bladeFactory;
	private ClusterConfiguration clusterConfiguration;
	private InputConfiguration inputConfiguration;
	private PlacementTable placementTable;



    public Cluster(String sheetLabel, List<VBom> vbom, BladeFactory bladeFactory, InputConfiguration inputConfiguration) throws UnexpectedSituationOccurredException {
        this.sheetLabel = sheetLabel;
        this.vbom = vbom;
        this.bladeFactory = bladeFactory;
        this.inputConfiguration = inputConfiguration;
        
        if (inputConfiguration.getClusterConfiguration().stream().filter(cc -> cc.getSheetLabel().equalsIgnoreCase(sheetLabel)).count() > 0) {
            this.clusterConfiguration = inputConfiguration.getClusterConfiguration().stream().filter(cc -> cc.getSheetLabel().equalsIgnoreCase(sheetLabel)).findFirst().get();
            
            if(clusterConfiguration.isNumaFlag()) {
            	bladeFactory.setEsxiCores(inputConfiguration.getEsxiCores());
            	bladeFactory.setTxrxCores(inputConfiguration.getTxrxCores());
            } else {
            	bladeFactory.setEsxiCores(0.0);
            	bladeFactory.setTxrxCores(0.0);
            }
        }else {
            throw new UnexpectedSituationOccurredException("Sheet Label: "  + sheetLabel +" not defined in input configuration. Please try to change configuration");
        }
    }

    public boolean isFoundation() {
        return sheetLabel.equalsIgnoreCase(ConfigurationManagement.getVbomConfiguration().getString("foundationSheetLabel"));
    }

    public Cluster() {
    }

    public PlacementTable getPlacementTable() {
        return placementTable;
    }

    public void setPlacementTable(PlacementTable placementTable) {
        this.placementTable = placementTable;
    }

    public String getSheetLabel() {
        return sheetLabel;
    }

    public void setSheetLabel(String sheetLabel) {
        this.sheetLabel = sheetLabel;
    }

    public List<VBom> getVbom() {
        return vbom;
    }

    public void setVbom(List<VBom> vbom) {
        this.vbom = vbom;
    }

    public BladeFactory getBladeFactory() {
        return bladeFactory;
    }

    public void setBladeFactory(BladeFactory bladeFactory) {
        this.bladeFactory = bladeFactory;
    }

    public ClusterConfiguration getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    public InputConfiguration getInputConfiguration() {
        return inputConfiguration;
    }

    public void setInputConfiguration(InputConfiguration inputConfiguration) {
        this.inputConfiguration = inputConfiguration;
    }


}
