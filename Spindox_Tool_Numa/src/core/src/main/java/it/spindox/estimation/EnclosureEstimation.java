package core.src.main.java.it.spindox.estimation;

import commons.src.main.java.it.spindox.model.catalog.CatalogConstants;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLineDetail;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import java.util.List;
import java.util.stream.Collectors;

public class EnclosureEstimation extends ComponentEstimation {
    private int freeSpaceOnFoundationEnclosure = 0;
    private int freeSpaceOnFoundationEnclosureHighPerformance = 0;

    public EnclosureEstimation() {
    }

    public EnclosureEstimation(Estimation estimation) {
        super(estimation);
    }

    @Override
    protected void estimateFoundation(String site) throws UnexpectedSituationOccurredException {

        int numberOfEnclosureToBuy = estimation.getCatalog().getC7KDellStdEnclosure().getDefaultValueFoundation();
        int bladeToCover = estimation.getCatalog().getC7KDellStdblade().getDefaultValueFoundation();
        int freeSpace = 0;
        int maxNumOfHostableUnit = estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit();

        if (estimation.isManagement()) {
            List<EstimationLine> estimationLineList = estimation.getEstimationTable().get(site);

            List<EstimationLine> foundationEstimationLine = estimationLineList.stream().filter(el -> {
                return el.getComponent() == estimation.getCatalog().getC7KDellStdblade()
                        && el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equals(EstimationLineDetail.FOUNDATION)).count() > 0;
            }).collect(Collectors.toList());

            if (foundationEstimationLine == null) {
                throw new UnexpectedSituationOccurredException("no foundation line found");
            }

            for (EstimationLine estimationLine : foundationEstimationLine) {
                for (EstimationLineDetail bladeLineFoundation : estimationLine.getEstimationLineDetail()) {
                    bladeToCover = bladeLineFoundation.getQuantity();
                }
            }
            numberOfEnclosureToBuy = (int) Math.ceil(new Double(bladeToCover) / new Double(maxNumOfHostableUnit));
        }

        freeSpace = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - bladeToCover;

