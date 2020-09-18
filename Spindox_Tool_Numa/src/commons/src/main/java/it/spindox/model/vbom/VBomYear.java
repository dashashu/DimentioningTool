package commons.src.main.java.it.spindox.model.vbom;

import java.util.ArrayList;
import java.util.List;

public class VBomYear {
    private String yearName;
    private boolean isYearEmpty;
    private List<String> siteList;
    private int vnfPerSite; //instance
    private int numberOfVnfPerTypeAndPerInstance; //clone
    
    //NUMA
    private String numaFlag;
    private String socket;
    
	private double numberOfVcpuPerVm;  // core
    private double ramPerVmInGB;
    private int storagePerVmDataDisk;
    private int storagePerVmOsDisk;
    private int storageIopsPerVmRunning;
    private int storageIopsPerVmLoading;
    private int storageVmWorkloadForRead;
    private int storageVmWorkloadForWrite;
    private int northSouthBandwidthRequirementPerVm;
    private int eastWestBandwidthRequirementPerVm;
    // private String additionalRequirement; // for human reading only


    public VBomYear() {
        siteList = new ArrayList<>();
        vnfPerSite = 0;
        numberOfVnfPerTypeAndPerInstance = 0;
        numberOfVcpuPerVm = 0;
        ramPerVmInGB = 0;
        storagePerVmDataDisk = 0;
        storagePerVmOsDisk = 0;
        storageIopsPerVmRunning = 0;
        storageIopsPerVmLoading = 0;
        storageVmWorkloadForRead = 0;
        storageVmWorkloadForWrite = 0;
        northSouthBandwidthRequirementPerVm = 0;
        eastWestBandwidthRequirementPerVm = 0;
        numaFlag = "";
        socket = "";
    }

    public boolean isYearEmpty() {
        return isYearEmpty;
    }

    public void setYearEmpty(boolean yearEmpty) {
        isYearEmpty = yearEmpty;
    }

    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public List<String> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<String> siteList) {
        this.siteList = siteList;
    }

    public int getVnfPerSite() {
        return vnfPerSite;
    }

    public void setVnfPerSite(int vnfPerSite) {
        this.vnfPerSite = vnfPerSite;
    }

    public int getNumberOfVnfPerTypeAndPerInstance() {
        return numberOfVnfPerTypeAndPerInstance;
    }

    public void setNumberOfVnfPerTypeAndPerInstance(int numberOfVnfPerTypeAndPerInstance) {
        this.numberOfVnfPerTypeAndPerInstance = numberOfVnfPerTypeAndPerInstance;
    }

    public double getNumberOfVcpuPerVm() {
        return numberOfVcpuPerVm;
    }

    public void setNumberOfVcpuPerVm(double numberOfVcpuPerVm) {
        this.numberOfVcpuPerVm = numberOfVcpuPerVm;
    }

    public double getRamPerVmInGB() {
        return ramPerVmInGB;
    }

    public void setRamPerVmInGB(double ramPerVmInGB) {
        this.ramPerVmInGB = ramPerVmInGB;
    }

    public int getStoragePerVmDataDisk() {
        return storagePerVmDataDisk;
    }

    public void setStoragePerVmDataDisk(int storagePerVmDataDisk) {
        this.storagePerVmDataDisk = storagePerVmDataDisk;
    }

    public int getStoragePerVmOsDisk() {
        return storagePerVmOsDisk;
    }

    public void setStoragePerVmOsDisk(int storagePerVmOsDisk) {
        this.storagePerVmOsDisk = storagePerVmOsDisk;
    }

    public int getStorageIopsPerVmRunning() {
        return storageIopsPerVmRunning;
    }

    public void setStorageIopsPerVmRunning(int storageIopsPerVmRunning) {
        this.storageIopsPerVmRunning = storageIopsPerVmRunning;
    }

    public int getStorageIopsPerVmLoading() {
        return storageIopsPerVmLoading;
    }

    public void setStorageIopsPerVmLoading(int storageIopsPerVmLoading) {
        this.storageIopsPerVmLoading = storageIopsPerVmLoading;
    }

    public int getStorageVmWorkloadForRead() {
        return storageVmWorkloadForRead;
    }

    public void setStorageVmWorkloadForRead(int storageVmWorkloadForRead) {
        this.storageVmWorkloadForRead = storageVmWorkloadForRead;
    }

    public int getStorageVmWorkloadForWrite() {
        return storageVmWorkloadForWrite;
    }

    public void setStorageVmWorkloadForWrite(int storageVmWorkloadForWrite) {
        this.storageVmWorkloadForWrite = storageVmWorkloadForWrite;
    }

    public int getNorthSouthBandwidthRequirementPerVm() {
        return northSouthBandwidthRequirementPerVm;
    }

    public void setNorthSouthBandwidthRequirementPerVm(int northSouthBandwidthRequirementPerVm) {
        this.northSouthBandwidthRequirementPerVm = northSouthBandwidthRequirementPerVm;
    }

    public int getEastWestBandwidthRequirementPerVm() {
        return eastWestBandwidthRequirementPerVm;
    }

    public void setEastWestBandwidthRequirementPerVm(int eastWestBandwidthRequirementPerVm) {
        this.eastWestBandwidthRequirementPerVm = eastWestBandwidthRequirementPerVm;
    }




	public String getNumaFlag() {
		return numaFlag;
	}

	public void setNumaFlag(String numaFlag) {
		this.numaFlag = numaFlag;
	}
    public String getSocket() {
		return socket;
	}

	public void setSocket(String socket) {
		this.socket = socket;
	}
	

	@Override
    public String toString() {
        return "VBomYear{" +
                "yearName=" + yearName +
                ", isYearEmpty=" + isYearEmpty +
                ", siteList='" + siteList.toString() + '\'' +
                ", vnfPerSite='" + vnfPerSite + '\'' +
                ", numberOfVnfPerTypeAndPerInstance='" + numberOfVnfPerTypeAndPerInstance + '\'' +
                ".numaFlag='"+ numaFlag +'\''+
                ".socket='"+ socket +'\''+
                ", numberOfVcpuPerVm='" + numberOfVcpuPerVm + '\'' +
                ", ramPerVmInGB='" + ramPerVmInGB + '\'' +
                ", storagePerVmDataDisk='" + storagePerVmDataDisk + '\'' +
                ", storagePerVmOsDisk='" + storagePerVmOsDisk + '\'' +
                ", storageIopsPerVmRunning='" + storageIopsPerVmRunning + '\'' +
                ", storageIopsPerVmLoading='" + storageIopsPerVmLoading + '\'' +
                ", storageVmWorkloadForRead='" + storageVmWorkloadForRead + '\'' +
                ", storageVmWorkloadForWrite='" + storageVmWorkloadForWrite + '\'' +
                ", northSouthBandwidthRequirementPerVm='" + northSouthBandwidthRequirementPerVm + '\'' +
                ", eastWestBandwidthRequirementPerVm='" + eastWestBandwidthRequirementPerVm + '\'' +
                '}';
    }

}
