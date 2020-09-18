package excelio.src.test.java.excelio;

import excelio.src.main.java.it.spindox.excelio.ExcelWorkbookManagement;
import excelio.src.main.java.it.spindox.excelio.VbomCustomerWorksheetManagement;
import excelio.src.main.java.it.spindox.excelio.VbomStandardWorksheetManagement;
import commons.src.main.java.it.spindox.model.VFEConstants;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.vbom.ScopedVbomCustomerList;
import commons.src.main.java.it.spindox.vfexception.excelio.*;
import junit.framework.TestCase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class ExcelioTest {
	/*
    private static final Logger logger = LogManager.getLogger(ExcelioTest.class);

    @Test
    public void countNumberOfYearsTest_VbomCustomer() throws InvalidInputFileException, IllegalMergedRegionException {
        ExcelWorkbookManagement ewbm = new ExcelWorkbookManagement();
        String path = getClass().getResource("/vBom_customer_draft.xls").getFile();
        ewbm.readWorkbookFromExcel(path);
        VbomCustomerWorksheetManagement vbCustomer = (VbomCustomerWorksheetManagement) ewbm.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);
        TestCase.assertEquals(4, vbCustomer.countNumberOfYears());
    }


    @Test
    public void getListOfYearsTest_VbomStandard() throws InvalidInputFileException, IllegalMergedRegionException {
        ExcelWorkbookManagement ewbm = new ExcelWorkbookManagement();
        String path = getClass().getResource("/vBomTemplate_Example_v4.0.xls").getFile();
        ewbm.readWorkbookFromExcel(path);
        VbomStandardWorksheetManagement vbStandard = (VbomStandardWorksheetManagement) ewbm.getSheetAtPosition(0, VFEConstants.VBOM_FILE_CODE);

        String[] years = {"2017.0", "2018.0", "2019.0", "20Y20", "2021.0"};
        Set<String> expectedYearNameList = new HashSet<>(Arrays.asList(years));
        TestCase.assertEquals(expectedYearNameList, vbStandard.getListOfYears());
    }

    @Test
    public void loadVbomCustomerParamsFromSheetTest_VbomCustomer() throws InvalidInputFileException,
            InvalidVirtualMachineException, IllegalEmptyCellException, IllegalCellFormatException, IllegalVBomYearException {

        ExcelWorkbookManagement ewbm = new ExcelWorkbookManagement();
        String path = getClass().getResource("/vBom_customer_draft.xls").getFile();
        ewbm.readWorkbookFromExcel(path);
        VbomCustomerWorksheetManagement vbCustomer = (VbomCustomerWorksheetManagement) ewbm.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);

        ScopedVbomCustomerList svbcs = vbCustomer.loadVbomCustomerParamsFromSheet(4, new InputConfiguration());
        TestCase.assertTrue(true);
    }

*/
}
