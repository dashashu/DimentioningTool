package commons.src.main.java.it.spindox.model.catalog;

/**
 * Created by Ashraf Uz Zaman on 24/03/2017.
 */
public class Catalog {
    private Compute SynSigBlade;
    private Compute SynMedBlade;
    private Compute SynDataBlade;
    private Compute c7KDellStdblade;
    private Compute c7kDellHighPerfBlade;
    
    private Container SynSigEnclousre; //c7kStandard
    private Container SynMedEnclosure; //c7khighPerformance
    private Container synDataEnclosure;
    private Container c7KDellStdEnclosure;
    private Container c7kDellHighPerfEnclosure;
    //Synergy Changes
    
    private Storage threePar;
    private Storage threeParExpansion;
    private Storage disk;
    private Storage driveEnclosureDisk;

	public Catalog() {
	}

	public Storage getThreePar() {
		return threePar;
	}

	public void setThreePar(Storage threePar) {
		this.threePar = threePar;
	}

	public Storage getThreeParExpansion() {
		return threeParExpansion;
	}

	public void setThreeParExpansion(Storage threeParExpansion) {
		this.threeParExpansion = threeParExpansion;
	}

	public Storage getDisk() {
		return disk;
	}

	public void setDisk(Storage disk) {
		this.disk = disk;
	}

	public Storage getDriveEnclosureDisk() {
		return driveEnclosureDisk;
	}

	public void setDriveEnclosureDisk(Storage driveEnclosureDisk) {
		this.driveEnclosureDisk = driveEnclosureDisk;
	}


	public Compute getSynSigBlade() {
		return SynSigBlade;
	}

	public void setSynSigBlade(Compute SynSigBlade) {
		this.SynSigBlade = SynSigBlade;
	}

	public Compute getSynMedBlade() {
		return SynMedBlade;
	}

	public void setSynMedBlade(Compute synMedBlade) {
		SynMedBlade = synMedBlade;
	}

	public Compute getSynDataBlade() {
		return SynDataBlade;
	}

	public void setSynDataBlade(Compute synDataBlade) {
		SynDataBlade = synDataBlade;
	}

	public Compute getC7KDellStdblade() {
		return c7KDellStdblade;
	}

	public void setC7KDellStdblade(Compute c7kDellStdblade) {
		c7KDellStdblade = c7kDellStdblade;
	}

	public Compute getC7kDellHighPerfBlade() {
		return c7kDellHighPerfBlade;
	}

	public void setC7kDellHighPerfBlade(Compute c7kDellHighPerfBlade) {
		this.c7kDellHighPerfBlade = c7kDellHighPerfBlade;
	}

	public Container getSynSigEnclousre() {
		return SynSigEnclousre;
	}

	public void setSynSigEnclousre(Container synSigEnclousre) {
		SynSigEnclousre = synSigEnclousre;
	}

	public Container getSynMedEnclosure() {
		return SynMedEnclosure;
	}

	public void setSynMedEnclosure(Container synMedEnclosure) {
		SynMedEnclosure = synMedEnclosure;
	}

	public Container getSynDataEnclosure() {
		return synDataEnclosure;
	}

	public void setSynDataEnclosure(Container synDataEnclosure) {
		this.synDataEnclosure = synDataEnclosure;
	}

	public Container getC7KDellStdEnclosure() {
		return c7KDellStdEnclosure;
	}

	public void setC7KDellStdEnclosure(Container c7kDellStdEnclosure) {
		c7KDellStdEnclosure = c7kDellStdEnclosure;
	}

	public Container getC7kDellHighPerfEnclosure() {
		return c7kDellHighPerfEnclosure;
	}

	public void setC7kDellHighPerfEnclosure(Container c7kDellHighPerfEnclosure) {
		this.c7kDellHighPerfEnclosure = c7kDellHighPerfEnclosure;
	}

	@Override
	public String toString() {
		return "Catalog{" +
				"SynSigBlade=" + SynSigBlade +
				", SynMedBlade=" + SynMedBlade +
				", SynDataBlade=" + SynDataBlade +
				", c7KDellStdblade=" + c7KDellStdblade +
				", c7kDellHighPerfBlade=" + c7kDellHighPerfBlade +
				", SynSigEnclousre=" + SynSigEnclousre +
				", SynMedEnclosure=" + SynMedEnclosure +
				", synDataEnclosure=" + synDataEnclosure +
				", c7KDellStdEnclosure=" + c7KDellStdEnclosure +
				", c7kDellHighPerfEnclosure=" + c7kDellHighPerfEnclosure +
				
				", threePar=" + threePar +
				", threeParExpansion=" + threeParExpansion +
				", disk=" + disk +
				", driveEnclosureDisk=" + driveEnclosureDisk +
				'}';
	}

}
