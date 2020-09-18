package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class TotalOutputCluster extends OutputTable {
	private String clusterName;
	
	public TotalOutputCluster() {
		
	}
	
	//Constructor with delta creation
	public TotalOutputCluster(TotalOutputCluster toc, Map<Integer, String> yearsOrder) {
		this.site = toc.getSite();
		this.clusterName = toc.getClusterName();
		
		Map<String, Integer> tempMap = toc.getValueForYears();
		Integer previous = 0;
		
		SortedSet<Integer> keys = new TreeSet<Integer>(yearsOrder.keySet());
		for (Integer number : keys) { 
		   String yearName = yearsOrder.get(number);
		   Integer valueThisYear = tempMap.get(yearName);
		   valueThisYear = (valueThisYear == null?previous:valueThisYear);
		   int deltaValue = (valueThisYear-previous);
		   this.valueForYears.put(yearName, deltaValue);
		   previous = valueThisYear;
		}
	}

	public String getClusterName() {
		return clusterName;
	}
	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
}
