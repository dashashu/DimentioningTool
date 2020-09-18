package commons.src.main.java.it.spindox.model.catalog.storage;

import commons.src.main.java.it.spindox.model.catalog.Storage;

public class Disk extends Storage {
	private int maxNumOfHostableDisks;

	public int getMaxNumOfHostableDisks() {
		return maxNumOfHostableDisks;
	}

	public void setMaxNumOfHostableDisks(int maxNumOfHostableDisks) {
		this.maxNumOfHostableDisks = maxNumOfHostableDisks;
	}
}