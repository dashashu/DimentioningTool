package excelio.src.main.java.it.spindox.excelio;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.vbom.VBomConstants;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;
import commons.src.main.java.it.spindox.vfexception.excelio.*;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.commons.configuration2.Configuration;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public abstract class VbomWorksheetManagement extends WorksheetManagement {

    //private static final Logger logger = LogManager.getLogger(VbomWorksheetManagement.class);
    final static Logger logger = Logger.getLogger(VbomWorksheetManagement.class);

    public VbomWorksheetManagement(Sheet sheet, String sheetName) {
        super(sheet, sheetName);
    }


    /**
     * @return the number of VBomYears present in the current sheet
     * @throws IllegalMergedRegionException
     */
    public int countNumberOfYears() throws IllegalMergedRegionException {

        int yearCount = 0;
        List<CellRangeAddress> tmpList = sheet.getMergedRegions();
        // in some rare occasions, we noticed that POI (inexplicably) counts the same merged region twice.
        // as a workaround of this issue, we pass the list through a set, to avoid getting wrong year count.
        Set<CellRangeAddress> tmpSet = new HashSet<>(tmpList);
        List<CellRangeAddress> mergedRegions = new ArrayList<>(tmpSet);

        // sorting merged regions by column
        // Collections.sort(mergedRegions, (r1, r2) -> r1.getFirstColumn() - r2.getFirstColumn());
        Collections.sort(mergedRegions, Comparator.comparing(CellRangeAddress::getFirstColumn));

        int totalMergedRegions = mergedRegions.size();
        int j, startingRow, startingColumn, endingRow, endingColumn;
        int maxNumOfYears = totalMergedRegions - config.getInt("numberOfSkippedRegions");

        for (int i = config.getInt("numberOfSkippedRegions"); i < totalMergedRegions; i++) {

            CellRangeAddress mr = mergedRegions.get(i);

            for (j = 0; j < maxNumOfYears; j++) {

                startingRow = config.getInt("rowIndexOfYearHeader");
                startingColumn = config.getInt("firstYear.firstColumn") + j * config.getInt("numberOfColumnsPerYear");
                endingRow = config.getInt("rowIndexOfYearHeader");
                endingColumn = startingColumn + config.getInt("numberOfColumnsPerYear") - 1;

                if (mr.getFirstRow() == startingRow && mr.getFirstColumn() == startingColumn
                        && mr.getLastRow() == endingRow && mr.getLastColumn() == endingColumn) {
                    yearCount++; // we are looking at (j+1)th year header
                } else {
                    if (mr.getFirstRow() == startingRow &&
                            mr.getLastRow() == endingRow &&
                            mr.getLastColumn() - mr.getFirstColumn() == config.getInt("numberOfColumnsPerYear") - 1)
                        continue; // the region is one of the years, but not the current one

                    else {// illegal region => re-check file formatting
                    	if(mr.getFirstRow() == startingRow &&
                            mr.getLastRow() == endingRow &&
                            mr.getLastColumn() - mr.getFirstColumn() == (config.getInt("numberOfColumnsPerYear") - 2) &&
                            mr.getFirstColumn() == 113)
                    		continue;
                    	else
                    		throw new IllegalMergedRegionException("Illegal Merged Region in File at (firstrow, firstcol, lastrow, lastcol) : ("
                                + mr.getFirstRow() + 1 + ", " + mr.getFirstColumn() + 1 + ", " + mr.getLastRow() + 1 + ", " + mr.getLastColumn() + 1 + ")");
                    }
                }
            }
        }
        logger.debug("yearCount: " + yearCount);
        return yearCount;
    }


    /**
     * @return the list of VBomYears' names present in the current sheet (e.g. 2017, 2018, ...)
     * @throws IllegalMergedRegionException
     */
    public Set<String> getListOfYears() throws IllegalMergedRegionException {

        Set<String> listOfYears = new LinkedHashSet<>();  // LinkedHashSet, so the entries maintain their order of insertion
        int yearCount = 0;
        List<CellRangeAddress> tmpList = sheet.getMergedRegions();
        Set<CellRangeAddress> tmpSet = new HashSet<>(tmpList);
        List<CellRangeAddress> mergedRegions = new ArrayList<>(tmpSet);

        // sorting merged regions by column
        Collections.sort(mergedRegions, Comparator.comparing(CellRangeAddress::getFirstColumn));

        int totalMergedRegions = mergedRegions.size();
        int j, startingRow, startingColumn, endingRow, endingColumn;
        int maxNumOfYears = totalMergedRegions - config.getInt("numberOfSkippedRegions");

        for (int i = config.getInt("numberOfSkippedRegions"); i < totalMergedRegions; i++) {

            CellRangeAddress mr = mergedRegions.get(i);

            for (j = 0; j < maxNumOfYears; j++) {

                startingRow = config.getInt("rowIndexOfYearHeader");
                startingColumn = config.getInt("firstYear.firstColumn") + j * config.getInt("numberOfColumnsPerYear");
                endingRow = config.getInt("rowIndexOfYearHeader");
                endingColumn = startingColumn + config.getInt("numberOfColumnsPerYear") - 1;

                if (mr.getFirstRow() == startingRow && mr.getFirstColumn() == startingColumn
                        && mr.getLastRow() == endingRow && mr.getLastColumn() == endingColumn) {
                    listOfYears.add(sheet.getRow(mr.getFirstRow()).getCell(mr.getFirstColumn()).toString());
                    yearCount++; // we are looking at (j+1)th year header
                } else {
                    if (mr.getFirstRow() == startingRow &&
                            mr.getLastRow() == endingRow &&
                            mr.getLastColumn() - mr.getFirstColumn() == config.getInt("numberOfColumnsPerYear") - 1)
                        continue; // the region is one of the years, but not the current one

                    else // illegal region => re-check file formatting
                        throw new IllegalMergedRegionException("Illegal Merged Region in File at (firstrow, firstcol, lastrow, lastcol) : ("
                                + mr.getFirstRow() + 1 + ", " + mr.getFirstColumn() + 1 + ", " + mr.getLastRow() + 1 + ", " + mr.getLastColumn() + 1 + ")");
                }
            }
        }
        logger.debug("yearCount: " + yearCount);
        return listOfYears;
    }


    /**
     * Reads the set of columns associated with each year of a standard or customer vBOM
     *
     * @param row            a row of the standard of customer vbom excel file
     * @param vbyr           an object of type VBomYear
     * @param startingColumn starting column's index for the year
     * @throws IllegalEmptyCellException
     * @throws IllegalCellFormatException
     * @throws IllegalVBomYearException
     */
    protected void readYearValue(Row row, VBomYear vbyr, int startingColumn, String defaultSiteName) throws IllegalEmptyCellException,
            IllegalCellFormatException, IllegalVBomYearException {

        try {
            int index = startingColumn;
            // siteList <=> Name of sites
            Cell cella = row.getCell(index++);
            String siti = "";
            
            if((defaultSiteName != null) && (!defaultSiteName.equals("")))
        		siti = defaultSiteName;
            else if (cella != null && !cella.toString().equalsIgnoreCase("")) {
            	siti = cella.getStringCellValue(); // toLowerCase()
            } else
                throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + (startingColumn + 1)
                        + " (" + VBomConstants.NAME_OF_SITES + ").");
            
            String[] splittedSites = splitStringByCommaOrSemicolon(siti);
            vbyr.setSiteList(new ArrayList<>(Arrays.asList(splittedSites)));

            //VNF per site
            vbyr.setVnfPerSite(Util.readNumberFromCell(row, index++, 0, VBomConstants.VNF_INSTANCES_NUMBER));

            //Number of VMs per type and per VNF instance
            vbyr.setNumberOfVnfPerTypeAndPerInstance(Util.readNumberFromCell(row, index++, 0, VBomConstants.VNF_CLONES_NUMBER));

            //numa flag  (a.k.a. NUMA) per VM
            vbyr.setNumaFlag(Util.readStringFromCell(row, index++, VBomConstants.VNF_NUMA_FLAG));
            //Numa socket number (a.k.a. socket) per VM
            vbyr.setSocket(Util.readStringFromCell(row, index++, VBomConstants.VNF_SOCKET_FLAG));
            //Number of vCPU (a.k.a. cores) per VM
            vbyr.setNumberOfVcpuPerVm(Util.readFloatNumberFromCell(row, index++, 0.0, VBomConstants.VM_CPU_NUMBER));
            
            //RAM (GB) per VM
            vbyr.setRamPerVmInGB(Util.readNumberFromCell(row, index++, 0, VBomConstants.VM_RAM));

            //Storage (GB) per VM  - Data Disk
            vbyr.setStoragePerVmDataDisk(Util.readNumberFromCell(row, index++, 0, VBomConstants.VM_STORAGE_DATA_DISK));

            //Storage (GB) per VM - OS Disk
            vbyr.setStoragePerVmOsDisk(Util.readNumberFromCell(row, index++, 0, VBomConstants.VM_STORAGE_OSDISK));

            //Storage IOPS per VM - Running
            vbyr.setStorageIopsPerVmRunning(Util.readNumberFromCell(row, index++, 0, VBomConstants.VM_IOPS_RUNNING));

            //Storage IOPS per VM - Loading
            vbyr.setStorageIopsPerVmLoading(Util.readNumberFromCell(row, index++, 0, VBomConstants.VM_IOPS_LOADING));

            cella = row.getCell(index++);   // Storage Read/Write VM workload distribution
            parseReadWriteWorkloadDistributionValue(cella.toString(), vbyr, row.getRowNum() + 1, index);

            //North/South bandwidth requirement per VM (Mbit/s)
            vbyr.setNorthSouthBandwidthRequirementPerVm(Util.readNumberFromCell(row, index++, 0, VBomConstants.NORTH_SOUTH_BANDWIDTH));

            //East/West  bandwidth requirement per VM (Mbit/s)
            vbyr.setEastWestBandwidthRequirementPerVm(Util.readNumberFromCell(row, index++, 0, VBomConstants.EAST_WEST_BANDWIDTH));

            // last column of the year (Additional requirements) has been skipped
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (startingColumn + 1)+"of site"+defaultSiteName + ": " + e.getMessage());
            throw e;
        }
    }

    private void parseReadWriteWorkloadDistributionValue(String inputStr, VBomYear vbyr, int rowNumber, int colNumber) throws IllegalCellFormatException {

        if (inputStr.indexOf('/') == -1) {
            throw new IllegalCellFormatException("Illegal cell format found at row " + rowNumber + " column " + colNumber +
                    " (" + VBomConstants.STORAGE_READ_WRITE + "). Expected value is <x%/y%> or <x/y> where <x> and <y> sum up to 100");
        } else {
            String[] inputStrSplitted = inputStr.split("/");
            if (inputStrSplitted.length != 2)
                throw new IllegalCellFormatException("Illegal cell format found at row " + rowNumber + " column " + colNumber +
                        " (" + VBomConstants.STORAGE_READ_WRITE + "). Expected value is <x%/y%> or <x/y> where <x> and <y> sum up to 100");

            String part1 = inputStrSplitted[0].trim();
            String part2 = inputStrSplitted[1].trim();

            if (part1.indexOf('%') != -1)
                part1 = part1.substring(0, part1.indexOf('%')).trim();
            if (part2.indexOf('%') != -1)
                part2 = part2.substring(0, part2.indexOf('%')).trim();

            int readWorkload, writeWorkload;
            try {
                readWorkload = Integer.parseInt(part1);
                writeWorkload = Integer.parseInt(part2);

            } catch (NumberFormatException e) {
                throw new IllegalCellFormatException("Illegal cell format found at row " + rowNumber + " column " + colNumber +
                        " (" + VBomConstants.STORAGE_READ_WRITE + "). Expected value is <x%/y%> or <x/y> where <x> and <y> sum up to 100");
            }

            if (readWorkload + writeWorkload == 100) {
                vbyr.setStorageVmWorkloadForRead(readWorkload);
                vbyr.setStorageVmWorkloadForWrite(writeWorkload);
            } else
                throw new IllegalCellFormatException("Illegal cell format found at row " + rowNumber + " column " + colNumber +
                        " (" + VBomConstants.STORAGE_READ_WRITE + "). Read-Write workload distribution values must sum up to 100%");
        }
    }

    public static Map<Integer, String> getYearOrder(VbomWorksheetManagement worksheet, boolean isVBomCustomer) throws IllegalMergedRegionException {
    	Map<Integer, String> yearOrder = new HashMap<Integer, String>();
    	Configuration tempConfigs = null;
    	if(isVBomCustomer)
    		tempConfigs = ConfigurationManagement.getVbomCustomerConfiguration();
    	else
    		tempConfigs = ConfigurationManagement.getVbomConfiguration();
    	
    	int yearCount = 0;
    	Sheet tempSheet = worksheet.getSheet();
        List<CellRangeAddress> tmpList = tempSheet.getMergedRegions();
        // in some rare occasions, we noticed that POI (inexplicably) counts the same merged region twice.
        // as a workaround of this issue, we pass the list through a set, to avoid getting wrong year count.
        Set<CellRangeAddress> tmpSet = new HashSet<>(tmpList);
        List<CellRangeAddress> mergedRegions = new ArrayList<>(tmpSet);

        // sorting merged regions by column
        // Collections.sort(mergedRegions, (r1, r2) -> r1.getFirstColumn() - r2.getFirstColumn());
        Collections.sort(mergedRegions, Comparator.comparing(CellRangeAddress::getFirstColumn));

        int totalMergedRegions = mergedRegions.size();
        int j, startingRow, startingColumn, endingRow, endingColumn;
        int maxNumOfYears = totalMergedRegions - tempConfigs.getInt("numberOfSkippedRegions");

        for (int i = tempConfigs.getInt("numberOfSkippedRegions"); i < totalMergedRegions; i++) {

            CellRangeAddress mr = mergedRegions.get(i);

            for (j = 0; j < maxNumOfYears; j++) {

                startingRow = tempConfigs.getInt("rowIndexOfYearHeader");
                startingColumn = tempConfigs.getInt("firstYear.firstColumn") + j * tempConfigs.getInt("numberOfColumnsPerYear");
                endingRow = tempConfigs.getInt("rowIndexOfYearHeader");
                endingColumn = startingColumn + tempConfigs.getInt("numberOfColumnsPerYear") - 1;

                if (mr.getFirstRow() == startingRow && mr.getFirstColumn() == startingColumn
                        && mr.getLastRow() == endingRow && mr.getLastColumn() == endingColumn) {
                	 yearCount++; // we are looking at (j+1)th year header
                	 yearOrder.put(yearCount, tempSheet.getRow(startingRow).getCell(startingColumn).toString());
                } else {
                    if (mr.getFirstRow() == startingRow &&
                            mr.getLastRow() == endingRow &&
                            mr.getLastColumn() - mr.getFirstColumn() == tempConfigs.getInt("numberOfColumnsPerYear") - 1)
                        continue; // the region is one of the years, but not the current one

                    else {// illegal region => re-check file formatting
                    	if(mr.getFirstRow() == startingRow &&
                            mr.getLastRow() == endingRow &&
                            mr.getLastColumn() - mr.getFirstColumn() == (tempConfigs.getInt("numberOfColumnsPerYear") - 2) &&
                            mr.getFirstColumn() == 113)
                    		continue;
                    	else
                    		throw new IllegalMergedRegionException("Illegal Merged Region in File at (firstrow, firstcol, lastrow, lastcol) : ("
                                + mr.getFirstRow() + 1 + ", " + mr.getFirstColumn() + 1 + ", " + mr.getLastRow() + 1 + ", " + mr.getLastColumn() + 1 + ")");
                    }
                }
            }
        }
    	
    	return yearOrder;
    }

    /**
     * @param row               Row containing the year
     * @param firstColumnOfYear Index of the starting column of the VBomYear
     * @return true is the year is empty, false otherwise
     * @throws IllegalVBomYearException
     */
    protected boolean isEmptyYear(Row row, int firstColumnOfYear, String yearName) {
        boolean result = false;

        Cell cell = row.getCell(firstColumnOfYear);
        if (cell == null || cell.toString().equalsIgnoreCase("")) {
            //yearName = "" + (int) Float.parseFloat(yearName); not doable on text year names.
            logger.debug("Found empty site at the row " + (row.getRowNum() + 1) + " for the year " + yearName);
            return true; // the year is empty
        }
        return result;
    }


    /**
     * @param str containing the value of "External Constraints" column of vBom excel.
     * @return a map containing following two entries: ("affinity": List<String>), ("antiaffinity": List<String>). The corresponding lists could be empty.
     * @throws IllegalCellFormatException
     */
    protected Map<String, List<String>> parseAffinityConstraints(String str) throws IllegalCellFormatException {
        Pattern affinityPattern = Pattern.compile(".*AFF\\{([^\\}]*)}.*");
        Pattern antiAffinityPattern = Pattern.compile(".*AAF\\{([^\\}]*)}.*");

        Matcher affinityMatcher = affinityPattern.matcher(str);
        Matcher antiAffinityMatcher = antiAffinityPattern.matcher(str);

        boolean affinityMatchFound = affinityMatcher.find();
        boolean antiAffinityMatchFound = antiAffinityMatcher.find();

        List<String> affinityList = new ArrayList<>();
        List<String> antiAffinityList = new ArrayList<>();

        if (!affinityMatchFound && !antiAffinityMatchFound) {
            throw new IllegalCellFormatException("Illegal cell format!");
        }
        if (affinityMatchFound) {
            String[] aff = splitStringByCommaOrSemicolon(affinityMatcher.group(1));
            affinityList = new ArrayList<>(Arrays.asList(aff));
        }
        if (antiAffinityMatchFound) {
            String[] antiAff = splitStringByCommaOrSemicolon(antiAffinityMatcher.group(1));
            antiAffinityList = new ArrayList<>(Arrays.asList(antiAff));
        }

        Map<String, List<String>> mapOfLists = new HashMap<>();
        mapOfLists.put("affinity", affinityList);
        mapOfLists.put("antiaffinity", antiAffinityList);
        return mapOfLists;
    }


    protected String[] splitStringByCommaOrSemicolon(String str) {
        String[] splitted = str.split("[,;]");

        int len = splitted.length;
        String ss;
        for (int i = 0; i < len; i++) {
            ss = splitted[i];
            splitted[i] = ss.trim();
        }
        return splitted;
    }


    protected boolean rowExists(Row row, int numOfCommonParams) {
        boolean exists = false;

        for (int cell = 0; cell < numOfCommonParams; cell++) {
            if (row.getCell(cell) != null && !row.getCell(cell).toString().equals(""))
                exists = true;
        }

        return exists;
    }


    /**
     * @param cellValue a numeric value, represented as string
     * @return the integer value of the input string. if the string is " " or "-" or "NA" the value is considered as 0
     * @throws IllegalVBomYearException
     */
    protected int parseIntegerCellValue(String cellValue) throws IllegalVBomYearException {

        if (cellValue == null || cellValue.equalsIgnoreCase("")) {
            return 0;
        } else if (NumberUtils.isCreatable(cellValue)) {
            return (int) Double.parseDouble(cellValue); // if cellValue is numeric, it's a Double. Therefore we need to
            // parse the double value from string and then cast it to int
        } else {
            switch (cellValue) {
                case " ":
                case "-":
                case "NA":
                    return 0;

                default:
                    throw new IllegalVBomYearException(" - couldn't parse int from input string");
            }
        }
    }

}

