package commons.src.main.java.it.spindox.model.catalog;

/**
 * Created by Ashraf Uz Zaman on 24/03/2017.
 */
public class Catalog {
    private Compute blade;
    private Compute bladeHighPerformance;
    private Container enclosure;
    private Container enclosureHighPerformance;
    private Storage threePar;
    private Storage threeParExpansion;
    private Storage disk;
    private Storage driveEnclosureDisk;

	public Catalog() {
	}

	public Compute getBlade() {
		return blade;
	}

	public void setBlade(Compute blade) {
		this.blade = blade;
	}

	public Container getEnclosure() {
		return enclosure;
	}

	public void setEnclosure(Container enclosure) {
		this.enclosure = enclosure;
	}

	public Container getEnclosureHighPerformance() {
		return enclosureHighPerformance;
	}

	public void setEnclosureHighPerformance(Container enclosureHighPerformance) {
		this.enclosureHighPerformance = enclosureHighPerformance;
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

	public Compute getBladeHighPerformance() {
		return bladeHighPerformance;
	}

	public void setBladeHighPerformance(Compute bladeHighPerformance) {
		this.bladeHighPerformance = bladeHighPerformance;
	}

	@Override
	public String toString() {
		return "Catalog{" +
				"blade=" + blade +
				", bladeHighPerformance=" + bladeHighPerformance +
				", enclosure=" + enclosure +
				", threePar=" + threePar +
				", threeParExpansion=" + threeParExpansion +
				", disk=" + disk +
				", driveEnclosureDisk=" + driveEnclosureDisk +
				'}';
	}
}
