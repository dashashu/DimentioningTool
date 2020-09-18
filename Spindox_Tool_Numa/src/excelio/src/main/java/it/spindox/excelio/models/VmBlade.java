package excelio.src.main.java.it.spindox.excelio.models;

public class VmBlade
{
	private String vmName;
	private Double vCPU;
	private Double vRAM;
	private int iops;
	private int bw;
	private int storage;
	
	public String getVmName() {
		return vmName;
	}
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public Double getvCPU() {
		return vCPU;
	}
	public void setvCPU(Double vCPU) {
		this.vCPU = vCPU;
	}
	public Double getvRAM() {
		return vRAM;
	}
	public void setvRAM(Double vRAM) {
		this.vRAM = vRAM;
	}
	public int getIops() {
		return iops;
	}
	public void setIops(int iops) {
		this.iops = iops;
	}
	public int getBw() {
		return bw;
	}
	public void setBw(int bw) {
		this.bw = bw;
	}
	public int getStorage() {
		return storage;
	}
	public void setStorage(int storage) {
		this.storage = storage;
	}
}
