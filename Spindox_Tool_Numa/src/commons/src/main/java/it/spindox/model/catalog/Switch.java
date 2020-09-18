package commons.src.main.java.it.spindox.model.catalog;

/**
 * Created by Ashraf Uz Zaman on 23/03/2017.
 */
public class Switch extends CatalogEntry {
	private int numberOfSupportedLinks;
	private int maxNumberOfExpansionSupported;
	
	public int getNumberOfSupportedLinks() {
		return numberOfSupportedLinks;
	}
	public void setNumberOfSupportedLinks(int numberOfSupportedLinks) {
		this.numberOfSupportedLinks = numberOfSupportedLinks;
	}
	public int getMaxNumberOfExpansionSupported() {
		return maxNumberOfExpansionSupported;
	}
	public void setMaxNumberOfExpansionSupported(int maxNumberOfExpansionSupported) {
		this.maxNumberOfExpansionSupported = maxNumberOfExpansionSupported;
	}
}
