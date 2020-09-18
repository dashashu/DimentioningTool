package excelio.src.main.java.it.spindox.excelio.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Map.Entry;

import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.catalog.Compute;
import commons.src.main.java.it.spindox.model.configurations.ClusterConfiguration;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLineDetail;
import commons.src.main.java.it.spindox.model.placementAndEstimation.PlacementResult;
import commons.src.main.java.it.spindox.model.placementAndEstimation.PlacementTable;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VMGroup;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import excelio.src.main.java.it.spindox.excelio.OutputExcelManagement;
import excelio.src.main.java.it.spindox.excelio.models.VmBlade;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.OutputBlades;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.OutputStorageEnclosure;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.SummaryOutput;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.Tetris;
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

public class OutputUtils_Test {


	public static void createOutputFile(List<Cluster> clusterList, Catalog catalog, Estimation estimation,
		Map<Integer, String> yearsOrder, InputConfiguration inputConfig) throws IOException {
		
		OutputExcelManagement outputExcelManagement = new OutputExcelManagement();
		
		Callable<Void> callable1 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		    	  SummaryOutput soutput = generateSummaryOutputData(clusterList, catalog, inputConfig);
		    	  outputExcelManagement.createSummaryOutPutSheet(soutput, yearsOrder, inputConfig);
		         return null;
		      }
		   };
		   Callable<Void> callable2 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		    	 OutputBlades obl = generateOutputBladesData(clusterList, catalog, yearsOrder); 
		  		outputExcelManagement.createOutPutBladesSheet(obl, yearsOrder, inputConfig);
		         return null;
		      }
		   };
		   Callable<Void> callable3 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		    	// Ashutosh :BOBBOS method call
		  		OutputBlades bbobj = NetworkPortEnclosure(clusterList, catalog, yearsOrder, inputConfig);
		  		outputExcelManagement.createOutPutNetworkSheet(bbobj, yearsOrder, inputConfig);
		        return null;
		      }
		   };

		   Callable<Void> callable4 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		    	// Ashutosh :BOBBOS method call
		    	OutputStorageEnclosure opse = generateOutputStorageEnclosureData(estimation, catalog, yearsOrder);
		  		outputExcelManagement.createOutPutStorageEnclosureSheet(opse, yearsOrder);
		        return null;
		      }
		   };
		   Callable<Void> callable5 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		    	TotalOutput totOP = generateTotalOutputData(clusterList, yearsOrder);
		  		outputExcelManagement.createTotalOutputSheet(totOP, yearsOrder);
		        return null;
		      }
		   };
		   Callable<Void> callable6 = new Callable<Void>()
		   {
		      @Override
		      public Void call() throws Exception
		      {
		  		Tetris ttrs = generateTetrisData(clusterList, catalog, inputConfig);
				List<String> sortedYearNames = ttrs.getSortedYearNames();
				sortedYearNames.forEach(item -> outputExcelManagement.createTetrisYearSheet(item, ttrs.getTetrisYearList().get(item)));
		        return null;
		      }
		   };
		 //add to a list
		   List<Callable<Void>> taskList = new ArrayList<Callable<Void>>();
		   taskList.add(callable1);
		   taskList.add(callable2);
		   taskList.add(callable3);
		   taskList.add(callable4);
		   taskList.add(callable5);
		   taskList.add(callable6);
		   
		 //create a pool executor with 3 threads
		   ExecutorService executor = Executors.newFixedThreadPool(3);
		   
		   try
		   {
		      //start the threads and wait for them to finish
		      executor.invokeAll(taskList);
		   }
		   catch (InterruptedException ie)
		   {
		      //do something if you care about interruption;
			   System.out.println(ie);
		   }
		OutputExcelManagement.writeWorkbookToFile();
	}

	private static SummaryOutput generateSummaryOutputData(List<Cluster> clusterList, Catalog catalog,
			InputConfiguration inputConfig) {
		SummaryOutput sheet = new SummaryOutput();
		List<SiteTable> siteTableList = new ArrayList<SiteTable>();
		List<SiteDetailsTable> siteDetailsTableList = new ArrayList<SiteDetailsTable>();

		for (Cluster c : clusterList) { // Scorro per cluster
			PlacementTable pt = c.getPlacementTable();

			for (String site : c.getPlacementTable().getSite()) { // Scorro per site//browse for site

				SiteTable clusterTable = new SiteTable(); // create the line for a given cluster/sito
				clusterTable.setRowName(c.getSheetLabel());
				clusterTable.setSite(site);

				Map<String, YearSummary> clusterMap = new HashMap<String, YearSummary>(); // first create the row for
																							// this cluster
				Map<String, List<Blade>> placement = pt.getPlacementForSite(site); // recovery placement per site /
																					// cluster

				Map<String, SiteDetails> clusterTotals = new HashMap<String, SiteDetails>();

				for (Map.Entry<String, List<Blade>> entry : placement.entrySet()) { // I look at the years and I look at
																					// the data
					YearSummary ys = new YearSummary();
					Integer storage = 0;

					if (c.getClusterConfiguration().isHighPerformanceBladeFlag()) { // del cluster // I insert the
																					// blades according to the cluster
																					// settings

						ys.setBladeNumber(0);
						ys.setHpBladeNumber(entry.getValue().size());
					} else {
						// Ashutosh: new flag :bladeBufferPercentage
						int bladeValue = entry.getValue().size();
						ys.setBladeNumber(bladeValue);
								//+ ((int) Math.ceil((inputConfig.getBladeBufferPercentage() / 100) * bladeValue)));
						ys.setBladeNumber(entry.getValue().size());
						ys.setHpBladeNumber(0);
					}

					for (Blade b : entry.getValue()) // conto lo storage
						storage += b.getHardDiskUsed();

					ys.setStorageTotal(storage);

					clusterMap.put(entry.getKey(), ys);

					clusterTotals = calculateClusterTotals(placement);
				}

				clusterTable.setYearSummaryList(clusterMap);

				siteTableList.add(clusterTable);

				// Ora aggiungo questa combinazione cluster/sito alla linea relativa ai totali
				// del sito
				SiteTable st = null;
				if (siteTableList.stream().filter(stl -> stl.getRowName().equals(site) && stl.getSite().equals(site))
						.findFirst().isPresent())
					st = siteTableList.stream()
							.filter(stl -> stl.getRowName().equals(site) && stl.getSite().equals(site)).findFirst()
							.get();

				if (st == null) { // Se i totali del sito ancora non esistevano, li creo
					st = new SiteTable();
					st.setRowName(site);
					st.setSite(site);

					Map<String, YearSummary> map = new HashMap<String, YearSummary>();
					for (Map.Entry<String, YearSummary> entry : clusterTable.getYearSummaryList().entrySet())
						map.put(entry.getKey(), new YearSummary(entry.getValue()));

					st.setYearSummaryList(new HashMap<String, YearSummary>(map));
					siteTableList.add(st);
				} else {
					Map<String, YearSummary> map = st.getYearSummaryList();

					for (Map.Entry<String, YearSummary> entry : clusterTable.getYearSummaryList().entrySet()) {
						YearSummary totalYs = map.get(entry.getKey());
						if (totalYs == null)
							continue;

						totalYs.setBladeNumber(totalYs.getBladeNumber() + entry.getValue().getBladeNumber());
						totalYs.setHpBladeNumber(totalYs.getHpBladeNumber() + entry.getValue().getHpBladeNumber());
						totalYs.setStorageTotal(totalYs.getStorageTotal() + entry.getValue().getStorageTotal());

						map.put(entry.getKey(), totalYs);
					}

					st.setYearSummaryList(map);
				}

				SiteDetailsTable sd = null;
				if (siteDetailsTableList.stream()
						.filter(e -> e.getSite().equals(site) && e.getCluster().equals(c.getSheetLabel())).findFirst()
						.isPresent())
					sd = siteDetailsTableList.stream()
							.filter(e -> e.getSite().equals(site) && e.getCluster().equals(c.getSheetLabel()))
							.findFirst().get();

				if (sd == null) {
					sd = new SiteDetailsTable();
					sd.setSite(site);
					sd.setCluster(c.getSheetLabel());

					Map<String, SiteDetails> map = new HashMap<String, SiteDetails>();
					for (Map.Entry<String, SiteDetails> entry : clusterTotals.entrySet())
						map.put(entry.getKey(), new SiteDetails(entry.getValue()));

					sd.setClusterTotal(new HashMap<String, SiteDetails>(map));
					sd.setHighPerformance(c.getClusterConfiguration().isHighPerformanceBladeFlag());
					siteDetailsTableList.add(sd);
				} else {
					sd.addToTotals(clusterTotals);
				}
			}
		}

		for (SiteDetailsTable sdt : siteDetailsTableList) {
			sdt.createFullTable(catalog);
		}

		sheet.setSiteTableList(siteTableList);
		sheet.setSiteDetailsTableList(siteDetailsTableList);
		return sheet;
	}

	private static OutputBlades generateOutputBladesData(List<Cluster> clusterList, Catalog catalog,
			Map<Integer, String> yearsOrder) {
		OutputBlades sheet = new OutputBlades();
		List<BladeCluster> cumulativeBladeClusters = new ArrayList<BladeCluster>(); // prima tabella: sommatoria totale
																					// blade per clusters e siti
		List<BladeCluster> deltaBladeClusters = new ArrayList<BladeCluster>(); // seconda tabella: delta della prima
		List<OutputTableSpecific> cumulativeBladesType = new ArrayList<OutputTableSpecific>(); // terza tabella:
																								// sommatoria totale
																								// blades per siti e
																								// modello
		List<OutputTableSpecific> deltaBladeType = new ArrayList<OutputTableSpecific>(); // quarta tabella: delta della
																							// terza
		List<OutputTable> cumulativeBladeTotals = new ArrayList<OutputTable>(); // quinta tabella: sommatoria totale
																				// blades per sito
		List<OutputTable> deltaBladeTotals = new ArrayList<OutputTable>(); // sesta tabella: delta della quinta

//        Cluster foundationCluster = clusterList.stream().filter(c -> c.isFoundation()).findAny().get();
//        for(String site : foundationCluster.getPlacementTable().getSite()) {
//        	cumulativeBladeTotals = addToTotals(site, foundationCluster.getPlacementTable().getPlacementForSite(site), cumulativeBladeTotals, yearsOrder, true); //per ogni sito aggiungo il tutto alla tabella dei totali
//        	
//        	BladeCluster cumulativeBladeCluster = new BladeCluster();
//        	cumulativeBladeCluster.setClusterName(foundationCluster.getSheetLabel());
//            cumulativeBladeCluster.setSite(site);
//            String bladeType = "";
//            if (foundationCluster.getClusterConfiguration().isHighPerformanceBladeFlag()) //per ogni cluster verifico se la blade è normale o HP e recupero la descrizione
//                bladeType = catalog.getBladeHighPerformance().getComponentDescription();
//            else
//                bladeType = catalog.getBlade().getComponentDescription();
//            
//            Map<String, List<Blade>> map = foundationCluster.getPlacementTable().getPlacementForSite(site);
//            Map<String, Integer> mapPerYear = new HashMap<String, Integer>();
//
//            for (Map.Entry<String, List<Blade>> element : map.entrySet()) //scorro il placement per il sito
//                mapPerYear.put(element.getKey(), element.getValue().size()); //inserisco nella mappa il valore per ogni anno
//            
//            cumulativeBladeCluster.setValueForYears(mapPerYear); //aggiungo la mappa dei totali all'oggetto cumulativo
//
//            cumulativeBladeClusters.add(cumulativeBladeCluster); //aggiungo l'oggetto dei totali alla lista
//
//            cumulativeBladesType = cumulativeBladesTypeAdd(cumulativeBladesType, site, bladeType, foundationCluster, yearsOrder); //riempio l'oggetto per la terza tabella
//        }

		for (Cluster cluster : clusterList) { // scorro i cluster
//        	if(cluster.isFoundation())
//        		continue;

			for (String site : cluster.getPlacementTable().getSite()) { // per ogni cluster scorro i siti

				cumulativeBladeTotals = addToTotals(site, cluster.getPlacementTable().getPlacementForSite(site),
						cumulativeBladeTotals, yearsOrder, cluster.isFoundation()); // per ogni sito aggiungo il tutto
																					// alla tabella dei totali

				BladeCluster cumulativeBladeCluster = new BladeCluster();

				cumulativeBladeCluster.setClusterName(cluster.getSheetLabel());
				cumulativeBladeCluster.setSite(site);

				String bladeType = "";
				if (cluster.getClusterConfiguration().isHighPerformanceBladeFlag()) // per ogni cluster verifico se la
																					// blade è normale o HP e recupero
																					// la descrizione
					bladeType = catalog.getBladeHighPerformance().getComponentDescription();
				else
					bladeType = catalog.getBlade().getComponentDescription();

				Map<String, List<Blade>> map = cluster.getPlacementTable().getPlacementForSite(site);
				Map<String, Integer> mapPerYear = new HashMap<String, Integer>();

				for (Map.Entry<String, List<Blade>> element : map.entrySet()) // scorro il placement per il sito
					mapPerYear.put(element.getKey(), element.getValue().size()); // inserisco nella mappa il valore per
																					// ogni anno

				cumulativeBladeCluster.setValueForYears(mapPerYear); // aggiungo la mappa dei totali all'oggetto
																		// cumulativo

				cumulativeBladeClusters.add(cumulativeBladeCluster); // aggiungo l'oggetto dei totali alla lista

				cumulativeBladesType = cumulativeBladesTypeAdd(cumulativeBladesType, site, bladeType, cluster,
						yearsOrder); // riempio l'oggetto per la terza tabella
			}
		}

		for (OutputTable bt : cumulativeBladeTotals) //
			deltaBladeTotals.add(new OutputTable(bt, yearsOrder));

		for (BladeCluster bc : cumulativeBladeClusters)
			deltaBladeClusters.add(new BladeCluster(bc, yearsOrder));//

		for (OutputTableSpecific ots : cumulativeBladesType)
			deltaBladeType.add(new OutputTableSpecific(ots, yearsOrder));

		sheet.setCumulativeBladeClusters(cumulativeBladeClusters);// table 1
		sheet.setCumulativeBladesType(cumulativeBladesType);// table 3
		sheet.setCumulativeBladeTotals(cumulativeBladeTotals);// table 5
		sheet.setDeltaBladeClusters(deltaBladeClusters);// table 2
		sheet.setDeltaBladeTotals(deltaBladeTotals);// table 6
		sheet.setDeltaBladeType(deltaBladeType);// table 4
		return sheet;
	}

	/**
	 * added by Ashutosh Dash. Method is Network sheet count, Network card, SAN
	 * ,total blade count etc.
	 */
	private static OutputBlades NetworkPortEnclosure(List<Cluster> clusterList, Catalog catalog,
			Map<Integer, String> yearsOrder, InputConfiguration inputConfig) {

		OutputBlades sheet = new OutputBlades();
		List<OutputTableSpecific> deltaBladeType = new ArrayList<OutputTableSpecific>();
		List<OutputTable> cumulativeBladeTotalsS = new ArrayList<OutputTable>();
		List<OutputTable> cumulativeBladeTotalsH = new ArrayList<OutputTable>();

		// Site-Year-blade map
		Map<String, Map<String, Integer>> siteYearBladeMapS = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> siteYearBladeMapH = new HashMap<String, Map<String, Integer>>();
		// Site-year-enclosure map
		Map<String, Map<String, Integer>> siteYearEnclosureMapS = new HashMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> siteYearEnclosureMapH = new HashMap<String, Map<String, Integer>>();

		List<BobbosCluster> resourceDetailTable = new ArrayList<BobbosCluster>();// for enclouser table data
		List<BobbosCluster> CumulativeEnclosureTable = new ArrayList<BobbosCluster>();// for cumulative table data
		List<BobbosCluster> DeltaEnclosureTable = new ArrayList<BobbosCluster>();// for delta table data

		// MAp for storing site wise
		Map<String, Integer> siteBladeS = new HashMap<String, Integer>();
		Map<String, Integer> siteBladeH = new HashMap<String, Integer>();

		List<BobbosCluster> cumulativeBladeClusters = new ArrayList<BobbosCluster>();
		List<BobbosCluster> deltaBladeClusters = new ArrayList<BobbosCluster>();
		for (Cluster cluster : clusterList) {
			for (String site : cluster.getPlacementTable().getSite()) { 
				
				if(!cluster.getClusterConfiguration().isHighPerformanceBladeFlag()) {
				cumulativeBladeTotalsS = addToTotals(site, cluster.getPlacementTable().getPlacementForSite(site),cumulativeBladeTotalsS, yearsOrder, cluster.isFoundation());
				}else {
				cumulativeBladeTotalsH = addToTotals(site, cluster.getPlacementTable().getPlacementForSite(site),cumulativeBladeTotalsH, yearsOrder, cluster.isFoundation());																	
				}
				BobbosCluster cumulativeBladeCluster = new BobbosCluster();
				Map<String, List<Blade>> map = cluster.getPlacementTable().getPlacementForSite(site);
				Map<String, Integer> mapPerYear = new HashMap<String, Integer>();

				for (Map.Entry<String, List<Blade>> element : map.entrySet())
					mapPerYear.put(element.getKey(), element.getValue().size());
				cumulativeBladeCluster.setClusterName(cluster.getSheetLabel());
				cumulativeBladeCluster.setSite(site);
				cumulativeBladeCluster.setValueForYears(mapPerYear);
				if (cluster.getClusterConfiguration().isHighPerformanceBladeFlag()) {
					cumulativeBladeCluster.setBladeType("high performance");
				}else {
					cumulativeBladeCluster.setBladeType("Standard");
				}
				cumulativeBladeClusters.add(cumulativeBladeCluster);
				if (cluster.getClusterConfiguration().isHighPerformanceBladeFlag()) {// HP
					if (siteYearBladeMapH.containsKey(site)) {
						Set<String> yearNames = siteYearBladeMapH.get(site).keySet();
						for (String year : yearNames) {
							mapPerYear.put(year, (siteYearBladeMapH.get(site).get(year) + mapPerYear.get(year)));
						}
						siteYearBladeMapH.put(site, mapPerYear);
					} else {
						siteYearBladeMapH.put(site, mapPerYear);
					}
				} else {// Standard
					if (siteYearBladeMapS.containsKey(site)) {
						Set<String> yearNames = siteYearBladeMapS.get(site).keySet();
						for (String year : yearNames) {
							mapPerYear.put(year, (siteYearBladeMapS.get(site).get(year) + mapPerYear.get(year)));
						}
						siteYearBladeMapS.put(site, mapPerYear);
					} else {
						siteYearBladeMapS.put(site, mapPerYear);
					}
				}
			}
		}//end of cluster loop
		//delta blade for resource count
		for (BobbosCluster bc : cumulativeBladeClusters)
			deltaBladeClusters.add(new BobbosCluster(bc,yearsOrder));//
		for (BobbosCluster item : deltaBladeClusters) {
			int bladeCountyear = item.getValueForYears().values().stream().mapToInt(i -> i).sum();
			if(item.getBladeType().equals("Standard")) {
				siteBladeS.put(item.getSite(), bladeCountyear);
			}else {
				siteBladeH.put(item.getSite(), bladeCountyear);
			}
		}
		// Resource details
		List<String> siteToExclude = new ArrayList<String>();
		for (Entry<String, Integer> item : siteBladeS.entrySet()) {
			for (Entry<String, Integer> item1 : siteBladeH.entrySet()) {
				if (item.getKey().equals(item1.getKey())) {
					int totalBladeCount = (((item.getValue()) + (item1.getValue() * 2)) / 2)
							+ ((item.getValue()) + (item1.getValue() * 2));
					// the formula is : ( No. Blades mgmt. + No. blade serviceS + (No. blade serviced) x 2) = Total/2 = Result + Total
					resourceDetailTable.add(modelName(totalBladeCount, catalog, item.getKey(), inputConfig));// call the model name method and set the List()
					siteToExclude.add(item1.getKey());
				}
			}
		}
		// Resource details -  for only Standard
		siteToExclude.forEach(item -> { siteBladeS.remove(item);});
		for (Entry<String, Integer> item : siteBladeS.entrySet()) {
			int totalBladeCount = ((item.getValue() / 2) + (item.getValue()));
			resourceDetailTable.add(modelName(totalBladeCount, catalog, item.getKey(), inputConfig));// call the model name method and set the List()
		}

		// Resource details -  for only HP
		siteToExclude.forEach(item -> { siteBladeH.remove(item);});
		
		for (Entry<String, Integer> item : siteBladeH.entrySet()) {
			int totalBladeCount = ((item.getValue() / 2) + (item.getValue()));
			resourceDetailTable.add(modelName(totalBladeCount, catalog, item.getKey(), inputConfig));// call the model name method and set the List()
		}

		// Cumulative enclosure table - Table2
		for (Entry<String, Map<String, Map<String, Integer>>> mapvalue : getEnclosure(catalog, siteYearBladeMapS,
				siteYearBladeMapH, inputConfig).entrySet()) {

			if (!mapvalue.equals(null)) {
				for (Map.Entry<String, Map<String, Integer>> values1 : mapvalue.getValue().entrySet()) {
					BobbosCluster CumulativeTable = new BobbosCluster();
					if (mapvalue.getKey().equalsIgnoreCase("Standard")) {
						CumulativeTable.setBladeType("Standard");// blade type
						CumulativeTable.setSite(values1.getKey());// site
						CumulativeTable.setValueForYears(values1.getValue());// yr-enclosuremap
						//Site-Year-Enclosure map for port number calculation.
						siteYearEnclosureMapS.put(values1.getKey(), values1.getValue());
					} else if (mapvalue.getKey().equalsIgnoreCase("HP")) {
						CumulativeTable.setBladeType("High Performance");// blade type
						CumulativeTable.setSite(values1.getKey());// site
						CumulativeTable.setValueForYears(values1.getValue());// yr-enclosuremap
						//Site-Year-Enclosure map for port number calculation.
						siteYearEnclosureMapH.put(values1.getKey(), values1.getValue());
					}
					CumulativeEnclosureTable.add(CumulativeTable);
				}
			}
		}
		

		for (BobbosCluster bc : CumulativeEnclosureTable)
			DeltaEnclosureTable.add(new BobbosCluster(bc, yearsOrder));//
		
		sheet.setTable1List(resourceDetailTable);// table 1
		sheet.setTableD2List(CumulativeEnclosureTable);// table 2
		sheet.setTableCumulativeD2List(DeltaEnclosureTable);// table 3
		sheet.setTable3List(portNumber(siteYearEnclosureMapS, siteYearEnclosureMapH));// table 4
		sheet.setDeltaBladeType(deltaBladeType);// table 4
		return sheet;
	}


	/**
	 * Added By : Ashutosh dash. To prepair map of standard/HP blade type and
	 * enclousre count
	 */
	private static Map<String, Map<String, Map<String, Integer>>> getEnclosure(Catalog catalog,
			Map<String, Map<String, Integer>> siteYearBladeMapS, Map<String, Map<String, Integer>> siteYearBladeMapD,
			InputConfiguration inputConfig) {
		Map<String, Map<String, Integer>> siteYearEnclosureMapMS = new HashMap<>();
		Map<String, Map<String, Integer>> siteYearEnclosureMapD = new HashMap<>();

		if (!siteYearBladeMapS.isEmpty()) {
			for (Map.Entry<String, Map<String, Integer>> element3 : siteYearBladeMapS.entrySet()) {
				String bladeType = "Standard";
				Map<String, Integer> YearEnclosureMapMS = new HashMap<>();
				for (Map.Entry<String, Integer> ele2 : element3.getValue().entrySet()) {
					YearEnclosureMapMS.put(ele2.getKey(),
							enclosureType(catalog, Math.abs(ele2.getValue()), bladeType, inputConfig));// enclosure
																										// valueperYear
																										// for standard
																										// blade
				}
				siteYearEnclosureMapMS.put(element3.getKey(), YearEnclosureMapMS);
			}
		}
		if (!siteYearBladeMapD.isEmpty()) {
			for (Map.Entry<String, Map<String, Integer>> element3 : siteYearBladeMapD.entrySet()) {
				String bladeType = "HP";
				Map<String, Integer> YearEnclosureMapD = new HashMap<>();
				for (Map.Entry<String, Integer> ele2 : element3.getValue().entrySet()) {// useful for enclosure count
					YearEnclosureMapD.put(ele2.getKey(),
							enclosureType(catalog, Math.abs(ele2.getValue()), bladeType, inputConfig));// for HP blade
																										// enlousure
					siteYearEnclosureMapD.put(element3.getKey(), YearEnclosureMapD);
				}
			}
		}
		Map<String, Map<String, Map<String, Integer>>> returnMap = new HashMap<String, Map<String, Map<String, Integer>>>();
		returnMap.put("Standard", siteYearEnclosureMapMS);
		returnMap.put("HP", siteYearEnclosureMapD);
		return returnMap;
	}

	/**
	 * Added By : Ashutosh dash. To count port numbers on delta enclosure count.
	 */
	private static Map<String, List<Map<String, Map<String, Integer>>>> portNumber(
			Map<String, Map<String, Integer>> siteYearEclosureMapMS,
			Map<String, Map<String, Integer>> siteYearEclosureMapD) {
		List<Map<String, Map<String, Integer>>> listOfMaps1 = new ArrayList<>();
		List<Map<String, Map<String, Integer>>> listOfMaps2 = new ArrayList<>();
		List<Map<String, Map<String, Integer>>> listOfMaps3 = new ArrayList<>();
		Map<String, Map<String, Integer>> Table40Map1 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table40Map2 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table40Map3 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table10GMap1 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table10GMap2 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table10GMap3 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table8GMap1 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table8GMap2 = new TreeMap<String, Map<String, Integer>>();
		Map<String, Map<String, Integer>> Table8GMap3 = new TreeMap<String, Map<String, Integer>>();
		Map<String, List<Map<String, Map<String, Integer>>>> mapToReturn = new TreeMap<String, List<Map<String, Map<String, Integer>>>>();
		List<String> listOfSites = new ArrayList<>();
		if (!(siteYearEclosureMapMS == null) && !(siteYearEclosureMapD == null)) {
			for (Entry<String, Map<String, Integer>> element : siteYearEclosureMapMS.entrySet()) {
				for (Entry<String, Map<String, Integer>> element2 : siteYearEclosureMapD.entrySet()) {
					Map<String, Integer> yrMap10G = new HashMap<String, Integer>();
					Map<String, Integer> yrMap40G = new HashMap<String, Integer>();
					Map<String, Integer> yrMap8G = new HashMap<String, Integer>();
					if (element.getKey().equalsIgnoreCase(element2.getKey())) {
						for (Entry<String, Integer> elm : element.getValue().entrySet()) {// MAPMS
							for (Entry<String, Integer> elm2 : element2.getValue().entrySet()) {// MAPD
								if (elm.getKey().equalsIgnoreCase(elm2.getKey())) {
									int port40G = Math.abs((8 * (elm.getValue())) + (16 * (elm2.getValue())));
									int port10G = Math.abs((4 * (elm.getValue())) + (4 * (elm2.getValue())));
									int port8G = Math.abs(4 * ((elm.getValue()) + (elm2.getValue())));
									yrMap40G.put(elm.getKey(), port40G);
									yrMap10G.put(elm.getKey(), port10G);
									yrMap8G.put(elm.getKey(), port8G);
									Table40Map1.put(element.getKey(), yrMap40G);
									Table10GMap1.put(element.getKey(), yrMap10G);
									Table8GMap1.put(element.getKey(), yrMap8G);
								}
							}
						}
						listOfSites.add(element.getKey());
					}
				}
			}
		}
		for (String key : listOfSites) {
			siteYearEclosureMapMS.remove(key);
			siteYearEclosureMapD.remove(key);
		}
		if (!(siteYearEclosureMapMS == null)) {
			for (Map.Entry<String, Map<String, Integer>> element1 : siteYearEclosureMapMS.entrySet()) {// site map
				Map<String, Integer> yrMap40G = new HashMap<String, Integer>();
				Map<String, Integer> yrMap10G = new HashMap<String, Integer>();
				Map<String, Integer> yrMap8G = new HashMap<String, Integer>();
				for (Map.Entry<String, Integer> elm : element1.getValue().entrySet()) {// year map
					int port40G = Math.abs((8 * (elm.getValue())) + (0));
					int port10G = Math.abs((4 * (elm.getValue())) + (4 * (elm.getValue())));
					int port8G = Math.abs(4 * ((elm.getValue()) + (elm.getValue())));
					yrMap40G.put(elm.getKey(), port40G);
					yrMap10G.put(elm.getKey(), port10G);
					yrMap8G.put(elm.getKey(), port8G);
					Table40Map2.put(element1.getKey(), yrMap40G);
					Table10GMap2.put(element1.getKey(), yrMap10G);
					Table8GMap2.put(element1.getKey(), yrMap8G);
				}
			}
		}
		if (!(siteYearEclosureMapD == null)) {
			for (Map.Entry<String, Map<String, Integer>> element2 : siteYearEclosureMapD.entrySet()) {
				Map<String, Integer> yrMap40G = new HashMap<String, Integer>();
				Map<String, Integer> yrMap10G = new HashMap<String, Integer>();
				Map<String, Integer> yrMap8G = new HashMap<String, Integer>();
				for (Entry<String, Integer> elm2 : element2.getValue().entrySet()) {
					int port40G = Math.abs((0) + (16 * (elm2.getValue())));
					int port10G = Math.abs((4 * (elm2.getValue())) + (4 * (elm2.getValue())));
					int port8G = Math.abs(4 * ((elm2.getValue()) + (elm2.getValue())));
					yrMap40G.put(elm2.getKey(), port40G);
					yrMap10G.put(elm2.getKey(), port10G);
					yrMap8G.put(elm2.getKey(), port8G);
					Table10GMap3.put(element2.getKey(), yrMap10G);
					Table8GMap3.put(element2.getKey(), yrMap8G);
					Table40Map3.put(element2.getKey(), yrMap40G);
				}
			}
		}
		listOfMaps1.add(Table40Map1);
		listOfMaps1.add(Table40Map2);
		listOfMaps1.add(Table40Map3);

		listOfMaps2.add(Table10GMap1);
		listOfMaps2.add(Table10GMap2);
		listOfMaps2.add(Table10GMap3);

		listOfMaps3.add(Table8GMap1);
		listOfMaps3.add(Table8GMap2);
		listOfMaps3.add(Table8GMap3);

		mapToReturn.put("40G", listOfMaps1);
		mapToReturn.put("10G", listOfMaps2);
		mapToReturn.put("8G", listOfMaps3);

		return mapToReturn;
	}

	/**
	 * Added By : Ashutosh dash. To count enclouser numbers, enclosure type depends
	 * on vendors, HP,Dell,Huwaie. Note: foundation condition has been added but not
	 * in use. As it will be use in pBOM sheet.
	 */
	private static Integer enclosureType(Catalog catalog, int bladeCountyear, String bladeType,
			InputConfiguration inputConfig) {

		int enclouser = 0;
		int foundationEnlosure = 0;
		int enclouserf = 0;
		int foundationBlades = 7;

		if (catalog.getEnclosure().getVendor().contains("HP")) {
			if (bladeType.equalsIgnoreCase("Standard")) {
				if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("AC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (14));// number of enclosure
						if (((bladeCountyear + foundationBlades) % (14)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouserf = (bladeCountyear) / (14);
					if (((bladeCountyear) % (14)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count

				} else if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("DC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (12));
						if (((bladeCountyear + foundationBlades) % (12)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;// enclosure count

					} else
						enclouser = (bladeCountyear) / (12);
					if (((bladeCountyear) % (12)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				}
			} else {// highperformance
				if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("AC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (13));// 2.5 = 3
						if (((bladeCountyear + foundationBlades) % (13)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouser = (bladeCountyear) / (13);// Max Nr of blades Std (AC/DC) N+N 13/12
					if (((bladeCountyear) % (13)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				} else if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("DC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (12));// 2.5 = 3
						if (((bladeCountyear + foundationBlades) % (12)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouser = (bladeCountyear) / (12);
					if (((bladeCountyear) % (12)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				}
			}
		} else if (catalog.getEnclosure().getVendor().contains("DELL")) {
			if (bladeType.equalsIgnoreCase("Standard")) {
				if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("AC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (16));// number of enclosure
						if (((bladeCountyear + foundationBlades) % (16)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouserf = (bladeCountyear) / (16);
					if (((bladeCountyear) % (16)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count

				} else if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("DC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (14));
						if (((bladeCountyear + foundationBlades) % (14)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;// enclosure count

					} else
						enclouser = (bladeCountyear) / (14);
					if (((bladeCountyear) % (14)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				}
			} else {// highperformance
				if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("AC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (16));// 2.5 = 3
						if (((bladeCountyear + foundationBlades) % (16)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouser = (bladeCountyear) / (16);// Max Nr of blades Std (AC/DC) N+N 13/12
					if (((bladeCountyear) % (16)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				} else if (catalog.getEnclosure().getAcdc().equalsIgnoreCase("DC")) {
					if (inputConfig.isFoundationFlag()) {
						foundationEnlosure = 1;
						enclouserf = ((bladeCountyear + foundationBlades) / (14));// 2.5 = 3
						if (((bladeCountyear + foundationBlades) % (14)) != 0) {
							enclouser = (enclouserf + 1) - foundationEnlosure;
						} else
							enclouser = enclouserf - foundationEnlosure;
					} else
						enclouser = (bladeCountyear) / (14);
					if (((bladeCountyear) % (14)) != 0) {
						enclouser = (enclouserf + 1);
					} else
						enclouser = enclouserf;// enclosure count
				}
			}
		} else if (catalog.getEnclosure().getVendor().contains("HUAWEI")) {
			if (inputConfig.isFoundationFlag()) {
				foundationEnlosure = 1;
				enclouserf = ((bladeCountyear + foundationBlades) / (14));// 2.5 = 3
				if (((bladeCountyear + foundationBlades) % (16)) != 0) {
					enclouser = (enclouserf + 1) - foundationEnlosure;
				} else
					enclouser = enclouserf - foundationEnlosure;
			} else
				enclouser = (bladeCountyear) / (16);// Max Nr of blades Std (AC/DC) N+N 16/16
			if (((bladeCountyear) % (14)) != 0) {
				enclouser = (enclouserf + 1);
			} else
				enclouser = enclouserf;// enclosure count
		}
		return Math.abs(enclouser);
	}

	/**
	 * Added by: Ashutosh Dash. To get the model name,Network,SAN,Storage array
	 * details.
	 */
	private static BobbosCluster modelName(int bladeD1, Catalog catalog, String string,
			InputConfiguration inputConfig) {

		BobbosCluster cumulativeBOBBladeCluster = new BobbosCluster();// table 1 objects
		String modelName = "";
		String bobBobtype = null;
		String network = null;
		String storageArray = null;
		String SAN = null;

		// For BOS 40 GB // TODO: as advice by design team(Tejas) to take only this. In
		// future may implement for others.

		/*
		 * if(PathModel.getOpcoName().equalsIgnoreCase("UK")||PathModel.getOpcoName().
		 * equalsIgnoreCase("IRELAND")||
		 * PathModel.getOpcoName().equalsIgnoreCase("ITALY")||PathModel.getOpcoName().
		 * equalsIgnoreCase("MALTA")||
		 * PathModel.getOpcoName().equalsIgnoreCase("ALBANIA")){
		 */
		bobBobtype = "BOS";
		// if(totalBladeCount not null ){
		if (bladeD1 > 0 & bladeD1 <= 128) {
			modelName = "Small";
			network = "5950";
			SAN = "SN6500B";
			storageArray = "3PAR 8440 2N";
		} else if (bladeD1 > 128 & bladeD1 <= 272) {
			modelName = "Medium";
			network = "7910";
			SAN = "SN6500B";
			storageArray = "3PAR 8440 4N";
			// networkCard ;
		} else if (bladeD1 > 272 & bladeD1 <= 640) {
			modelName = "Large";
			network = "12908";
			SAN = "SN8000B";
			storageArray = "3PAR 8440 4N";
		} else
			modelName = "Undefined";
		/*
		 * } //For BOB 40 GB : as advice by design team to take only this. In future may
		 * implement for others. else {//Note: ALL THE TESTING center belongs to bob as
		 * mentioned by the designers. bobBobtype= "BOB"; if(bladeD1>0 & bladeD1<= 128){
		 * modelName = "Small"; network = "9504"; SAN = "MDS9148"; storageArray =
		 * "UNITY 400F"; } else if (bladeD1>128 & bladeD1<= 272){ modelName = "Medium";
		 * network = "9504"; SAN = "MDS9396"; storageArray = "UNITY 500F"; } else if
		 * (bladeD1>272 & bladeD1<= 640){ modelName = "Large"; network = "9508"; SAN =
		 * "MDS9700"; storageArray = "VMAX 250"; } else modelName = "Undefined"; }
		 */

		cumulativeBOBBladeCluster.setBobBobtype(bobBobtype);
		cumulativeBOBBladeCluster.setNetwork(network);
		cumulativeBOBBladeCluster.setSAN(SAN);
		cumulativeBOBBladeCluster.setStorageArray(storageArray);
		cumulativeBOBBladeCluster.setModelName(modelName);
		cumulativeBOBBladeCluster.setSite(string);
		cumulativeBOBBladeCluster.setTotalBladeCount(bladeD1);
		return cumulativeBOBBladeCluster;
	}

	private static Tetris generateTetrisData(List<Cluster> clusterList, Catalog catalog,
			InputConfiguration inputConfig) {
		Tetris sheet = new Tetris();
		Map<String, TetrisYear> tetrisYearList = new HashMap<String, TetrisYear>();

		for (Cluster cluster : clusterList) {
			ClusterConfiguration clusterConfig = inputConfig.getClusterConfiguration().stream()
					.filter(cc -> cc.getSheetLabel().equals(cluster.getSheetLabel())).findFirst().get();

			Compute catalogBlade = null;
			if (cluster.getClusterConfiguration().isHighPerformanceBladeFlag())
				catalogBlade = catalog.getBladeHighPerformance();
			else
				catalogBlade = catalog.getBlade();

			for (PlacementResult pr : cluster.getPlacementTable().getPlacementResultList()) {
				TetrisYear tetrisYear = new TetrisYear();
				String site = pr.getSite();
				String year = null;

				if (cluster.isFoundation())
					year = "Foundation";
				else
					year = pr.getYear();

				int unit = 0;
				unit++;

				if (tetrisYearList.containsKey(year)) {
					tetrisYear = tetrisYearList.get(year);
				}

				for (Blade blade : pr.getPlacement()) {
					TetrisBlade tb = new TetrisBlade();
					Double occupiedCpu = 0.0;
					Double occupiedRam = 0.0;
					Double occupiedIops = 0.0;
					Double occupiedBw = 0.0;

					tb.setSite(site);
					tb.setClusterName(cluster.getSheetLabel());
					tb.setUnits(unit);
					tb.setServerModel(catalogBlade.getComponentDescription());
					tb.setvCPU((int) (Math.round(catalogBlade.getNumberOfCores() * catalogBlade.getNumberOfSockets())
							* clusterConfig.getCpuOverProvisioning()));
					tb.setvRAM((int) (catalogBlade.getRamInGb() * clusterConfig.getRamOverProvisioning()));
					tb.setIops((int) (catalogBlade.getMaxRunningIopsSupported()
							* clusterConfig.getMaxRunningIOPSPerBladeOverprovisioning()));
					tb.setBw((int) (catalogBlade.getMaxThroughputSupported()
							* clusterConfig.getMaxThroughputPerBladeOverprovisioning()));

					for (VMGroup vmg : blade.getVmGroupAssignedToThisBladeList()) {
						for (VirtualMachine vm : vmg.getVmList()) {
							occupiedCpu += vm.getTotalCores();
							occupiedRam += vm.getRam();
							occupiedIops += vm.getRunningIOPS();
							occupiedBw += vm.getEastWestBW() + vm.getNorthSouthBW();

							VmBlade vmb = new VmBlade();
							vmb.setVmName(vm.getCompleteName());
							vmb.setvCPU(vm.getTotalCores());
							vmb.setvRAM(vm.getRam());
							vmb.setIops(vm.getRunningIOPS());
							vmb.setBw(vm.getEastWestBW() + vm.getNorthSouthBW());
							vmb.setStorage(vm.getHardDisk());

							tb.getVmBladeList().add(vmb);
						}
					}

					tb.setUsedVcpu((int) Math.round(occupiedCpu));
					tb.setUsedVram((int) Math.round(occupiedRam));
					tb.setUsedIOPS((int) Math.round(occupiedIops));
					tb.setUsedBW((int) Math.round(occupiedBw));

					tb.setFreeVcpu((int) (tb.getvCPU() - Math.round(occupiedCpu)));
					tb.setFreeVram((int) (tb.getvRAM() - Math.round(occupiedRam)));
					tb.setFreeIops((int) (tb.getIops() - Math.round(occupiedIops)));
					tb.setFreeBw((int) (tb.getBw() - Math.round(occupiedBw)));

					tb.setPercVcpu((int) Math.floor(new Double(occupiedCpu) * 100 / tb.getvCPU()));
					tb.setPercVram((int) Math.floor(new Double(occupiedRam) * 100 / tb.getvRAM()));
					tb.setPercIops((int) Math.floor(new Double(occupiedIops) * 100 / tb.getIops()));
					tb.setPercBw((int) Math.floor(new Double(occupiedBw) * 100 / tb.getBw()));

					tetrisYear.addBlade(tb);
				}
				tetrisYearList.put(year, tetrisYear);
			}
		}

		sheet.setTetrisYearList(tetrisYearList);
		return sheet;
	}

	private static OutputStorageEnclosure generateOutputStorageEnclosureData(Estimation estimation, Catalog catalog,
			Map<Integer, String> yearsOrder) {
		OutputStorageEnclosure sheet = new OutputStorageEnclosure();
		List<TableStorageEnclosure> cumulativeStorage = new ArrayList<TableStorageEnclosure>(); // prima tabella:
																								// sommatoria totale
																								// elementi storage per
																								// clusters e siti
		List<TableStorageEnclosure> deltaStorage = new ArrayList<TableStorageEnclosure>(); // seconda tabella: delta
																							// della prima
		List<TableStorageEnclosure> cumulativeEnclosure = new ArrayList<TableStorageEnclosure>(); // terza tabella:
																									// sommatoria totale
																									// enclosures per
																									// siti e modello
		List<TableStorageEnclosure> deltaEnclosure = new ArrayList<TableStorageEnclosure>(); // quarta tabella: delta
																								// della terza

		Set<String> storageElements = new HashSet<String>();
		storageElements.add(catalog.getThreePar().getComponentId());
		storageElements.add(catalog.getThreeParExpansion().getComponentId());
		storageElements.add(catalog.getDisk().getComponentId());
		storageElements.add(catalog.getDriveEnclosureDisk().getComponentId());

		Set<String> enclosureElements = new HashSet<String>();
		enclosureElements.add(catalog.getEnclosure().getComponentId());
		enclosureElements.add(catalog.getEnclosureHighPerformance().getComponentId());

		for (Map.Entry<String, List<EstimationLine>> entry : estimation.getEstimationTable().entrySet()) {
			for (EstimationLine el : entry.getValue()) {
				if (storageElements.contains(el.getComponent().getComponentId())) {
					TableStorageEnclosure tempOs = new TableStorageEnclosure();
					TableStorageEnclosure tempFoundation = null;
					tempOs.setSite(entry.getKey());
					tempOs.setComponentType(el.getComponent().getComponentDescription());

					Map<String, Integer> tempMap = new HashMap<String, Integer>();
					Map<String, Integer> foundationMap = new HashMap<String, Integer>();
					int tot = 0;

					for (EstimationLineDetail eld : el.getEstimationLineDetail()) {
						if (!eld.getLineReference().equals("foundation")) {
							tot += eld.getQuantity();
							tempMap.put(eld.getLineReference(), tot);
						} else {
							tempFoundation = new TableStorageEnclosure();
							foundationMap.put(eld.getLineReference(), eld.getQuantity());
							tempFoundation.setSite(entry.getKey());
							tempFoundation.setComponentType(el.getComponent().getComponentDescription());
							tempFoundation.setFoundation(true);
							tempFoundation.setValueForYears(foundationMap);
						}
					}

					tempOs.setValueForYears(tempMap);

					if (tempFoundation != null)
						cumulativeStorage.add(tempFoundation);

					cumulativeStorage.add(tempOs);
				}

				if (enclosureElements.contains(el.getComponent().getComponentId())) {
					TableStorageEnclosure tempOe = new TableStorageEnclosure();
					TableStorageEnclosure tempFoundation = null;
					tempOe.setSite(entry.getKey());
					tempOe.setComponentType(el.getComponent().getComponentDescription());

					Map<String, Integer> tempMap = new HashMap<String, Integer>();
					Map<String, Integer> foundationMap = new HashMap<String, Integer>();
					int tot = 0;

					for (EstimationLineDetail eld : el.getEstimationLineDetail()) {
						if (!eld.getLineReference().equals("foundation")) {
							tot += eld.getQuantity();
							tempMap.put(eld.getLineReference(), tot);
						} else {
							tempFoundation = new TableStorageEnclosure();
							foundationMap.put(eld.getLineReference(), eld.getQuantity());
							tempFoundation.setSite(entry.getKey());
							tempFoundation.setComponentType(el.getComponent().getComponentDescription());
							tempFoundation.setFoundation(true);
							tempFoundation.setValueForYears(foundationMap);
						}
					}

					tempOe.setValueForYears(tempMap);

					if (tempFoundation != null)
						cumulativeEnclosure.add(tempFoundation);

					cumulativeEnclosure.add(tempOe);
				}
			}
		}

		for (TableStorageEnclosure cs : cumulativeStorage) { // scorro la tabella dei totali e creo quella dei delta

			deltaStorage.add(
					new TableStorageEnclosure(cs.getSite(), cs.getComponentType(), cs.isFoundation(), cs, yearsOrder));
		}

		for (TableStorageEnclosure cs : cumulativeEnclosure) { // scorro la tabella dei totali e creo quella dei delta
			deltaEnclosure.add(
					new TableStorageEnclosure(cs.getSite(), cs.getComponentType(), cs.isFoundation(), cs, yearsOrder));
		}

		cumulativeEnclosure = setFullYears(cumulativeEnclosure, yearsOrder);
		cumulativeStorage = setFullYears(cumulativeStorage, yearsOrder);

		sheet.setCumulativeEnclosure(cumulativeEnclosure);
		sheet.setCumulativeStorage(cumulativeStorage);
		sheet.setDeltaEnclosure(deltaEnclosure);
		sheet.setDeltaStorage(deltaStorage);
		return sheet;
	}

	private static List<TableStorageEnclosure> setFullYears(List<TableStorageEnclosure> cumulativeList,
			Map<Integer, String> yearsOrder) {
		List<TableStorageEnclosure> returnList = new ArrayList<TableStorageEnclosure>();

		for (TableStorageEnclosure element : cumulativeList) {
			if (element.isFoundation()) {
				returnList.add(element);
				continue;
			}

			Map<String, Integer> valueForYears = element.getValueForYears();
			int previous = 0;
			for (int i = 1; i <= 5; i++) {
				String anno = yearsOrder.get(i);
				if (valueForYears.get(anno) == null) {
					valueForYears.put(anno, previous);
				} else
					previous = valueForYears.get(anno);
			}

			element.setValueForYears(valueForYears);
			returnList.add(element);
		}

		return returnList;
	}

	private static TotalOutput generateTotalOutputData(List<Cluster> clusterList, Map<Integer, String> yearsOrder) {
		TotalOutput sheet = new TotalOutput();
		List<TotalOutputCluster> cpuTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> cpuDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> cpuTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> cpuDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> ramTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> ramDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> ramTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> ramDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> storageTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> storageDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> storageTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> storageDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> iopsTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> iopsDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> iopsTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> iopsDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> bwTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> bwDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> bwTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> bwDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> nsTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> nsDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> nsTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> nsDeltaOutput = new ArrayList<OutputTable>();

		List<TotalOutputCluster> ewTotalOutputCluster = new ArrayList<TotalOutputCluster>();
		List<TotalOutputCluster> ewDeltaOutputCluster = new ArrayList<TotalOutputCluster>();
		List<OutputTable> ewTotalOutput = new ArrayList<OutputTable>();
		List<OutputTable> ewDeltaOutput = new ArrayList<OutputTable>();

		for (Cluster c : clusterList) {
			for (PlacementResult pr : c.getPlacementTable().getPlacementResultList()) {
				int totalCpu = 0;
				int totalRam = 0;
				int totalStorage = 0;
				int totalIops = 0;
				int totalBw = 0;
				int totalNSBw = 0;
				int totalEWBw = 0;

				for (Blade b : pr.getPlacement()) {
					totalCpu += b.getCoreUsed();
					totalRam += b.getRamUsed();

					for (VMGroup vmg : b.getVmGroupAssignedToThisBladeList()) {
						for (VirtualMachine vm : vmg.getVmList()) {
							totalStorage += vm.getHardDisk();
							totalIops += vm.getRunningIOPS();
							totalBw += vm.getNorthSouthBW() + vm.getEastWestBW();
							totalNSBw += vm.getNorthSouthBW();
							totalEWBw += vm.getEastWestBW();
						}
					}
				}

				String clusterName = c.getSheetLabel();

				// CPU
				cpuTotalOutputCluster = addToTotalClusterSite(cpuTotalOutputCluster, pr, clusterName, totalCpu);
				cpuTotalOutput = addToTotalSite(cpuTotalOutput, pr, totalCpu);

				// RAM
				ramTotalOutputCluster = addToTotalClusterSite(ramTotalOutputCluster, pr, clusterName, totalRam);
				ramTotalOutput = addToTotalSite(ramTotalOutput, pr, totalRam);

				// Storage
				storageTotalOutputCluster = addToTotalClusterSite(storageTotalOutputCluster, pr, clusterName,
						totalStorage);
				storageTotalOutput = addToTotalSite(storageTotalOutput, pr, totalStorage);

				// IOPS
				iopsTotalOutputCluster = addToTotalClusterSite(iopsTotalOutputCluster, pr, clusterName, totalIops);
				iopsTotalOutput = addToTotalSite(iopsTotalOutput, pr, totalIops);

				// BW
				bwTotalOutputCluster = addToTotalClusterSite(bwTotalOutputCluster, pr, clusterName, totalBw);
				bwTotalOutput = addToTotalSite(bwTotalOutput, pr, totalBw);

				// NS-BW
				nsTotalOutputCluster = addToTotalClusterSite(nsTotalOutputCluster, pr, clusterName, totalNSBw);
				nsTotalOutput = addToTotalSite(nsTotalOutput, pr, totalNSBw);

				// EW-BW
				ewTotalOutputCluster = addToTotalClusterSite(ewTotalOutputCluster, pr, clusterName, totalEWBw);
				ewTotalOutput = addToTotalSite(ewTotalOutput, pr, totalEWBw);
			}
		}

		for (TotalOutputCluster toc : cpuTotalOutputCluster)
			cpuDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : ramTotalOutputCluster)
			ramDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : storageTotalOutputCluster)
			storageDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : iopsTotalOutputCluster)
			iopsDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : bwTotalOutputCluster)
			bwDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : nsTotalOutputCluster)
			nsDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (TotalOutputCluster toc : ewTotalOutputCluster)
			ewDeltaOutputCluster.add(new TotalOutputCluster(toc, yearsOrder));

		for (OutputTable ot : cpuTotalOutput)
			cpuDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : ramTotalOutput)
			ramDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : storageTotalOutput)
			storageDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : iopsTotalOutput)
			iopsDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : bwTotalOutput)
			bwDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : nsTotalOutput)
			nsDeltaOutput.add(new OutputTable(ot, yearsOrder));

		for (OutputTable ot : ewTotalOutput)
			ewDeltaOutput.add(new OutputTable(ot, yearsOrder));

		sheet.setCpuTotalOutput(cpuTotalOutput);
		sheet.setCpuTotalOutputCluster(cpuTotalOutputCluster);
		sheet.setCpuDeltaOutput(cpuDeltaOutput);
		sheet.setCpuDeltaOutputCluster(cpuDeltaOutputCluster);

		sheet.setRamTotalOutput(ramTotalOutput);
		sheet.setRamTotalOutputCluster(ramTotalOutputCluster);
		sheet.setRamDeltaOutput(ramDeltaOutput);
		sheet.setRamDeltaOutputCluster(ramDeltaOutputCluster);

		sheet.setStorageTotalOutput(storageTotalOutput);
		sheet.setStorageTotalOutputCluster(storageTotalOutputCluster);
		sheet.setStorageDeltaOutput(storageDeltaOutput);
		sheet.setStorageDeltaOutputCluster(storageDeltaOutputCluster);

		sheet.setIopsTotalOutput(iopsTotalOutput);
		sheet.setIopsTotalOutputCluster(iopsTotalOutputCluster);
		sheet.setIopsDeltaOutput(iopsDeltaOutput);
		sheet.setIopsDeltaOutputCluster(iopsDeltaOutputCluster);

		sheet.setBwTotalOutput(bwTotalOutput);
		sheet.setBwTotalOutputCluster(bwTotalOutputCluster);
		sheet.setBwDeltaOutput(bwDeltaOutput);
		sheet.setBwDeltaOutputCluster(bwDeltaOutputCluster);

		sheet.setNsTotalOutput(nsTotalOutput);
		sheet.setNsTotalOutputCluster(nsTotalOutputCluster);
		sheet.setNsDeltaOutput(nsDeltaOutput);
		sheet.setNsDeltaOutputCluster(nsDeltaOutputCluster);

		sheet.setEwTotalOutput(ewTotalOutput);
		sheet.setEwTotalOutputCluster(ewTotalOutputCluster);
		sheet.setEwDeltaOutput(ewDeltaOutput);
		sheet.setEwDeltaOutputCluster(ewDeltaOutputCluster);

		return sheet;
	}

	private static Map<String, SiteDetails> calculateClusterTotals(Map<String, List<Blade>> placement) {
		Map<String, SiteDetails> map = new HashMap<String, SiteDetails>();

		for (Map.Entry<String, List<Blade>> entry : placement.entrySet()) {
			SiteDetails st = new SiteDetails();
			Double cpu = 0.0D;
			Double mem = 0.0D;
			Double txputNS = 0.0D;
			Double txputEW = 0.0D;
			Double txputTot = 0.0D;
			Double iopsRunning = 0.0D;
			Double iopsLoading = 0.0D;

			st.setBladeNumber(entry.getValue().size());

			for (Blade b : entry.getValue()) {
				cpu += b.getCoreUsed();
				mem += b.getRamUsed();

				for (VMGroup vmg : b.getVmGroupAssignedToThisBladeList()) {
					for (VirtualMachine vm : vmg.getVmList()) {
						txputNS += vm.getNorthSouthBW();
						txputEW += vm.getEastWestBW();
						txputTot += (vm.getNorthSouthBW() + vm.getEastWestBW());
						iopsRunning += vm.getRunningIOPS();
						iopsLoading += vm.getLoadingIOPS();
					}
				}
			}

			st.setCpu(cpu);
			st.setMem(mem);
			st.setTxputNS(txputNS);
			st.setTxputEW(txputEW);
			st.setTxputTot(txputTot);
			st.setIopsLoading(iopsLoading);
			st.setIopsRunning(iopsRunning);

			map.put(entry.getKey(), st);
		}

		return map;
	}

	/**
	 * @param cumulativeBladesType la lista dei dati sommati fino ad ora
	 * @param site                 il sito per il quale stiamo sommando i dati
	 * @param bladeType            la tipologia che stiamo contando
	 * @param cluster              il cluster
	 *                             <p>
	 *                             Questa funzione presi un sito e un cluster scorre
	 *                             la lista esistente alla ricerca di un elemento
	 *                             relativo ad essi. Nel caso in cui lo trovi
	 *                             aggiunge il valore relativo a quella combinazione
	 *                             sito/cluster all'elemento relativo. Altrimenti ne
	 *                             crea uno nuovo.
	 */
	private static List<OutputTableSpecific> cumulativeBladesTypeAdd(List<OutputTableSpecific> cumulativeBladesType,
			String site, String bladeType, Cluster cluster, Map<Integer, String> yearsOrder) {
		boolean found = false;

		for (OutputTableSpecific bt : cumulativeBladesType) {
			if (bt.getSite().equals(site) && bt.getComponentType().equals(bladeType)) {
				bt.addCumulativeYears(site, cluster, yearsOrder);
				found = true;
			}
		}

		if (!found)
			cumulativeBladesType.add(new OutputTableSpecific(site, bladeType, cluster, yearsOrder));

		return cumulativeBladesType;
	}

	/**
	 * @param site                  il sito per il quale stiamo sommando i dati
	 * @param map                   il totale da sommare
	 * @param cumulativeBladeTotals la lista da aggiornare
	 *                              <p>
	 *                              Questa funzione preso un sito e una mappa di
	 *                              valori, aggiorna la lista dei valori totali.
	 */
	private static List<OutputTable> addToTotals(String site, Map<String, List<Blade>> map,
			List<OutputTable> cumulativeBladeTotals, Map<Integer, String> yearsOrder, boolean isFoundation) {
		if (cumulativeBladeTotals.stream().filter(cbt -> cbt.getSite().equals(site)).findAny().isPresent()) {
			OutputTable temp = cumulativeBladeTotals.stream().filter(cbt -> cbt.getSite().equals(site)).findFirst()
					.get(); // recupero oggetto di quel sito

			Map<String, Integer> map2 = temp.getValueForYears(); // recupero la sua map
			Integer previous = 0;

			if (isFoundation) {
				int value = 0;
				for (Map.Entry<String, List<Blade>> mapEntry : map.entrySet()) {
					if (temp != null)
						value = mapEntry.getValue().size();
				}
				String anno = yearsOrder.get(1);

				if (map2.get(anno) != null)
					value = map2.get(anno) + value;

				map2.put(anno, value);

				int precedente = value;

				for (int i = 2; i <= 5; i++) {
					anno = yearsOrder.get(i);
					if (map2.get(anno) < precedente) {
						map2.put(anno, precedente);
					} else
						precedente = map2.get(anno);
				}
			} else {
				for (int i = 1; i <= 5; i++) { // Scorro gli anni
					String anno = yearsOrder.get(i);

					if (map2.containsKey(anno)) {
						int annoValue = 0;
						if (map.get(anno) != null)
							annoValue = map.get(anno).size(); // Verifico che valore otterrebbe quell'anno

						int newValue = map2.get(anno) + annoValue; // Verifico che valore otterrebbe quell'anno
						if (newValue > previous) { // Se è più grande lo inserisco
							map2.put(anno, newValue);
							previous = newValue;
						} else
							map2.put(anno, previous); // Altrimenti inserisco il valore dell'anno prima
					}
				}
			}

			temp.setValueForYears(map2);
		} else {
			OutputTable temp = new OutputTable();
			Map<String, Integer> map2 = new HashMap<String, Integer>();
			temp.setSite(site);
			Integer previous = 0;

			if (isFoundation) {
				int value = 0;
				for (Map.Entry<String, List<Blade>> mapEntry : map.entrySet()) {
					if (temp != null)
						value = mapEntry.getValue().size();
				}
				String anno = yearsOrder.get(1);

				if (map2.get(anno) != null)
					value = map2.get(anno) + value;

				map2.put(anno, value);
			} else {
				for (int i = 1; i <= 5; i++) { // Scorro gli anni
					String anno = yearsOrder.get(i);

					int newValue = 0;
					if (map.get(anno) != null)
						newValue = map.get(anno).size(); // Verifico che valore otterrebbe quell'anno

					if (newValue > previous) { // Se è più grande lo inserisco
						map2.put(anno, newValue);
						previous = newValue;
					} else
						map2.put(anno, previous);
				}
			}

			temp.setValueForYears(map2);
			cumulativeBladeTotals.add(temp);
		}

		return cumulativeBladeTotals;
	}

	private static List<TotalOutputCluster> addToTotalClusterSite(List<TotalOutputCluster> totalOutputCluster,
			PlacementResult pr, String cluster, Integer total) {
		boolean trovato = false;
		for (TotalOutputCluster toc : totalOutputCluster) { // Scorro gli elementi già creati
			if (toc.getSite().equals(pr.getSite()) && toc.getClusterName().equals(cluster)) { // Se ne esiste già uno
																								// per quella
																								// combinazione
																								// site/cluster
				trovato = true;

				Map<String, Integer> map = toc.getValueForYears(); // Allora recupero la mappa dei dati per anno
				map.put(pr.getYear(), total); // E aggiungo la somma del dato per quell'anno
				toc.setValueForYears(map); // E setto nuovamente la mappa
			}
		}

		if (!trovato) { // Se non c'era ancora una combinazione per quel site/cluster
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put(pr.getYear(), total); // Creo la mappa

			TotalOutputCluster temp = new TotalOutputCluster(); // Creo il nuovo elemento relativo a questa combinazione
			temp.setSite(pr.getSite()); // Setto i dati nel nuovo elemento
			temp.setClusterName(cluster);
			temp.setValueForYears(map);
			totalOutputCluster.add(temp); // Inserisco l'elemento nuovo nella lista
		}

		return totalOutputCluster;
	}

	private static List<OutputTable> addToTotalSite(List<OutputTable> totalOutput, PlacementResult pr, Integer total) {
		boolean trovato = false;
		for (OutputTable ot : totalOutput) { // Scorro gli elementi già creati
			if (ot.getSite().equals(pr.getSite())) { // Se ne esiste già uno per quella combinazione site/cluster
				trovato = true;

				Map<String, Integer> map = ot.getValueForYears(); // Allora recupero la mappa dei dati per anno

				if (map.get(pr.getYear()) != null)
					map.put(pr.getYear(), map.get(pr.getYear()) + total); // E aggiungo la somma del dato per quell'anno
				else
					map.put(pr.getYear(), total);

				ot.setValueForYears(map); // E setto nuovamente la mappa
			}
		}

		if (!trovato) { // Se non c'era ancora una combinazione per quel site/cluster
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put(pr.getYear(), total); // Creo la mappa

			OutputTable temp = new OutputTable(); // Creo il nuovo elemento relativo a questa combinazione
			temp.setSite(pr.getSite()); // Setto i dati nel nuovo elemento
			temp.setValueForYears(map);
			totalOutput.add(temp); // Inserisco l'elemento nuovo nella lista
		}

		return totalOutput;
	}


}