        EstimationLine estimationLine = null;
        List<Cluster> foundationList = estimation.getClusterList().stream().filter(cluster -> cluster.isFoundation()).collect(Collectors.toList());
        int oneFlagCheck = 0;
        if (!foundationList.isEmpty()) {
            Cluster foundationCluster = foundationList.get(0);
            if (foundationCluster.getClusterConfiguration().isSynSigBladeFlag()) {
            	freeSpaceOnFoundationEnclosureHighPerformance = freeSpace;
            	 estimationLine = new EstimationLine(estimation.getCatalog().getSynSigEnclousre(), estimation.getCatalog().getSynSigEnclousre().getComponentDescription());
            	 oneFlagCheck = +1;
            }else if (foundationCluster.getClusterConfiguration().isSynMedBladeFlag()) {
                freeSpaceOnFoundationEnclosureHighPerformance = freeSpace;
                estimationLine = new EstimationLine(estimation.getCatalog().getSynMedEnclosure(), estimation.getCatalog().getSynMedEnclosure().getComponentDescription());
                oneFlagCheck = +1;
            } else if(foundationCluster.getClusterConfiguration().isSynDataBladeFlag()){
            	freeSpaceOnFoundationEnclosure = freeSpace;
            	estimationLine = new EstimationLine(estimation.getCatalog().getSynDataEnclosure(), estimation.getCatalog().getSynDataEnclosure().getComponentDescription());
            	oneFlagCheck = +1;
            } else if(foundationCluster.getClusterConfiguration().isC7KDellStdbladeFlag()) {
            	freeSpaceOnFoundationEnclosure = freeSpace;
            	estimationLine = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription());
            	oneFlagCheck = +1;
            } else if(foundationCluster.getClusterConfiguration().isC7kDellHighPerfBladeFlag()) {
                freeSpaceOnFoundationEnclosure = freeSpace;
                estimationLine = new EstimationLine(estimation.getCatalog().getC7kDellHighPerfEnclosure(), estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentDescription());
                oneFlagCheck = +1;
            }else {
            	throw new UnexpectedSituationOccurredException("No Blade choose for the cluster"+ " - "+site);
            }
            if (oneFlagCheck>1) 
            	throw new UnexpectedSituationOccurredException("Only one blade can be selected. But here multiple blades are seletced for the cluster: "+ foundationCluster.getSheetLabel());
        } else {
            freeSpaceOnFoundationEnclosure = freeSpace;
            estimationLine = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription());
        }

        EstimationLineDetail estimationLineDetail = new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION);
        estimationLineDetail.setQuantity(numberOfEnclosureToBuy);
        estimationLine.getEstimationLineDetail().add(estimationLineDetail);
        estimation.getEstimationTable().get(site).add(estimationLine);

    }

    @Override
    protected void estimateInitiative(String site) throws UnexpectedSituationOccurredException {
        estimateInitiativeClassic(site); //standard or 	c7KDellStdblade blade& enclosure 
        estimateInitiativeHighPerformance(site);//HP or c7kDellHighPerfBlade blade and Enclosure
        //synergy changes
        estimateInitiativeSynSig(site);
        estimateInitiativeSynMed(site);
        estimateInitiativeSynData(site);
    }

    private void estimateInitiativeSynData(String site) throws UnexpectedSituationOccurredException{

        int numberOfEnclosureToBuy = 0;

        int maxNumOfHostableUnit = estimation.getCatalog().getSynDataEnclosure().getMaxNumOfHostableUnit();
        String classicEnclosureId = estimation.getCatalog().getSynDataEnclosure().getComponentId();
        String classicBladeId = estimation.getCatalog().getSynDataBlade().getComponentId();//Synergy Data blade id
        
        EstimationLine estimationLineEnclosure;
        if (estimation.isFoundation()) {
        	List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
                    .filter(el -> el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(el -> el.getComponent().getComponentId().equals(classicEnclosureId)).collect(Collectors.toList());
        	
        	if(!temp.isEmpty())
        		estimationLineEnclosure = temp.get(0);
        	else {
        		estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
        		estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        	}
        } else {
            estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
            estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        }

        List<EstimationLine> bladeList =
                estimation.getEstimationTable().get(site).stream()
                        .filter(el -> el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).count() > 0)
                        .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
                        .filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(classicBladeId))
                        .collect(Collectors.toList());

        if(bladeList.size() > 0) {
	        EstimationLine estimationLine = bladeList.get(0);
	        for (EstimationLineDetail lineDetail : estimationLine.getEstimationLineDetail()) {
	            if (lineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {
	                EstimationLineDetail esEnclosureDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, lineDetail.getLineReference());
	
	                int bladeToPutForThisYear = lineDetail.getQuantity();
	
	                if (bladeList.size() > 1) {
	                    List<EstimationLineDetail> eldFiltered = bladeList.get(1).getEstimationLineDetail().stream().filter(eld -> eld.getLineReference().equalsIgnoreCase(lineDetail.getLineReference())).collect(Collectors.toList());
	                    for (EstimationLineDetail detail : eldFiltered) {
	                        bladeToPutForThisYear = bladeToPutForThisYear + detail.getQuantity();
	                    }
	                }
	
	                if (freeSpaceOnFoundationEnclosure == 0) {
	                    freeSpaceOnFoundationEnclosure = -bladeToPutForThisYear;
	                }
	
	
	                if (freeSpaceOnFoundationEnclosure > 0) {
	                    freeSpaceOnFoundationEnclosure = freeSpaceOnFoundationEnclosure - bladeToPutForThisYear;
	                }
	                if (freeSpaceOnFoundationEnclosure < 0) {
	                    int numberOfBladeToPut = Math.abs(freeSpaceOnFoundationEnclosure);
	                    numberOfEnclosureToBuy = (int) Math.ceil(new Double(numberOfBladeToPut) / new Double(maxNumOfHostableUnit));
	                    freeSpaceOnFoundationEnclosure = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - numberOfBladeToPut;
	                    esEnclosureDetail.setQuantity(numberOfEnclosureToBuy);
	                } else {
	                    esEnclosureDetail.setQuantity(0);
	                }
	
	                estimationLineEnclosure.getEstimationLineDetail().add(esEnclosureDetail);
	
	
	            }
	        }
        }
		
	}

	private void estimateInitiativeSynMed(String site) throws UnexpectedSituationOccurredException{
        int numberOfEnclosureToBuy = 0;

        int maxNumOfHostableUnit = estimation.getCatalog().getSynMedEnclosure().getMaxNumOfHostableUnit();
        String classicEnclosureId = estimation.getCatalog().getSynMedEnclosure().getComponentId();
        String classicBladeId = estimation.getCatalog().getSynMedBlade().getComponentId();//Synergy Media blade id
        
        EstimationLine estimationLineEnclosure;
        if (estimation.isFoundation()) {
        	List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
                    .filter(el -> el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(el -> el.getComponent().getComponentId().equals(classicEnclosureId)).collect(Collectors.toList());
        	
        	if(!temp.isEmpty())
        		estimationLineEnclosure = temp.get(0);
        	else {
        		estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
        		estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        	}
        } else {
            estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
            estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        }

        List<EstimationLine> bladeList =
                estimation.getEstimationTable().get(site).stream()
                        .filter(el -> el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).count() > 0)
                        .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
                        .filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(classicBladeId))
                        .collect(Collectors.toList());

        if(bladeList.size() > 0) {
	        EstimationLine estimationLine = bladeList.get(0);
	        for (EstimationLineDetail lineDetail : estimationLine.getEstimationLineDetail()) {
	            if (lineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {
	                EstimationLineDetail esEnclosureDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, lineDetail.getLineReference());
	
	                int bladeToPutForThisYear = lineDetail.getQuantity();
	
	                if (bladeList.size() > 1) {
	                    List<EstimationLineDetail> eldFiltered = bladeList.get(1).getEstimationLineDetail().stream().filter(eld -> eld.getLineReference().equalsIgnoreCase(lineDetail.getLineReference())).collect(Collectors.toList());
	                    for (EstimationLineDetail detail : eldFiltered) {
	                        bladeToPutForThisYear = bladeToPutForThisYear + detail.getQuantity();
	                    }
	                }
	
	                if (freeSpaceOnFoundationEnclosure == 0) {
	                    freeSpaceOnFoundationEnclosure = -bladeToPutForThisYear;
	                }
	
	
	                if (freeSpaceOnFoundationEnclosure > 0) {
	                    freeSpaceOnFoundationEnclosure = freeSpaceOnFoundationEnclosure - bladeToPutForThisYear;
	                }
	                if (freeSpaceOnFoundationEnclosure < 0) {
	                    int numberOfBladeToPut = Math.abs(freeSpaceOnFoundationEnclosure);
	                    numberOfEnclosureToBuy = (int) Math.ceil(new Double(numberOfBladeToPut) / new Double(maxNumOfHostableUnit));
	                    freeSpaceOnFoundationEnclosure = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - numberOfBladeToPut;
	                    esEnclosureDetail.setQuantity(numberOfEnclosureToBuy);
	                } else {
	                    esEnclosureDetail.setQuantity(0);
	                }
	
	                estimationLineEnclosure.getEstimationLineDetail().add(esEnclosureDetail);
	
	
	            }
	        }
        }
    
		
	
	}

	private void estimateInitiativeSynSig(String site) throws UnexpectedSituationOccurredException{
		// TODO Auto-generated method stub

        int numberOfEnclosureToBuy = 0;

        int maxNumOfHostableUnit = estimation.getCatalog().getSynSigEnclousre().getMaxNumOfHostableUnit();
        String classicEnclosureId = estimation.getCatalog().getSynSigEnclousre().getComponentId();
        String classicBladeId = estimation.getCatalog().getSynSigBlade().getComponentId();//Synergy signaling blade id
        
        EstimationLine estimationLineEnclosure;
        if (estimation.isFoundation()) {
        	List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
                    .filter(el -> el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(el -> el.getComponent().getComponentId().equals(classicEnclosureId)).collect(Collectors.toList());
        	
        	if(!temp.isEmpty())
        		estimationLineEnclosure = temp.get(0);
        	else {
        		estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
        		estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        	}
        } else {
            estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
            estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        }

        List<EstimationLine> bladeList =
                estimation.getEstimationTable().get(site).stream()
                        .filter(el -> el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).count() > 0)
                        .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
                        .filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(classicBladeId))
                        .collect(Collectors.toList());

        if(bladeList.size() > 0) {
	        EstimationLine estimationLine = bladeList.get(0);
	        for (EstimationLineDetail lineDetail : estimationLine.getEstimationLineDetail()) {
	            if (lineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {
	                EstimationLineDetail esEnclosureDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, lineDetail.getLineReference());
	
	                int bladeToPutForThisYear = lineDetail.getQuantity();
	
	                if (bladeList.size() > 1) {
	                    List<EstimationLineDetail> eldFiltered = bladeList.get(1).getEstimationLineDetail().stream().filter(eld -> eld.getLineReference().equalsIgnoreCase(lineDetail.getLineReference())).collect(Collectors.toList());
	                    for (EstimationLineDetail detail : eldFiltered) {
	                        bladeToPutForThisYear = bladeToPutForThisYear + detail.getQuantity();
	                    }
	                }
	
	                if (freeSpaceOnFoundationEnclosure == 0) {
	                    freeSpaceOnFoundationEnclosure = -bladeToPutForThisYear;
	                }
	
	
	                if (freeSpaceOnFoundationEnclosure > 0) {
	                    freeSpaceOnFoundationEnclosure = freeSpaceOnFoundationEnclosure - bladeToPutForThisYear;
	                }
	                if (freeSpaceOnFoundationEnclosure < 0) {
	                    int numberOfBladeToPut = Math.abs(freeSpaceOnFoundationEnclosure);
	                    numberOfEnclosureToBuy = (int) Math.ceil(new Double(numberOfBladeToPut) / new Double(maxNumOfHostableUnit));
	                    freeSpaceOnFoundationEnclosure = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - numberOfBladeToPut;
	                    esEnclosureDetail.setQuantity(numberOfEnclosureToBuy);
	                } else {
	                    esEnclosureDetail.setQuantity(0);
	                }
	
	                estimationLineEnclosure.getEstimationLineDetail().add(esEnclosureDetail);
	
	
	            }
	        }
        }
    
		
	}

	protected void estimateInitiativeClassic(String site) throws UnexpectedSituationOccurredException {
        int numberOfEnclosureToBuy = 0;

        int maxNumOfHostableUnit = estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit();
        String classicEnclosureId = estimation.getCatalog().getC7KDellStdEnclosure().getComponentId();
        String classicBladeId = estimation.getCatalog().getC7KDellStdblade().getComponentId();
        
        EstimationLine estimationLineEnclosure;
        if (estimation.isFoundation()) {
        	List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
                    .filter(el -> el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(el -> el.getComponent().getComponentId().equals(classicEnclosureId)).collect(Collectors.toList());
        	
        	if(!temp.isEmpty())
        		estimationLineEnclosure = temp.get(0);
        	else {
        		estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
        		estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        	}
        } else {
            estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7KDellStdEnclosure(), estimation.getCatalog().getC7KDellStdEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7KDellStdEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
            estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
        }

        List<EstimationLine> bladeList =
                estimation.getEstimationTable().get(site).stream()
                        .filter(el -> el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).count() > 0)
                        .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
                        .filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(classicBladeId))
                        .collect(Collectors.toList());

        if(bladeList.size() > 0) {
	        EstimationLine estimationLine = bladeList.get(0);
	        for (EstimationLineDetail lineDetail : estimationLine.getEstimationLineDetail()) {
	            if (lineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {
	                EstimationLineDetail esEnclosureDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, lineDetail.getLineReference());
	
	                int bladeToPutForThisYear = lineDetail.getQuantity();
	
	                if (bladeList.size() > 1) {
	                    List<EstimationLineDetail> eldFiltered = bladeList.get(1).getEstimationLineDetail().stream().filter(eld -> eld.getLineReference().equalsIgnoreCase(lineDetail.getLineReference())).collect(Collectors.toList());
	                    for (EstimationLineDetail detail : eldFiltered) {
	                        bladeToPutForThisYear = bladeToPutForThisYear + detail.getQuantity();
	                    }
	                }
	
	                if (freeSpaceOnFoundationEnclosure == 0) {
	                    freeSpaceOnFoundationEnclosure = -bladeToPutForThisYear;
	                }
	
	
	                if (freeSpaceOnFoundationEnclosure > 0) {
	                    freeSpaceOnFoundationEnclosure = freeSpaceOnFoundationEnclosure - bladeToPutForThisYear;
	                }
	                if (freeSpaceOnFoundationEnclosure < 0) {
	                    int numberOfBladeToPut = Math.abs(freeSpaceOnFoundationEnclosure);
	                    numberOfEnclosureToBuy = (int) Math.ceil(new Double(numberOfBladeToPut) / new Double(maxNumOfHostableUnit));
	                    freeSpaceOnFoundationEnclosure = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - numberOfBladeToPut;
	                    esEnclosureDetail.setQuantity(numberOfEnclosureToBuy);
	                } else {
	                    esEnclosureDetail.setQuantity(0);
	                }
	
	                estimationLineEnclosure.getEstimationLineDetail().add(esEnclosureDetail);
	
	
	            }
	        }
        }
    }

    protected void estimateInitiativeHighPerformance(String site) throws UnexpectedSituationOccurredException {
        int numberOfEnclosureToBuy = 0;

        int maxNumOfHostableUnit = estimation.getCatalog().getC7kDellHighPerfEnclosure().getMaxNumOfHostableUnit();
        String hpEnclosureId = estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentId();
        String hpBladeId = estimation.getCatalog().getC7kDellHighPerfBlade().getComponentId();


        EstimationLine estimationLineEnclosure;
        
        boolean isHp = false;
        for(EstimationLine el:estimation.getEstimationTable().get(site).stream().filter(el ->
        el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE) &&
                el.getComponent().getComponentId().equals(hpEnclosureId)).collect(Collectors.toList())) {
        	if(el.getComponent().getComponentId().equals(hpEnclosureId))
        		isHp = true;
        }
        
        if (estimation.isFoundation() && isHp) {
        	List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream().filter(el ->
            el.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE) &&
                    el.getComponent().getComponentId().equals(hpEnclosureId)).collect(Collectors.toList());
        	
        	if(!temp.isEmpty())
        		estimationLineEnclosure = temp.get(0);
        	else
        		estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7kDellHighPerfEnclosure(), estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7kDellHighPerfEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");

        } else {
            estimationLineEnclosure = new EstimationLine(estimation.getCatalog().getC7kDellHighPerfEnclosure(), estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentDescription() + " with " + estimation.getCatalog().getC7kDellHighPerfEnclosure().getMaxNumOfHostableUnit() + " Number of Unit Housed");
        }

        List<EstimationLine> bladeList =
                estimation.getEstimationTable().get(site).stream()
                        .filter(el -> el.getEstimationLineDetail().stream().filter(eld -> eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).count() > 0).collect(Collectors.toList());

        List<EstimationLine> bladeList2 = bladeList.stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE)).collect(Collectors.toList());

        List<EstimationLine> bladeList3 = bladeList2.stream().filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(hpBladeId)).collect(Collectors.toList());

