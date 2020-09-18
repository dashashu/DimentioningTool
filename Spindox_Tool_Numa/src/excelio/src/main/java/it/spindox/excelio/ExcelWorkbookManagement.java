package excelio.src.main.java.it.spindox.excelio;

import commons.src.main.java.it.spindox.model.VFEConstants;
import commons.src.main.java.it.spindox.vfexception.excelio.InvalidInputFileException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Created by Ashraf on 21/02/2017.
 */
public class ExcelWorkbookManagement {

    //private static final Logger logger = LogManager.getLogger(ExcelWorkbookManagement.class);
	final static Logger logger = Logger.getLogger(ExcelWorkbookManagement.class);

    private Workbook workbook;
    private int numOfSheets;

    public Workbook getWorkbook() {
        return workbook;
    }

    public int getNumOfSheets() {
        return numOfSheets;
    }


    /**
     * @param filePath a string containing path of the input excel file, including filename with extension
     * @return true upon success
     * @throws IOException
     * @throws InvalidInputFileException
     */
    public boolean readWorkbookFromExcel(String filePath) throws InvalidInputFileException {
        String fileExtension = filePath.substring(filePath.lastIndexOf('.'));
         BufferedInputStream bis = null;
        //OPCPackage opcPackage = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(filePath));
        	
        	InputStream inputFS = new FileInputStream(filePath);
            switch (fileExtension) {
                case ".xls":
                    workbook = new HSSFWorkbook(inputFS);
                    evaluateAndReplaceWorkbookFormulas();
                    numOfSheets = workbook.getNumberOfSheets();
                    logger.debug("Found: HSSF workbook with " + numOfSheets + " sheets.");
                    return true;

                case ".xlsx":
                	//workbook = new XSSFWorkbook(opcPackage);
                	workbook = new XSSFWorkbook(bis);
                    evaluateAndReplaceWorkbookFormulas();
                    numOfSheets = workbook.getNumberOfSheets();
                    logger.debug("Found: XSSF workbook with " + numOfSheets + " sheets.");
                    return true;

                default:
                    throw new InvalidInputFileException("Invalid file extension (" + fileExtension + "), expected .xls or .xlsx");
            }
        } catch (IOException e) {
            throw new InvalidInputFileException("Could not load file from path specified: " + filePath, e);
        }
    }


    /**
     * @param sheetPosition index of the sheet we want to read
     * @param workBookType  indicates if we want to read vBom, catalog, threeParCharacterization or vBomCustomer
     * @return an object that contains a sheet and its name. Each data row of the sheet is a vBom
     */
    public WorksheetManagement getSheetAtPosition(int sheetPosition, int workBookType) {
        WorksheetManagement worksheet = null;
        switch (workBookType) {
            case VFEConstants.VBOM_FILE_CODE:
                worksheet = new VbomStandardWorksheetManagement(workbook.getSheetAt(sheetPosition), workbook.getSheetName(sheetPosition));
                break;
            case VFEConstants.VBOM_CUSTOMER_FILE_CODE:
                worksheet = new VbomCustomerWorksheetManagement(workbook.getSheetAt(sheetPosition), workbook.getSheetName(sheetPosition));
                break;
            case VFEConstants.CATALOG_FILE_CODE:
                worksheet = new CatalogWorksheetManagement(workbook.getSheetAt(sheetPosition), workbook.getSheetName(sheetPosition));
                break;
            case VFEConstants.THREEPAR_CHARACTERIZATION_CODE:
                worksheet = new ThreeParCharacterizationWorksheetManagement(workbook.getSheetAt(sheetPosition), workbook.getSheetName(sheetPosition));
        }
        logger.debug("Returning worksheet at position " + sheetPosition);
        return worksheet;
    }
    

    // replace workbook's formulas by their evaluated values
    private void evaluateAndReplaceWorkbookFormulas() {
        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
        for (Sheet sheet : workbook) {
            for (Row r : sheet) {
                for (Cell c : r) {
                    if (c.getCellTypeEnum() == CellType.FORMULA) {
                        evaluator.evaluateInCell(c);
                    }
                }
            }
        }
    }

}
