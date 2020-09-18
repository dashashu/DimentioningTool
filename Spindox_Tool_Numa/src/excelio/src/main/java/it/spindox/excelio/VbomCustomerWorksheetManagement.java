package excelio.src.main.java.it.spindox.excelio;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.enumeration.Affinity;
import commons.src.main.java.it.spindox.model.vbom.*;
import commons.src.main.java.it.spindox.vfexception.excelio.*;

import org.apache.commons.collections4.ListUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

/**
 * Created by Ashraf on September 2017.
 */
public class VbomCustomerWorksheetManagement extends VbomWorksheetManagement {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(VbomCustomerWorksheetManagement.class);
    private Set<String> virtualMachineSet;  // will be used to check and ensure that the virtual machines in vBOM are unique

    public VbomCustomerWorksheetManagement(Sheet sheet, String sheetName) {
        super(sheet, sheetName);
        this.virtualMachineSet = new HashSet<>();
        this.config = ConfigurationManagement.getVbomCustomerConfiguration();
    }

    /**
     * @param numOfYears total number of years found in the sheet
     * @return a "ScopedVbomCustomerList" object containing the sheet name and a list of Customer vBOMs
     * @throws IllegalCellFormatException
     * @throws IllegalEmptyCellException
     * @throws IllegalVBomYearException
     * @throws InvalidVirtualMachineException
     */
    public ScopedVbomCustomerList loadVbomCustomerParamsFromSheet(int numOfYears, InputConfiguration inputConfig) throws IllegalCellFormatException,
            IllegalEmptyCellException, IllegalVBomYearException, InvalidVirtualMachineException {

        int totalRows = sheet.getPhysicalNumberOfRows();
        ScopedVbomCustomerList scopedVbomCustomerList = new ScopedVbomCustomerList();
        scopedVbomCustomerList.setSheetName(sheetName);
        logger.debug("Total rows: " + totalRows + ", Worksheet name: " + sheetName);
        
        Row siteRow = sheet.getRow(config.getInt("siteRowIndex"));
        Cell siteCell = siteRow.getCell(config.getInt("siteColumnIndex"));
        String defaultSiteName = siteCell.toString().trim();

        for (int i = config.getInt("firstDataRowIndex"); i < totalRows; i++) {
            int firstYearFirstColumn = config.getInt("firstYear.firstColumn");
            Row row = sheet.getRow(i);

            if (row == null || !rowExists(row, firstYearFirstColumn - 1))
                continue;

            VBom vBomCustomer = new VBom();
            vBomCustomer.setRowNumber(i + 1);

            // fill all fields of vBomCustomer except "vBomYearList"
            readCommonParam(row, vBomCustomer, inputConfig);

            int columnsPerYear = config.getInt("numberOfColumnsPerYear");

            for (int y = 0; y < numOfYears; y++) {

                int startingColumn = firstYearFirstColumn + (y * columnsPerYear);
                VBomYear vbyr = new VBomYear();

                // set Year Name
                Row tempRow = sheet.getRow(config.getInt("rowIndexOfYearNames"));
                Cell tempCell = tempRow.getCell(startingColumn);
                vbyr.setYearName(tempCell.toString().trim());

                if (isEmptyYear(row, startingColumn+1, vbyr.getYearName())) {
                    vbyr.setYearEmpty(true);
                    vBomCustomer.getvBomYearList().add(vbyr);
                    continue; // Params of this year will have their default "zero values"
                }

                readYearValue(row, vbyr, startingColumn, defaultSiteName);
                vBomCustomer.getvBomYearList().add(vbyr);
            }

            if (countVbYearsInVbomCustomer(vBomCustomer, numOfYears) == 0)
                logger.debug("At row " + vBomCustomer.getRowNumber() + ": all vbomCustomer years are empty...");
            else
                scopedVbomCustomerList.getvBomCustomerList().add(vBomCustomer);
        }
        return scopedVbomCustomerList;
    }