//                        .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.BLADE))
//                        .filter(estimationLine -> estimationLine.getComponent().getComponentId().equalsIgnoreCase(hpBladeId))
//                        .collect(Collectors.toList());


        if(bladeList3.size() > 0) {
	        EstimationLine estimationLine = bladeList3.get(0);
	        for (EstimationLineDetail lineDetail : estimationLine.getEstimationLineDetail()) {
	            if (lineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {
	                EstimationLineDetail esEnclosureDetail = new EstimationLineDetail(EstimationLineDetail.INITIATIVE, lineDetail.getLineReference());
	
	                int bladeToPutForThisYear = lineDetail.getQuantity();
	
	                if (bladeList.size() > 1) {
	                    List<EstimationLineDetail> eldFiltered = bladeList.get(1).getEstimationLineDetail().stream().filter(eld -> eld.getLineReference().equalsIgnoreCase(lineDetail.getLineReference())).collect(Collectors.toList());
	                    for (EstimationLineDetail detail : eldFiltered) {
	                        bladeToPutForThisYear = bladeToPutForThisYear + detail.getQuantity();
	                    }
	                }
	
	                if (freeSpaceOnFoundationEnclosureHighPerformance == 0) {
	                    freeSpaceOnFoundationEnclosureHighPerformance = -bladeToPutForThisYear;
	                }
	
	
	                if (freeSpaceOnFoundationEnclosureHighPerformance > 0) {
	                    freeSpaceOnFoundationEnclosureHighPerformance = freeSpaceOnFoundationEnclosureHighPerformance - bladeToPutForThisYear;
	                }
	                if (freeSpaceOnFoundationEnclosureHighPerformance < 0) {
	                    int numberOfBladeToPut = Math.abs(freeSpaceOnFoundationEnclosureHighPerformance);
	                    numberOfEnclosureToBuy = (int) Math.ceil(new Double(numberOfBladeToPut) / new Double(maxNumOfHostableUnit));
	                    freeSpaceOnFoundationEnclosureHighPerformance = (numberOfEnclosureToBuy * maxNumOfHostableUnit) - numberOfBladeToPut;
	                    esEnclosureDetail.setQuantity(numberOfEnclosureToBuy);
	                } else {
	                    esEnclosureDetail.setQuantity(0);
	                }
	
	                estimationLineEnclosure.getEstimationLineDetail().add(esEnclosureDetail);
	
	
	            }
	        }
        }
        
        estimation.getEstimationTable().get(site).add(estimationLineEnclosure);
    }

    @Override
    public void executeBeforeEstimate() throws VfException {

    }


