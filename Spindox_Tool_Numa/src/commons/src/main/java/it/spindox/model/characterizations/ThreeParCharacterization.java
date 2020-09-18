package commons.src.main.java.it.spindox.model.characterizations;

public class ThreeParCharacterization {
	private String threeParModel;
	private int id;
	private String raidLevel;
	private Double diskType;
	private int diskQuantity;
	private int block;
	private Double usableCapacity;
	private int blockSize;
	private int read;
	private int write;
	private Double iopsMax;
	private Double iops90;
	
	public String getThreeParModel() {
		return threeParModel;
	}
	public void setThreeParModel(String threeParModel) {
		this.threeParModel = threeParModel;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getRaidLevel() {
		return raidLevel;
	}
	public void setRaidLevel(String raidLevel) {
		this.raidLevel = raidLevel;
	}
	public Double getDiskType() {
		return diskType;
	}
	public void setDiskType(Double diskType) {
		this.diskType = diskType;
	}
	public int getDiskQuantity() {
		return diskQuantity;
	}
	public void setDiskQuantity(int diskQuantity) {
		this.diskQuantity = diskQuantity;
	}
	public int getBlock() {
		return block;
	}
	public void setBlock(int block) {
		this.block = block;
	}
	public Double getUsableCapacity() {
		return usableCapacity;
	}
	public void setUsableCapacity(Double usableCapacity) {
		this.usableCapacity = usableCapacity;
	}
	public int getBlockSize() {
		return blockSize;
	}
	public void setBlockSize(int blockSize) {
		this.blockSize = blockSize;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public int getWrite() {
		return write;
	}
	public void setWrite(int write) {
		this.write = write;
	}
	public Double getIopsMax() {
		return iopsMax;
	}
	public void setIopsMax(Double iopsMax) {
		this.iopsMax = iopsMax;
	}
	public Double getIops90() {
		return iops90;
	}
	public void setIops90(Double iops90) {
		this.iops90 = iops90;
	}
}