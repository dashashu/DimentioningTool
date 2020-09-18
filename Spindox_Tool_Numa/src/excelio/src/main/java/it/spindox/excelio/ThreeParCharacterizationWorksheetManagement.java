package excelio.src.main.java.it.spindox.excelio;

import java.util.ArrayList;
import java.util.List;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import excelio.src.main.java.it.spindox.excelio.util.Util;
import commons.src.main.java.it.spindox.model.characterizations.ThreeParCharConstants;
import commons.src.main.java.it.spindox.model.characterizations.ThreeParCharacterization;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalCellFormatException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalEmptyCellException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ThreeParCharacterizationWorksheetManagement extends WorksheetManagement {
    //private static final Logger logger = LogManager.getLogger(ThreeParCharacterizationWorksheetManagement.class);
    final static Logger logger = Logger.getLogger(ThreeParCharacterizationWorksheetManagement.class);

    public ThreeParCharacterizationWorksheetManagement(Sheet sheet, String sheetName) {
        super(sheet, sheetName);
        this.config = ConfigurationManagement.getThreeParCharConfiguration();
    }

    public List<ThreeParCharacterization> loadThreeParCharFromSheet() throws IllegalCellFormatException,
            IllegalEmptyCellException, UnexpectedSituationOccurredException {
        List<ThreeParCharacterization> returnList = new ArrayList<ThreeParCharacterization>();

        int totalRows = sheet.getPhysicalNumberOfRows();
        logger.debug("Total rows: " + totalRows + ", Worksheet name: " + sheetName);

        for (int i = config.getInt("threeParCharFirstDataRowIndex"); i < totalRows; i++) {
            Row row = sheet.getRow(i);

            ThreeParCharacterization tpc = new ThreeParCharacterization();

            String[] paramsList = ((String) config.getProperty("threeParCharParams")).split(",");
            int index = 0;

            try {
                for (; index < paramsList.length; index++) {
                    switch (paramsList[index]) {
                        case "":
                            break;
                        case ThreeParCharConstants.THREEPAR_MODEL:
                            tpc.setThreeParModel(Util.readMandatoryStringFromCell(row, index, ThreeParCharConstants.THREEPAR_MODEL));
                            break;
                        case ThreeParCharConstants.ID:
                            tpc.setId(Util.readNumberFromCellMajorThanZero(row, index, ThreeParCharConstants.ID));
                            break;
                        case ThreeParCharConstants.RAID_LEVEL:
                            tpc.setRaidLevel(Util.readMandatoryStringFromCell(row, index, ThreeParCharConstants.RAID_LEVEL));
                            break;
                        case ThreeParCharConstants.DISK_TYPE:
                            tpc.setDiskType(Util.readDiskTypeFromCell(row, index, ThreeParCharConstants.DISK_TYPE));
                            break;
                        case ThreeParCharConstants.DISK_QUANTITY:
                            tpc.setDiskQuantity(Util.readNumberFromCell(row, index, 0, ThreeParCharConstants.DISK_QUANTITY));
                            break;
                        case ThreeParCharConstants.USABLE_CAPACITY:
                            tpc.setUsableCapacity(Util.readFloatNumberFromCell(row, index, 0.0, ThreeParCharConstants.USABLE_CAPACITY));
                            break;
                        case ThreeParCharConstants.BLOCK_SIZE:
                            tpc.setBlockSize(Util.readNumberFromCell(row, index, 0, ThreeParCharConstants.BLOCK_SIZE));
                            break;
                        case ThreeParCharConstants.READ_WRITE:
                            tpc.setRead(Util.readIntPartOfString(row, index, ThreeParCharConstants.BLOCK_SIZE, "left"));
                            tpc.setWrite(Util.readIntPartOfString(row, index, ThreeParCharConstants.BLOCK_SIZE, "right"));
                            break;
                        case ThreeParCharConstants.IOPS_MAX:
                            tpc.setIopsMax((double) Util.readFloatNumberFromCell(row, index, 0.0D, ThreeParCharConstants.IOPS_MAX));
                            break;
                        case ThreeParCharConstants.IOPS_90:
                            tpc.setIops90((double) Util.readFloatNumberFromCell(row, index, 0.0D, ThreeParCharConstants.IOPS_90));
                            break;
                    }
                }

                returnList.add(tpc);

            } catch (Exception e) {
                logger.error("Unexpected error found at row " + (row.getRowNum() + 1) + " column " + (index + 1) + ": " + e.getMessage());
                throw e;
            }
        }

        return returnList;
    }
}