//	public Map<Integer, Map<String, ComponentCooq>> getLines() {
//		CatalogEntry enclosure = catalog.getChosenComponent(Component.ENCLOSURE);
//		bladeForEnclosure = catalog.getNumberOfBladeForEnclosure();
//		Map<Integer, Map<String, ComponentCooq>> enclosureLines = new HashMap<Integer, Map<String, ComponentCooq>>();
//		Map<String, ComponentCooq> enclosureLine = new HashMap<String, ComponentCooq>();
//		Map<Integer, ComponentCooq> enclosureLineTemp = new HashMap<Integer, ComponentCooq>();
//		Map<String, Integer> numeroAnno = new HashMap<String, Integer>(); //mappa per essere sicuro di processare gli anni in ordine
//
//		Set<String> yearsKeySet = sitePlacement.keySet();
//		Iterator<String> iterator = yearsKeySet.iterator();
//		int i = 0;
//	    while(iterator.hasNext()) {
//	    	String year = iterator.next();
//	    	numeroAnno.put(year, i);
//	    	enclosureLineTemp.put(i, new ComponentCooq(enclosure));
//	    	i++;
//	    }
//
//		int previousYearEnclosures = 0;
//		i=0;
//
//		for(String year:yearsKeySet) {
//			//per ogni anno
//			int yearEnclosureNumber = (int)Math.ceil((double)(sitePlacement.get(year).size()/bladeForEnclosure)); //blade di quest'anno
//
//			if(yearEnclosureNumber<previousYearEnclosures)
//				yearEnclosureNumber=previousYearEnclosures;
//
//			int tmp = yearEnclosureNumber;
//			yearEnclosureNumber-=previousYearEnclosures;
//			previousYearEnclosures=tmp;
//
//			enclosureLineTemp.get(i).setQuantity(yearEnclosureNumber);
//			i++;
//		}
//
//		iterator = yearsKeySet.iterator();
//	    while(iterator.hasNext()) {
//	    	String year = iterator.next();
//	    	enclosureLine.put(year, enclosureLineTemp.get(numeroAnno.get(year)));
//	    }
//
//	    Pricer pricer = new Pricer();
//	    enclosureLine = pricer.calculatePrices(enclosureLine, enclosure.getCost());
//
//	    enclosureLines.put(enclosure.getComponentId(), enclosureLine);
//
//		return enclosureLines;
//	}

}
