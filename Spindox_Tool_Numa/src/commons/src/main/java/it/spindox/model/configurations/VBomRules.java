package commons.src.main.java.it.spindox.model.configurations;

import java.util.List;

public class VBomRules {
	private List<VBomPreProcessingRule> preProcessRules;
	private List<ClusterSelection> clusterSelectRules;
	
	public List<VBomPreProcessingRule> getPreProcessRules() {
		return preProcessRules;
	}
	public void setPreProcessRules(List<VBomPreProcessingRule> preProcessRules) {
		this.preProcessRules = preProcessRules;
	}
	public List<ClusterSelection> getClusterSelectRules() {
		return clusterSelectRules;
	}
	public void setClusterSelectRules(List<ClusterSelection> clusterSelectRules) {
		this.clusterSelectRules = clusterSelectRules;
	}
}