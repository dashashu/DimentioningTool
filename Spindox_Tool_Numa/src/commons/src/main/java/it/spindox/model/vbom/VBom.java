package commons.src.main.java.it.spindox.model.vbom;

import commons.src.main.java.it.spindox.model.enumeration.Affinity;

import java.util.ArrayList;
import java.util.List;

public class VBom {
    private int rowNumber; // row number in the excel file
    private String vnfName;
    private String vmTypeName;
    private String vmWorkloadType; //only for vbomcustomer
    private Affinity selfConstraint;
    private List<String> externalConstraintAffinity;
    private List<String> externalConstraintAntiAffinity;
    // private String vmRoleAndFunctionDescription; // for human reading only
    private int highThroughputVswitchResources; // coresPerVm
    private int blockSize;
    private List<VBomYear> vBomYearList;

    public VBom() {
        rowNumber = -1;
        vnfName = "";
        vmTypeName = "";
        vmWorkloadType = "";
        selfConstraint = null;
        highThroughputVswitchResources = 0;
        externalConstraintAffinity = new ArrayList<>();
        externalConstraintAntiAffinity = new ArrayList<>();
        vBomYearList = new ArrayList<>();
        blockSize = 0;
        vmWorkloadType = "";
    }
    
    public VBom(VBom vBom) {
    	this.rowNumber = vBom.getRowNumber();
    	this.vnfName = vBom.getVnfName();
    	this.vmTypeName = vBom.getVmTypeName();
    	this.vmWorkloadType = vBom.getVmWorkloadType();
    	this.selfConstraint = vBom.getSelfConstraint();
    	this.externalConstraintAffinity = vBom.getExternalConstraintAffinity();
    	this.externalConstraintAntiAffinity = vBom.getExternalConstraintAntiAffinity();
    	this.highThroughputVswitchResources = vBom.getHighThroughputVswitchResources();
    	this.blockSize = vBom.getBlockSize();
    	this.vBomYearList = new ArrayList<VBomYear>();
    	this.vBomYearList.addAll(vBom.getvBomYearList());
    }
    
    public String getCompleteName() {
    	return this.vnfName+"."+this.vmTypeName;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getVnfName() {
        return vnfName;
    }

    public void setVnfName(String vnfName) {
        this.vnfName = vnfName;
    }

    public String getVmTypeName() {
        return vmTypeName;
    }

    public void setVmTypeName(String vmTypeName) {
        this.vmTypeName = vmTypeName;
    }
    
    public Affinity getSelfConstraint() {
        return selfConstraint;
    }

    public void setSelfConstraint(Affinity selfConstraint) {
        this.selfConstraint = selfConstraint;
    }

    public List<String> getExternalConstraintAffinity() {
        return externalConstraintAffinity;
    }

    public List<String> getExternalConstraintAntiAffinity() {
        return externalConstraintAntiAffinity;
    }

    public void setExternalConstraintAffinity(List<String> externalConstraintAffinity) {
        this.externalConstraintAffinity = externalConstraintAffinity;
    }

    public void setExternalConstraintAntiAffinity(List<String> externalConstraintAntiAffinity) {
        this.externalConstraintAntiAffinity = externalConstraintAntiAffinity;
    }

    public int getHighThroughputVswitchResources() {
        return highThroughputVswitchResources;
    }

    public void setHighThroughputVswitchResources(int highThroughputVswitchResources) {
        this.highThroughputVswitchResources = highThroughputVswitchResources;
    }

    public List<VBomYear> getvBomYearList() {
        return vBomYearList;
    }

    public void setvBomYearList(List<VBomYear> vBomYearList) {
        this.vBomYearList = vBomYearList;
    }


    public int getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }

    public String getVmWorkloadType() {
        return vmWorkloadType;
    }

    public void setVmWorkloadType(String vmWorkloadType) {
        this.vmWorkloadType = vmWorkloadType;
    }

    @Override
    public String toString() {
        return "VBom{" +
                "rowNumber=" + rowNumber +
                ", vnfName='" + vnfName + '\'' +
                ", vmTypeName='" + vmTypeName + '\'' +
                ", selfConstraint=" + selfConstraint +
                ", externalConstraintAffinity=" + externalConstraintAffinity.toString() + '\'' +
                ", externalConstraintAntiAffinity=" + externalConstraintAntiAffinity.toString() + '\'' +
                ", highThroughputVswitchResources='" + highThroughputVswitchResources + '\'' +
                ", vmWorkloadType='" + vmWorkloadType + '\'' +
                ", vBomYearList=" + vBomYearList +
                '}';
    }
}
