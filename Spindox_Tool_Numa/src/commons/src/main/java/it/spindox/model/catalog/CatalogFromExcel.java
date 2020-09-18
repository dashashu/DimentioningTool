package commons.src.main.java.it.spindox.model.catalog;

import commons.src.main.java.it.spindox.vfexception.VfException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.log4j.Logger;


/**
 * The object that will mirror the catalog excel file
 * later, we will refer to this and fetch necessary fields
 */
public class CatalogFromExcel {
	//private static final Logger logger = LogManager.getLogger(CatalogFromExcel.class);
	final static Logger logger = Logger.getLogger(CatalogFromExcel.class);
	
    private static List<CatalogEntry> catalogEntryList = new ArrayList<CatalogEntry>();

    public static CatalogEntry getCatalogEntry(String catalogEntryType, String entryCode, String componentDescription) throws VfException {

    	try {
        //TODO gestire il caso in cui ci siano dei valori duplicati
        //TODO SISTEMARE!     
	        switch (catalogEntryType) {
	            case CatalogConstants.COMPUTE:
	                return catalogEntryList.stream().filter(ce -> ce instanceof Compute && ce.getComponentId().equals(entryCode)).findFirst().get();
	            case CatalogConstants.CONTAINER:
	                return catalogEntryList.stream().filter(ce -> ce instanceof Container && ce.getComponentId().equals(entryCode)).findFirst().get();
	            case CatalogConstants.STORAGE:
	                return catalogEntryList.stream().filter(ce -> ce instanceof Storage && ce.getComponentId().equals(entryCode)).findFirst().get();
	            case CatalogConstants.SWITCH:
	                return catalogEntryList.stream().filter(ce -> ce instanceof Switch && ce.getComponentId().equals(entryCode)).findFirst().get();
	        }
    	} catch(NoSuchElementException e) {
    		throw new VfException("Cannot retrieve the following element from catalog: "+componentDescription+" - code: "+entryCode+catalogEntryType);
    	}

        return null;
    }

    public static CatalogEntry getThreeParDependency(String dependencyType, String threeParCode, String componentDescription) throws VfException {
    	try {
	        switch (dependencyType) {
	            case CatalogConstants.THREEPAR_EXPANSION:
	                return catalogEntryList.stream()
	                        .filter(ce -> ce.getType().equalsIgnoreCase(CatalogConstants.THREEPAR_EXPANSION))
	                        .filter(ce -> ((Storage) ce).getThreeParReference().equals(threeParCode))
	                        .findFirst().get();
	
	            case CatalogConstants.DISK:
	                return catalogEntryList.stream()
	                        .filter(ce -> ce.getType().equalsIgnoreCase(CatalogConstants.DISK))
	                        .filter(ce -> ((Storage) ce).getThreeParReference().equals(threeParCode))
	                        .findFirst().get();
	
	            case CatalogConstants.DRIVE_ENCLOSURE_DISK:
	                return catalogEntryList.stream().
	                        filter(ce -> ce.getType().equalsIgnoreCase(CatalogConstants.DRIVE_ENCLOSURE_DISK))
	                        .filter(ce -> ((Storage) ce).getThreeParReference().equals(threeParCode))
	                        .findFirst().get();
	        }
    	} catch(NoSuchElementException e) {
    		throw new VfException("Cannot retrieve the following element from catalog: "+componentDescription+" - ThreePar code: "+threeParCode);
    	}

        return null;
    }

    public static List<CatalogEntry> getCatalogEntryList() {
        return catalogEntryList;
    }

    public static void addEntriesToCatalogList(List<CatalogEntry> catalogEntryList) {
        CatalogFromExcel.catalogEntryList.addAll(catalogEntryList);
    }
}
