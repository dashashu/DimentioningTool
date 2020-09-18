package excelio.src.main.java.it.spindox.excelio.util;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.CatalogWorksheetManagement;
import excelio.src.main.java.it.spindox.excelio.ExcelWorkbookManagement;
import excelio.src.main.java.it.spindox.excelio.ThreeParCharacterizationWorksheetManagement;
import excelio.src.main.java.it.spindox.excelio.VbomCustomerWorksheetManagement;
import excelio.src.main.java.it.spindox.excelio.VbomWorksheetManagement;
import excelio.src.main.java.it.spindox.excelio.VbomStandardWorksheetManagement;
import commons.src.main.java.it.spindox.model.VFEConstants;
import commons.src.main.java.it.spindox.model.catalog.CatalogConstants;
import commons.src.main.java.it.spindox.model.catalog.CatalogFromExcel;
import commons.src.main.java.it.spindox.model.characterizations.ThreeParCharacterization;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.configurations.VBomPreProcessingRule;
import commons.src.main.java.it.spindox.model.preprocessing.PreProcessingConstants;
import commons.src.main.java.it.spindox.model.vbom.ScopedVbomCustomerList;
import commons.src.main.java.it.spindox.model.vbom.ScopedVbomList;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomConstants;
import commons.src.main.java.it.spindox.utils.RulesUtil;
import commons.src.main.java.it.spindox.vfexception.NoValidPathException;
import commons.src.main.java.it.spindox.vfexception.excelio.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;

/**
 * Created by Ashraf on 10/03/2017.
 */
public class Util {


    public static int getSheetIndexFromLabel(String path, String s) throws NoValidPathException {
        return readSheetLabel(path).indexOf(s);
    }

    public static List<VBom> readSheet(String filepath, int sheetPosition) throws InvalidInputFileException, IllegalMergedRegionException, InvalidVirtualMachineException, IllegalEmptyCellException, IllegalCellFormatException, IllegalVBomYearException, UnexpectedSituationOccurredException {
        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        List<VBom> vbvbvb = null;
        if (rwExcel.readWorkbookFromExcel(filepath)) {
            VbomStandardWorksheetManagement vbWorksheet = (VbomStandardWorksheetManagement) rwExcel.getSheetAtPosition(sheetPosition, VFEConstants.VBOM_FILE_CODE);
            int numOfYears = vbWorksheet.countNumberOfYears();
            ScopedVbomList sheet = vbWorksheet.loadVbomParamsFromSheet(numOfYears);
            vbvbvb = sheet.getvBomList();
            vbWorksheet.validateVbomForVm(vbvbvb);
        }

        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, new ArrayList<VBomPreProcessingRule>());

