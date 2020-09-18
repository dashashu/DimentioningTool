package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.vbom.Cluster;

public class OutputTableSpecific extends OutputTable {
    protected String componentType;
    protected String clusterName;
	protected String modelName;
    protected int totalBladeCount;
    protected String bobBobtype;
    protected String network;
    protected String storageArray;
    protected int enclosure;
    protected int networkCard;
    protected String SAN;
    
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
    public OutputTableSpecific() {

    }

    public OutputTableSpecific(String site, String componentType, Cluster cluster, Map<Integer, String> yearsOrder) {
        this.site = site;
        this.componentType = componentType;

        for (Map.Entry<String, List<Blade>> entry : cluster.getPlacementTable().getPlacementForSite(site).entrySet()) {
        	if(yearsOrder.containsValue(entry.getKey()))
        		valueForYears.put(entry.getKey(), entry.getValue().size());
        	else
        		valueForYears.put(yearsOrder.get(1), entry.getValue().size());
        }
    }
    
    public OutputTableSpecific(OutputTableSpecific ots, Map<Integer, String> yearsNames) {
		this.site = ots.getSite();
		this.componentType = ots.getComponentType();
		Integer oldValue = 0;
		
		SortedSet<Integer> keys = new TreeSet<Integer>(yearsNames.keySet());
		for (Integer number : keys) { 
		   String yearName = yearsNames.get(number);
		   Integer valueThisYear = ots.getValueForYears().get(yearName);
		   valueThisYear = (valueThisYear == null?oldValue:valueThisYear);
		   int deltaValue = (valueThisYear-oldValue);
		   this.valueForYears.put(yearName, deltaValue);
		   oldValue = valueThisYear;
		}
	}

    //Constructor with delta creation
    public OutputTableSpecific(String site, String componentType, OutputTableSpecific ots) {
        this.site = site;
        this.componentType = componentType;

        Map<String, Integer> tempMap = ots.getValueForYears();

        Integer previous = 0;

        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            valueForYears.put(entry.getKey(), entry.getValue() - previous);
            previous = entry.getValue();
        }
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }
}
