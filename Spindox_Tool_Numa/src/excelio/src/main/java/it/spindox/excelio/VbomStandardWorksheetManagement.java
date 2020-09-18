package excelio.src.main.java.it.spindox.excelio;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.enumeration.Affinity;
import commons.src.main.java.it.spindox.model.vbom.ScopedVbomList;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomConstants;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;
import commons.src.main.java.it.spindox.vfexception.excelio.*;

import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;


/**
 * Created by Ashraf on 01/03/2017.
 */
public class VbomStandardWorksheetManagement extends VbomWorksheetManagement {

    //private static final Logger logger = LogManager.getLogger(VbomStandardWorksheetManagement.class);
	final static Logger logger = Logger.getLogger(VbomStandardWorksheetManagement.class);
    private Set<String> virtualMachineSet;  // will be used to check and ensure that the virtual machines in vBOM are unique

    public VbomStandardWorksheetManagement(Sheet sheet, String sheetName) {
        super(sheet, sheetName);
        this.virtualMachineSet = new HashSet<>();
        this.config = ConfigurationManagement.getVbomConfiguration();
    }
    
    /**
     * @param numOfYears total number of years found in the sheet
     * @return a "ScopedVbomList" object containing the sheet name and a list of Vboms
     * @throws IllegalCellFormatException
     * @throws IllegalEmptyCellException
     * @throws IllegalVBomYearException
     * @throws InvalidVirtualMachineException
     */
    public ScopedVbomList loadVbomParamsFromSheet(int numOfYears) throws IllegalCellFormatException,
            IllegalEmptyCellException, IllegalVBomYearException, InvalidVirtualMachineException {

        int totalRows = sheet.getPhysicalNumberOfRows();
        ScopedVbomList scopedVbomList = new ScopedVbomList();
        scopedVbomList.setSheetName(sheetName);
        logger.debug("Total rows: " + totalRows + ", Worksheet name: " + sheetName);

        for (int i = config.getInt("firstDataRowIndex"); i < totalRows; i++) {
            int firstYearFirstColumn = config.getInt("firstYear.firstColumn");
            Row row = sheet.getRow(i);

            if (row == null || !rowExists(row, firstYearFirstColumn - 1))
                continue;

            VBom vBom = new VBom();
            vBom.setRowNumber(i + 1);

            // fill all fields of vBom except "vBomYearList"
            readCommonParam(row, vBom);

            int columnsPerYear = config.getInt("numberOfColumnsPerYear");


            for (int y = 0; y < numOfYears; y++) {

                int startingColumn = firstYearFirstColumn + (y * columnsPerYear);
                VBomYear vbyr = new VBomYear();

                // set Year Name
                Row tempRow = sheet.getRow(config.getInt("rowIndexOfYearNames"));
                Cell tempCell = tempRow.getCell(startingColumn);
                vbyr.setYearName(tempCell.toString().trim());

                if (isEmptyYear(row, startingColumn, vbyr.getYearName())) {
                    vbyr.setYearEmpty(true);
                    vBom.getvBomYearList().add(vbyr);
                    continue; // Params of this year will have their default "zero values"
                }

                readYearValue(row, vbyr, startingColumn, null);
                vBom.getvBomYearList().add(vbyr);
            }

            if (countVbYearsInVbom(vBom, numOfYears) == 0)
                logger.debug("At row " + vBom.getRowNumber() + ": all vbom years are empty...");
                //throw new IllegalVBomYearException("At row " + vBom.getRowNumber() + ": There must be at least one non-empty year!");
            else
                scopedVbomList.getvBomList().add(vBom);
        }
        return scopedVbomList;
    }


