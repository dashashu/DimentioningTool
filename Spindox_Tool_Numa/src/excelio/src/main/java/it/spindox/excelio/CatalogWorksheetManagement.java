package excelio.src.main.java.it.spindox.excelio;

import java.util.ArrayList;
import java.util.List;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.catalog.CatalogConstants;
import commons.src.main.java.it.spindox.model.catalog.CatalogEntry;
import commons.src.main.java.it.spindox.model.catalog.Compute;
import commons.src.main.java.it.spindox.model.catalog.Container;
import commons.src.main.java.it.spindox.model.catalog.Storage;
import commons.src.main.java.it.spindox.model.catalog.Switch;
import commons.src.main.java.it.spindox.model.catalog.storage.Disk;
import commons.src.main.java.it.spindox.model.catalog.storage.DriveEnclosureDisk;
import commons.src.main.java.it.spindox.model.catalog.storage.ThreePar;
import commons.src.main.java.it.spindox.model.catalog.storage.ThreeParExpansion;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Cost;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalCellFormatException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalEmptyCellException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class CatalogWorksheetManagement extends WorksheetManagement {
    //private static final Logger logger = LogManager.getLogger(CatalogWorksheetManagement.class);
    final static Logger logger = Logger.getLogger(CatalogWorksheetManagement.class);

    public CatalogWorksheetManagement(Sheet sheet, String sheetName) {
        super(sheet, sheetName);
        this.config = ConfigurationManagement.getCatalogConfiguration();
    }

    public List<CatalogEntry> loadCatalogEntriesFromSheet(String sheetName) throws IllegalCellFormatException,
            IllegalEmptyCellException, UnexpectedSituationOccurredException {
        List<CatalogEntry> cfe = new ArrayList<CatalogEntry>();

        int totalRows = sheet.getPhysicalNumberOfRows();
        logger.debug("Total rows: " + totalRows + ", Worksheet name: " + sheetName);

        for (int i = config.getInt("catalogFirstDataRowIndex"); i < totalRows; i++) {
            Row row = sheet.getRow(i);

            if (row == null || hasNoId(row))
                continue;

            CatalogEntry catalogEntry = null;

            switch (sheetName) {
                case CatalogConstants.COMPUTE:
                    catalogEntry = new Compute();
                    break;
                case CatalogConstants.CONTAINER:
                    catalogEntry = new Container();
                    break;
                case CatalogConstants.STORAGE:
                    catalogEntry = new Storage();
                    break;
                case CatalogConstants.SWITCH:
                    catalogEntry = new Switch();
                    break;
                default:
                    throw new UnexpectedSituationOccurredException("Processing a sheet not present in the catalog file: " + sheetName);
            }

            // fill catalog entries common fields
            int numOfCommonParams = readCommonParams(row, catalogEntry);

            if (sheetName.equals(CatalogConstants.STORAGE)) {
                Storage specificEntry = null;
                if (catalogEntry.getType().equals(CatalogConstants.THREEPAR))
                    specificEntry = new ThreePar();
                if (catalogEntry.getType().equals(CatalogConstants.THREEPAR_EXPANSION))
                    specificEntry = new ThreeParExpansion();
                if (catalogEntry.getType().equals(CatalogConstants.DISK))
                    specificEntry = new Disk();
                if (catalogEntry.getType().equals(CatalogConstants.DRIVE_ENCLOSURE_DISK))
                    specificEntry = new DriveEnclosureDisk();

                specificEntry.setCommonParams(catalogEntry);
                specificEntry = (Storage) readSpecificParams(row, specificEntry, numOfCommonParams);
                cfe.add(specificEntry);
            } else {
                readSpecificParams(row, catalogEntry, numOfCommonParams);
                cfe.add(catalogEntry);
            }
        }

        return cfe;
    }

    private int readCommonParams(Row row, CatalogEntry catalogEntry) throws IllegalCellFormatException, IllegalEmptyCellException {
        String[] commonParamsList = ((String) config.getProperty("commonParams")).split(",");
        int i = 0;

        try {
            Cost tempCost = new Cost();

            for (; i < commonParamsList.length; i++) {
                switch (commonParamsList[i]) {
                    case "":
                        break;
                    case CatalogConstants.TYPE:
                        catalogEntry.setType(Util.readMandatoryStringFromCell(row, i, CatalogConstants.TYPE));
                        break;
                    case CatalogConstants.VENDOR:
                        catalogEntry.setVendor(Util.readMandatoryStringFromCell(row, i, CatalogConstants.VENDOR));
                        break;
                        //ashutos
                    case CatalogConstants.ACDC:
                        catalogEntry.setAcdc(Util.readMandatoryStringFromCell(row, i, CatalogConstants.ACDC));
                        break;
                    case CatalogConstants.COMPONENT_ID:
                        catalogEntry.setComponentId(Util.readStringFromCell(row, i, CatalogConstants.COMPONENT_ID));
                        break;
                    case CatalogConstants.COMPONENT_DESCRIPTION:
                        catalogEntry.setComponentDescription(Util.readMandatoryStringFromCell(row, i, CatalogConstants.COMPONENT_DESCRIPTION));
                        break;
                    case CatalogConstants.FOUNDATION_DEFAULT_VALUE:
                        catalogEntry.setDefaultValueFoundation(Util.readNumberFromCell(row, i, 0, CatalogConstants.FOUNDATION_DEFAULT_VALUE));
                        break;
                    case CatalogConstants.FOUNDATION_INITIATIVE:
                        catalogEntry.setFoundationInitiative(Util.readMandatoryStringFromCell(row, i, "false", CatalogConstants.FOUNDATION_INITIATIVE));
                        break;
                    case CatalogConstants.CAPEX:
                        tempCost.setCapexCost(Util.readFloatNumberFromCell(row, i, 0.0, CatalogConstants.CAPEX));
                        break;
                    case CatalogConstants.OPEX_3_YEARS:
                        tempCost.setOpex3yearCost(Util.readFloatNumberFromCell(row, i, 0.0, CatalogConstants.OPEX_3_YEARS));
                        break;
                    case CatalogConstants.OPEX_2_YEARS:
                        tempCost.setOpex2yearCost(Util.readFloatNumberFromCell(row, i, 0.0, CatalogConstants.OPEX_2_YEARS));
                        break;
                }
            }

            catalogEntry.setCost(tempCost);
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (i + 1) + ": " + e.getMessage());
            throw e;
        }

        return commonParamsList.length;
    }

    private CatalogEntry readSpecificParams(Row row, CatalogEntry catalogEntry, int numOfCommonParams) throws IllegalCellFormatException, IllegalEmptyCellException, UnexpectedSituationOccurredException {
        if (catalogEntry instanceof Compute) {
            readComputeParams(row, (Compute) catalogEntry, numOfCommonParams);
        }
        if (catalogEntry instanceof Container) {
            readContainerParams(row, (Container) catalogEntry, numOfCommonParams);
        }
        if (catalogEntry instanceof Storage) {
            catalogEntry = readStorageParams(row, (Storage) catalogEntry, numOfCommonParams);
        }
        if (catalogEntry instanceof Switch) {
            readSwitchParams(row, (Switch) catalogEntry, numOfCommonParams);
        }

        return catalogEntry;
    }

    private void readComputeParams(Row row, Compute catalogEntry, int numOfCommonParams) throws IllegalCellFormatException, IllegalEmptyCellException {
        int i = numOfCommonParams;

        try {
            String[] paramsList = ((String) config.getProperty("computeParams")).split(",");

            for (; i < numOfCommonParams + paramsList.length; i++) {
                switch (paramsList[i - numOfCommonParams]) {
                    case "":
                        break;
                    case CatalogConstants.NUMBER_OF_CORES:
                        catalogEntry.setNumberOfCores(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_CORES));
                        break;
                    case CatalogConstants.NUMBER_OF_SOCKETS:
                        catalogEntry.setNumberOfSockets(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_SOCKETS));
                        break;
                    case CatalogConstants.RAM_GB:
                        catalogEntry.setRamInGb(Util.readNumberFromCell(row, i, 0, CatalogConstants.RAM_GB));
                        break;
                    case CatalogConstants.MAX_THROUGHPUT_SUPPORTED:
                        catalogEntry.setMaxThroughputSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_THROUGHPUT_SUPPORTED));
                        break;
                    case CatalogConstants.MAX_RUNNING_IOPS_SUPPORTED:
                        catalogEntry.setMaxRunningIopsSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_RUNNING_IOPS_SUPPORTED));
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (i + 1) + ": " + e.getMessage());
            throw e;
        }
    }

    private void readContainerParams(Row row, Container catalogEntry, int numOfCommonParams) throws IllegalCellFormatException, IllegalEmptyCellException {
        int i = numOfCommonParams;

        try {
            String[] paramsList = ((String) config.getProperty("containerParams")).split(",");

            for (; i < numOfCommonParams + paramsList.length; i++) {
                switch (paramsList[i - numOfCommonParams]) {
                    case "":
                        break;
                    case CatalogConstants.MAX_NUM_HOSTABLE_UNITS:
                        catalogEntry.setMaxNumOfHostableUnit(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_NUM_HOSTABLE_UNITS));
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (i + 1) + ": " + e.getMessage());
            throw e;
        }
    }

    private CatalogEntry readStorageParams(Row row, Storage catalogEntry, int numOfCommonParams) throws IllegalCellFormatException, IllegalEmptyCellException, UnexpectedSituationOccurredException {
        int i = numOfCommonParams;

        try {
//            String[] paramsList = ((String) config.getProperty("storageParams")).split(",");

//			  for(int k=i; k<numOfCommonParams+paramsList.length; k++) {
//				switch(paramsList[i-numOfCommonParams]) {
//					//For now, storage has only common params
//				}
//			}

            String type = row.getCell(0).getStringCellValue();

            switch (type) {
                case CatalogConstants.THREEPAR:
                    ThreePar threeParEntry = new ThreePar();
                    threeParEntry.setCommonParams(catalogEntry);

                    String[] threeParParamsList = ((String) config.getProperty("threeParParams")).split(",");

                    for (; i < numOfCommonParams + threeParParamsList.length; i++) {
                        switch (threeParParamsList[i - numOfCommonParams]) {
                            case "":
                                break;
                            case CatalogConstants.NUMBER_OF_NODES:
                                threeParEntry.setNumberOfNodes(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_NODES));
                                break;
                            case CatalogConstants.MAX_ENCLOSURE_SUPPORTED:
                                threeParEntry.setMaxEnclosureSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_ENCLOSURE_SUPPORTED));
                                break;
                            case CatalogConstants.MAX_EXPANSION_SUPPORTED:
                                threeParEntry.setMaxExpansionSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_EXPANSION_SUPPORTED));
                                break;
                            case CatalogConstants.THREEPAR_REFERENCE:
                                threeParEntry.setThreeParReference(Util.readStringFromCell(row, i, CatalogConstants.THREEPAR_REFERENCE));
                                break;
                            case CatalogConstants.USABLE_DISK_CAPACITY:
                                threeParEntry.setUsableDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.USABLE_DISK_CAPACITY));
                                break;
                            case CatalogConstants.RAW_DISK_CAPACITY:
                                threeParEntry.setRawDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.RAW_DISK_CAPACITY));
                                break;
                            case CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK:
                                threeParEntry.setNumberOfDisksForBlock(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK));
                                break;
                            case CatalogConstants.NUMBER_OF_LINKS_USED:
                                threeParEntry.setNumberOfLinksUsed(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_LINKS_USED));
                                break;
                            case CatalogConstants.MAX_NUM_HOSTABLE_DISKS:
                                threeParEntry.setMaxNumberOfDisksHoused(Util.readNumberFromCellMajorThanZero(row, i, CatalogConstants.NUMBER_OF_LINKS_USED));
                                break;
                            case CatalogConstants.DISK_TYPE:
                                threeParEntry.setDiskType(Util.readFloatNumberFromCell(row, i, 0.0D, CatalogConstants.DISK_TYPE));
                                break;
                        }
                    }

                    catalogEntry = threeParEntry;
                    return catalogEntry;

                case CatalogConstants.THREEPAR_EXPANSION:
                    ThreeParExpansion threeParExpansionEntry = new ThreeParExpansion();
                    threeParExpansionEntry.setCommonParams(catalogEntry);

                    String[] threeParExpansionParamsList = ((String) config.getProperty("threeParParams")).split(",");

                    for (; i < numOfCommonParams + threeParExpansionParamsList.length; i++) {
                        switch (threeParExpansionParamsList[i - numOfCommonParams]) {
                            case "":
                                break;
                            case CatalogConstants.NUMBER_OF_NODES:
                                threeParExpansionEntry.setNumberOfNodes(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_NODES));
                                break;
                            case CatalogConstants.MAX_ENCLOSURE_SUPPORTED:
                                threeParExpansionEntry.setMaxEnclosureSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_ENCLOSURE_SUPPORTED));
                                break;
                            case CatalogConstants.MAX_EXPANSION_SUPPORTED:
                                threeParExpansionEntry.setMaxExpansionSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_EXPANSION_SUPPORTED));
                                break;
                            case CatalogConstants.THREEPAR_REFERENCE:
                                threeParExpansionEntry.setThreeParReference(Util.readStringFromCell(row, i, CatalogConstants.THREEPAR_REFERENCE));
                                break;
                            case CatalogConstants.USABLE_DISK_CAPACITY:
                                threeParExpansionEntry.setUsableDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.USABLE_DISK_CAPACITY));
                                break;
                            case CatalogConstants.RAW_DISK_CAPACITY:
                                threeParExpansionEntry.setRawDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.RAW_DISK_CAPACITY));
                                break;
                            case CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK:
                                threeParExpansionEntry.setNumberOfDisksForBlock(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK));
                                break;
                            case CatalogConstants.NUMBER_OF_LINKS_USED:
                                threeParExpansionEntry.setNumberOfLinksUsed(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_LINKS_USED));
                                break;
                            case CatalogConstants.DISK_TYPE:
                                threeParExpansionEntry.setDiskType(Util.readFloatNumberFromCell(row, i, 0.0D, CatalogConstants.DISK_TYPE));
                                break;
                        }
                    }

                    catalogEntry = threeParExpansionEntry;
                    return catalogEntry;

                case CatalogConstants.DISK:
                    Disk diskEntry = new Disk();
                    diskEntry.setCommonParams(catalogEntry);

                    String[] diskParamsList = ((String) config.getProperty("diskParams")).split(",");

                    for (; i < numOfCommonParams + diskParamsList.length; i++) {
                        switch (diskParamsList[i - numOfCommonParams]) {
                            case "":
                                break;
                            case CatalogConstants.NUMBER_OF_NODES:
                                diskEntry.setNumberOfNodes(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_NODES));
                                break;
                            case CatalogConstants.THREEPAR_REFERENCE:
                                diskEntry.setThreeParReference(Util.readStringFromCell(row, i, CatalogConstants.THREEPAR_REFERENCE));
                                break;
                            case CatalogConstants.USABLE_DISK_CAPACITY:
                                diskEntry.setUsableDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.USABLE_DISK_CAPACITY));
                                break;
                            case CatalogConstants.RAW_DISK_CAPACITY:
                                diskEntry.setRawDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.RAW_DISK_CAPACITY));
                                break;
                            case CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK:
                                diskEntry.setNumberOfDisksForBlock(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK));
                                break;
                            case CatalogConstants.DISK_TYPE:
                                diskEntry.setDiskType(Util.readFloatNumberFromCell(row, i, 0.0D, CatalogConstants.DISK_TYPE));
                                break;
                        }
                    }

                    catalogEntry = diskEntry;
                    return catalogEntry;

                case CatalogConstants.DRIVE_ENCLOSURE_DISK:
                    DriveEnclosureDisk driveEnclosureDiskEntry = new DriveEnclosureDisk();
                    driveEnclosureDiskEntry.setCommonParams(catalogEntry);

                    String[] driveEnclosureDiskParamsList = ((String) config.getProperty("diskEnclosureParams")).split(",");

                    for (; i < numOfCommonParams + driveEnclosureDiskParamsList.length; i++) {
                        switch (driveEnclosureDiskParamsList[i - numOfCommonParams]) {
                            case "":
                                break;
                            case CatalogConstants.NUMBER_OF_NODES:
                                driveEnclosureDiskEntry.setNumberOfNodes(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_NODES));
                                break;
                            case CatalogConstants.THREEPAR_REFERENCE:
                                driveEnclosureDiskEntry.setThreeParReference(Util.readStringFromCell(row, i, CatalogConstants.THREEPAR_REFERENCE));
                                break;
                            case CatalogConstants.USABLE_DISK_CAPACITY:
                                driveEnclosureDiskEntry.setUsableDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.USABLE_DISK_CAPACITY));
                                break;
                            case CatalogConstants.RAW_DISK_CAPACITY:
                                driveEnclosureDiskEntry.setRawDiskCapacity(Util.readNumberFromCell(row, i, 0, CatalogConstants.RAW_DISK_CAPACITY));
                                break;
                            case CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK:
                                driveEnclosureDiskEntry.setNumberOfDisksForBlock(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_DISKS_FOR_BLOCK));
                                break;
                            case CatalogConstants.MAX_NUM_HOSTABLE_DISKS:
                                driveEnclosureDiskEntry.setMaxNumOfHostableDisks(Util.readNumberFromCellMajorThanZero(row, i, CatalogConstants.MAX_NUM_HOSTABLE_DISKS));
                                break;
                            case CatalogConstants.DISK_TYPE:
                                driveEnclosureDiskEntry.setDiskType(Util.readFloatNumberFromCell(row, i, 0.0D, CatalogConstants.DISK_TYPE));
                                break;
                        }
                    }

                    catalogEntry = driveEnclosureDiskEntry;
                    return catalogEntry;
            }

            return null;

        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (i + 1) + ": " + e.getMessage());
            throw e;
        }
    }

    private void readSwitchParams(Row row, Switch catalogEntry, int numOfCommonParams) throws IllegalCellFormatException, IllegalEmptyCellException {
        int i = numOfCommonParams;

        try {
            String[] paramsList = ((String) config.getProperty("switchParams")).split(",");

            for (; i < numOfCommonParams + paramsList.length; i++) {
                switch (paramsList[i - numOfCommonParams]) {
                    case "":
                        break;
                    case CatalogConstants.NUMBER_OF_SUPPORTED_LINKS:
                        catalogEntry.setNumberOfSupportedLinks(Util.readNumberFromCell(row, i, 0, CatalogConstants.NUMBER_OF_SUPPORTED_LINKS));
                        break;
                    case CatalogConstants.MAX_NUMBER_OF_EXPANSION_SUPPORTED:
                        catalogEntry.setMaxNumberOfExpansionSupported(Util.readNumberFromCell(row, i, 0, CatalogConstants.MAX_NUMBER_OF_EXPANSION_SUPPORTED));
                        break;
                }
            }
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (i + 1) + ": " + e.getMessage());
            throw e;
        }
    }

    private boolean hasNoId(Row row) {
        Cell cella = row.getCell(2);
        if (cella == null || cella.toString().equals(""))
            return true;
        else
            return false;
    }
}
