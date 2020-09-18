package commons.src.main.java.it.spindox.model.configurations;

/**
 * Created by fabrizio.sanfilippo on 23/03/2017.
 */
public class CatalogChoices {

    String blade;
    String highPerformanceBlade;
    String enclosure;
    String highPerformanceEnclosure;
    String threePar;
    String sanSwitchExpansion;
    String sanSwitch;

    public CatalogChoices(String blade, String highPerformanceBlade, String enclosure, String highPerformanceEnclosure, String threePar, String sanSwitchExpansion, String sanSwitch) {
        this.blade = blade;
        this.highPerformanceBlade = highPerformanceBlade;
        this.highPerformanceEnclosure = highPerformanceEnclosure;
        this.enclosure = enclosure;
        this.threePar = threePar;
        this.sanSwitchExpansion = sanSwitchExpansion;
        this.sanSwitch = sanSwitch;
    }

    public String getBlade() {
        return blade;
    }

    public void setBlade(String blade) {
        this.blade = blade;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public String getHighPerformanceEnclosure() {
		return highPerformanceEnclosure;
	}


	public void setHighPerformanceEnclosure(String highPerformanceEnclosure) {
		this.highPerformanceEnclosure = highPerformanceEnclosure;
	}


	public String getThreePar() {
        return threePar;
    }

    public void setThreePar(String threePar) {
        this.threePar = threePar;
    }

    public String getBlockDisks() {
        return sanSwitchExpansion;
    }

    public void setBlockDisks(String blockDisks) {
        this.sanSwitchExpansion = blockDisks;
    }

    public String getSanSwitch() {
        return sanSwitch;
    }

    public void setSanSwitch(String sanSwitch) {
        this.sanSwitch = sanSwitch;
    }

    public String getHighPerformanceBlade() {
        return highPerformanceBlade;
    }

    public void setHighPerformanceBlade(String highPerformanceBlade) {
        this.highPerformanceBlade = highPerformanceBlade;
    }

    public String getSanSwitchExpansion() {
        return sanSwitchExpansion;
    }

    public void setSanSwitchExpansion(String sanSwitchExpansion) {
        this.sanSwitchExpansion = sanSwitchExpansion;
    }

    @Override
	public String toString() {
		return "CatalogChoices [blade=" + blade + ", highPerformanceBlade="
				+ highPerformanceBlade + ", enclosure=" + enclosure
				+ ", highPerformanceEnclosure=" + highPerformanceEnclosure
				+ ", threePar=" + threePar + ", sanSwitchExpansion="
				+ sanSwitchExpansion + ", sanSwitch=" + sanSwitch + "]";
	}
}
