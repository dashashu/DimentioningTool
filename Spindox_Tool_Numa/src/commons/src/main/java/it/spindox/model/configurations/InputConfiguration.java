package commons.src.main.java.it.spindox.model.configurations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashraf Uz Zaman on 15/03/2017.
 */
public class InputConfiguration {

    private int spareNumber;
    private boolean vBomCustomer;
    private boolean extrasocketEnabled;
    private String vBomFilePath;
    
    private String catalogFilePath;
    private String threeparCharacterizationPath;
    private boolean foundationFlag;
    private boolean managementFlag;
    private boolean affinityOnlyPerTypePerInstance;
    private double esxiCores = 0.0;
    private double txrxCores = 0.0;
    private int defaultHighThroughput;
    private int defaultBlockSize;

    private double defaultFoundationSpace = 0.0;
    private double defaultAvgBlockSize = 0.0;
    private double defaultAvgReadWrite = 0.0;
    private double defaultAvgIOPS = 0.0;
    
    //Ashutosh:
    private int bladeBufferPercentage;
    private int storageBufferPercentage;
    private int IOPSBufferPercentage;
    private int NSBWBufferPercentage;		


    private CatalogChoices catalogChoices;
    private List<ClusterConfiguration> clusterConfiguration;

    public InputConfiguration() {
        clusterConfiguration = new ArrayList<>();
    }

    public InputConfiguration(int spareNumber, boolean extrasocketEnabled, boolean vBomCustomer, String vBomFilePath, String catalogFilePath, String threeparCharacterizationPath, boolean foundationFlag, boolean managementFlag, boolean affinityOnlyPerTypePerInstance, CatalogChoices catalogChoices, List<ClusterConfiguration> clusterConfiguration,int bladeBufferPercentage, int storageBufferPercentage, int IOPSBufferPercentage , int NSBWBufferPercentage) {

        this.spareNumber = spareNumber;
        this.extrasocketEnabled = extrasocketEnabled;
        this.vBomCustomer = vBomCustomer;
        this.vBomFilePath = vBomFilePath;
        this.catalogFilePath = catalogFilePath;
        this.threeparCharacterizationPath = threeparCharacterizationPath;
        this.foundationFlag = foundationFlag;
        this.managementFlag = managementFlag;

        this.affinityOnlyPerTypePerInstance = affinityOnlyPerTypePerInstance;
        this.catalogChoices = catalogChoices;
        this.clusterConfiguration = clusterConfiguration;
        this.storageBufferPercentage = storageBufferPercentage;
        this.IOPSBufferPercentage = IOPSBufferPercentage;
        this.NSBWBufferPercentage = NSBWBufferPercentage;
    }


  


    public CatalogChoices getCatalogChoices() {
        return catalogChoices;
    }

    public void setCatalogChoices(CatalogChoices catalogChoices) {
        this.catalogChoices = catalogChoices;
    }

    public boolean isVbomCustomer() {
        return vBomCustomer;
    }

    public void setVbomCustomer(boolean vBomCustomer) {
        this.vBomCustomer = vBomCustomer;
    }

    public int getSpareNumber() {
        return spareNumber;
    }

    public void setSpareNumber(int spareNumber) {
        this.spareNumber = spareNumber;
    }

    public boolean isExtrasocketEnabled() {
        return extrasocketEnabled;
    }

    public void setExtrasocketEnabled(boolean extrasocketEnabled) {
        this.extrasocketEnabled = extrasocketEnabled;
    }

    public String getvBomFilePath() {
        return vBomFilePath;
    }

    public void setvBomFilePath(String vBomFilePath) {
        this.vBomFilePath = vBomFilePath;
    }

    public String getCatalogFilePath() {
        return catalogFilePath;
    }

    public void setCatalogFilePath(String catalogFilePath) {
        this.catalogFilePath = catalogFilePath;
    }

    public String getThreeparCharacterizationPath() {
        return threeparCharacterizationPath;
    }

    public void setThreeparCharacterizationPath(String threeparCharacterizationPath) {
        this.threeparCharacterizationPath = threeparCharacterizationPath;
    }

    public boolean isFoundationFlag() {
        return foundationFlag;
    }

    public void setFoundationFlag(boolean foundationFlag) {
        this.foundationFlag = foundationFlag;
    }

    public boolean isManagementFlag() {
        return managementFlag;
    }

    public void setManagementFlag(boolean managementFlag) {
        this.managementFlag = managementFlag;
    }

    public boolean isAffinityOnlyPerTypePerInstance() {
        return affinityOnlyPerTypePerInstance;
    }

    public void setAffinityOnlyPerTypePerInstance(boolean affinityOnlyPerTypePerInstance) {
        this.affinityOnlyPerTypePerInstance = affinityOnlyPerTypePerInstance;
    }

    public List<ClusterConfiguration> getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(List<ClusterConfiguration> clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    public double getEsxiCores() {
        return esxiCores;
    }

    public void setEsxiCores(double esxiCores) {
        this.esxiCores = esxiCores;
    }

    public double getTxrxCores() {
        return txrxCores;
    }

    public void setTxrxCores(double txrxCores) {
        this.txrxCores = txrxCores;
    }


    public int getDefaultHighThroughput() {
        return defaultHighThroughput;
    }

    public void setDefaultHighThroughput(int defaultHighThroughput) {
        this.defaultHighThroughput = defaultHighThroughput;
    }

    public int getDefaultBlockSize() {
        return defaultBlockSize;
    }

    public void setDefaultBlockSize(int defaultBlockSize) {
        this.defaultBlockSize = defaultBlockSize;
    }

    public double getDefaultFoundationSpace() {
        return defaultFoundationSpace;
    }

    public void setDefaultFoundationSpace(double defaultFoundationSpace) {
        this.defaultFoundationSpace = defaultFoundationSpace;
    }

    public double getDefaultAvgBlockSize() {
        return defaultAvgBlockSize;
    }

    public void setDefaultAvgBlockSize(double defaultAvgBlockSize) {
        this.defaultAvgBlockSize = defaultAvgBlockSize;
    }

    public double getDefaultAvgReadWrite() {
        return defaultAvgReadWrite;
    }

    public void setDefaultAvgReadWrite(double defaultAvgReadWrite) {
        this.defaultAvgReadWrite = defaultAvgReadWrite;
    }

    public double getDefaultAvgIOPS() {
        return defaultAvgIOPS;
    }

    public void setDefaultAvgIOPS(double defaultAvgIOPS) {
        this.defaultAvgIOPS = defaultAvgIOPS;
    }

	public int getStorageBufferPercentage() {
		return storageBufferPercentage;
	}

	public void setStorageBufferPercentage(int storageBufferPercentage) {
		this.storageBufferPercentage = storageBufferPercentage;
	}

	public int getIOPSBufferPercentage() {
		return IOPSBufferPercentage;
	}

	public void setIOPSBufferPercentage(int iOPSBufferPercentage) {
		IOPSBufferPercentage = iOPSBufferPercentage;
	}

	public int getNSBWBufferPercentage() {
		return NSBWBufferPercentage;
	}

	public void setNSBWBufferPercentage(int nSBWBufferPercentage) {
		NSBWBufferPercentage = nSBWBufferPercentage;
	}

	public int getBladeBufferPercentage() {
		return bladeBufferPercentage;
	}

	public void setBladeBufferPercentage(int bladeBufferPercentage) {
		this.bladeBufferPercentage = bladeBufferPercentage;
	}
}
