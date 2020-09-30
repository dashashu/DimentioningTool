package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

public class YearSummary {
	private int synSigBlade;
	private int SynMedBlade;
	private int SynDataBlade;
	private int c7KDellStdblade;
	private int c7kDellHighPerfBlade;
	private int storageTotal;
	
	public YearSummary() {
	}
		
	public YearSummary(YearSummary ys) {
		this.synSigBlade = ys.getSynSigBlade();
		this.SynMedBlade = ys.getSynMedBlade();
		this.SynDataBlade = ys.getSynDataBlade();
		this.c7KDellStdblade = ys.getC7KDellStdblade();
		this.c7kDellHighPerfBlade = ys.getC7kDellHighPerfBlade();
		this.storageTotal = ys.getStorageTotal();
	}

	public int getStorageTotal() {
		return storageTotal;
	}

	public void setStorageTotal(int storageTotal) {
		this.storageTotal = storageTotal;
	}

	public int getSynSigBlade() {
		return synSigBlade;
	}

	public void setSynSigBlade(int synSigBlade) {
		this.synSigBlade = synSigBlade;
	}

	public int getSynMedBlade() {
		return SynMedBlade;
	}

	public void setSynMedBlade(int synMedBlade) {
		SynMedBlade = synMedBlade;
	}

	public int getSynDataBlade() {
		return SynDataBlade;
	}

	public void setSynDataBlade(int synDataBlade) {
		SynDataBlade = synDataBlade;
	}

	public int getC7KDellStdblade() {
		return c7KDellStdblade;
	}

	public void setC7KDellStdblade(int c7kDellStdblade) {
		c7KDellStdblade = c7kDellStdblade;
	}

	public int getC7kDellHighPerfBlade() {
		return c7kDellHighPerfBlade;
	}

	public void setC7kDellHighPerfBlade(int c7kDellHighPerfBlade) {
		this.c7kDellHighPerfBlade = c7kDellHighPerfBlade;
	}
	
	
}
