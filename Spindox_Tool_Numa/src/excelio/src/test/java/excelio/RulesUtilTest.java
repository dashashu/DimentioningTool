package excelio.src.test.java.excelio;

import com.google.gson.Gson;
import excelio.src.main.java.it.spindox.excelio.ExcelWorkbookManagement;
import excelio.src.main.java.it.spindox.excelio.VbomStandardWorksheetManagement;
import commons.src.main.java.it.spindox.model.VFEConstants;
import commons.src.main.java.it.spindox.model.configurations.VBomRules;
import commons.src.main.java.it.spindox.model.vbom.ScopedVbomList;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.utils.RulesUtil;
import commons.src.main.java.it.spindox.vfexception.VfException;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class RulesUtilTest {
	/*
    VBomRules vBomRules;
    File rulesFile = new File(RulesUtilTest.class.getResource("/vfe-vbom-rules.json").toURI());


    public RulesUtilTest() throws URISyntaxException {
    }


    @Test
    public void applyPreProcessingRules_ReturnsVbomList() throws VfException {

        ExcelWorkbookManagement ewbm = new ExcelWorkbookManagement();
//        String path = getClass().getResource("/vBom_customer_draft.xls").getFile();
        String path = getClass().getResource("/vBomTemplate_Example_v4.0.xls").getFile();
        ewbm.readWorkbookFromExcel(path);
//        VbomCustomerWorksheetManagement vbCustomer = (VbomCustomerWorksheetManagement) ewbm.getSheetAtPosition(0, VFEConstants.VBOM_CUSTOMER_FILE_CODE);
        VbomStandardWorksheetManagement vbStandard = (VbomStandardWorksheetManagement) ewbm.getSheetAtPosition(0, VFEConstants.VBOM_FILE_CODE);

        ScopedVbomList svbs = vbStandard.loadVbomParamsFromSheet(4);
        List<VBom> vBomList = svbs.getvBomList();

        fillRulesFile(rulesFile);
        List<VBom> vBomList2 = RulesUtil.applyPreProcessingRules(vBomList, vBomRules.getPreProcessRules());


        TestCase.assertTrue(true);
    }


    // converts a customer vBom to standard vBom - might be useful for future
    private VBom convertVBomCustomerToVbom(VBom vbc) {
        VBom vbom = new VBom();
        vbom.setRowNumber(vbc.getRowNumber());
        vbom.setVnfName(vbc.getVnfName());
        vbom.setVmTypeName(vbc.getVmTypeName());
        vbom.setSelfConstraint(vbc.getSelfConstraint());
        vbom.setExternalConstraintAffinity(vbc.getExternalConstraintAffinity());
        vbom.setExternalConstraintAntiAffinity(vbc.getExternalConstraintAntiAffinity());
        vbom.setvBomYearList(vbc.getvBomYearList());
        vbom.setVmWorkloadType(vbc.getVmWorkloadType());
        // fields "highThroughputVswitchResources" and "blockSize" are not present in VBomCustomer

        return vbom;
    }

    // copied from service layer
    private void fillRulesFile(File file) throws VfException {
        try {
            Gson gson = new Gson();
            StringBuilder sb = new StringBuilder();
            Files.lines(Paths.get(file.getAbsolutePath())).forEach(s -> sb.append(s));

            vBomRules = gson.fromJson(sb.toString().replace("\\", "\\\\"), VBomRules.class);

        } catch (FileNotFoundException e) {
            throw new VfException("Error: VBomRules file not found at path: " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new VfException("Error:  VBomRules file not found at path: " + file.getAbsolutePath());
        }
    }
*/}