    private void readCommonParam(Row row, VBom vBomCustomer, InputConfiguration inputConfig) throws IllegalCellFormatException, IllegalEmptyCellException, InvalidVirtualMachineException {
        int columnIndex = 0;

        try {
            // VNF Name
            columnIndex++;
            vBomCustomer.setVnfName(Util.readStringNoSpaceFromCell(row, 0, VBomConstants.VNF_NAME));

            // VM Type Name
            columnIndex++;
            Cell cella = row.getCell(1);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                if (!cella.toString().trim().contains(" ")) {
                    vBomCustomer.setVmTypeName(cella.getStringCellValue().trim());

                    // check for duplicate <vnfName.vmTypeName> and populate the VM set
                    String vnfDotVmTYpe = vBomCustomer.getVnfName() + "." + vBomCustomer.getVmTypeName();
                    if (virtualMachineSet.contains(vnfDotVmTYpe))
                        throw new InvalidVirtualMachineException("Duplicate virtual machine found in row " + vBomCustomer.getRowNumber() + " - \"" + vnfDotVmTYpe + "\" already exists.");
                    else
                        virtualMachineSet.add(vnfDotVmTYpe);
                } else
                    throw new IllegalCellFormatException("Illegal cell format found at row " + (row.getRowNum() + 1) + " column " + 2 +
                            " (VM Type Name). Make sure the value does not contain any spaces.");
            } else {
                throw new IllegalEmptyCellException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + 2 + " (VM Type Name).");
            }

            // AFFINITY RULES - INTRA VNF
            columnIndex++;
            cella = row.getCell(2);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                if (cella.getStringCellValue().equalsIgnoreCase("AFF"))
                    vBomCustomer.setSelfConstraint(Affinity.AFF);
                else if (cella.getStringCellValue().equalsIgnoreCase("AAF"))
                    vBomCustomer.setSelfConstraint(Affinity.AAF);
                else if (cella.getStringCellValue().equalsIgnoreCase("AMS"))
                    vBomCustomer.setSelfConstraint(Affinity.AMS);
                else
                    throw new IllegalCellFormatException("Illegal cell value found at row " + (row.getRowNum() + 1) + " column " + 3 +
                            " (Self Constraints). A non-empty cell can only have AAF, AFF or AMS as its value");
            } else
                vBomCustomer.setSelfConstraint(null);

            // AFFINITY RULES - INTER VNF
            columnIndex++;
            cella = row.getCell(3);
            if (cella != null && !cella.toString().equalsIgnoreCase("")) {
                String externalConstraint = cella.getStringCellValue();
                try {
                    Map<String, List<String>> mappa = parseAffinityConstraints(externalConstraint);
                    vBomCustomer.setExternalConstraintAffinity(mappa.get("affinity"));
                    vBomCustomer.setExternalConstraintAntiAffinity(mappa.get("antiaffinity"));
                } catch (IllegalCellFormatException e) {
                    throw new IllegalCellFormatException("Illegal empty cell found at row " + (row.getRowNum() + 1) + " column " + 4 +
                            " (External Contraints). A non-empty cell can only have AAF{...} and/or AFF{...} as its value");
                }
            } // else, the lists are empty

            vBomCustomer.setVmWorkloadType(Util.readMandatoryStringFromCell(row, 4, VBomConstants.VM_WORKLOAD_TYPE));
            
            columnIndex++;
            
            vBomCustomer.setHighThroughputVswitchResources(Util.readNumberFromCell(row, 5, inputConfig.getDefaultHighThroughput(), VBomConstants.HIGH_THROUGHPUT));

            columnIndex++;
            vBomCustomer.setBlockSize(Util.readNumberFromCell(row, 6, inputConfig.getDefaultBlockSize(), VBomConstants.BLOCK_SIZE));
        } catch (Exception e) {
            logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + columnIndex + ": " + e.getMessage());
            throw e;
        }

        return; // end of Method
    }
    

    public void validateVbomCustomerForVm(List<VBom> vBomCustomerList) throws InvalidVirtualMachineException {
        // check if a <vnfName.vmTypeName> of AFF/AAF list exists in the VM set
        List<String> strList;
        for (VBom vbc : vBomCustomerList) {
            // merging the two lists AAF & AFF
            strList = ListUtils.union(vbc.getExternalConstraintAntiAffinity(), vbc.getExternalConstraintAffinity());

            if (strList.size() == 0) continue;

            for (String str : strList) {
                if (!virtualMachineSet.contains(str.trim())) {
                    throw new InvalidVirtualMachineException("In row " + vbc.getRowNumber() + ": virtual machine " + str.trim() + " does not exist.");
                }
            }
        }
        return; // the VMs in AFF & AFF lists are valid
    }


    private int countVbYearsInVbomCustomer(VBom vBomCustomer, int numOfYears) {
        int i, vbYearCount = 0;
        for (i = 0; i < numOfYears; i++) {
            VBomYear vbYear = vBomCustomer.getvBomYearList().get(i);
            if (!vbYear.isYearEmpty())
                vbYearCount++;
        }
        return vbYearCount;
    }
}

