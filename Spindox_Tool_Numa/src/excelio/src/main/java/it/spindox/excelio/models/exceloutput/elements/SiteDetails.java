package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

public class SiteDetails {
	private double cpu;	
	private double mem;
	private double txputNS;
	private double txputEW;
	private double txputTot;
	private double iopsRunning;
	private double iopsLoading;
	private int bladeNumber;
	
	public SiteDetails() {
		
	}
	
	public SiteDetails(SiteDetails sd) {
		this.cpu = sd.getCpu();	
		mem = sd.getMem();
		txputNS = sd.getTxputNS();
		txputEW = sd.getTxputEW();
		txputTot = sd.getTxputTot();
		iopsRunning = sd.getIopsRunning();
		iopsLoading = sd.getIopsLoading();
		bladeNumber = sd.getBladeNumber();
	}

	public double getCpu() {
		return cpu;
	}

	public void setCpu(double cpu) {
		this.cpu = cpu;
	}

	public double getMem() {
		return mem;
	}

	public void setMem(double mem) {
		this.mem = mem;
	}

	public double getTxputNS() {
		return txputNS;
	}

	public void setTxputNS(double txputNS) {
		this.txputNS = txputNS;
	}

	public double getTxputEW() {
		return txputEW;
	}

	public void setTxputEW(double txputEW) {
		this.txputEW = txputEW;
	}

	public double getTxputTot() {
		return txputTot;
	}

	public void setTxputTot(double txputTot) {
		this.txputTot = txputTot;
	}

	public double getIopsRunning() {
		return iopsRunning;
	}

	public void setIopsRunning(double iopsRunning) {
		this.iopsRunning = iopsRunning;
	}

	public double getIopsLoading() {
		return iopsLoading;
	}

	public void setIopsLoading(double iopsLoading) {
		this.iopsLoading = iopsLoading;
	}

	public int getBladeNumber() {
		return bladeNumber;
	}

	public void setBladeNumber(int bladeNumber) {
		this.bladeNumber = bladeNumber;
	}
}
