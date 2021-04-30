package commons.src.main.java.it.spindox.model.vbom;

import commons.src.main.java.it.spindox.model.enumeration.Affinity;

import java.util.ArrayList;
import java.util.List;

public class VBom {
    private int rowNumber; // row number in the excel file
    private String rqstType;
    private String vnfName;
    private String vmTypeName;
    
    //Changes V3.1.5
    private List<String> siteList;
    private String nsxt;
    private String vmWorkloadType; //only for vbomcustomer
    private Affinity selfConstraint;
    private List<String> externalConstraintAffinity;
    private List<String> externalConstraintAntiAffinity;
    // private String vmRoleAndFunctionDescription; // for human reading only
    //private int highThroughputVswitchResources; // coresPerVm
    private String verticalDomainOwner;
    private int blockSize;
    private List<VBomYear> vBomYearList;


    public VBom() {
        rowNumber = -1;
        rqstType = "";
        vnfName = "";
        vmTypeName = "";
        nsxt = "";
        vmWorkloadType = "";
        selfConstraint = null;
        //highThroughputVswitchResources = 0;
        externalConstraintAffinity = new ArrayList<>();
        externalConstraintAntiAffinity = new ArrayList<>();
        vBomYearList = new ArrayList<>();
        blockSize = 0;
        vmWorkloadType = "";
        siteList = new ArrayList<>();
        verticalDomainOwner = "";
    }
    
    public VBom(VBom vBom) {
    	this.rowNumber = vBom.getRowNumber();
    	this.vnfName = vBom.getVnfName();
    	this.vmTypeName = vBom.getVmTypeName();
    	//this.vmWorkloadType = vBom.getVmWorkloadType();
    	this.selfConstraint = vBom.getSelfConstraint();
    	this.externalConstraintAffinity = vBom.getExternalConstraintAffinity();
    	this.externalConstraintAntiAffinity = vBom.getExternalConstraintAntiAffinity();
    	//this.highThroughputVswitchResources = vBom.getHighThroughputVswitchResources();
    	this.blockSize = vBom.getBlockSize();
    	this.vBomYearList = new ArrayList<VBomYear>();
    	this.vBomYearList.addAll(vBom.getvBomYearList());
    	this.verticalDomainOwner = vBom.getVerticalDomainOwner();
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

//    public int getHighThroughputVswitchResources() {
//        return highThroughputVswitchResources;
//    }
//
//    public void setHighThroughputVswitchResources(int highThroughputVswitchResources) {
//        this.highThroughputVswitchResources = highThroughputVswitchResources;
//    }

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

    public List<String> getSiteList() {
		return siteList;
	}

	public void setSiteList(List<String> siteList) {
		this.siteList = siteList;
	}

	public String getRqstType() {
		return rqstType;
	}

	public void setRqstType(String rqstType) {
		this.rqstType = rqstType;
	}

	public String getNsxt() {
		return nsxt;
	}

	public void setNsxt(String nsxt) {
		this.nsxt = nsxt;
	}

	public String getVerticalDomainOwner() {
		return verticalDomainOwner;
	}

	public void setVerticalDomainOwner(String verticalDomainOwner) {
		this.verticalDomainOwner = verticalDomainOwner;
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
               // ", highThroughputVswitchResources='" + highThroughputVswitchResources + '\'' +
                ", vmWorkloadType='" + vmWorkloadType + '\'' +
                ", vBomYearList=" + vBomYearList +
                ", siteList='" + siteList.toString()+
                '}';
    }
}
