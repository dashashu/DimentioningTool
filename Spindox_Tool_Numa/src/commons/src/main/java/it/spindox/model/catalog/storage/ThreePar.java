package commons.src.main.java.it.spindox.model.catalog.storage;

import commons.src.main.java.it.spindox.model.catalog.Storage;

public class ThreePar extends Storage {
	private int maxEnclosureSupported;
	private int maxExpansionSupported;
	private int numberOfLinksUsed;
	private int maxNumberOfDisksHoused;
	
	public int getMaxEnclosureSupported() {
		return maxEnclosureSupported;
	}
	public void setMaxEnclosureSupported(int maxEnclosureSupported) {
		this.maxEnclosureSupported = maxEnclosureSupported;
	}
	public int getMaxExpansionSupported() {
		return maxExpansionSupported;
	}
	public void setMaxExpansionSupported(int maxExpansionSupported) {
		this.maxExpansionSupported = maxExpansionSupported;
	}
	public int getNumberOfLinksUsed() {
		return numberOfLinksUsed;
	}
	public void setNumberOfLinksUsed(int numberOfLinksUsed) {
		this.numberOfLinksUsed = numberOfLinksUsed;
	}
	public int getMaxNumberOfDisksHoused() {
		return maxNumberOfDisksHoused;
	}
	public void setMaxNumberOfDisksHoused(int maxNumberOfDisksHoused) {
		this.maxNumberOfDisksHoused = maxNumberOfDisksHoused;
	}
}
