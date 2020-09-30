package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.HashMap;
import java.util.Map;

import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.catalog.Compute;

public class SiteDetailsTable {
	private String site;
	private String cluster;
	private Map<String, SiteDetails> clusterTotal = new HashMap<String, SiteDetails>();
	private Map<String, SiteDetails> clusterAverage = new HashMap<String, SiteDetails>();
	private Map<String, SiteDetails> bladeLimit = new HashMap<String, SiteDetails>();
	private Map<String, SiteDetails> clusterAverageVsBladeLimit = new HashMap<String, SiteDetails>();
	private boolean highPerformance = false;
	
	public void addToTotals(Map<String, SiteDetails> cluster) {
		for(Map.Entry<String, SiteDetails> entry:cluster.entrySet()) {
			SiteDetails sd = clusterTotal.get(entry.getKey());
			
			sd.setCpu(sd.getCpu() + entry.getValue().getCpu());
			sd.setMem(sd.getMem() + entry.getValue().getMem());
			sd.setTxputNS(sd.getTxputNS() + entry.getValue().getTxputNS());
			sd.setTxputEW(sd.getTxputEW() + entry.getValue().getTxputEW());
			sd.setTxputTot(sd.getTxputTot() + entry.getValue().getTxputTot());
			sd.setIopsRunning(sd.getIopsRunning() + entry.getValue().getIopsRunning());
			sd.setIopsLoading(sd.getIopsLoading() + entry.getValue().getIopsLoading());
			sd.setBladeNumber(sd.getBladeNumber() + entry.getValue().getBladeNumber());
			
			clusterTotal.put(entry.getKey(), sd);
		}
	}
	
	public void createFullTable(Catalog catalog) {
		for(Map.Entry<String, SiteDetails> entry:clusterTotal.entrySet()) {
			Integer bladeNumber = entry.getValue().getBladeNumber();
			SiteDetails totals = entry.getValue();
			SiteDetails average = new SiteDetails();
			average.setCpu(totals.getCpu() / bladeNumber);
			average.setMem(totals.getMem() / bladeNumber);
			average.setTxputNS(totals.getTxputNS() / bladeNumber);
			average.setTxputEW(totals.getTxputEW() / bladeNumber);
			average.setTxputTot(totals.getTxputTot() / bladeNumber);
			average.setIopsRunning(totals.getIopsRunning() / bladeNumber);
			average.setIopsLoading(totals.getIopsLoading() / bladeNumber);
			this.clusterAverage.put(entry.getKey(), average);
			
			SiteDetails bladeLimit = new SiteDetails();
			
			Compute blade = null;
			if(this.highPerformance)
				blade = catalog.getC7kDellHighPerfBlade();
			else
				blade = catalog.getC7KDellStdblade();
			
			bladeLimit.setCpu(blade.getNumberOfCores() * blade.getNumberOfSockets());
			bladeLimit.setMem(new Double(blade.getRamInGb()));
			bladeLimit.setTxputTot(new Double(blade.getMaxThroughputSupported()));
			bladeLimit.setIopsRunning(new Double(blade.getMaxRunningIopsSupported()));
			this.bladeLimit.put(entry.getKey(), bladeLimit);
			
			SiteDetails percentageAverage = new SiteDetails();
			percentageAverage.setCpu(((totals.getCpu() / totals.getBladeNumber()) / bladeLimit.getCpu()) * 100);
			percentageAverage.setMem(((totals.getMem() / totals.getBladeNumber()) / bladeLimit.getMem()) * 100);
			percentageAverage.setTxputTot(((totals.getTxputTot() / totals.getBladeNumber()) / bladeLimit.getTxputTot()) * 100);
			percentageAverage.setIopsRunning(((totals.getIopsRunning() / totals.getBladeNumber()) / bladeLimit.getIopsRunning()) * 100);
			this.clusterAverageVsBladeLimit.put(entry.getKey(), percentageAverage);
		}
	}
	
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getCluster() {
		return cluster;
	}
	public void setCluster(String cluster) {
		this.cluster = cluster;
	}
	public Map<String, SiteDetails> getClusterTotal() {
		return clusterTotal;
	}
	public void setClusterTotal(Map<String, SiteDetails> clusterTotal) {
		this.clusterTotal = clusterTotal;
	}
	public Map<String, SiteDetails> getClusterAverage() {
		return clusterAverage;
	}
	public void setClusterAverage(Map<String, SiteDetails> clusterAverage) {
		this.clusterAverage = clusterAverage;
	}
	public Map<String, SiteDetails> getBladeLimit() {
		return bladeLimit;
	}
	public void setBladeLimit(Map<String, SiteDetails> bladeLimit) {
		this.bladeLimit = bladeLimit;
	}
	public Map<String, SiteDetails> getClusterAverageVsBladeLimit() {
		return clusterAverageVsBladeLimit;
	}
	public void setClusterAverageVsBladeLimit(Map<String, SiteDetails> clusterAverageVsBladeLimit) {
		this.clusterAverageVsBladeLimit = clusterAverageVsBladeLimit;
	}

	public boolean isHighPerformance() {
		return highPerformance;
	}

	public void setHighPerformance(boolean highPerformance) {
		this.highPerformance = highPerformance;
	}
}
