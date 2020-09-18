package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

public class YearSummary {
	private int bladeNumber;
	private int hpBladeNumber;
	private int storageTotal;
	
	public YearSummary() {
	}
		
	public YearSummary(YearSummary ys) {
		this.bladeNumber = ys.getBladeNumber();
		this.hpBladeNumber = ys.getHpBladeNumber();
		this.storageTotal = ys.getStorageTotal();
	}

	public int getBladeNumber() {
		return bladeNumber;
	}

	public void setBladeNumber(int bladeNumber) {
		this.bladeNumber = bladeNumber;
	}

	public int getHpBladeNumber() {
		return hpBladeNumber;
	}

	public void setHpBladeNumber(int hpBladeNumber) {
		this.hpBladeNumber = hpBladeNumber;
	}

	public int getStorageTotal() {
		return storageTotal;
	}

	public void setStorageTotal(int storageTotal) {
		this.storageTotal = storageTotal;
	}
	
	
}
