package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class TableStorageEnclosure extends OutputTableSpecific {
	private boolean foundation;
	
	public TableStorageEnclosure() {
		
	}
	
	//Constructor with delta creation
	public TableStorageEnclosure(String site, String componentType, boolean foundation, OutputTableSpecific ots, Map<Integer, String> yearsOrder) {
		this.site = site;
		this.componentType = componentType;
		this.foundation = foundation;
		
		Map<String, Integer> tempMap = ots.getValueForYears();
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

	public boolean isFoundation() {
		return foundation;
	}

	public void setFoundation(boolean foundation) {
		this.foundation = foundation;
	}

}
