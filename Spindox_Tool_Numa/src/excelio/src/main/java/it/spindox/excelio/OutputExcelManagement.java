package excelio.src.main.java.it.spindox.excelio;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Color;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import excelio.src.main.java.it.spindox.excelio.models.VmBlade;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.ExcelOutputConstants;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.OutputBlades;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.OutputStorageEnclosure;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.SummaryOutput;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.TotalOutput;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.BladeCluster;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.BobbosCluster;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.OutputTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.OutputTableSpecific;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.SiteDetails;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.SiteDetailsTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.SiteTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TableStorageEnclosure;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TetrisBlade;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TetrisYear;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.TotalOutputCluster;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.YearSummary;


/**
 * Created by Ashraf Uz Zaman on 10/11/2017.
 */
public class OutputExcelManagement {

	//private static final Logger logger = LogManager.getLogger(OutputExcelManagement.class);
	final static Logger logger = Logger.getLogger(OutputExcelManagement.class);
	public static XSSFWorkbook outputWorkbook;
	public static String filename;
	static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	static Date date = new Date();
	static String datestring =dateFormat.format(date);
	//System.out.println(dateFormat.format(date)); //2016/11/16 12:08:43
	
	public OutputExcelManagement() {
		
		outputWorkbook = new XSSFWorkbook();
		  filename = "C://Users//DashA2//Desktop//Spindox_Tool_release_v3.1.0//Dimensioning Tool Release V3.1.1//Output.xlsx";		
		  //filename = "./Output.xlsx";
	}

	/**
	 * Write the outputWorkbook in a .xlsx file. Intended to be called as the
	 * "last method" regarding OutputExcelManagement operations.
	 *
	 * @throws IOException
	 */
	
