package excelio.src.main.java.it.spindox.excelio.models.exceloutput;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.BladeCluster;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.BobbosCluster;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.OutputTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.OutputTableSpecific;

public class OutputBlades {
	List<BladeCluster> cumulativeBladeClusters = new ArrayList<BladeCluster>(); //prima tabella: sommatoria totale blade per clusters e siti
	
	List<BladeCluster> deltaBladeClusters = new ArrayList<BladeCluster>(); //seconda tabella: delta della prim
	List<OutputTableSpecific> cumulativeBladesType = new ArrayList<OutputTableSpecific>(); //terza tabella: sommatoria totale blades per siti e modello
	List<OutputTableSpecific> deltaBladeType = new ArrayList<OutputTableSpecific>(); //quarta tabella: delta della terza
	List<OutputTable> cumulativeBladeTotals = new ArrayList<OutputTable>(); //quinta tabella: sommatoria totale blades per sito
	List<OutputTable> deltaBladeTotals = new ArrayList<OutputTable>(); //sesta tabella: delta della quinta
	//Ashutosh
	List<BobbosCluster> cumulativeBOBBladeClusters = new ArrayList<BobbosCluster>();//Added for Network details
	Map<String, List<Map<String, Map<String, Integer>>>> table3List = new HashMap<String, List<Map<String,Map<String,Integer>>>>(); 
	List<BobbosCluster> table2List= new ArrayList<BobbosCluster>();//Added for Network details
	List<BobbosCluster> tableD2List= new ArrayList<BobbosCluster>();//Added for Network details
	List<BobbosCluster> tableCumulativeD2List= new ArrayList<BobbosCluster>();//Added for Network details
	List<BobbosCluster> table1List = new ArrayList<BobbosCluster>(); //Added for Network details
	
	
	public List<BobbosCluster> getCumulativeBOBBladeClusters() {
		return cumulativeBOBBladeClusters;
	}
	public void setCumulativeBOBBladeClusters(List<BobbosCluster> cumulativeBOBBladeClusters) {
		this.cumulativeBOBBladeClusters = cumulativeBOBBladeClusters;
	}
	public List<BladeCluster> getCumulativeBladeClusters() {
		return cumulativeBladeClusters;
	}
	public void setCumulativeBladeClusters(List<BladeCluster> cumulativeBladeClusters) {
		this.cumulativeBladeClusters = cumulativeBladeClusters;
	}
	public List<BladeCluster> getDeltaBladeClusters() {
		return deltaBladeClusters;
	}
	public void setDeltaBladeClusters(List<BladeCluster> deltaBladeClusters) {
		this.deltaBladeClusters = deltaBladeClusters;
	}
	public List<OutputTableSpecific> getCumulativeBladesType() {
		return cumulativeBladesType;
	}
	public void setCumulativeBladesType(List<OutputTableSpecific> cumulativeBladesType) {
		this.cumulativeBladesType = cumulativeBladesType;
	}
	public List<OutputTableSpecific> getDeltaBladeType() {
		return deltaBladeType;
	}
	public void setDeltaBladeType(List<OutputTableSpecific> deltaBladeType) {
		this.deltaBladeType = deltaBladeType;
	}
	public List<OutputTable> getCumulativeBladeTotals() {
		return cumulativeBladeTotals;
	}
	public void setCumulativeBladeTotals(List<OutputTable> cumulativeBladeTotals) {
		this.cumulativeBladeTotals = cumulativeBladeTotals;
	}
	public List<OutputTable> getDeltaBladeTotals() {
		return deltaBladeTotals;
	}
	public void setDeltaBladeTotals(List<OutputTable> deltaBladeTotals) {
		this.deltaBladeTotals = deltaBladeTotals;
	}
	public List<BobbosCluster> getTable2List() {
		return table2List;
	}
	public void setTable2List(List<BobbosCluster> table2List) {
		this.table2List = table2List;
	}
	public List<BobbosCluster> getTable1List() {
		return table1List;
	}
	public void setTable1List(List<BobbosCluster> table1List) {
		this.table1List = table1List;
	}
	
	public List<BobbosCluster> getTableD2List() {
		return tableD2List;
	}
	public void setTableD2List(List<BobbosCluster> tableD2List) {
		this.tableD2List = tableD2List;
	}
	public Map<String, List<Map<String, Map<String, Integer>>>> getTable3List() {
		return table3List;
	}
	public void setTable3List(Map<String, List<Map<String, Map<String, Integer>>>> table3List) {
		this.table3List = table3List;
	}
	public List<BobbosCluster> getTableCumulativeD2List() {
		return tableCumulativeD2List;
	}
	public void setTableCumulativeD2List(List<BobbosCluster> tableCumulativeD2List) {
		this.tableCumulativeD2List = tableCumulativeD2List;
	}
	
}