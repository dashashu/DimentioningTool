package core.src.main.java.it.spindox.estimation;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLineDetail;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BladeEstimation extends ComponentEstimation {

    public BladeEstimation() {
    }

    public BladeEstimation(Estimation estimation) {
        super(estimation);
    }


    @Override
    protected void estimateInitiative(String site) throws UnexpectedSituationOccurredException {
//        estimateInitiativeServiceCluster(site);
//        estimateInitiativeServiceManagement(site);

        List<Cluster> clusterList = estimation.getClusterList().stream().filter(cluster -> !cluster.isFoundation()).collect(Collectors.toList());

        for (Cluster cluster : clusterList) {
//            Map<String, List<Blade>> placementCluster = estimation.getServiceCluster().getPlacementForSite(site);

            Map<String, List<Blade>> placementCluster = cluster.getPlacementTable().getPlacementForSite(site);

            Set<String> yearServiceCluster = placementCluster.keySet();

            EstimationLine estimationLine;

            if (cluster.getClusterConfiguration().isHighPerformanceBladeFlag()) {
                estimationLine = new EstimationLine(estimation.getCatalog().getBladeHighPerformance(), estimation.getCatalog().getBladeHighPerformance().getComponentDescription() + " for Cluster " + cluster.getSheetLabel());
            } else {
                estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBlade().getComponentDescription() + " for Cluster " + cluster.getSheetLabel());
            }

            estimationLine.getEstimationLineDetail().addAll(getEstimationLineDetailForSinglePlacementAndYear(yearServiceCluster, placementCluster));
            estimation.getEstimationTable().get(site).add(estimationLine);
        }


    }

    @Override
    protected void estimateFoundation(String site) throws UnexpectedSituationOccurredException {

        int numberOfBladeToBuy = 0;

        EstimationLine estimationLine;
        if (estimation.isManagement()) {

            List<Cluster> foundationList = estimation.getClusterList().stream().filter(cluster -> cluster.isFoundation()).collect(Collectors.toList());
            Cluster foundationCluster = foundationList.get(0);
            Map<String, List<Blade>> infrastructureManagementPlacement;

            if (foundationList.size() > 0)
                infrastructureManagementPlacement = foundationList.get(0).getPlacementTable().getPlacementForSite(site);
            else
                throw new UnexpectedSituationOccurredException("No Foundation Table defined");

            Set<String> listOfYear = infrastructureManagementPlacement.keySet();
            if (listOfYear.size() > 1) {
                throw new UnexpectedSituationOccurredException("List of Year in infrastructure management major than 1");
            } else {
                for (String year : listOfYear) {
                    List<Blade> blades = infrastructureManagementPlacement.get(year);
                    numberOfBladeToBuy = blades.size();
                }
            }

            if (foundationCluster.getClusterConfiguration().isHighPerformanceBladeFlag())
                estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBladeHighPerformance().getComponentDescription());
            else
                estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBlade().getComponentDescription());
        } else {
            numberOfBladeToBuy = estimation.getCatalog().getBlade().getFoundationDefaultValue();
            estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBlade().getComponentDescription());
        }


        EstimationLineDetail estimationLineDetail = new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION);

        estimationLineDetail.setQuantity(numberOfBladeToBuy);
        estimationLine.getEstimationLineDetail().add(estimationLineDetail);
        estimation.getEstimationTable().get(site).add(estimationLine);


    }