    public static void writeWorkbookToFile() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(filename);
        outputWorkbook.write(fileOut);
        logger.info("successfully saved file output.....");
        fileOut.close();
    }	

	
	/**
	 * Add "Output Blades" sheet to the workbook.
	 */
	public void createOutPutBladesSheet(OutputBlades outputBlades, Map<Integer, String> yearsOrder,InputConfiguration inputConfig) {
		Sheet sheet = outputWorkbook.createSheet("Output Blades");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		// observation : there are 3 types of tables
		List<String> titles_type1, titles_type2, titles_type3;
		List<List<Object>> tableDataBlade;
		List<Object> tempRow; // will be used to add rows to tableData

		// titles/names of the years, as found in vBOM (e.g. FY1, FY2,... or
		// 2017, 2018, ...)
		List<String> yearNames = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}

		// constructing titles for table - Type 1
		titles_type1 = new ArrayList<>();
		titles_type1.add("Site");
		titles_type1.add("ClusterName");
		titles_type1.add("BladeType");
		titles_type1.addAll(yearNames);

		// constructing titles for table - Type 2
		titles_type2 = new ArrayList<>();
		titles_type2.add("Site");
		titles_type2.add("BladeType");
		titles_type2.add("");
		titles_type2.addAll(yearNames);

		// constructing titles for table - Type 3
		titles_type3 = new ArrayList<>();
		titles_type3.add("Site");
		titles_type3.add("");
		titles_type3.add("");
		titles_type3.addAll(yearNames);

		// observation : all tables have the same number of columns. lower
		// tables have more empty columns, but the total number is the same.
		int totalTableColumns = titles_type1.size();

		/**
		 * 1. Table "Cumulative n. blades per cluster" (Type 1)
		 */
		tableDataBlade = new ArrayList<>();
		List<BladeCluster> cumulativeBladeClusters = outputBlades.getCumulativeBladeClusters();

		for (int i = 0; i < cumulativeBladeClusters.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeBladeClusters.get(i).getSite()); // site
			tempRow.add(cumulativeBladeClusters.get(i).getClusterName()); // clusterName
			tempRow.add(cumulativeBladeClusters.get(i).getComponentType()); // bladeType
			if (cumulativeBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation")) {
				for (Map.Entry<String, Integer> x : cumulativeBladeClusters.get(i).getValueForYears().entrySet())
					if (x != null)
						tempRow.add(x.getValue());
			} else {
				for (String yr : yearNames) {
					// for "cumulative" tables: if current years's value is
					// null, use the value of the previous year
					if (cumulativeBladeClusters.get(i).getValueForYears().get(yr) == null) {
						int currentYearIndex = yearNames.indexOf(yr);
						if (currentYearIndex == 0) { // this was the first
														// element of yearNames,
														// so there is no
														// "previous year" =>
														// put 0
							tempRow.add(0);
						} else {
							String prevYear = yearNames.get(currentYearIndex - 1);
							System.out.println(prevYear);
							
							//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
							int yearvalue = cumulativeBladeClusters.get(i).getValueForYears().get(prevYear) == null ? 0
									: cumulativeBladeClusters.get(i).getValueForYears().get(prevYear);
							tempRow.add(yearvalue);
									//(yearvalue)+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100f)*(yearvalue))));
						}
					} else { // no null => business as usual
						//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
						int yearvalue = cumulativeBladeClusters.get(i).getValueForYears().get(yr) == null ? 0
								: cumulativeBladeClusters.get(i).getValueForYears().get(yr);
							tempRow.add(yearvalue);
									//)+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100f)*(yearvalue))));
					}
				}
			}
			tableDataBlade.add(tempRow);
		}
		// printing table in excel sheet
		int rowIndex = 1; // print from the second row of the sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_BLADES_CLUSTER, tableDataBlade,
				titles_type1, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 2. Table "Delta n. blades per cluster" (Type 1)
		 */
		tableDataBlade = new ArrayList<>();
		List<BladeCluster> deltaBladeClusters = outputBlades.getDeltaBladeClusters();

		for (int i = 0; i < deltaBladeClusters.size(); i++) {
			if (deltaBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation"))
				continue;

			tempRow = new ArrayList<>();
			tempRow.add(deltaBladeClusters.get(i).getSite()); // site
			tempRow.add(deltaBladeClusters.get(i).getClusterName()); // clusterName
			tempRow.add(deltaBladeClusters.get(i).getComponentType()); // bladeType

			for (String yr : yearNames) {
				//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
				
				int yearvalue = deltaBladeClusters.get(i).getValueForYears().get(yr) == null ? 0
						: deltaBladeClusters.get(i).getValueForYears().get(yr);
				tempRow.add(yearvalue);
						//+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100)*yearvalue)));
			}
			tableDataBlade.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.DELTA_BLADES_CLUSTER, tableDataBlade,
				titles_type1, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 3. Table "Cumulative n. blades per type of blade" (Type 2)
		 */
		tableDataBlade = new ArrayList<>();
		List<OutputTableSpecific> cumulativeBladesType = outputBlades.getCumulativeBladesType();

		for (int i = 0; i < cumulativeBladesType.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeBladesType.get(i).getSite()); // site
			tempRow.add(cumulativeBladesType.get(i).getComponentType()); // bladeType
			tempRow.add(""); // empty column
			if (cumulativeBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation")) {
				Integer yearvalue = null;
				for (Map.Entry<String, Integer> x : cumulativeBladeClusters.get(i).getValueForYears().entrySet())
					if (x != null)
						tempRow.add(x.getValue());
				//Ashutosh: 28/08/2019:  Not adding new flag : BladeBufferPercentage"as nothing was mentioned in the requirement for foundation"
						
			} else {
				for (String yr : yearNames) {
					// for "cumulative" tables: if current years's value is
					// null, use the value of the previous year
					if (cumulativeBladesType.get(i).getValueForYears().get(yr) == null) {
						int currentYearIndex = yearNames.indexOf(yr);
						if (currentYearIndex == 0) { // this was the first
														// element of yearNames,
														// so there is no
														// "previous year" =>
														// put 0
							tempRow.add(0);
						} else {
							String prevYear = yearNames.get(currentYearIndex - 1);
							int yearvalue = cumulativeBladesType.get(i).getValueForYears().get(prevYear) == null ? 0
									: cumulativeBladesType.get(i).getValueForYears().get(prevYear);
							tempRow.add(yearvalue);
									//+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100)*yearvalue)));
						}
					} else { // no null => business as usual
						int yearvalue = cumulativeBladesType.get(i).getValueForYears().get(yr) == null ? 0
								: cumulativeBladesType.get(i).getValueForYears().get(yr);
						
						//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
						tempRow.add(yearvalue);
								//+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100)*yearvalue)));
					}
				}
			}

			tableDataBlade.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_BLADES_TYPEBLADE, tableDataBlade,
				titles_type2, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 4. Table "Delta n. blades per type of blade" (Type 2)
		 */
		tableDataBlade = new ArrayList<>();
		List<OutputTableSpecific> deltaBladeType = outputBlades.getDeltaBladeType();

		for (int i = 0; i < deltaBladeType.size(); i++) {
			if (deltaBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation"))
				continue;

			tempRow = new ArrayList<>();
			tempRow.add(deltaBladeType.get(i).getSite()); // site
			tempRow.add(deltaBladeType.get(i).getComponentType()); // bladeType
			tempRow.add(""); // empty column
			for (String yr : yearNames) {
				//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
				int yearvalue = deltaBladeType.get(i).getValueForYears().get(yr) == null ? 0
						: deltaBladeType.get(i).getValueForYears().get(yr);
				tempRow.add(yearvalue);
						//+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100)*yearvalue)));
			}
			tableDataBlade.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.DELTA_BLADES_TYPEBLADE, tableDataBlade,
				titles_type2, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 5. Table "Cumulative total n. blades" (Type 3)
		 */
		tableDataBlade = new ArrayList<>();
		List<OutputTable> cumulativeBladeTotals = outputBlades.getCumulativeBladeTotals();
		Map<String, Integer> totalCumulativeBladesMap = new HashMap<>();

		for (int i = 0; i < cumulativeBladeTotals.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeBladeTotals.get(i).getSite()); // site
			tempRow.add(""); // empty column
			tempRow.add(""); // empty column
			if (cumulativeBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation")) {
				for (Map.Entry<String, Integer> x : cumulativeBladeClusters.get(i).getValueForYears().entrySet())
					if (x != null)
						tempRow.add(x.getValue());
			} else {
				for (String yr : yearNames) {
					int temp = cumulativeBladeTotals.get(i).getValueForYears().get(yr) == null ? 0
							: cumulativeBladeTotals.get(i).getValueForYears().get(yr);
					//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
					int blade = (temp);
							//+((int) Math.ceil((inputConfig.getBladeBufferPercentage()/100)*temp)));
					tempRow.add(blade);
					Integer previousValue1 = totalCumulativeBladesMap.get(yr);
					if (previousValue1 == null)
						previousValue1 = 0;
					totalCumulativeBladesMap.put(yr, previousValue1 + blade);
				}
			}
			tableDataBlade.add(tempRow);
		}

		// adding total row at bottom of the table
		tempRow = new ArrayList<>();
		tempRow.add("TOTAL");
		tempRow.add(""); // empty column
		tempRow.add("");
		for (String yr : yearNames) {
			tempRow.add(totalCumulativeBladesMap.get(yr) == null ? 0 : totalCumulativeBladesMap.get(yr));
		}
		tableDataBlade.add(tempRow);

		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_TOTAL_BLADES, tableDataBlade,
				titles_type3, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 6. Table "Delta total n. blades" (Type 3)
		 */
		tableDataBlade = new ArrayList<>();
		List<OutputTable> deltaBladeTotals = outputBlades.getDeltaBladeTotals();
		Map<String, Integer> totalDeltaBladesMap = new HashMap<>();

		for (int i = 0; i < deltaBladeTotals.size(); i++) {
			if (deltaBladeClusters.get(i).getClusterName().equalsIgnoreCase("Foundation"))
				continue;

			tempRow = new ArrayList<>();
			tempRow.add(deltaBladeTotals.get(i).getSite()); // site
			tempRow.add(""); // empty column
			tempRow.add(""); // empty column
			for (String yr : yearNames) {
				int temp = deltaBladeTotals.get(i).getValueForYears().get(yr) == null ? 0
						: deltaBladeTotals.get(i).getValueForYears().get(yr);
				tempRow.add(temp);
				Integer previousValue2 = totalDeltaBladesMap.get(yr);
				if (previousValue2 == null)
					previousValue2 = 0;
				totalDeltaBladesMap.put(yr, previousValue2 + temp);
			}
			tableDataBlade.add(tempRow);
		}
		// adding total row at bottom of the table
		tempRow = new ArrayList<>();
		tempRow.add("TOTAL");
		tempRow.add(""); // empty column
		tempRow.add("");
		for (String yr : yearNames) {
			tempRow.add(totalDeltaBladesMap.get(yr));
		}
		tableDataBlade.add(tempRow);

		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.DELTA_TOTAL_BLADES, tableDataBlade,
				titles_type3, totalTableColumns);
		sheet.createRow(rowIndex++); // an empty row between tables

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		// should be done AFTER the values have been put into cells
		sheet.setColumnWidth(0, 15 * 256); // first column : 15 characters wide
		for (int q = 1; q < totalTableColumns; q++) {
			sheet.autoSizeColumn(q);
		}
	}

	/**
	 * Add "Output BOSBOB count details" sheet to the workbook.
	 */
	public void createOutPutNetworkSheet(OutputBlades outputBlades, Map<Integer, String> yearsOrder,
			InputConfiguration inputConfig) {
		Sheet sheet = outputWorkbook.createSheet("Network & Port details ");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(false);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		
		
		/*String opcoName = PathModel.getOpcoName();*/
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		/*cell.setCellValue("OPCO:" + "[" + opcoName.toUpperCase() + "] ");*/
		// observation : there are 3 types of tables
		List<String> titles_type1, titles_type2, titles_type3;
		List<List<Object>> tableDataBlade;
		List<Object> tempRow; // will be used to add rows to tableData

		// titles/names of the years, as found in vBOM (e.g. FY1, FY2,... or
		// 2017, 2018, ...)
		List<String> yearNames = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}
		titles_type1 = new ArrayList<>();

		titles_type1.add("Site");
		titles_type1.add("BladeCount(Assumed for model identification)");
		titles_type1.add("ModelName");
		titles_type1.add("Network");
		titles_type1.add("SAN");
		titles_type1.add("Storage Array");

		// constructing titles for table - Type 2
		titles_type2 = new ArrayList<>();
		titles_type2.add("Site");
		titles_type2.add("Blade Type");
		titles_type2.addAll(yearNames);

		// constructing titles for table - Type 3
		titles_type3 = new ArrayList<>();
		titles_type3.add("Site");
		titles_type3.add("Type of Port");
		titles_type3.addAll(yearNames);

		// observation : all tables have the same number of columns. lower
		// tables have more empty columns, but the total number is the same.
		int totalTableColumns = titles_type1.size();
		int totalTableColumns2 = titles_type2.size();
		int totalTableColumns3 = titles_type3.size();

		/**
		 * 1. Table "Network details as per site" (Type 1)
		 */
		tableDataBlade = new ArrayList<>();
		List<BobbosCluster> cumulativeBladeClusters = outputBlades.getTable1List();

		for (int i = 0; i < cumulativeBladeClusters.size(); i++) {
			tempRow = new ArrayList<>();

			tempRow.add(cumulativeBladeClusters.get(i).getSite());
			tempRow.add(cumulativeBladeClusters.get(i).getTotalBladeCount());
			tempRow.add(cumulativeBladeClusters.get(i).getModelName());
			// tempRow.add(cumulativeBladeClusters.get(i).getBobBobtype());
			tempRow.add(cumulativeBladeClusters.get(i).getNetwork());
			tempRow.add(cumulativeBladeClusters.get(i).getSAN());
			tempRow.add(cumulativeBladeClusters.get(i).getStorageArray());

			tableDataBlade.add(tempRow);
		}
		int rowIndex = 1; // print from the second row of the sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.Network_Details, tableDataBlade,
				titles_type1, totalTableColumns);
		for (int i = ExcelOutputConstants.Network_TABLE_STARTING_COL_INDEX; i <= titles_type1.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		sheet.createRow(rowIndex++); // an empty row between tables

		/**
		 * 2. Table "enclouser table"
		 */
		tableDataBlade = new ArrayList<>();
		List<BobbosCluster> enclouserNoTableNetwork = outputBlades.getTableD2List();
		if (enclouserNoTableNetwork != null) {
			for (int i = 0; i < enclouserNoTableNetwork.size(); i++) {
				tempRow = new ArrayList<>();
				tempRow.add(enclouserNoTableNetwork.get(i).getSite()); // site
				tempRow.add(enclouserNoTableNetwork.get(i).getBladeType());
				for (String yr : yearNames) {
					tempRow.add(enclouserNoTableNetwork.get(i).getValueForYears().get(yr));
				}
				tableDataBlade.add(tempRow);
			}
		}
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.Enclouser_Table, tableDataBlade,
				titles_type2, totalTableColumns2);
		for (int i = ExcelOutputConstants.Network_TABLE_STARTING_COL_INDEX; i <= titles_type2.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		sheet.createRow(rowIndex++); // an empty row between tables
		/**
		 * 3. Table "enclouser table"
		 */
		tableDataBlade = new ArrayList<>();
		List<BobbosCluster> enclosureDeltatable = outputBlades.getTableCumulativeD2List();
		if (enclouserNoTableNetwork != null) {
			for (int i = 0; i < enclosureDeltatable.size(); i++) {
				tempRow = new ArrayList<>();
				tempRow.add(enclosureDeltatable.get(i).getSite()); // site
				tempRow.add(enclosureDeltatable.get(i).getBladeType());
				for (String yr : yearNames) {
					tempRow.add(enclosureDeltatable.get(i).getValueForYears().get(yr));
				
				tableDataBlade.add(tempRow);
			}
		}
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.Cumulative_Enclouser_Table, tableDataBlade,
				titles_type2, totalTableColumns2);
		for (int i = ExcelOutputConstants.Network_TABLE_STARTING_COL_INDEX; i <= titles_type2.size(); i++) {
			sheet.autoSizeColumn(i);
		}
		sheet.createRow(rowIndex++);

		/**
		 * 4. Table "port number count for 40G/10G/8G".
		 */
		tableDataBlade = new ArrayList<>();
		List<String> portName = new ArrayList<String>();
		portName.add("40G");
		portName.add("10G");
		portName.add("8G");
		Map<String, List<Map<String, Map<String, Integer>>>> portNumbers = outputBlades.getTable3List();

		for (int j = 0; j < portName.size(); j++) {
			if (portNumbers.containsKey(portName.get(j))) {
				String headerOfTable = "Site and Year wise Port numbers: " + (portName.get(j));
				List<Map<String, Map<String, Integer>>> mapToReturn = portNumbers.get(portName.get(j));
				List<OutputTable> tableData = loopMethod(mapToReturn);

				for (int i = 0; i < (tableData).size(); i++) {
					for (int k = 0; k < 2; k++) {
						tempRow = new ArrayList<>();
						tempRow.add((tableData).get(i).getSite());
						if (k % 2 == 0) {
							tempRow.add("from 6125 to Aggr. Switch");
						} else if (k % 2 != 0)
							tempRow.add("from Aggr. Switch to 6125");
						for (String yr : yearNames) {
							tempRow.add((tableData).get(i).getValueForYears().get(yr));
						}
						tableDataBlade.add(tempRow);
					}
				}
				rowIndex = insertOutputTable(sheet, rowIndex, headerOfTable, tableDataBlade, titles_type3,
						totalTableColumns3);
				for (int i = 2; i <= titles_type2.size(); i++) {
					sheet.autoSizeColumn(i);
				}
				sheet.createRow(rowIndex++);
				tableDataBlade.clear();
			}}
		}
	}

	public void createNumaPlacementSheet( List<Cluster> clusterList,Map<String, Map<String, Map<String, List<String>>>> clusterSiteYrSocketMap,Map<Integer, String> yearsOrder) {
		// create this sheet only if any blade consist a NUMA VM
		Map<String, Map<String, List<String>>> siteYrSocketMap = new HashMap<String, Map<String, List<String>>>();
		Sheet sheet = outputWorkbook.createSheet("NUMA Blade");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		ArrayList titles_type1 = new ArrayList<>();
		ArrayList subtitles_type = new ArrayList<>();
		List<String> yearNames = new ArrayList<>();
		titles_type1.add("Site");
		
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}
		for(int i = 0;i<yearNames.size();i++) {
			titles_type1.add(yearNames.get(i));
			titles_type1.add("");
		}
		//titles_type1.add(subtitles_type);

		int rowIndex = 1;
		
		String headerOfTable = "";
		ArrayList tableNumaData = new ArrayList<>();
		for (Cluster cluster: clusterList) {
			headerOfTable = "Numa Blade in cluster : " + cluster.getSheetLabel();
			siteYrSocketMap = clusterSiteYrSocketMap.get(cluster.getSheetLabel());//one cluster
			for (Map.Entry<String, Map<String, List<String>>> element0 : siteYrSocketMap.entrySet()) {//one key -value
				
				List<Object> tempRow = new ArrayList<>();
				tempRow.add(element0.getKey());//site
				//List<Object> tempRow1 = new ArrayList<>();
				for(Map.Entry<String, List<String>> element1: element0.getValue().entrySet()) {
					for (String element2 : element1.getValue()) {
					tempRow.add(element2);
				}
				}
				//tempRow.addAll(tempRow1);//SocketLists
				tableNumaData.add(tempRow);
			}
			int totalTableColumns = titles_type1.size();
			rowIndex = insertOutputTable(sheet, rowIndex, headerOfTable, tableNumaData, titles_type1,totalTableColumns);
			for (int k = 2; k <= titles_type1.size(); k++) {
				sheet.autoSizeColumn(k);
			}
			sheet.createRow(rowIndex++);
			tableNumaData.clear();
		}
	}
	
	public List<OutputTable> loopMethod(List<Map<String, Map<String, Integer>>> mapToReturn) {
		ArrayList<OutputTable> objList1 = new ArrayList<OutputTable>();
		for (Map<String, Map<String, Integer>> TableMap : mapToReturn) {
			for (Entry<String, Map<String, Integer>> yrMap40G : TableMap.entrySet()) {
				BobbosCluster oboobj = new BobbosCluster();
				oboobj.setSite(yrMap40G.getKey());
				Map<String, Integer> yrmap = yrMap40G.getValue();
				oboobj.setValueForYears(yrmap);
				objList1.add(oboobj);

			}
		}
		return objList1;
	}

	/**
	 * Insert data in output sheet in tabular format. Compatible with
	 * "Output Blades", "Output Storage-Enclosure" and "Total Output" sheets
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param tableTitle
	 * @param data
	 *            2-D List of data
	 * @param titles
	 *            array of column headers of table
	 * @return
	 */

	private int insertOutputTable(Sheet sheet, int rowIndex, String tableTitle, List<List<Object>> data,
			List<String> titles, int totalTableColumns) {
		Map<String, CellStyle> styles = createStylesForOutputSheets(outputWorkbook);

		// title row
		Row titleRow = sheet.createRow(rowIndex++);
		titleRow.setHeightInPoints(15);
		Cell titleCell = titleRow.createCell(0);
		titleCell.setCellValue(tableTitle);
		titleCell.setCellStyle(styles.get("title"));
		// adding cells, for merging
		for (int x = 1; x < totalTableColumns; x++) {
			Cell titleMergedCell = titleRow.createCell(x);
			titleMergedCell.setCellValue("");
			titleMergedCell.setCellStyle(styles.get("title"));
		}
		sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, totalTableColumns - 1));

		// header row
		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.setHeightInPoints(15);
		Cell headerCell;
		for (int i = 0; i < totalTableColumns; i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titles.get(i));
			headerCell.setCellStyle(styles.get("header"));
		}

		// setting data in table
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.createRow(rowIndex++);
			row.setHeightInPoints(14);
			for (int j = 0; j < totalTableColumns; j++) { // should be
															// data[i].length ==
															// title.length ==
															// totalTableColumns
				Cell cell = row.createCell(j);

				if (data.get(i).size() <= j || data.get(i).get(j) == null) {
					cell.setCellStyle(styles.get("cell"));
					continue;
				} else if (data.get(i).get(j) instanceof String) {
					cell.setCellValue((String) data.get(i).get(j));
					cell.setCellStyle(styles.get("cell"));
				} else {
					cell.setCellValue((Integer) data.get(i).get(j));
					cell.setCellStyle(styles.get("cell"));
				}

				// if there is a last row "TOTAL", make the entries bold
				if ((data.get(i).get(0).equals("TOTAL")) && (i == data.size() - 1)) {
					cell.setCellStyle(styles.get("cellBold"));
				}
			}
		}

		return rowIndex;
	}

	public void createOutPutStorageEnclosureSheet(OutputStorageEnclosure outputStorageEnclosure,
			Map<Integer, String> yearsOrder) {
		Sheet sheet = outputWorkbook.createSheet("Output Storage-Enclosure");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		// observation : there are 2 types of tables
		List<String> titles_type1, titles_type2;
		List<List<Object>> tableDataStorage;
		List<Object> tempRow; // will be used to add rows to tableData

		// titles/names of the years, as found in vBOM (e.g. FY1, FY2,... or
		// 2017, 2018, ...)
		List<String> yearNames = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}

		// constructing titles for table - Type 1
		titles_type1 = new ArrayList<>();
		titles_type1.add("Site");
		titles_type1.add("");
		titles_type1.add("Storage Type");
		titles_type1.add("");
		titles_type1.addAll(yearNames);

		// constructing titles for table - Type 2
		titles_type2 = new ArrayList<>();
		titles_type2.add("Site");
		titles_type2.add("");
		titles_type2.add("Enclosure Type");
		titles_type2.add("");
		titles_type2.addAll(yearNames);

		// observation : all tables have the same number of columns.
		int totalTableColumns = titles_type1.size();

		/**
		 * Table "Cumulative TOTAL storage per site" (Type 1)
		 */
		tableDataStorage = new ArrayList<>();
		List<TableStorageEnclosure> cumulativeStorage = outputStorageEnclosure.getCumulativeStorage();

		for (int i = 0; i < cumulativeStorage.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeStorage.get(i).getSite()); // site
			tempRow.add(cumulativeStorage.get(i).isFoundation() ? "Foundation" : "Initiative");
			tempRow.add(cumulativeStorage.get(i).getComponentType()); // storageType
			tempRow.add(""); // empty column

			if (cumulativeStorage.get(i).isFoundation()) { // for foundation,
															// only the first
															// year will have a
															// value
				tempRow.add(cumulativeStorage.get(i).getValueForYears().get("foundation"));
				for (int k = 1; k < yearNames.size(); k++)
					tempRow.add("");
			} else {
				for (String yr : yearNames) {
					tempRow.add(cumulativeStorage.get(i).getValueForYears().get(yr) == null ? 0
							: cumulativeStorage.get(i).getValueForYears().get(yr));
				}
			}
			tableDataStorage.add(tempRow);
		}
		// printing table in excel sheet
		int rowIndex = 1; // print from the second row of the sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_TOTAL_STORAGE_SITE, tableDataStorage,
				titles_type1, totalTableColumns);
		sheet.createRow(rowIndex++);
		sheet.createRow(rowIndex++); // two empty rows between tables

		/**
		 * Table "Cumulative Delta storage per site" (Type 1)
		 */
		tableDataStorage = new ArrayList<>();
		List<TableStorageEnclosure> deltaStorage = outputStorageEnclosure.getDeltaStorage();

		for (int i = 0; i < deltaStorage.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(deltaStorage.get(i).getSite()); // site
			tempRow.add(deltaStorage.get(i).isFoundation() ? "Foundation" : "Initiative");
			tempRow.add(deltaStorage.get(i).getComponentType()); // storageType
			tempRow.add(""); // empty column

			if (deltaStorage.get(i).isFoundation()) {
				continue;// for foundation, only the first year will have a
							// value
				// tempRow.add(deltaStorage.get(i).getValueForYears().get("foundation"));
				// for (int k = 1; k < yearNames.size(); k++)
				// tempRow.add("");
			} else {
				for (String yr : yearNames) {
					tempRow.add(deltaStorage.get(i).getValueForYears().get(yr) == null ? 0
							: deltaStorage.get(i).getValueForYears().get(yr));
				}
			}
			tableDataStorage.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_DELTA_STORAGE_SITE, tableDataStorage,
				titles_type1, totalTableColumns);
		sheet.createRow(rowIndex++);
		sheet.createRow(rowIndex++); // two empty rows between tables

		/**
		 * Table "Cumulative TOTAL enclosure per site" (Type 2)
		 */
		tableDataStorage = new ArrayList<>();
		List<TableStorageEnclosure> cumulativeEnclosure = outputStorageEnclosure.getCumulativeEnclosure();

		for (int i = 0; i < cumulativeEnclosure.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeEnclosure.get(i).getSite()); // site
			tempRow.add(cumulativeEnclosure.get(i).isFoundation() ? "Foundation" : "Initiative");
			tempRow.add(cumulativeEnclosure.get(i).getComponentType()); // storageType
			tempRow.add(""); // empty column

			if (cumulativeEnclosure.get(i).isFoundation()) { // for foundation,
																// only the
																// first year
																// will have a
																// value
				tempRow.add(cumulativeEnclosure.get(i).getValueForYears().get("foundation"));
				for (int k = 1; k < yearNames.size(); k++)
					tempRow.add("");
			} else {
				for (String yr : yearNames) {
					tempRow.add(cumulativeEnclosure.get(i).getValueForYears().get(yr) == null ? 0
							: cumulativeEnclosure.get(i).getValueForYears().get(yr));
				}
			}
			tableDataStorage.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_TOTAL_ENCLOSURE, tableDataStorage,
				titles_type2, totalTableColumns);
		sheet.createRow(rowIndex++);
		sheet.createRow(rowIndex++); // two empty rows between tables

		/**
		 * Table "Cumulative Delta enclosure per site" (Type 2)
		 */
		tableDataStorage = new ArrayList<>();
		List<TableStorageEnclosure> deltaEnclosure = outputStorageEnclosure.getDeltaEnclosure();

		for (int i = 0; i < deltaEnclosure.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(deltaEnclosure.get(i).getSite()); // site
			tempRow.add(deltaEnclosure.get(i).isFoundation() ? "Foundation" : "Initiative");
			tempRow.add(deltaEnclosure.get(i).getComponentType()); // storageType
			tempRow.add(""); // empty column

			if (deltaEnclosure.get(i).isFoundation()) { // for foundation, only
														// the first year will
														// have a value
				continue;
				// tempRow.add(deltaEnclosure.get(i).getValueForYears().get("foundation"));
				// for (int k = 1; k < yearNames.size(); k++)
				// tempRow.add("");
			} else {
				for (String yr : yearNames) {
					tempRow.add(deltaEnclosure.get(i).getValueForYears().get(yr) == null ? 0
							: deltaEnclosure.get(i).getValueForYears().get(yr));
				}
			}
			tableDataStorage.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, ExcelOutputConstants.CUMU_DELTA_ENCLOSURE, tableDataStorage,
				titles_type2, totalTableColumns);
		sheet.createRow(rowIndex++);
		sheet.createRow(rowIndex++); // two empty rows between tables

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		// should be done AFTER the values have been put into cells
		sheet.setColumnWidth(0, 10 * 256); // first column : 10 characters wide
		sheet.setColumnWidth(3, 15 * 256); // fourth column : 10 characters wide
		for (int i = 1; i < totalTableColumns; i++) {
			if (i != 3) // skipping the column before yearValues
				sheet.autoSizeColumn(i);
		}

	}
	// Method to print the details. print the details in lines per cluster/wh
	// //Ashutosh
	/*
	 * public void createbobbossheet(TotalOutput totalOutput, Map<Integer,
	 * String> yearsOrder) { Sheet sheet =
	 * outputWorkbook.createSheet("BOS/BOB"); PrintSetup printSetup =
	 * sheet.getPrintSetup(); printSetup.setLandscape(true);
	 * sheet.setFitToPage(true); sheet.setHorizontallyCenter(true);
	 * 
	 * }
	 */

	public void createTotalOutputSheet(TotalOutput totalOutput, Map<Integer, String> yearsOrder) {
		Sheet sheet = outputWorkbook.createSheet("Total output");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		// titles/names of the years, as found in vBOM (e.g. FY1, FY2,... or
		// 2017, 2018, ...)
		List<String> yearNames = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}

		// observation : there are 2 types of tables
		List<String> titles_type1, titles_type2;

		// constructing titles for table - Type 1
		titles_type1 = new ArrayList<>();
		titles_type1.add("Site");
		titles_type1.add("Cluster Name");
		titles_type1.addAll(yearNames);

		// constructing titles for table - Type 2
		titles_type2 = new ArrayList<>();
		titles_type2.add("Site");
		titles_type2.add("");
		titles_type2.addAll(yearNames);

		// observation : all tables have the same number of columns.
		int totalTableColumns = titles_type1.size();

		int rowIndex = 1; // print from the second row of the sheet
		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "vCPU", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getCpuTotalOutputCluster(), totalOutput.getCpuDeltaOutputCluster(),
				totalOutput.getCpuTotalOutput(), totalOutput.getCpuDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "vRAM", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getRamTotalOutputCluster(), totalOutput.getRamDeltaOutputCluster(),
				totalOutput.getRamTotalOutput(), totalOutput.getRamDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "Storage", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getStorageTotalOutputCluster(),
				totalOutput.getStorageDeltaOutputCluster(), totalOutput.getStorageTotalOutput(),
				totalOutput.getStorageDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "IOPS", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getIopsTotalOutputCluster(),
				totalOutput.getIopsDeltaOutputCluster(), totalOutput.getIopsTotalOutput(),
				totalOutput.getIopsDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "BW", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getBwTotalOutputCluster(), totalOutput.getBwDeltaOutputCluster(),
				totalOutput.getBwTotalOutput(), totalOutput.getBwDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "NS BW", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getNsTotalOutputCluster(), totalOutput.getNsDeltaOutputCluster(),
				totalOutput.getNsTotalOutput(), totalOutput.getNsDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		rowIndex = prepareAndInsertTotalOutputTables(sheet, rowIndex, "EW BW", totalTableColumns, titles_type1,
				titles_type2, yearNames, totalOutput.getEwTotalOutputCluster(), totalOutput.getEwDeltaOutputCluster(),
				totalOutput.getEwTotalOutput(), totalOutput.getEwDeltaOutput());
		sheet.createRow(rowIndex++); // empty row

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		// should be done AFTER the values have been put into cells
		sheet.setColumnWidth(0, 15 * 256); // 15 characters wide
		for (int i = 1; i < totalTableColumns; i++) {
			sheet.autoSizeColumn(i);
		}

	}

	private int prepareAndInsertTotalOutputTables(Sheet sheet, int rowIndex, String itemName, int totalTableColumns,
			List<String> titles_type1, List<String> titles_type2, List<String> yearNames,
			List<TotalOutputCluster> cumulativeDelta1, List<TotalOutputCluster> cumulativeDelta2,
			List<OutputTable> totCumulativeDelta1, List<OutputTable> totCumulativeDelta2) {
		List<List<Object>> tableDataTotOutput;
		List<Object> tempRow; // will be used to add rows to tableData

		String tableHeading1 = "Cumulative " + itemName + " per cluster";
		String tableHeading2 = "Delta " + itemName + " per cluster";
		String tableHeading3 = "Cumulative total " + itemName;
		String tableHeading4 = "Delta total " + itemName;

		/**
		 * 1. Table "Cumulative AAAA per cluster" (Type 1)
		 */
		tableDataTotOutput = new ArrayList<>();
		for (int i = 0; i < cumulativeDelta1.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeDelta1.get(i).getSite()); // site
			tempRow.add(cumulativeDelta1.get(i).getClusterName()); // cluster
			for (String yr : yearNames) {
				tempRow.add(cumulativeDelta1.get(i).getValueForYears().get(yr) == null ? 0
						: cumulativeDelta1.get(i).getValueForYears().get(yr));
			}
			tableDataTotOutput.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, tableHeading1, tableDataTotOutput, titles_type1,
				totalTableColumns);
		sheet.createRow(rowIndex++); // empty row between tables

		/**
		 * 2. Table "Delta AAAA per cluster" (Type 1)
		 */
		tableDataTotOutput = new ArrayList<>();
		for (int i = 0; i < cumulativeDelta2.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(cumulativeDelta2.get(i).getSite()); // site
			tempRow.add(cumulativeDelta2.get(i).getClusterName()); // cluster
			for (String yr : yearNames) {
				tempRow.add(cumulativeDelta2.get(i).getValueForYears().get(yr) == null ? 0
						: cumulativeDelta2.get(i).getValueForYears().get(yr));
			}
			tableDataTotOutput.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, tableHeading2, tableDataTotOutput, titles_type1,
				totalTableColumns);
		sheet.createRow(rowIndex++); // empty row between tables

		/**
		 * 3. Cumulative total AAAA" (Type 2)
		 */
		tableDataTotOutput = new ArrayList<>();
		for (int i = 0; i < totCumulativeDelta1.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(totCumulativeDelta1.get(i).getSite()); // site
			tempRow.add("");
			for (String yr : yearNames) {
				tempRow.add(totCumulativeDelta1.get(i).getValueForYears().get(yr) == null ? 0
						: totCumulativeDelta1.get(i).getValueForYears().get(yr));
			}
			tableDataTotOutput.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, tableHeading3, tableDataTotOutput, titles_type2,
				totalTableColumns);
		sheet.createRow(rowIndex++); // empty row between tables

		/**
		 * 4. Table "Delta total AAAA" (Type 2)
		 */
		tableDataTotOutput = new ArrayList<>();
		for (int i = 0; i < totCumulativeDelta2.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(totCumulativeDelta2.get(i).getSite()); // site
			tempRow.add("");
			for (String yr : yearNames) {
				tempRow.add(totCumulativeDelta2.get(i).getValueForYears().get(yr) == null ? 0
						: totCumulativeDelta2.get(i).getValueForYears().get(yr));
			}
			tableDataTotOutput.add(tempRow);
		}
		// printing table in excel sheet
		rowIndex = insertOutputTable(sheet, rowIndex, tableHeading4, tableDataTotOutput, titles_type2,
				totalTableColumns);
		sheet.createRow(rowIndex++); // empty row between tables

		return rowIndex;
	}

	public void createSummaryOutPutSheet(SummaryOutput summaryOutput, Map<Integer, String> yearsOrder , InputConfiguration inputConfig) {
		Sheet sheet = outputWorkbook.createSheet("Summary output");
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);

		summaryOutput.countSitesAndClusters(); // Very important - need to call
												// it exactly once before
												// calling getAllSites() or
												// getAllSitesAndClusters()
		// Ashutosh
		/*String opcoName = PathModel.getOpcoName();*/
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		/*cell.setCellValue("OPCO:" + "[" + opcoName.toUpperCase() + "] ");*/

		int rowIndex = ExcelOutputConstants.SUMMARY_TABLE_STARTING_ROW_INDEX;
		Set<String> allSites = summaryOutput.getAllSites();

		List<String> titles_siteTable, titles_siteTableDetailed;

		// titles/names of the years, as found in vBOM (e.g. FY1, FY2,... or
		// 2017, 2018, ...)
		List<String> yearNames = new ArrayList<>();
		for (int i = 1; i < 6; i++) {
			if (yearsOrder.get(i) != null)
				yearNames.add(yearsOrder.get(i));
		}

		// summary table's headings
		titles_siteTable = new ArrayList<>();
		titles_siteTable.add("PLACEHOLDER"); // will be modified every time
												// before printing
		yearNames.forEach(e -> titles_siteTable.add(e));

		// detailed table's headings
		titles_siteTableDetailed = new ArrayList<>();
		titles_siteTableDetailed.add("PLACEHOLDER"); // will be modified every
														// time before printing
		for (int i = 0; i < yearNames.size(); i++) {
			titles_siteTableDetailed.add("cpu");
			titles_siteTableDetailed.add("mem");
			titles_siteTableDetailed.add("txput ns");
			titles_siteTableDetailed.add("txput ew");
			titles_siteTableDetailed.add("txput tot");
			titles_siteTableDetailed.add("iops run");
			titles_siteTableDetailed.add("iops load");
		}

		for (String st : allSites) {
			rowIndex = prepareAndInsertSummaryTable(summaryOutput, st, sheet, rowIndex, titles_siteTable, yearNames,inputConfig);
			rowIndex = prepareAndInsertSummaryDetailsTable(summaryOutput, st, sheet, rowIndex, titles_siteTableDetailed,
					yearNames);
		}

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		// should be done AFTER the values have been put into cells
		for (int i = ExcelOutputConstants.SUMMARY_TABLE_STARTING_COL_INDEX; i <= titles_siteTableDetailed.size(); i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private int prepareAndInsertSummaryTable(SummaryOutput summaryOutput, String siteName, Sheet sheet, int rowIndex,
			List<String> titles_siteTable, List<String> yearNames, InputConfiguration inputConfig) {

		List<List<Object>> tableData;
		List<Object> tempRow; // will be used to add rows to tableData
		tableData = new ArrayList<>();
		List<SiteTable> siteTableList = summaryOutput.getSiteTableList();
		List<String> clustersForThisSite = summaryOutput.getAllSitesAndClusters().get(siteName);

		titles_siteTable.set(0, siteName); // ensuring first element of "titles"
											// contains appropriate site name

		for (int i = 0; i < siteTableList.size(); i++) {
			// getSite() must be == siteName
			// getRowName() must be == either siteName or one of the
			// clustersForThisSite
			if (siteTableList.get(i).getSite().equalsIgnoreCase(siteName)) {
				tempRow = new ArrayList<>();
				tempRow.add(siteTableList.get(i).getRowName()); // site or
																// cluster

				for (String yr : yearNames) {
					YearSummary yearSummary = siteTableList.get(i).getYearSummaryList().get(yr);
					int bladeNumber, hpBladeNumber, storageTotal;
					if (yearSummary == null) {
						if (siteTableList.get(i).getRowName().equalsIgnoreCase("Foundation")) {
							for (Map.Entry<String, YearSummary> x : siteTableList.get(i).getYearSummaryList()
									.entrySet()) {
								if (x != null)
									yearSummary = x.getValue();
							}

							bladeNumber = yearSummary.getBladeNumber();
							hpBladeNumber = yearSummary.getHpBladeNumber();
							storageTotal = yearSummary.getStorageTotal();
						} else {
							bladeNumber = 0;
							hpBladeNumber = 0;
							storageTotal = 0;
						}
					} else {
						bladeNumber = yearSummary.getBladeNumber();
						hpBladeNumber = yearSummary.getHpBladeNumber();
						storageTotal = yearSummary.getStorageTotal();
					}
					StringBuilder sb = new StringBuilder("Blade type 1 total: ");
					//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
					//sb.append(bladeNumber+((double)(bladeNumber*inputConfig.getBladeBufferPercentage())/100));	
					sb.append((bladeNumber));
							//+((inputConfig.getBladeBufferPercentage()/100.0f)*bladeNumber)));
					sb.append("\nBlade type 2 total: ");
					//Ashutosh: 28/08/2019:  adding new flag : BladeBufferPercentage
					sb.append(hpBladeNumber);
							//+((inputConfig.getBladeBufferPercentage()/100)*hpBladeNumber)));
					sb.append("\nStorage TB total: ");
					sb.append(storageTotal);
					tempRow.add(sb.toString());
				}
				if (siteTableList.get(i).getRowName().equalsIgnoreCase(siteTableList.get(i).getSite())) {
					tableData.add(0, tempRow); // the TOTAL for that site =>
												// this row should be the first
												// row of the table
				} else if (clustersForThisSite.contains(siteTableList.get(i).getRowName())) {
					tableData.add(tempRow); // one of the clusters of this site
											// => add to the table
				} else {
					continue; // not a cluster or "total" of this site => ignore
								// (the flow should never come here)
				}
			}
		}

		rowIndex = insertSummarySiteTable(sheet, rowIndex, tableData, titles_siteTable);
		sheet.createRow(rowIndex++); // an empty row between tables
		sheet.createRow(rowIndex++); // an empty row between tables
		return rowIndex;
	}

	/**
	 * will be called from prepareAndInsertSummaryTable() method
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param data
	 * @param titles
	 * @return
	 */
	private int insertSummarySiteTable(Sheet sheet, int rowIndex, List<List<Object>> data, List<String> titles) {
		Map<String, CellStyle> summaryStyleMap = createStylesForSummarySheet(outputWorkbook);

		int colIndex = ExcelOutputConstants.SUMMARY_TABLE_STARTING_COL_INDEX;
		int colsToAdd = ExcelOutputConstants.SUMMARY_TABLE_NUM_OF_MERGED_COLUMNS - 1;

		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.setHeightInPoints(25);
		Cell headerCell;

		// title
		for (int k = 0; k < titles.size(); k++) {
			int tempColIndex = colIndex + k;
			headerCell = headerRow.createCell(tempColIndex++);
			headerCell.setCellValue(titles.get(k));
			if (k == 0)
				headerCell.setCellStyle(summaryStyleMap.get("titleBlue"));
			else {
				headerCell.setCellStyle(summaryStyleMap.get("titleGrey"));
				for (int x = 0; x < colsToAdd; x++) {
					Cell cellll = headerRow.createCell(tempColIndex++);
					cellll.setCellValue("");
					cellll.setCellStyle(summaryStyleMap.get("titleGrey"));
				}
			}
			colIndex = tempColIndex - k - 1;
			if (k > 0) {
				int mergeColIndexEnd = tempColIndex - 1;
				int mergeColIndexStart = mergeColIndexEnd - colsToAdd;
				int mergeRowIndex = rowIndex - 1;
				sheet.addMergedRegion(
						new CellRangeAddress(mergeRowIndex, mergeRowIndex, mergeColIndexStart, mergeColIndexEnd));
			}
		}

		// data rows
		for (int i = 0; i < data.size(); i++) {
			colIndex = ExcelOutputConstants.SUMMARY_TABLE_STARTING_COL_INDEX;
			Row row = sheet.createRow(rowIndex++);
			row.setHeightInPoints(60);
			for (int k = 0; k < titles.size(); k++) {
				int tempColIndex = colIndex + k;
				Cell cell = row.createCell(tempColIndex++);
				/*
				 * if (data.get(i).get(j) instanceof String) not checking as I
				 * expect all cells to contain values of type String
				 */
				cell.setCellValue((String) data.get(i).get(k));
				cell.setCellStyle(summaryStyleMap.get("cellBig"));

				if (k > 0) {
					for (int x = 0; x < colsToAdd; x++) {
						Cell cellll = row.createCell(tempColIndex++);
						cellll.setCellValue("");
						cellll.setCellStyle(summaryStyleMap.get("cellBig"));

					}
					int mergeColIndexEnd = tempColIndex - 1;
					int mergeColIndexStart = mergeColIndexEnd - colsToAdd;
					int mergeRowIndex = rowIndex - 1;
					sheet.addMergedRegion(
							new CellRangeAddress(mergeRowIndex, mergeRowIndex, mergeColIndexStart, mergeColIndexEnd));
				}
				colIndex = tempColIndex - k - 1;
			}
		}
		return rowIndex;
	}

	private int prepareAndInsertSummaryDetailsTable(SummaryOutput summaryOutput, String siteName, Sheet sheet,
			int rowIndex, List<String> titles_siteTableDetailed, List<String> yearNames) {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);

		List<List<Object>> tableDataDetailed;
		List<Object> tempRow; // will be used to add rows to tableData

		List<SiteDetailsTable> siteDetailsTableList = summaryOutput.getSiteDetailsTableList();
		List<String> clusterNamesForSite = summaryOutput.getAllSitesAndClusters().get(siteName); // get
																									// all
																									// clusters
																									// for
																									// this
																									// site

		titles_siteTableDetailed.set(0, siteName + " detail"); // ensuring first
																// element of
																// "titles"
																// contains
																// appropriate
																// site name
		tableDataDetailed = new ArrayList<>();

		for (int i = 0; i < siteDetailsTableList.size(); i++) {
			for (String clstr : clusterNamesForSite) {
				SiteDetailsTable siteDetailsTable = siteDetailsTableList.get(i);

				if (siteName.equalsIgnoreCase(siteDetailsTable.getSite())
						&& clstr.equalsIgnoreCase(siteDetailsTable.getCluster())) {
					tempRow = new ArrayList<>();
					tempRow.add(clstr + " Total");
					if (clstr.equalsIgnoreCase("Foundation")) {
						SiteDetails siteDetails = null;
						for (Map.Entry<String, SiteDetails> x : siteDetailsTable.getClusterTotal().entrySet()) {
							if (x != null)
								siteDetails = x.getValue();
						}
						if (siteDetails == null) {
							tempRow.add(""); // cpu
							tempRow.add(""); // mem
							tempRow.add(""); // txput ns
							tempRow.add(""); // txput ew
							tempRow.add(""); // txput tot
							tempRow.add(""); // iops run
							tempRow.add(""); // ipos load
						} else {
							tempRow.add(df.format(siteDetails.getCpu()));
							tempRow.add(df.format(siteDetails.getMem()));
							tempRow.add(df.format(siteDetails.getTxputNS()));
							tempRow.add(df.format(siteDetails.getTxputEW()));
							tempRow.add(df.format(siteDetails.getTxputTot()));
							tempRow.add(df.format(siteDetails.getIopsRunning()));
							tempRow.add(df.format(siteDetails.getIopsLoading()));
						}
					} else {
						for (String yr : yearNames) {
							SiteDetails siteDetails = siteDetailsTable.getClusterTotal().get(yr);
							if (siteDetails == null) {
								tempRow.add(""); // cpu
								tempRow.add(""); // mem
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(""); // txput tot
								tempRow.add(""); // iops run
								tempRow.add(""); // ipos load
							} else {
								tempRow.add(df.format(siteDetails.getCpu()));
								tempRow.add(df.format(siteDetails.getMem()));
								tempRow.add(df.format(siteDetails.getTxputNS()));
								tempRow.add(df.format(siteDetails.getTxputEW()));
								tempRow.add(df.format(siteDetails.getTxputTot()));
								tempRow.add(df.format(siteDetails.getIopsRunning()));
								tempRow.add(df.format(siteDetails.getIopsLoading()));
							}
						}
					}
					tableDataDetailed.add(tempRow);
					tempRow = new ArrayList<>();
					tempRow.add(clstr + " Average");
					if (clstr.equalsIgnoreCase("Foundation")) {
						SiteDetails siteDetails = null;
						for (Map.Entry<String, SiteDetails> x : siteDetailsTable.getClusterAverage().entrySet()) {
							if (x != null)
								siteDetails = x.getValue();
						}
						if (siteDetails == null) {
							tempRow.add(""); // cpu
							tempRow.add(""); // mem
							tempRow.add(""); // txput ns
							tempRow.add(""); // txput ew
							tempRow.add(""); // txput tot
							tempRow.add(""); // iops run
							tempRow.add(""); // ipos load
						} else {
							tempRow.add(df.format(siteDetails.getCpu()));
							tempRow.add(df.format(siteDetails.getMem()));
							tempRow.add(df.format(siteDetails.getTxputNS()));
							tempRow.add(df.format(siteDetails.getTxputEW()));
							tempRow.add(df.format(siteDetails.getTxputTot()));
							tempRow.add(df.format(siteDetails.getIopsRunning()));
							tempRow.add(df.format(siteDetails.getIopsLoading()));
						}
					} else {
						for (String yr : yearNames) {
							SiteDetails siteDetails = siteDetailsTable.getClusterAverage().get(yr);
							if (siteDetails == null) {
								tempRow.add(""); // cpu
								tempRow.add(""); // mem
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(""); // txput tot
								tempRow.add(""); // iops run
								tempRow.add(""); // ipos load
							} else {
								tempRow.add(df.format(siteDetails.getCpu()));
								tempRow.add(df.format(siteDetails.getMem()));
								tempRow.add(df.format(siteDetails.getTxputNS()));
								tempRow.add(df.format(siteDetails.getTxputEW()));
								tempRow.add(df.format(siteDetails.getTxputTot()));
								tempRow.add(df.format(siteDetails.getIopsRunning()));
								tempRow.add(df.format(siteDetails.getIopsLoading()));
							}
						}
					}
					tableDataDetailed.add(tempRow);
					tempRow = new ArrayList<>();
					tempRow.add("Blade Limit");
					if (clstr.equalsIgnoreCase("Foundation")) {
						SiteDetails siteDetails = null;
						for (Map.Entry<String, SiteDetails> x : siteDetailsTable.getBladeLimit().entrySet()) {
							if (x != null)
								siteDetails = x.getValue();
						}
						if (siteDetails == null) {
							tempRow.add(""); // cpu
							tempRow.add(""); // mem
							tempRow.add(""); // txput ns
							tempRow.add(""); // txput ew
							tempRow.add(""); // txput tot
							tempRow.add(""); // iops run
							tempRow.add(""); // ipos load
						} else {
							tempRow.add(df.format(siteDetails.getCpu()));
							tempRow.add(df.format(siteDetails.getMem()));
							tempRow.add(df.format(siteDetails.getTxputNS()));
							tempRow.add(df.format(siteDetails.getTxputEW()));
							tempRow.add(df.format(siteDetails.getTxputTot()));
							tempRow.add(df.format(siteDetails.getIopsRunning()));
							tempRow.add(df.format(siteDetails.getIopsLoading()));
						}
					} else {
						for (String yr : yearNames) {
							SiteDetails siteDetails = siteDetailsTable.getBladeLimit().get(yr);
							if (siteDetails == null) {
								tempRow.add(""); // cpu
								tempRow.add(""); // mem
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(""); // txput tot
								tempRow.add(""); // iops run
								tempRow.add(""); // ipos load
							} else {
								tempRow.add(df.format(siteDetails.getCpu()));
								tempRow.add(df.format(siteDetails.getMem()));
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(df.format(siteDetails.getTxputTot()));
								tempRow.add(df.format(siteDetails.getIopsRunning()));
								tempRow.add(""); // ipos load
							}
						}
					}
					tableDataDetailed.add(tempRow);
					tempRow = new ArrayList<>();
					tempRow.add(clstr + " average % (versus Blade Limit)");
					if (clstr.equalsIgnoreCase("Foundation")) {
						SiteDetails siteDetails = null;
						for (Map.Entry<String, SiteDetails> x : siteDetailsTable.getClusterAverageVsBladeLimit()
								.entrySet()) {
							if (x != null)
								siteDetails = x.getValue();
						}
						if (siteDetails == null) {
							tempRow.add(""); // cpu
							tempRow.add(""); // mem
							tempRow.add(""); // txput ns
							tempRow.add(""); // txput ew
							tempRow.add(""); // txput tot
							tempRow.add(""); // iops run
							tempRow.add(""); // ipos load
						} else {
							tempRow.add(df.format(siteDetails.getCpu()));
							tempRow.add(df.format(siteDetails.getMem()));
							tempRow.add(df.format(siteDetails.getTxputNS()));
							tempRow.add(df.format(siteDetails.getTxputEW()));
							tempRow.add(df.format(siteDetails.getTxputTot()));
							tempRow.add(df.format(siteDetails.getIopsRunning()));
							tempRow.add(df.format(siteDetails.getIopsLoading()));
						}
					} else {
						for (String yr : yearNames) {
							SiteDetails siteDetails = siteDetailsTable.getClusterAverageVsBladeLimit().get(yr);
							if (siteDetails == null) {
								tempRow.add(""); // cpu
								tempRow.add(""); // mem
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(""); // txput tot
								tempRow.add(""); // iops run
								tempRow.add(""); // ipos load
							} else {
								tempRow.add(df.format(siteDetails.getCpu()) + "%");
								tempRow.add(df.format(siteDetails.getMem()) + "%");
								tempRow.add(""); // txput ns
								tempRow.add(""); // txput ew
								tempRow.add(df.format(siteDetails.getTxputTot()) + "%");
								tempRow.add(df.format(siteDetails.getIopsRunning()) + "%");
								tempRow.add(""); // ipos load
							}
						}
					}
					tableDataDetailed.add(tempRow);
				}
			}
		}

		rowIndex =

		insertSummarySiteDetailsTable(sheet, rowIndex, tableDataDetailed, titles_siteTableDetailed);
		sheet.createRow(rowIndex++); // an empty row between tables
		sheet.createRow(rowIndex++); // an empty row between tables

		return rowIndex;
	}

	/**
	 * will be called from prepareAndInsertSummaryDetailsTable() method
	 *
	 * @param sheet
	 * @param rowIndex
	 * @param data
	 * @param titles
	 * @return
	 */

	private int insertSummarySiteDetailsTable(Sheet sheet, int rowIndex, List<List<Object>> data, List<String> titles) {
		Map<String, CellStyle> summaryStyleMap = createStylesForSummarySheet(outputWorkbook);
		int colIndex = ExcelOutputConstants.SUMMARY_TABLE_STARTING_COL_INDEX;

		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.setHeightInPoints(25);
		Cell headerCell;

		// title
		for (int k = 0; k < titles.size(); k++) {
			int tempColIndex = colIndex + k;
			headerCell = headerRow.createCell(tempColIndex++);
			headerCell.setCellValue(titles.get(k));
			if (k == 0)
				headerCell.setCellStyle(summaryStyleMap.get("titleBlue"));
			else
				headerCell.setCellStyle(summaryStyleMap.get("titleGrey"));

			colIndex = tempColIndex - k - 1;
		}

		// data rows
		for (int i = 0; i < data.size(); i++) {
			colIndex = ExcelOutputConstants.SUMMARY_TABLE_STARTING_COL_INDEX;
			Row row = sheet.createRow(rowIndex++);
			row.setHeightInPoints(18);
			for (int k = 0; k < titles.size(); k++) {
				int tempColIndex = colIndex + k;
				Cell cell = row.createCell(tempColIndex++);

				if (data.get(i).size() <= k || data.get(i).get(k) == null) {
					cell.setCellStyle(summaryStyleMap.get("cellBig"));
					cell.setCellValue("");
				} else if (data.get(i).get(k) instanceof String) {
					cell.setCellValue((String) data.get(i).get(k));
					cell.setCellStyle(summaryStyleMap.get("cellBig"));
				} else if (data.get(i).get(k) instanceof Double) {
					cell.setCellValue((Double) data.get(i).get(k));
					cell.setCellStyle(summaryStyleMap.get("cellBig"));
				} else {
					cell.setCellValue(String.valueOf(data.get(i).get(k)));
					cell.setCellStyle(summaryStyleMap.get("cellBig"));
				}
				colIndex = tempColIndex - k - 1;
			}
		}
		return rowIndex;
	}

	/**
	 * will be called for each Tetris year
	 */
	public void createTetrisYearSheet(String tetrisYearName, TetrisYear tetrisYear) {
		String[] ttArray = { "Site", "Cluster Name", "Units", "Server Model", "vCPU", "vRAM", "IOPS", "BW", "used vCPU",
				"used vRAM", "used IOPS", "used BW", "free vCPU", "free vRAM", "free IOPS", "free BW", "% vCPU",
				"% vRAM", "% IOPS", "% BW" };
		List<String> tetrisTitleList = new ArrayList<>(Arrays.asList(ttArray));

		int maxVms = tetrisYear.getMaxNumOfVms();

		for (int i = 1; i <= maxVms; i++) {
			tetrisTitleList.add("VM" + i);
			tetrisTitleList.add("vCPU");
			tetrisTitleList.add("vRAM");
			tetrisTitleList.add("IOPS");
			tetrisTitleList.add("BW");
			tetrisTitleList.add("Storage");
		}
		int totalTableColumns = tetrisTitleList.size();

		List<List<Object>> tetrisTableData;
		List<Object> tempRow; // will be used to add rows to tableData

		tetrisTableData = new ArrayList<>();
		List<TetrisBlade> tetrisBladeList = tetrisYear.getTetrisBladeList();
		for (int i = 0; i < tetrisBladeList.size(); i++) {
			tempRow = new ArrayList<>();
			tempRow.add(tetrisBladeList.get(i).getSite());
			tempRow.add(tetrisBladeList.get(i).getClusterName());
			tempRow.add(tetrisBladeList.get(i).getUnits());
			tempRow.add(tetrisBladeList.get(i).getServerModel());
			tempRow.add(tetrisBladeList.get(i).getvCPU());
			tempRow.add(tetrisBladeList.get(i).getvRAM());
			tempRow.add(tetrisBladeList.get(i).getIops());
			tempRow.add(tetrisBladeList.get(i).getBw());
			tempRow.add(tetrisBladeList.get(i).getUsedVcpu());
			tempRow.add(tetrisBladeList.get(i).getUsedVram());
			tempRow.add(tetrisBladeList.get(i).getUsedIOPS());
			tempRow.add(tetrisBladeList.get(i).getUsedBW());
			tempRow.add(tetrisBladeList.get(i).getFreeVcpu());
			tempRow.add(tetrisBladeList.get(i).getFreeVram());
			tempRow.add(tetrisBladeList.get(i).getFreeIops());
			tempRow.add(tetrisBladeList.get(i).getFreeBw());
			tempRow.add(tetrisBladeList.get(i).getPercVcpu());
			tempRow.add(tetrisBladeList.get(i).getPercVram());
			tempRow.add(tetrisBladeList.get(i).getPercIops());
			tempRow.add(tetrisBladeList.get(i).getPercBw());

			List<VmBlade> vmblades = tetrisBladeList.get(i).getVmBladeList();
			for (int k = 0; k < vmblades.size(); k++) {
				tempRow.add(vmblades.get(k).getVmName());
				tempRow.add(vmblades.get(k).getvCPU());
				tempRow.add(vmblades.get(k).getvRAM());
				tempRow.add(vmblades.get(k).getIops());
				tempRow.add(vmblades.get(k).getBw());
				tempRow.add(vmblades.get(k).getStorage());
			}
			tetrisTableData.add(tempRow);
		}

		String tetrisSheetname = "Tetris year " + StringUtils.removeEnd(tetrisYearName, ".0");
		Sheet sheet = outputWorkbook.createSheet(tetrisSheetname);
		PrintSetup printSetup = sheet.getPrintSetup();
		printSetup.setLandscape(true);
		sheet.setFitToPage(true);
		sheet.setHorizontallyCenter(true);
		// sheet.setZoom(90); // 90% magnification - Ashutosh

		insertTetrisTable(sheet, 1, tetrisTableData, tetrisTitleList);

		/**
		 * there is a bug in the POI version used in this project, which creates
		 * issues with "FreezePane" feature of XSSF workbooks
		 */
		// sheet.createFreezePane(12, 1, 0, 1); // how many columns, how many
		// rows, starting column index, starting row index

		// finally set column widths, the width is measured in units of 1/256th
		// of a character width
		// should be done AFTER the values have been put into cells
		sheet.setColumnWidth(0, 15 * 256); // 15 characters wide
		for (int i = 1; i < totalTableColumns; i++) {
			sheet.autoSizeColumn(i);
		}
	}

	private void insertTetrisTable(Sheet sheet, int rowIndex, List<List<Object>> data, List<String> titles) {
		Map<String, CellStyle> tetrisStyleMap = createStylesForTetrisSheets(outputWorkbook);

		// header row
		Row headerRow = sheet.createRow(rowIndex++);
		headerRow.setHeightInPoints(18);
		Cell headerCell;

		for (int i = 0; i < titles.size(); i++) {
			headerCell = headerRow.createCell(i);
			headerCell.setCellValue(titles.get(i));

			// assigning specific colors to header cells
			if (i == 0 || i == 1) {
				headerCell.setCellStyle(tetrisStyleMap.get("header_acidicLitmus"));
				continue;
			} else if (i == 2 || i == 3) {
				headerCell.setCellStyle(tetrisStyleMap.get("header_bloodRed"));
				continue;
			} else if (i > 3 && i <= 7) {
				headerCell.setCellStyle(tetrisStyleMap.get("header_navyBlue"));
				continue;
			} else if (i > 7 && i <= 11) {
				headerCell.setCellStyle(tetrisStyleMap.get("header_yellow"));
				continue;
			} else if (i > 11 && i <= 19) {
				headerCell.setCellStyle(tetrisStyleMap.get("header_lightGreen"));
				continue;
			} else {
				headerCell.setCellStyle(tetrisStyleMap.get("header_grey"));
			}
		}

		// setting data
		for (int i = 0; i < data.size(); i++) {
			Row row = sheet.createRow(rowIndex++);
			for (int j = 0; j < data.get(i).size(); j++) {
				Cell cell = row.createCell(j);

				if (data.get(i).get(j) == null) {
					cell.setCellStyle(tetrisStyleMap.get("cella"));
					continue;
				} else if (data.get(i).get(j) instanceof String) {
					cell.setCellValue((String) data.get(i).get(j));
					cell.setCellStyle(tetrisStyleMap.get("cella"));
				} else if (data.get(i).get(j) instanceof Double) {
					cell.setCellValue((Double) data.get(i).get(j));
					cell.setCellStyle(tetrisStyleMap.get("cella"));
				} else {
					cell.setCellValue((Integer) data.get(i).get(j));
					cell.setCellStyle(tetrisStyleMap.get("cella"));
				}
			}
		}

	}

	/**
	 * Create a library of cell styles for sheets "Output Blades",
	 * "Output Storage.Enclosure" and "Total Output"
	 */
	private Map<String, CellStyle> createStylesForOutputSheets(Workbook wb) {
		Map<String, CellStyle> styleMap = new HashMap<>();
		XSSFCellStyle style;

		Color pinkie = new XSSFColor(new java.awt.Color(220, 182, 182));
		Color greyy = new XSSFColor(new java.awt.Color(219, 219, 219));

		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 12);
		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) pinkie);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(titleFont);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("title", style);

		Font headerFont = wb.createFont();
		headerFont.setFontHeightInPoints((short) 11);
		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) greyy);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFont);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("header", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("cell", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		Font cellBoldFont = wb.createFont();
		cellBoldFont.setBold(true);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setWrapText(true);
		style.setFont(cellBoldFont);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("cellBold", style);

		return styleMap;
	}

	/**
	 * Create a library of cell styles for "Summary output" sheet
	 */
	private Map<String, CellStyle> createStylesForSummarySheet(Workbook wb) {
		Map<String, CellStyle> styleMap = new HashMap<>();
		XSSFCellStyle style;

		Color cyanBlue = new XSSFColor(new java.awt.Color(221, 235, 247));
		Color greyy = new XSSFColor(new java.awt.Color(217, 217, 217));

		Font titleFont = wb.createFont();
		titleFont.setFontHeightInPoints((short) 12);
		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) cyanBlue);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setFont(titleFont);
		styleMap.put("titleBlue", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) greyy);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setFont(titleFont);
		styleMap.put("titleGrey", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("cellSmall", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("cellBig", style);

		// TODO maybe we don't need both cellSmall and cellBig - verify and
		// clean

		return styleMap;
	}

	/**
	 * Create a library of cell styles for "Tetris" sheets
	 */
	private Map<String, CellStyle> createStylesForTetrisSheets(Workbook wb) {
		Map<String, CellStyle> styleMap = new HashMap<>();

		Color acidicLitmus = new XSSFColor(new java.awt.Color(244, 176, 132));
		Color bloodRed = new XSSFColor(new java.awt.Color(192, 0, 0));
		Color navyBlue = new XSSFColor(new java.awt.Color(32, 55, 100));
		Color yellow = new XSSFColor(new java.awt.Color(255, 192, 0));
		Color lightGreen = new XSSFColor(new java.awt.Color(198, 239, 206));
		Color grey = new XSSFColor(new java.awt.Color(166, 166, 166));

		Font headerFontWhite = wb.createFont();
		headerFontWhite.setFontHeightInPoints((short) 11);
		headerFontWhite.setColor(IndexedColors.WHITE.getIndex());

		Font headerFontGreen = wb.createFont();
		headerFontGreen.setFontHeightInPoints((short) 11);
		headerFontGreen.setColor(IndexedColors.GREEN.getIndex());

		Font headerFontBlack = wb.createFont();
		headerFontBlack.setFontHeightInPoints((short) 11);

		XSSFCellStyle style;

		// I had to create different XSSFCellStyle objects for each header
		// color, otherwise it was only modifying
		// the same object and, as a result, applied the last
		// FillForegroundColor to ALL header cells of the row :-|

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) acidicLitmus);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontBlack);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_acidicLitmus", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) bloodRed);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontWhite);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_bloodRed", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) navyBlue);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontWhite);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_navyBlue", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) yellow);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontBlack);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_yellow", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) lightGreen);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontGreen);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_lightGreen", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor((XSSFColor) grey);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setFont(headerFontBlack);
		style.setWrapText(true);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.MEDIUM);
		styleMap.put("header_grey", style);

		style = (XSSFCellStyle) wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setWrapText(true);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		styleMap.put("cella", style);

		return styleMap;
	}
	

}
