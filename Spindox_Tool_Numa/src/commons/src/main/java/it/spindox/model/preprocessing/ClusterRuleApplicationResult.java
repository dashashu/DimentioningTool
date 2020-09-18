package commons.src.main.java.it.spindox.model.preprocessing;

import commons.src.main.java.it.spindox.model.vbom.VBom;

public class ClusterRuleApplicationResult {
	private VBom vBom;
	private String clusterName;
	private boolean success;
	
	public VBom getvBom() {
		return vBom;
	}
	public void setvBom(VBom vBom) {
		this.vBom = vBom;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
