package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class BobbosCluster extends OutputTableSpecific {
	private String clusterName;
	private String modelName;
	private int totalBladeCount;
	private String bobBobtype;
	private String network;
	private String storageArray;
	private int enclosure;
	private int networkCard;
	private String SAN;
	private int bladeCountyear;//Blade count year wise
	private int foundationEnclosure;
	private String bladeType;
	protected Map<String, Integer> siteBladecount = new HashMap<String, Integer>();
	protected Map<String, Integer> deltabladeperyr = new HashMap<String, Integer>();
	public BobbosCluster() {
		
	}
	


	public BobbosCluster(BobbosCluster bl, Map<Integer, String> yearsNames) {
		this.clusterName = bl.getClusterName();
		this.site = bl.getSite();
		this.componentType = bl.getComponentType();
		this.bladeType = bl.getBladeType();
		Integer oldValue = 0;
		
		SortedSet<Integer> keys = new TreeSet<Integer>(yearsNames.keySet());
		for (Integer number : keys) { 
		   String yearName = yearsNames.get(number);
		   Integer valueThisYear = bl.getValueForYears().get(yearName);
		   valueThisYear = (valueThisYear == null?oldValue:valueThisYear);
		   int deltaValue = (valueThisYear-oldValue);
		   this.valueForYears.put(yearName, deltaValue);
		   oldValue = valueThisYear;
		}
	}
	
	public int getBladeCountyear() {
		return bladeCountyear;
	}
	public void setBladeCountyear(int bladeCountyear) {
		this.bladeCountyear = bladeCountyear;
	}
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public int getTotalBladeCount() {
		return totalBladeCount;
	}

	public void setTotalBladeCount(int totalBladeCount) {
		this.totalBladeCount = totalBladeCount;
	}

	public String getBobBobtype() {
		return bobBobtype;
	}

	public void setBobBobtype(String bobBobtype) {
		this.bobBobtype = bobBobtype;
	}

	public String getNetwork() {
		return network;
	}

	public void setNetwork(String network) {
		this.network = network;
	}

	public String getStorageArray() {
		return storageArray;
	}

	public void setStorageArray(String storageArray) {
		this.storageArray = storageArray;
	}

	public int getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(int enclosure) {
		this.enclosure = enclosure;
	}

	public int getNetworkCard() {
		return networkCard;
	}

	public void setNetworkCard(int networkCard) {
		this.networkCard = networkCard;
	}
	public String getSAN() {
		return SAN;
	}

	public void setSAN(String sAN) {
		SAN = sAN;
	}
	public Map<String, Integer> getSiteBladecount() {
		return siteBladecount;
	}

	public void setSiteBladecount(Map<String, Integer> siteBladecount) {
		this.siteBladecount = siteBladecount;
	}

	public Map<String, Integer> getDeltabladeperyr() {
		return deltabladeperyr;
	}

	public void setDeltabladeperyr(Map<String, Integer> deltabladeperyr) {
		this.deltabladeperyr = deltabladeperyr;
	}

	public int getFoundationEnclosure() {
		return foundationEnclosure;
	}

	public void setFoundationEnclosure(int foundationEnclosure) {
		this.foundationEnclosure = foundationEnclosure;
	}

	public String getBladeType() {
		return bladeType;
	}

	public void setBladeType(String bladeType) {
		this.bladeType = bladeType;
	}
	

	
}