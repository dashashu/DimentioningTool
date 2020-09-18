package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class BladeCluster extends OutputTableSpecific {
	private String clusterName;
	
	public BladeCluster() {
		
	}
	
	public BladeCluster(BladeCluster bl, Map<Integer, String> yearsNames) {
		this.clusterName = bl.getClusterName();
		this.site = bl.getSite();
		this.componentType = bl.getComponentType();
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
	
	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