    private void readCommonParam(Row row, VBom vBom) throws IllegalCellFormatException, IllegalEmptyCellException, InvalidVirtualMachineException {
        int columnIndex = 0;

        try {
            // VNF Name
            columnIndex++;
            vBom.setVnfName(Util.readStringNoSpaceFromCell(row, 0, VBomConstants.VNF_NAME));

            // Non generalizzato perche in questo caso si controlla che i valori letti non siano duplicati
            // VM Type Name
            columnIndex++;
            Cell cella = row.getCell(1);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                if (!cella.toString().trim().contains(" ")) {
                    vBom.setVmTypeName(cella.getStringCellValue().trim());

                    // check for duplicate <vnfName.vmTypeName> and populate the VM set
                    String vnfDotVmTYpe = vBom.getVnfName() + "." + vBom.getVmTypeName();
                    if (virtualMachineSet.contains(vnfDotVmTYpe))
                        throw new InvalidVirtualMachineException("Duplicate virtual machine found in row " + vBom.getRowNumber() + " - \"" + vnfDotVmTYpe + "\" already exists.");
                    else
                        virtualMachineSet.add(vnfDotVmTYpe);
                } else
                    throw new IllegalCellFormatException("Illegal cell format found at row " + (row.getRowNum() + 1) + " column " + 2 +
                            " (VM Type Name). Make sure the value does not contain any spaces.");
            } else {
                throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + 2 + " (VM Type Name).");
            }

            // Self Constraints
            columnIndex++;
            cella = row.getCell(2);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                if (cella.getStringCellValue().equalsIgnoreCase("AFF"))
                    vBom.setSelfConstraint(Affinity.AFF);
                else if (cella.getStringCellValue().equalsIgnoreCase("AAF"))
                    vBom.setSelfConstraint(Affinity.AAF);
                else if (cella.getStringCellValue().equalsIgnoreCase("AMS"))
                    vBom.setSelfConstraint(Affinity.AMS);
                else
                    throw new IllegalCellFormatException("Illegal cell value found at row " + (row.getRowNum() + 1) + " column " + 3 +
                            " (Self Constraints). A non-empty cell can only have AAF, AFF or AMS as its value");
            } else
                vBom.setSelfConstraint(null);


            // External Constraints
            columnIndex++;
            cella = row.getCell(3);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                String externalConstraint = cella.getStringCellValue();
                try {
                    Map<String, List<String>> mappa = parseAffinityConstraints(externalConstraint);
                    vBom.setExternalConstraintAffinity(mappa.get("affinity"));
                    vBom.setExternalConstraintAntiAffinity(mappa.get("antiaffinity"));
                } catch (IllegalCellFormatException e) {
                    throw new IllegalCellFormatException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + 4 +
                            " (External Contraints). A non-empty cell can only have AAF{...} and/or AFF{...} as its value");
                }
            } // else, the lists are empty


            columnIndex++;
            columnIndex++;
            // High throughput vSwitch resources
            vBom.setHighThroughputVswitchResources(Util.readNumberFromCell(row, 5, 0, VBomConstants.HIGH_THROUGHPUT));

            columnIndex++;
            vBom.setBlockSize(Util.readBlockSizeFromCell(row, 6, VBomConstants.BLOCK_SIZE));
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + columnIndex + ": " + e.getMessage());
            throw e;
        }

        return; // end of Method
    }

    public void validateVbomForVm(List<VBom> vBomlist) throws InvalidVirtualMachineException {
        // check if a <vnfName.vmTypeName> of AFF/AAF list exists in the VM set
        List<String> strList;
        for (VBom vb : vBomlist) {
            // merging the two lists AAF & AFF
            strList = ListUtils.union(vb.getExternalConstraintAntiAffinity(), vb.getExternalConstraintAffinity());

            if (strList.size() == 0) continue;

            for (String str : strList) {
                if (!virtualMachineSet.contains(str.trim())) {
                    throw new InvalidVirtualMachineException("In row " + vb.getRowNumber() + ": virtual machine " + str.trim() + " does not exist.");
                }
            }
        }
        return; // the VMs in AFF & AFF lists are valid
    }

    private int countVbYearsInVbom(VBom vBom, int numOfYears) {
        int i, vbYearCount = 0;
        for (i = 0; i < numOfYears; i++) {
            VBomYear vbYear = vBom.getvBomYearList().get(i);
            if (!vbYear.isYearEmpty())
                vbYearCount++;
        }
        return vbYearCount;
    }


}

