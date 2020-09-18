package commons.src.main.java.it.spindox.model.catalog;

/**
 * Created by Ashraf Uz Zaman on 23/03/2017.
 */
public class Storage extends CatalogEntry {
	private int numberOfNodes;
	private String threeParReference;
	private int usableDiskCapacity;
	private int rawDiskCapacity;
	private int numberOfDisksForBlock;
	private Double diskType;
	
	public int getNumberOfNodes() {
		return numberOfNodes;
	}
	public void setNumberOfNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
	}
	public String getThreeParReference() {
		return threeParReference;
	}
	public void setThreeParReference(String threeParReference) {
		this.threeParReference = threeParReference;
	}
	public int getUsableDiskCapacity() {
		return usableDiskCapacity;
	}
	public void setUsableDiskCapacity(int usableDiskCapacity) {
		this.usableDiskCapacity = usableDiskCapacity;
	}
	public int getRawDiskCapacity() {
		return rawDiskCapacity;
	}
	public void setRawDiskCapacity(int rawDiskCapacity) {
		this.rawDiskCapacity = rawDiskCapacity;
	}
	public int getNumberOfDisksForBlock() {
		return numberOfDisksForBlock;
	}
	public void setNumberOfDisksForBlock(int numberOfDisksForBlock) {
		this.numberOfDisksForBlock = numberOfDisksForBlock;
	}
	public Double getDiskType() {
		return diskType;
	}
	public void setDiskType(Double diskType) {
		this.diskType = diskType;
	}
}
