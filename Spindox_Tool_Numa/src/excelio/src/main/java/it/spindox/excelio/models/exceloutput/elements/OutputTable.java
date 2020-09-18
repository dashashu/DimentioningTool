package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.*;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.vbom.Cluster;

public class OutputTable {
    protected String site;
    protected Map<String, Integer> valueForYears = new HashMap<String, Integer>();
    protected List<String> sortedYearNames;  // contains the "keys" of Map, in sorted manner. sorting is done in Getter

    public OutputTable() {
        valueForYears = new HashMap<>();  // initialize the map, or you'll get an NPE :-)
    }

    public OutputTable(OutputTable ots, Map<Integer, String> yearsNames) {
		this.site = ots.getSite();
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
    public OutputTable(OutputTable ot) {
        this.site = ot.getSite();

        Map<String, Integer> tempMap = ot.getValueForYears();

        Integer previous = 0;

        for (Map.Entry<String, Integer> entry : tempMap.entrySet()) {
            valueForYears.put(entry.getKey(), entry.getValue() - previous);
            previous = entry.getValue();
        }
    }

    public void addCumulativeYears(String site, Cluster cluster, Map<Integer, String> yearsOrder) {
    	int previous = 0;
    	
    	Map<String, List<Blade>> map = cluster.getPlacementTable().getPlacementForSite(site); //Recupero il placement per quel sito/cluster
    	if(cluster.isFoundation()) { //Se è un cluster di foundation, mettiamo il valore all'interno del primo anno
			Integer tempValue = 0;
			for(Map.Entry<String, List<Blade>> tempMap:map.entrySet()) {
				tempValue = tempMap.getValue().size();
			}
			valueForYears.put(yearsOrder.get(1), valueForYears.get(yearsOrder.get(1)) + tempValue);
		} else {
	    	for(int i=1; i<=yearsOrder.size(); i++) { //Se non è foundation, scorriamo gli anni
	    		Integer todayValue = valueForYears.get(yearsOrder.get(i)); //Recupero il valore attuale per quell'anno
	    		if(todayValue == null) //Se non esisteva, inserisco 0
	    			todayValue = 0;
	    		
	    		if(map.containsKey(yearsOrder.get(i))) { //Se c'è un placement per quell'anno
	    			Integer tempValue = map.get(yearsOrder.get(i)).size(); //Recupero il valore di quell'anno
	    			if(todayValue+tempValue >= previous) { //Se il valore attuale e quello da aggiungere sono più grandi del valore dell'anno precedente
	    				valueForYears.put(yearsOrder.get(i), todayValue+tempValue); //Li inserisco nel nuovo anno
	    				previous = tempValue; //E aggiorno il valore di 'anno precedente'
	    			} else { //Altrimenti aggiorno la mappa riportando il valore dell'anno precedente
	    				valueForYears.put(yearsOrder.get(i), previous);
	    			}
	    		} else  {
	    			if(todayValue >= previous) {
	    				valueForYears.put(yearsOrder.get(i), todayValue);
	    				previous = todayValue;
	    			} else {
	    				valueForYears.put(yearsOrder.get(i), previous);
	    			}
	    		}
	    	}
    	}
    }


    public List<String> getSortedYearNames() {
        sortedYearNames = new ArrayList<>(valueForYears.keySet());
        Collections.sort(sortedYearNames);
        return sortedYearNames;
    }


    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Map<String, Integer> getValueForYears() {
        return valueForYears;
    }

    public void setValueForYears(Map<String, Integer> valueForYears) {
        this.valueForYears = valueForYears;
    }

}
