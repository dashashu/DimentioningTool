package commons.src.main.java.it.spindox.model.catalog;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Cost;

/**
 * Created by Ashraf Uz Zaman on 24/03/2017.
 */
public class CatalogEntry {
    protected String id;
    protected String type;
    protected String vendor;
    protected String acdc; //ashutosh
    protected String category;
    protected String componentId;
    protected String componentDescription;
    protected String referenceContainerType;
    protected int numberOfUnitsOccupied;
    protected int foundationDefaultValue;
    private String foundationInitiative;
    protected int power;
    
    protected Cost cost;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getComponentId() {
		return componentId;
	}

	public void setComponentId(String componentId) {
		this.componentId = componentId;
	}

	public String getComponentDescription() {
		return componentDescription;
	}

	public void setComponentDescription(String componentDescription) {
		this.componentDescription = componentDescription;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public int getDefaultValueFoundation() {
		return foundationDefaultValue;
	}

	public void setDefaultValueFoundation(int defaultValueFoundation) {
		this.foundationDefaultValue = defaultValueFoundation;
	}

	public String getFoundationInitiative() {
		return foundationInitiative;
	}

	public void setFoundationInitiative(String foundationInitiative) {
		this.foundationInitiative = foundationInitiative;
	}

	public String getReferenceContainerType() {
		return referenceContainerType;
	}

	public void setReferenceContainerType(String referenceContainerType) {
		this.referenceContainerType = referenceContainerType;
	}

	public int getNumberOfUnitsOccupied() {
		return numberOfUnitsOccupied;
	}

	public void setNumberOfUnitsOccupied(int numberOfUnitsOccupied) {
		this.numberOfUnitsOccupied = numberOfUnitsOccupied;
	}

	public int getFoundationDefaultValue() {
		return foundationDefaultValue;
	}

	public void setFoundationDefaultValue(int foundationDefaultValue) {
		this.foundationDefaultValue = foundationDefaultValue;
	}

	public Cost getCost() {
		return cost;
	}

	public void setCost(Cost cost) {
		this.cost = cost;
	}
	
	public void setCommonParams(CatalogEntry catalogEntry) {
		this.id = catalogEntry.getId();
		this.type = catalogEntry.getType();
		this.vendor = catalogEntry.getVendor();
		this.acdc = catalogEntry.getAcdc();
		this.category = catalogEntry.getCategory();
		this.componentId = catalogEntry.getComponentId();
		this.componentDescription = catalogEntry.getComponentDescription();
		this.referenceContainerType = catalogEntry.getReferenceContainerType();
		this.numberOfUnitsOccupied = catalogEntry.getNumberOfUnitsOccupied();
		this.foundationDefaultValue = catalogEntry.getFoundationDefaultValue();
		this.foundationInitiative = catalogEntry.getFoundationInitiative();
		this.power = catalogEntry.getPower();
	}
	//ashutosh
	public String getAcdc() {
		return acdc;
	}

	public void setAcdc(String acdc) {
		this.acdc = acdc;
	}
}
