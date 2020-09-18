package commons.src.main.java.it.spindox.model.placementAndEstimation;

public class Cost {
	private Double capexCost;
	private Double opex3yearCost;
	private Double opex2yearCost;
	
	public Cost(){
		capexCost = 0d;
		opex3yearCost = 0d;
		opex2yearCost = 0d;
	}
	
	public Cost(Double capexCost, Double opex3yearCost, Double opex2yearCost) {
		this.capexCost = capexCost;
		this.opex3yearCost = opex3yearCost;
		this.opex2yearCost = opex2yearCost;
	}

	public Double getCapexCost() {
		return capexCost;
	}

	public void setCapexCost(Double capexCost) {
		this.capexCost = capexCost;
	}

	public Double getOpex3yearCost() {
		return opex3yearCost;
	}

	public void setOpex3yearCost(Double opex3yearCost) {
		this.opex3yearCost = opex3yearCost;
	}
	
	public Double getOpex2yearCost() {
		return opex2yearCost;
	}

	public void setOpex2yearCost(Double opex2yearCost) {
		this.opex2yearCost = opex2yearCost;
	}
}