        return vbvbvb;
    }

    public static List<VBom> readSheet(String filepath, int sheetPosition, List<VBomPreProcessingRule> vBomPreProcessing) throws InvalidInputFileException, IllegalMergedRegionException, InvalidVirtualMachineException, IllegalEmptyCellException, IllegalCellFormatException, IllegalVBomYearException, UnexpectedSituationOccurredException {
        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        List<VBom> vbvbvb = null;
        if (rwExcel.readWorkbookFromExcel(filepath)) {
            VbomStandardWorksheetManagement vbWorksheet = (VbomStandardWorksheetManagement) rwExcel.getSheetAtPosition(sheetPosition, VFEConstants.VBOM_FILE_CODE);
            int numOfYears = vbWorksheet.countNumberOfYears();
            ScopedVbomList sheet = vbWorksheet.loadVbomParamsFromSheet(numOfYears);
            vbvbvb = sheet.getvBomList();
            vbWorksheet.validateVbomForVm(vbvbvb);
        }

        vbvbvb = RulesUtil.applyPreProcessingRules(vbvbvb, vBomPreProcessing);

        return vbvbvb;
    }

    public static List<VBom> readCustomerSheet(InputConfiguration inputConfig) throws InvalidInputFileException, IllegalMergedRegionException, IllegalCellFormatException, IllegalEmptyCellException, IllegalVBomYearException, InvalidVirtualMachineException {
        List<VBom> vbvbvb = null;

        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        if (rwExcel.readWorkbookFromExcel(inputConfig.getvBomFilePath())) {
            VbomCustomerWorksheetManagement vbWorksheet = (VbomCustomerWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);
            int numOfYears = vbWorksheet.countNumberOfYears();
            ScopedVbomCustomerList sheet = vbWorksheet.loadVbomCustomerParamsFromSheet(numOfYears, inputConfig);
            vbvbvb = sheet.getvBomCustomerList();
            vbWorksheet.validateVbomCustomerForVm(vbvbvb);
        }

        return vbvbvb;
    }
    
    public static Map<Integer, String> getYearOrder(InputConfiguration inputConfig) throws InvalidInputFileException, IllegalMergedRegionException {
    	Map<Integer, String> yearOrder = new HashMap<Integer, String>();
    	
    	ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        if (rwExcel.readWorkbookFromExcel(inputConfig.getvBomFilePath())) {
        	VbomWorksheetManagement vbWorksheet = null;
        	if(inputConfig.isVbomCustomer())
        		vbWorksheet = (VbomCustomerWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);
        	else
        		vbWorksheet = (VbomStandardWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.VBOM_FILE_CODE);
        	
        	yearOrder = VbomWorksheetManagement.getYearOrder(vbWorksheet, inputConfig.isVbomCustomer());
        }
        
        return yearOrder;
    }

    public static List<VBom> readSheet(String filepath, String sheetLabel, List<VBomPreProcessingRule> vBomPreProcessing) throws InvalidInputFileException, IllegalMergedRegionException, InvalidVirtualMachineException, IllegalEmptyCellException, IllegalCellFormatException, IllegalVBomYearException, NoValidPathException, UnexpectedSituationOccurredException {
        return readSheet(filepath, Util.getSheetIndexFromLabel(filepath, sheetLabel), vBomPreProcessing);
    }

    public static List<String> readSheetLabel(String path) throws NoValidPathException {
        List<String> sheetLabel = new ArrayList<>();
        ExcelWorkbookManagement excelWorkbookManagement = new ExcelWorkbookManagement();
        try {
            excelWorkbookManagement.readWorkbookFromExcel(path);
        } catch (InvalidInputFileException e) {
            throw new NoValidPathException("We are trying to access to path: " + path + " but seems be impossible");
        }
        Workbook workbook = excelWorkbookManagement.getWorkbook();
        int numOfSheets = workbook.getNumberOfSheets();

        for (int i = 0; i < numOfSheets; i++) {
            sheetLabel.add(workbook.getSheetName(i));
        }

        return sheetLabel;
    }


    public static boolean haveFoundation(String path) throws NoValidPathException {
        return readSheetLabel(path).stream().filter(s -> s.equalsIgnoreCase(ConfigurationManagement.getVbomConfiguration().getString("foundationSheetLabel"))).count() > 0;
    }

    public static void readCatalogSheet(String filepath, String sheetName) throws UnexpectedSituationOccurredException,
            IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException {

        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        int sheetPosition;

        switch (sheetName) {
            case CatalogConstants.COMPUTE:
                sheetPosition = 0;
                break;
            case CatalogConstants.CONTAINER:
                sheetPosition = 1;
                break;
            case CatalogConstants.STORAGE:
                sheetPosition = 2;
                break;
            case CatalogConstants.SWITCH:
                sheetPosition = 3;
                break;
            default:
                throw new UnexpectedSituationOccurredException("Attempting to read a sheet that is not present in the catalog file: " + sheetName);
        }

        if (rwExcel.readWorkbookFromExcel(filepath)) {
            CatalogWorksheetManagement catalogSheet = (CatalogWorksheetManagement) rwExcel.getSheetAtPosition(sheetPosition, VFEConstants.CATALOG_FILE_CODE);

            CatalogFromExcel.addEntriesToCatalogList(catalogSheet.loadCatalogEntriesFromSheet(sheetName));
        }
    }

    public static List<ThreeParCharacterization> readThreeParCharSheet(String filepath) throws UnexpectedSituationOccurredException, IllegalCellFormatException,
            IllegalEmptyCellException, InvalidInputFileException {
        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        ThreeParCharacterizationWorksheetManagement threeParCharacterizationSheet = null;

        if (rwExcel.readWorkbookFromExcel(filepath)) {
            threeParCharacterizationSheet = (ThreeParCharacterizationWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.THREEPAR_CHARACTERIZATION_CODE);
        }

        return threeParCharacterizationSheet.loadThreeParCharFromSheet();
    }

    public static String readStringFromCell(Row row, int cellIndex, String columnName) {
        Cell cella = row.getCell(cellIndex);
        cella.setCellType(CellType.STRING);
        try {
            return cella.getStringCellValue().trim();
        } catch (IllegalStateException e) {
            return cella.toString();
        }
    }

    // Reads a mandatory string and throws exception if it doesn't exist
    public static String readMandatoryStringFromCell(Row row, int cellIndex, String columnName) throws IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);
        if (cella != null && !cella.toString().trim().equalsIgnoreCase(""))
            return cella.getStringCellValue().trim();
        else
            throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) + " (" + columnName + ").");
    }

    // Reads a mandatory string and gives back a default value if it doesn't exist
    public static String readMandatoryStringFromCell(Row row, int cellIndex, String defaultValue, String columnName) throws IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);
        if (cella != null && !cella.toString().trim().equalsIgnoreCase(""))
            return cella.getStringCellValue().trim();
        else
            return defaultValue;
    }

    public static String readStringNoSpaceFromCell(Row row, int cellIndex, String columnName) throws IllegalCellFormatException, IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);
        if (cella != null && !cella.toString().trim().equalsIgnoreCase("")) {
            if (!cella.toString().trim().contains(" "))
                return cella.getStringCellValue().trim();
            else
                throw new IllegalCellFormatException("Illegal cell format found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                        " (" + columnName + "). Make sure the value does not contain any spaces.");
        } else
            throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) + " (" + columnName + ").");
    }

    public static int readNumberFromCell(Row row, int cellIndex, int defaultValue, String columnName) throws IllegalCellFormatException, IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);
        try {
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                return (int) cella.getNumericCellValue();
            } else
                return defaultValue;
        } catch (NumberFormatException e) {
            throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                    " (" + columnName + "). This cell must contain a numeric value.");
        } catch (IllegalStateException e) {
            try {
                return Integer.parseInt(cella.getStringCellValue());
            } catch (NumberFormatException a) {
                throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                        " (" + columnName + "). This cell must contain a numeric value.");
            }
        }
    }

    public static Double readFloatNumberFromCell(Row row, int cellIndex, Double defaultValue, String columnName) throws IllegalCellFormatException, IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);
        try {
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                return cella.getNumericCellValue();
            } else
                return Double.parseDouble(defaultValue + "");
        } catch (NumberFormatException e) {
            throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                    " (" + columnName + "). This cell must contain a numeric value.");
        } catch (IllegalStateException e) {
            try {
                return Double.parseDouble(cella.getStringCellValue());
            } catch (NumberFormatException a) {
                throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                        " (" + columnName + "). This cell must contain a numeric value.");
            }
        }
    }

    public static int readBlockSizeFromCell(Row row, int cellIndex, String columnName) throws IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);

        if (cella == null) {
            return 16;
        }
        String stringa = null;

        try {
            stringa = cella.getStringCellValue().trim();
        } catch (IllegalStateException e) {
            stringa = cella.toString();
        }

        if (stringa.equals(""))
            return 16;

        String pattern = "([0-9]+)(\\.0)?K?";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stringa);
        boolean b = m.find();

        boolean contained = false;
        for (Integer i : VBomConstants.BLOCK_SIZE_VALUES)
            if (i == Integer.parseInt(m.group(1)))
                contained = true;

        if (b && contained)
            return Integer.parseInt(m.group(1));
        else
            return 16;
    }

    public static int readNumberFromCellMajorThanZero(Row row, int cellIndex, String columnName) throws IllegalCellFormatException, IllegalEmptyCellException, UnexpectedSituationOccurredException {
        Cell cella = row.getCell(cellIndex);
        try {
            int value = (int) cella.getNumericCellValue();
            if (value > 0)
                return value;
            else
                throw new UnexpectedSituationOccurredException("The value present at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                        " (" + columnName + "). This cell must contain a numeric value major than 0.");
        } catch (NumberFormatException e) {
            throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                    " (" + columnName + "). This cell must contain a numeric value.");
        } catch (NullPointerException e) {
            throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                    " (" + columnName + "). This cell must contain a numeric value.");
        } catch (IllegalStateException e) {
            try {
                return Integer.parseInt(cella.getStringCellValue());
            } catch (NumberFormatException a) {
                throw new IllegalCellFormatException("Illegal content found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) +
                        " (" + columnName + "). This cell must contain a numeric value.");
            }
        }
    }

    public static Set<String> checkYearsConsistency(String vbomPath, boolean isVbomCustomer) throws IllegalMergedRegionException, InvalidInputFileException, InconsistentYearsException {
        Set<String> yearNames = null;

        ExcelWorkbookManagement rwExcel = new ExcelWorkbookManagement();
        rwExcel.readWorkbookFromExcel(vbomPath);
        int totalSheets = rwExcel.getNumOfSheets();
        VbomWorksheetManagement vbWorksheet1, vbWorksheet2;
        String foundationSheetLabel = ConfigurationManagement.getVbomConfiguration().getString("foundationSheetLabel");

        if (isVbomCustomer)
            vbWorksheet1 = (VbomWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);
        else
            vbWorksheet1 = (VbomWorksheetManagement) rwExcel.getSheetAtPosition(0, VFEConstants.VBOM_FILE_CODE);

        for (int i = 1; i < totalSheets; i++) {
            vbWorksheet2 = (VbomWorksheetManagement) rwExcel.getSheetAtPosition(i, VFEConstants.VBOM_FILE_CODE);

            // skip the check for "Foundation" sheet
            if (vbWorksheet2.getSheetName().equalsIgnoreCase(foundationSheetLabel) || vbWorksheet1.getSheetName().equalsIgnoreCase(foundationSheetLabel))
                continue;

            else if (!vbWorksheet1.getListOfYears().equals(vbWorksheet2.getListOfYears()))
                throw new InconsistentYearsException("Inconsistent years found in input vBOM - between sheet: " + vbWorksheet1.getSheetName() +
                        " and sheet: " + vbWorksheet2.getSheetName());
        }

        // yearNames are consistent => return the list
        yearNames = vbWorksheet1.getListOfYears();

        return yearNames;
    }


    public static int readIntPartOfString(Row row, int cellIndex, String columnName, String side) throws IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);

        if (cella == null || cella.toString().equals(""))
            throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) + " (" + columnName + ").");

        String stringa = null;

        try {
            stringa = cella.getStringCellValue().trim();
        } catch (IllegalStateException e) {
            stringa = cella.toString();
        }

        String pattern = "([0-9]+)/([0-9]+)";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stringa);
        boolean b = m.find();

        if (side.equals("left"))
            return Integer.parseInt(m.group(1));
        else if (side.equals("right"))
            return Integer.parseInt(m.group(2));

        return 0;
    }

    public static Double readDiskTypeFromCell(Row row, int cellIndex, String columnName) throws IllegalEmptyCellException {
        Cell cella = row.getCell(cellIndex);

        if (cella == null || cella.toString().equals(""))
            throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + (cellIndex + 1) + " (" + columnName + ").");

        String stringa = null;

        try {
            stringa = cella.getStringCellValue().trim();
        } catch (IllegalStateException e) {
            stringa = cella.toString();
        }

        String pattern = "([0-9]+\\.[0-9]+) ?TB";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stringa);
        boolean b = m.find();

        return Double.parseDouble(m.group(1));
    }

    public static int readLeft(String input) {
        return Integer.parseInt(input.split("/")[0]);
    }

    public static int readRight(String input) {
        return Integer.parseInt(input.split("/")[1]);
    }
}