//    private void estimateInitiativeServiceCluster(String site) throws UnexpectedSituationOccurredException {
//        Map<String, List<Blade>> placementServiceCluster = estimation.getServiceCluster().getPlacementForSite(site);
//        Set<String> yearServiceCluster = placementServiceCluster.keySet();
//
//        EstimationLine estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBlade().getComponentDescription() + " for Service Cluster");
//
//        estimationLine.getEstimationLineDetail().addAll(getEstimationLineDetailForSinglePlacementAndYear(yearServiceCluster, placementServiceCluster));
//        estimation.getEstimationTable().get(site).add(estimationLine);
//    }

    private List<EstimationLineDetail> getEstimationLineDetailForSinglePlacementAndYear(Set<String> yearAvailable, Map<String, List<Blade>> placement) throws UnexpectedSituationOccurredException {
        List<EstimationLineDetail> estimationLineDetailList = new ArrayList<>();

        int alreadyBuy = 0;

        for (String year : yearAvailable) {
            EstimationLineDetail estimationLineDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year);
            int toBuy = placement.get(year).size() - alreadyBuy;

            if (toBuy > 0) {
                estimationLineDetail.setQuantity(toBuy);
                alreadyBuy = alreadyBuy + toBuy;
            } else {
                estimationLineDetail.setQuantity(0);
            }
            estimationLineDetailList.add(estimationLineDetail);
        }
        return estimationLineDetailList;

    }


//    private void estimateInitiativeServiceManagement(String site) throws UnexpectedSituationOccurredException {
//        Map<String, List<Blade>> placementManagementCluster = estimation.getManagementCluster().getPlacementForSite(site);
//        Set<String> yearManagementCluster = placementManagementCluster.keySet();
//
//        EstimationLine estimationLine = new EstimationLine(estimation.getCatalog().getBlade(), estimation.getCatalog().getBlade().getComponentDescription() + " for Service Management");
//        estimationLine.getEstimationLineDetail().addAll(getEstimationLineDetailForSinglePlacementAndYear(yearManagementCluster, placementManagementCluster));
//
//        estimation.getEstimationTable().get(site).add(estimationLine);
//    }

    @Override
    public void executeBeforeEstimate() throws VfException {

    }


//	private Map<String, List<Blade>> sitePlacement = null;


//	public Map<Integer, Map<String, ComponentCooq>> getLines() {
//		CatalogEntry blade = catalog.getChosenComponent(Component.BLADE);
//
//		Map<String, ComponentCooq> bladeLine = new HashMap<String, ComponentCooq>();
//		Map<Integer, Map<String, ComponentCooq>> bladeLines = new HashMap<Integer, Map<String, ComponentCooq>>();
//		Map<Integer, ComponentCooq> bladeLineTemp = new HashMap<Integer, ComponentCooq>();
//		Map<String, Integer> numeroAnno = new HashMap<String, Integer>(); //mappa per essere sicuro di processare gli anni in ordine
//
//		Set<String> yearsKeySet = sitePlacement.keySet();
//		Iterator<String> iterator = yearsKeySet.iterator();
//		int i = 0;
//	    while(iterator.hasNext()) {
//	    	String year = iterator.next();
//	    	numeroAnno.put(year, i);
//	    	bladeLineTemp.put(i, new ComponentCooq(blade));
//	    	i++;
//	    }
//
//		int previousYearBladeNumber = 0;
//		i=0;
//
//		for(String year:yearsKeySet) {
//			//per ogni anno
//			int yearBladesNumber = sitePlacement.get(year).size(); //blade di quest'anno
//
//			if(yearBladesNumber<previousYearBladeNumber)
//				yearBladesNumber=previousYearBladeNumber;
//
//			int tmp = yearBladesNumber;
//			yearBladesNumber-=previousYearBladeNumber;
//			previousYearBladeNumber=tmp;
//
//			bladeLineTemp.get(i).setQuantity(yearBladesNumber);
//			i++;
//		}
//
//		iterator = yearsKeySet.iterator();
//	    while(iterator.hasNext()) {
//	    	String year = iterator.next();
//	    	bladeLine.put(year, bladeLineTemp.get(numeroAnno.get(year)));
//	    }
//
//	    Pricer pricer = new Pricer();
//	    bladeLine = pricer.calculatePrices(bladeLine, blade.getCost());
//
//	    bladeLines.put(blade.getComponentId(), bladeLine);
//
//		return bladeLines;
//	}
//
//	public void setCatalog(Catalog catalog) {
//		this.catalog = catalog;
//	}
//
//	public void setSitePlacement(Map<String, List<Blade>> sitePlacement) {
//		this.sitePlacement = sitePlacement;
//	}
}
