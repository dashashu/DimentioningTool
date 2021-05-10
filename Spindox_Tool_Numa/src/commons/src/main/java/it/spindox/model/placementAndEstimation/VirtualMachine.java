package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.vbom.VBom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class VirtualMachine implements Comparator {

    private String vmName;

    private int instance;
    private int clone;

    private List<VirtualMachine> aff;
    private List<VirtualMachine> aaf;
    private boolean ams = false;
    private Double ram;
    private Integer hardDisk;
    private Integer highThroughputCore;
    private Double core;

    private Integer runningIOPS;
    private Integer loadingIOPS;
    private Integer northSouthBW;
    private Integer eastWestBW;
    //NUMA
    private boolean numaflag;
    private String socketflag;
    
    // private Integer storageDataDisk;
    //private Integer storageOsDisk;
    
    public VirtualMachine(int instance, int clone, String vmName) {
        this.vmName = vmName;
        this.instance = instance;
        this.clone = clone;
        this.aff = new ArrayList<VirtualMachine>();
        this.aaf = new ArrayList<VirtualMachine>();
        
        ams = false;
        core = 0.0;
        ram = 0.0;
        hardDisk = 0;
    }
    //Ashutosh : Numa
//    public VirtualMachine(int instance, int clone, String vmName) {
//    	this.vmName = vmName;
//    	this.instance = instance;
//        this.clone = clone;
//    }
    public VirtualMachine(int instance, int clone, VBom vbom, int yearNumber, Double storageReservation,InputConfiguration inputConfig) {
        this.vmName = vbom.getVnfName() + "." + vbom.getVmTypeName();
        this.instance = instance;
        this.clone = clone;
        this.aff = new ArrayList<VirtualMachine>();
        this.aaf = new ArrayList<VirtualMachine>();
        ams = false;
        numaflag = Boolean.parseBoolean(vbom.getvBomYearList().get(yearNumber).getNumaFlag());
        socketflag = vbom.getvBomYearList().get(yearNumber).getSocket();
        core = vbom.getvBomYearList().get(yearNumber).getNumberOfVcpuPerVm();
        ram = vbom.getvBomYearList().get(yearNumber).getRamPerVmInGB();
        
		/* Ashutosh: New Flag: IOPSBufferPercentage */
        //runningIOPS = vbom.getvBomYearList().get(yearNumber).getStorageIopsPerVmRunning();
        //loadingIOPS = vbom.getvBomYearList().get(yearNumber).getStorageIopsPerVmLoading();
        Integer runningIOPSTemp = vbom.getvBomYearList().get(yearNumber).getStorageIopsPerVmRunning();
        runningIOPS = runningIOPSTemp + ((runningIOPSTemp*inputConfig.getIOPSBufferPercentage())/100);
        Integer loadingIOPSTemp = vbom.getvBomYearList().get(yearNumber).getStorageIopsPerVmLoading();
        //northSouthBW = vbom.getvBomYearList().get(yearNumber).getNorthSouthBandwidthRequirementPerVm();
        loadingIOPS = loadingIOPSTemp + ((loadingIOPSTemp*inputConfig.getIOPSBufferPercentage())/100);
        
		/* Ashutosh: new flag: NSBWBufferPercentage */
        Integer northSouthBWTemp = vbom.getvBomYearList().get(yearNumber).getNorthSouthBandwidthRequirementPerVm();
        northSouthBW = northSouthBWTemp + ((northSouthBWTemp*inputConfig.getNSBWBufferPercentage())/100);
        eastWestBW = vbom.getvBomYearList().get(yearNumber).getEastWestBandwidthRequirementPerVm();
        // storageDataDisk = vbom.getvBomYearList().get(yearNumber).getStoragePerVmDataDisk();
        // storageOsDisk = vbom.getvBomYearList().get(yearNumber).getStoragePerVmOsDisk();

        Double temp = (vbom.getvBomYearList().get(yearNumber).getStoragePerVmDataDisk() + vbom.getvBomYearList().get(yearNumber).getStoragePerVmOsDisk()) * storageReservation;
        //hardDisk = (int) Math.round(temp);
        
		/* Ashutosh : new flag storageBufferPercentage */
        Integer hardDiskTemp = (int) Math.round(temp);
        hardDisk = hardDiskTemp+((hardDiskTemp*inputConfig.getStorageBufferPercentage())/100);
        //highThroughputCore = vbom.getHighThroughputVswitchResources();
    }

    public VirtualMachine(int instance, int clone) {
        this(instance, clone, "");
    }

    public String getCompleteName() {
        return getVmName() + "_" + instance + "_" + clone;
    }


    public List<VirtualMachine> selfAFF() {
        return Arrays.asList(
                getAff().stream().filter(vm ->
                        vm.getVmName().equalsIgnoreCase(this.getVmName())
                ).toArray(VirtualMachine[]::new));

    }

    public String getAffinityList() {
        StringBuffer r = new StringBuffer();
        boolean first = true;

        for (VirtualMachine vm : this.aff) {
            if (!first)
                r.append(", ");

            r.append(vm.getCompleteName());
            first = false;
        }

        return r.toString();
    }

    public String getAntiAffinityList() {
        StringBuffer r = new StringBuffer();
        boolean first = true;

        for (VirtualMachine vm : this.aaf) {
            if (!first)
                r.append(", ");

            r.append(vm.getCompleteName());
            first = false;
        }

        return r.toString();
    }

    public Integer getNSEWThroughput() {
        return (this.northSouthBW + this.eastWestBW);
    }


    public String print() {
        String r = "VM[" + getCompleteName() + " (ram: " + this.getRam() + " core: " + this.core + " hd: " + hardDisk + ")";

        StringBuffer affP = new StringBuffer();
        affP.append("AFF[" + aff.size() + "]{");
        aff.forEach(
                f -> {
                    affP.append(f.getCompleteName());
                    if ((aff.indexOf(f) < aff.size() - 1) && aff.size() > 1) {
                        affP.append(", ");
                    }
                }
        );

        affP.append("}");

        r = r + " " + affP.toString();

        StringBuffer aafP = new StringBuffer();
        aafP.append("AAF[" + aaf.size() + "]{");
        aaf.forEach(
                f -> {
                    aafP.append(f.getCompleteName());
                    if ((aaf.indexOf(f) < aaf.size() - 1) && aaf.size() > 1) {
                        aafP.append(", ");
                    }
                }
        );

        aafP.append("}");
        r = r + " " + aafP.toString();
        r = r + "]";
        return r;
    }

//
    public Double getTotalCores() {
        return core + (highThroughputCore == -1 ? 0 : highThroughputCore);
    }
//    public Double getTotalCores() {
//        return core ;
//    }

    public Double getRam() {
        return ram;
    }

    public void setRam(Double ram) {
        this.ram = ram;
    }

    public void setClone(int clone) {
        this.clone = clone;
    }

    public int getInstance() {
        return instance;
    }

    public void setInstance(int instance) {
        this.instance = instance;
    }

    public String getVmName() {
        return vmName;
    }

    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    public List<VirtualMachine> getAff() {
        return aff;
    }

    public void setAff(List<VirtualMachine> aff) {
        this.aff = aff;
    }

    public List<VirtualMachine> getAaf() {
        return aaf;
    }

    public void setAaf(List<VirtualMachine> aaf) {
        this.aaf = aaf;
    }

    public boolean isAms() {
        return ams;
    }

    public void setAms(boolean ams) {
        this.ams = ams;
    }

    public double getCore() {
        return core;
    }

    public void setCore(Double core) {
        this.core = core;
    }

    public int getClone() {
        return clone;
    }

    public Integer getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(Integer hardDisk) {
        this.hardDisk = hardDisk;
    }

    public Integer getHighThroughputCore() {
        return highThroughputCore;
    }

    public Integer getHighThroughputCoreQuantity() {
        return highThroughputCore == -1 ? 0 : highThroughputCore;
    }

    public void setHighThroughputCore(Integer highThroughputCore) {
        this.highThroughputCore = highThroughputCore;
    }


    public Integer getRunningIOPS() {
        return runningIOPS;
    }

    public void setRunningIOPS(Integer runningIOPS) {
        this.runningIOPS = runningIOPS;
    }

    public Integer getLoadingIOPS() {
		return loadingIOPS;
	}

	public void setLoadingIOPS(Integer loadingIOPS) {
		this.loadingIOPS = loadingIOPS;
	}

	public Integer getNorthSouthBW() {
        return northSouthBW;
    }

    public void setNorthSouthBW(Integer northSouthBW) {
        this.northSouthBW = northSouthBW;
    }

    public Integer getEastWestBW() {
        return eastWestBW;
    }

    public void setEastWestBW(Integer eastWestBW) {
        this.eastWestBW = eastWestBW;
    }


    @Override
    public String toString() {
        return "VirtualMachine{" +
                "vmName='" + vmName + '\'' +
                ", instance=" + instance +
                ", clone=" + clone +
//                ", aff=" + aff +
//                ", aaf=" + aaf +
//                ", ams=" + ams +
                ", ram=" + ram +
                ", hardDisk=" + hardDisk +
                ", core=" + core +
                '}';
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == o2) {
            return 0;
        } else {

            if (o1 instanceof VirtualMachine && o2 instanceof VirtualMachine) {
                return ((VirtualMachine) o1).getCompleteName().compareTo(((VirtualMachine) o2).getCompleteName());
            }

            return -1;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return this.compare(this, obj) == 0;
    }

	public boolean isNumaflag() {
		return numaflag;
	}

	public void setNumaflag(boolean numaflag) {
		this.numaflag = numaflag;
	}
	public String getSocketflag() {
		return socketflag;
	}
	public void setSocketflag(String socketflag) {
		this.socketflag = socketflag;
	}


}