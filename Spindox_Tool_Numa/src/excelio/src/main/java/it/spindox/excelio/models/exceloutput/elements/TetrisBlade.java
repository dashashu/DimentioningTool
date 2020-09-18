package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.ArrayList;
import java.util.List;

import excelio.src.main.java.it.spindox.excelio.models.VmBlade;

public class TetrisBlade {
    private String site;
    private String clusterName;
    private int units;
    private String serverModel;
    private int vCPU;
    private int vRAM;
    private int iops;
    private int bw;
    private int usedVcpu;
    private int usedVram;
    private int usedIOPS;
    private int usedBW;
    private int freeVcpu;
    private int freeVram;
    private int freeIops;
    private int freeBw;
    private int percVcpu;
    private int percVram;
    private int percIops;
    private int percBw;
    private List<VmBlade> vmBladeList;

    public TetrisBlade() {
        vmBladeList = new ArrayList<>();
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getServerModel() {
        return serverModel;
    }

    public void setServerModel(String serverModel) {
        this.serverModel = serverModel;
    }

    public int getvCPU() {
        return vCPU;
    }

    public void setvCPU(int vCPU) {
        this.vCPU = vCPU;
    }

    public int getvRAM() {
        return vRAM;
    }

    public void setvRAM(int vRAM) {
        this.vRAM = vRAM;
    }

    public int getIops() {
        return iops;
    }

    public void setIops(int iops) {
        this.iops = iops;
    }

    public int getBw() {
        return bw;
    }

    public void setBw(int bw) {
        this.bw = bw;
    }

    public int getUsedVcpu() {
        return usedVcpu;
    }

    public void setUsedVcpu(int usedVcpu) {
        this.usedVcpu = usedVcpu;
    }

    public int getUsedVram() {
        return usedVram;
    }

    public void setUsedVram(int usedVram) {
        this.usedVram = usedVram;
    }

    public int getUsedIOPS() {
        return usedIOPS;
    }

    public void setUsedIOPS(int usedIOPS) {
        this.usedIOPS = usedIOPS;
    }

    public int getUsedBW() {
        return usedBW;
    }

    public void setUsedBW(int usedBW) {
        this.usedBW = usedBW;
    }

    public int getFreeVcpu() {
        return freeVcpu;
    }

    public void setFreeVcpu(int freeVcpu) {
        this.freeVcpu = freeVcpu;
    }

    public int getFreeVram() {
        return freeVram;
    }

    public void setFreeVram(int freeVram) {
        this.freeVram = freeVram;
    }

    public int getFreeIops() {
        return freeIops;
    }

    public void setFreeIops(int freeIops) {
        this.freeIops = freeIops;
    }

    public int getFreeBw() {
        return freeBw;
    }

    public void setFreeBw(int freeBw) {
        this.freeBw = freeBw;
    }

    public int getPercVcpu() {
        return percVcpu;
    }

    public void setPercVcpu(int percVcpu) {
        this.percVcpu = percVcpu;
    }

    public int getPercVram() {
        return percVram;
    }

    public void setPercVram(int percVram) {
        this.percVram = percVram;
    }

    public int getPercIops() {
        return percIops;
    }

    public void setPercIops(int percIops) {
        this.percIops = percIops;
    }

    public int getPercBw() {
        return percBw;
    }

    public void setPercBw(int percBw) {
        this.percBw = percBw;
    }

    public List<VmBlade> getVmBladeList() {
        return vmBladeList;
    }

    public void setVmBladeList(List<VmBlade> vmBladeList) {
        this.vmBladeList = vmBladeList;
    }

}
