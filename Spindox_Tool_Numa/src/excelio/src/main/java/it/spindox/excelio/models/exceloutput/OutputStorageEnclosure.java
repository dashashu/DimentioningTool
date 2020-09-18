package excelio.src.main.java.it.spindox.excelio.models.exceloutput;

import java.util.ArrayList;
import java.util.List;

import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TableStorageEnclosure;

public class OutputStorageEnclosure {
	List<TableStorageEnclosure> cumulativeStorage = new ArrayList<TableStorageEnclosure>(); //prima tabella: sommatoria totale elementi storage per clusters e siti
	List<TableStorageEnclosure> deltaStorage = new ArrayList<TableStorageEnclosure>(); //seconda tabella: delta della prima
	List<TableStorageEnclosure> cumulativeEnclosure = new ArrayList<TableStorageEnclosure>(); //terza tabella: sommatoria totale enclosures per siti e modello
	List<TableStorageEnclosure> deltaEnclosure = new ArrayList<TableStorageEnclosure>(); //quarta tabella: delta della terza
	
	public List<TableStorageEnclosure> getCumulativeStorage() {
		return cumulativeStorage;
	}
	public void setCumulativeStorage(List<TableStorageEnclosure> cumulativeStorage) {
		this.cumulativeStorage = cumulativeStorage;
	}
	public List<TableStorageEnclosure> getDeltaStorage() {
		return deltaStorage;
	}
	public void setDeltaStorage(List<TableStorageEnclosure> deltaStorage) {
		this.deltaStorage = deltaStorage;
	}
	public List<TableStorageEnclosure> getCumulativeEnclosure() {
		return cumulativeEnclosure;
	}
	public void setCumulativeEnclosure(List<TableStorageEnclosure> cumulativeEnclosure) {
		this.cumulativeEnclosure = cumulativeEnclosure;
	}
	public List<TableStorageEnclosure> getDeltaEnclosure() {
		return deltaEnclosure;
	}
	public void setDeltaEnclosure(List<TableStorageEnclosure> deltaEnclosure) {
		this.deltaEnclosure = deltaEnclosure;
	}
}