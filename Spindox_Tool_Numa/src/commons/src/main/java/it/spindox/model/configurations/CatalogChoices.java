package commons.src.main.java.it.spindox.model.configurations;

/**
 * Created by fabrizio.sanfilippo on 23/03/2017.
 */
public class CatalogChoices {


    //Synergy changes
	String SynSigBlade;
    String SynMedBlade;
    String SynDataBlade;
    String c7KDellStdblade;
    String c7kDellHighPerfBlade;
    
    String SynSigEnclousre;
    String SynMedEnclosure;
    String synDataEnclosure;
    String c7KDellStdEnclosure;
    String c7kDellHighPerfEnclosure;
   
    String threePar;
    String sanSwitchExpansion;
    String sanSwitch;

    public CatalogChoices(String SynSigBlade, String SynMedBlade,String SynDataBlade, String c7KDellStdblade, String c7kDellHighPerfBlade, String SynSigEnclousre,String SynMedEnclosure,String synDataEnclosure,String c7KDellStdEnclosure,String c7kDellHighPerfEnclosure, String threePar, String sanSwitchExpansion, String sanSwitch) {
        this.SynSigBlade = SynSigBlade;
        this.SynMedBlade = SynMedBlade;
        this.SynDataBlade  =SynDataBlade;
        this.c7KDellStdblade = c7KDellStdblade;
        this.c7kDellHighPerfBlade = c7kDellHighPerfBlade;
        
        this.SynSigEnclousre = SynSigEnclousre;
        this.SynMedEnclosure = SynMedEnclosure;
        this.synDataEnclosure = synDataEnclosure;
        this.c7KDellStdEnclosure = c7KDellStdEnclosure;
        this.c7kDellHighPerfEnclosure = c7kDellHighPerfEnclosure;
        
        this.threePar = threePar;
        this.sanSwitchExpansion = sanSwitchExpansion;
        this.sanSwitch = sanSwitch;
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

    public String getSanSwitchExpansion() {
        return sanSwitchExpansion;
    }

    public void setSanSwitchExpansion(String sanSwitchExpansion) {
        this.sanSwitchExpansion = sanSwitchExpansion;
    }

    @Override
	public String toString() {
		return "CatalogChoices [SynSigBlade=" + SynSigBlade 
				+ ", SynMedBlade="+ SynMedBlade 
				+ ", SynDataBlade=" + SynDataBlade
				+ ", c7KDellStdblade=" + c7KDellStdblade
				+ ", c7kDellHighPerfBlade=" + c7kDellHighPerfBlade
				
				+ ", SynSigEnclousre=" + SynSigEnclousre
				+ ", SynMedEnclosure=" + SynMedEnclosure				
				+ ", synDataEnclosure=" + synDataEnclosure
				+ ", c7KDellStdEnclosure=" + c7KDellStdEnclosure
				+ ", c7kDellHighPerfEnclosure=" + c7kDellHighPerfEnclosure
				+ ", threePar=" + threePar + ", sanSwitchExpansion="
				+ sanSwitchExpansion + ", sanSwitch=" + sanSwitch + "]";
	}

	public String getSynSigBlade() {
		return SynSigBlade;
	}

	public void setSynSigBlade(String SynSigBlade) {
		this.SynSigBlade = SynSigBlade;
	}

	public String getSynMedBlade() {
		return SynMedBlade;
	}

	public void setSynMedBlade(String synMedBlade) {
		SynMedBlade = synMedBlade;
	}

	public String getSynDataBlade() {
		return SynDataBlade;
	}

	public void setSynDataBlade(String synDataBlade) {
		SynDataBlade = synDataBlade;
	}

	public String getC7KDellStdEnclosure() {
		return c7KDellStdEnclosure;
	}

	public void setC7KDellStdEnclosure(String c7kDellStdEnclosure) {
		c7KDellStdEnclosure = c7kDellStdEnclosure;
	}

	public String getC7kDellHighPerfEnclosure() {
		return c7kDellHighPerfEnclosure;
	}

	public void setC7kDellHighPerfEnclosure(String c7kDellHighPerfEnclosure) {
		this.c7kDellHighPerfEnclosure = c7kDellHighPerfEnclosure;
	}

	public String getC7KDellStdblade() {
		return c7KDellStdblade;
	}

	public void setC7KDellStdblade(String c7kDellStdblade) {
		c7KDellStdblade = c7kDellStdblade;
	}

	public String getC7kDellHighPerfBlade() {
		return c7kDellHighPerfBlade;
	}

	public void setC7kDellHighPerfBlade(String c7kDellHighPerfBlade) {
		this.c7kDellHighPerfBlade = c7kDellHighPerfBlade;
	}

	public String getSynSigEnclousre() {
		return SynSigEnclousre;
	}

	public void setSynSigEnclousre(String synSigEnclousre) {
		SynSigEnclousre = synSigEnclousre;
	}

	public String getSynMedEnclosure() {
		return SynMedEnclosure;
	}

	public void setSynMedEnclosure(String synMedEnclosure) {
		SynMedEnclosure = synMedEnclosure;
	}

	public String getSynDataEnclosure() {
		return synDataEnclosure;
	}

	public void setSynDataEnclosure(String synDataEnclosure) {
		this.synDataEnclosure = synDataEnclosure;
	}

}
