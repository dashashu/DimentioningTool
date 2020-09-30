
package core.src.main.java.it.spindox.estimation.hp;

import java.util.*;
import java.util.stream.Collectors;

import core.src.main.java.it.spindox.estimation.ComponentEstimation;
import excelio.src.main.java.it.spindox.excelio.util.Util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import commons.src.main.java.it.spindox.model.catalog.CatalogConstants;
import commons.src.main.java.it.spindox.model.catalog.storage.Disk;
import commons.src.main.java.it.spindox.model.catalog.storage.DriveEnclosureDisk;
import commons.src.main.java.it.spindox.model.catalog.storage.ThreePar;
import commons.src.main.java.it.spindox.model.catalog.storage.ThreeParExpansion;
import commons.src.main.java.it.spindox.model.characterizations.ThreeParCharacterization;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Averages;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Estimation;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.EstimationLineDetail;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.VfException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalCellFormatException;
import commons.src.main.java.it.spindox.vfexception.excelio.IllegalEmptyCellException;
import commons.src.main.java.it.spindox.vfexception.excelio.InvalidInputFileException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;
/**
 * Created by fabrizio.sanfilippo on 13/04/2017.
 */
public class SanAreaEstimation extends ComponentEstimation {


    private static final Logger logger = (Logger) LogManager
            .getLogger(SanAreaEstimation.class);

    public static List<ThreeParCharacterization> tpcList = null;

    int numberOfEnclosureConnected = 0;

    public SanAreaEstimation() throws UnexpectedSituationOccurredException, IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException {
    }

    public SanAreaEstimation(Estimation estimation) throws UnexpectedSituationOccurredException, IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException {
        super(estimation);
    }

    public void executeBeforeEstimate() throws VfException {
        setTpcList();
    }

    @Override
    protected void estimateFoundation(String site) throws UnexpectedSituationOccurredException {

        ThreePar threePar = (ThreePar) estimation.getCatalog().getThreePar();

        EstimationLine threeParEstimationLine = new EstimationLine(threePar, threePar.getComponentDescription());
        threeParEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION, 1));
        estimation.getEstimationTable().get(site).add(threeParEstimationLine);

        evaluateThreeParExpansionForFoundation(site);
        estimateDiskAndDriveEnclosureDiskQuantityForFoundation(site);
    }

    private void estimateDiskAndDriveEnclosureDiskQuantityForFoundation(String site) throws UnexpectedSituationOccurredException {
    	Double avgReadWrite;
    	Double avgBlockSize;
    	Double avgIops;
    	Integer gbForInfrastructureManagement;
    	
    	if(estimation.isFoundation() && estimation.isManagement()) {
    		Averages avg = estimation.getAverages().stream().filter(av -> av.getSite().equals(site)).findFirst().get();
            if (avg.getAvgBlockSizeFoundation() == null && avg.getAvgIopsFoundation() == null
                    && avg.getAvgReadWriteFoundation() == null) {
                logger.warn("It is not possible to evaluate disk and drive enclosure disk quantity the foundation because its sheet is empty.");
                return;
            }
            
            avgReadWrite = avg.getAvgReadWriteFoundation();
        	avgBlockSize = avg.getAvgBlockSizeFoundation();
        	avgIops = avg.getAvgIopsFoundation();
        	gbForInfrastructureManagement = calculateGbForFoundation(site); //Capacità necessaria
    	} else {
    		avgReadWrite = estimation.getDefaultAvgReadWrite();
        	avgBlockSize = estimation.getDefaultAvgBlockSize();
        	avgIops = estimation.getDefaultAvgIops();
        	gbForInfrastructureManagement = estimation.getDefaultFoundationUsedSpace().intValue();
    	}    	

        ThreePar threePar = (ThreePar) estimation.getCatalog().getThreePar();
        Disk disk = (Disk) estimation.getCatalog().getDisk();
        DriveEnclosureDisk driveEnclosureDisk = (DriveEnclosureDisk) estimation.getCatalog().getDriveEnclosureDisk();

        int numberOfDiskForBlock = disk.getNumberOfDisksForBlock(); //Numero dischi in un blocco

        int numberOfBlockOfDiskHoused3Par = threePar.getMaxNumberOfDisksHoused() / numberOfDiskForBlock; //Numero blocchi in un 3Par
        int numberOfBlockOfDiskHousedDriveEnclosure = driveEnclosureDisk.getMaxNumOfHostableDisks() / numberOfDiskForBlock; //Numero blocchi in driveenclosure

        int usableBlockCapacity = disk.getUsableDiskCapacity() * numberOfDiskForBlock; //Capacità di un blocco
        
        int numberOfBlockToBuy = (int) Math.ceil(new Double(gbForInfrastructureManagement) / new Double(usableBlockCapacity)); //Numero blocchi da comprare

        int minDisk = filter3ParChar(avgBlockSize, avgReadWrite, avgIops);
        
        if (minDisk > new Double((numberOfDiskForBlock * numberOfBlockToBuy))) {
            numberOfBlockToBuy = minDisk / numberOfDiskForBlock;
        }

        if (numberOfBlockToBuy > numberOfBlockOfDiskHoused3Par) {
            int numberOfBlockToCoverWithExpansion = numberOfBlockToBuy - numberOfBlockOfDiskHoused3Par;
            int numberOfDriveEnclosureToBuy = (int) Math.ceil(new Double(numberOfBlockToCoverWithExpansion) / new Double(numberOfBlockOfDiskHousedDriveEnclosure));

            EstimationLine driveEnclosureDiskEstimationLine = new EstimationLine(driveEnclosureDisk, driveEnclosureDisk.getComponentDescription());
            driveEnclosureDiskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION, numberOfDriveEnclosureToBuy));
            estimation.getEstimationTable().get(site).add(driveEnclosureDiskEstimationLine);

            //QUANDO TU COMPRI UNA DRIVE COMPRI ANCHE UN BLOCCO
            numberOfBlockToBuy = numberOfBlockToBuy - numberOfDriveEnclosureToBuy;
        }

        EstimationLine diskEstimationLine = new EstimationLine(disk, disk.getComponentDescription());
        diskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION, (numberOfBlockToBuy)));
        estimation.getEstimationTable().get(site).add(diskEstimationLine);
    }

    private Integer calculateGbForFoundation(String site) {
        Integer tbNum = 0;
        Cluster foundationCluster = estimation.getClusterList().stream().filter(cluster -> cluster.isFoundation()).findFirst().get();
        Map<String, List<Blade>> infrastructureManagementPlacement = foundationCluster.getPlacementTable().getPlacementForSite(site);
        Set<String> yearNameSet = foundationCluster.getPlacementTable().getYear();

        for (String year : yearNameSet) {
            int hdUsed = 0;
            List<Blade> infrastructureManagementBlades = infrastructureManagementPlacement.get(year);
            for (Blade blade : infrastructureManagementBlades)
                hdUsed += blade.getHardDiskUsed();
            tbNum = hdUsed;
        }

        return tbNum;
    }

    private void evaluateThreeParExpansionForFoundation(String site) throws UnexpectedSituationOccurredException {

        ThreeParExpansion threeParExpansion = (ThreeParExpansion) estimation.getCatalog().getThreeParExpansion();
        ThreePar threePar = (ThreePar) estimation.getCatalog().getThreePar();

        //EVALUATE THE NUMBER OF THREEPAR EXPANSION TO BUY FOR FOUNDATION
//        List<EstimationLine> enclosureList = estimation.getEstimationTable().get(site).stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE)).collect(Collectors.toList());
        String normalEnclosureCode = estimation.getCatalog().getC7KDellStdEnclosure().getComponentId();
        String hpEnclosureCode = estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentId();
        
        EstimationLine threeParExpansionEstimationLine = new EstimationLine(threeParExpansion, threeParExpansion.getComponentDescription());
        
        List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals(normalEnclosureCode)).collect(Collectors.toList());
        EstimationLine enclosure = null;
        if(!temp.isEmpty())
        	enclosure = temp.get(0);
        
        temp = estimation.getEstimationTable().get(site).stream()
        		.filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
        		.filter(estimationLine -> estimationLine.getComponent().getComponentId().equals(hpEnclosureCode)).collect(Collectors.toList());
        EstimationLine enclosureHp = null;
        if(!temp.isEmpty())
        	enclosureHp = temp.get(0);

        int numberOfEnclosureForFoundation = countEnclosures(enclosure, enclosureHp);
        
        /*se il 3par supporta abbastanza enclosure da contenere tutte quelle necessarie per la foundation 
          non è necessario comprare 3par expansion, altrimenti si comprano*/
        if (threePar.getMaxEnclosureSupported() < numberOfEnclosureForFoundation) {

            int numberOfEnclosureInAdvance = numberOfEnclosureForFoundation - threePar.getMaxEnclosureSupported();
            int maxEnclosureSupported = threeParExpansion.getMaxEnclosureSupported();
            int numberOfExpansionToBuy = (int) Math.ceil(new Double(numberOfEnclosureInAdvance) / new Double(maxEnclosureSupported));
            
            //Se le espansioni da comprare sono più di quelle supportate dal 3par si lancia errore
            if (numberOfExpansionToBuy > threePar.getMaxExpansionSupported()) {
                throw new UnexpectedSituationOccurredException("The number of Expansion to Buy (" + numberOfExpansionToBuy + ") is greater than the Max number of expansions supported by 3PAR (" + threePar.getMaxExpansionSupported() + ")");
            } else {
                threeParExpansionEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION, numberOfExpansionToBuy));
            }
        } else {
            threeParExpansionEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.FOUNDATION, EstimationLineDetail.FOUNDATION, 0));
            logger.debug("No 3PAR Expansion Necessary");
        }

        estimation.getEstimationTable().get(site).add(threeParExpansionEstimationLine);
    }

    private void evaluateThreeParExpansionForInitiative(String site) throws UnexpectedSituationOccurredException {

        if (estimation.getEstimationTable().get(site).stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR)).count() == 1) {

            ThreeParExpansion threeParExpansion = (ThreeParExpansion) estimation.getCatalog().getThreeParExpansion();
            ThreePar threePar = (ThreePar) estimation.getCatalog().getThreePar();

            //EVALUATE THE NUMBER OF THREEPAR EXPANSION TO BUY FOR INITIATIVE
            String normalEnclosureCode = estimation.getCatalog().getC7KDellStdEnclosure().getComponentId();
            String hpEnclosureCode = estimation.getCatalog().getC7kDellHighPerfEnclosure().getComponentId();

            List<EstimationLine> temp = estimation.getEstimationTable().get(site).stream()
                    .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(estimationLine -> estimationLine.getComponent().getComponentId().equals(normalEnclosureCode)).collect(Collectors.toList());
            EstimationLine enclosure = null;
            if(!temp.isEmpty())
            	enclosure = temp.get(0);
            
            temp = estimation.getEstimationTable().get(site).stream()
                    .filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE))
                    .filter(estimationLine -> estimationLine.getComponent().getComponentId().equals(hpEnclosureCode)).collect(Collectors.toList());
            EstimationLine enclosureHp = null;
            if(!temp.isEmpty())
            	enclosureHp = temp.get(0);

            int enclosureFoundationQuantity = countEnclosures(enclosure, enclosureHp);

            EstimationLine threeParExpansionEstimationLine;
            int spaceAvailableInThreeParPlusExpansion = threePar.getMaxEnclosureSupported();

            if (estimation.getEstimationTable().get(site).stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR_EXPANSION)).count() == 1) {
                threeParExpansionEstimationLine = estimation.getEstimationTable().get(site).stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.THREEPAR_EXPANSION)).findFirst().get();
                int quantityOfExpansionForFoundation = threeParExpansionEstimationLine.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)).findFirst().get().getQuantity();

                spaceAvailableInThreeParPlusExpansion = spaceAvailableInThreeParPlusExpansion + (quantityOfExpansionForFoundation * threeParExpansion.getMaxEnclosureSupported());

            } else {
                threeParExpansionEstimationLine = new EstimationLine(threeParExpansion, threeParExpansion.getComponentDescription());
                estimation.getEstimationTable().get(site).add(threeParExpansionEstimationLine);
            }

            int emptySpaceForEnclosure = spaceAvailableInThreeParPlusExpansion - enclosureFoundationQuantity;

            logger.debug("Empty Space For Enclosure: " + emptySpaceForEnclosure);


            List<EstimationLineDetail> enclosureEstimationLineDetail = enclosure.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).collect(Collectors.toList());

            for (EstimationLineDetail yearEld : enclosureEstimationLineDetail) {

                int quantity = 0;

                if ((emptySpaceForEnclosure - yearEld.getQuantity()) > 0) {
                    emptySpaceForEnclosure = emptySpaceForEnclosure - yearEld.getQuantity();
                } else {
                    int numberOfEnclosureToCover = yearEld.getQuantity() - emptySpaceForEnclosure;
                    int maxEnclosureSupported = threeParExpansion.getMaxEnclosureSupported();
                    int numberOfExpansionToBuy = (int) Math.ceil(new Double(numberOfEnclosureToCover) / new Double(maxEnclosureSupported));

                    int numberOfExpansionAlreadyBuy = threeParExpansionEstimationLine.getEstimationLineDetail().stream().mapToInt(value -> value.getQuantity()).sum();

                    if (threePar.getMaxExpansionSupported() < (numberOfExpansionToBuy + numberOfExpansionAlreadyBuy)) {
                        throw new UnexpectedSituationOccurredException
                                ("The number of Expansion to Buy + The number already bought (" + (numberOfExpansionToBuy + numberOfExpansionAlreadyBuy) + ") is greater than the Max number of expansions supported by 3PAR (" + threePar.getMaxExpansionSupported() + ")");
                    } else {
                        quantity = numberOfExpansionToBuy;
                        emptySpaceForEnclosure = (numberOfExpansionToBuy * maxEnclosureSupported) - numberOfEnclosureToCover;
                    }


                }

                threeParExpansionEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, yearEld.getLineReference(), quantity));
            }
        } else {
            logger.debug("No 3PAR CAUSED BY NO FOUNDATION - IMPOSSIBLE DEFINE THE NUMBER OF EXPANSION TO BUY");
        }
    }


    @Override
    protected void estimateInitiative(String site) throws UnexpectedSituationOccurredException, IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException {

        if (estimation.isFoundation()) {
            //EVALUATE THE NUMBER OF THREEPAR EXPANSION TO BUY
            evaluateThreeParExpansionForInitiative(site);
        }
        estimateDiskAndDriveEnclosureDiskQuantityForInitiative(site);
    }

    private void estimateDiskAndDriveEnclosureDiskQuantityForInitiative(String site) throws UnexpectedSituationOccurredException {
        //load from the catalog
        ThreePar threePar = (ThreePar) estimation.getCatalog().getThreePar();
        Disk disk = (Disk) estimation.getCatalog().getDisk();
        DriveEnclosureDisk driveEnclosureDisk = (DriveEnclosureDisk) estimation.getCatalog().getDriveEnclosureDisk();

        //recover the disk estimation already loaded, if not there I put a new one
        Optional<EstimationLine> first = estimation.getEstimationTable().get(site).stream().filter(estimationLine ->
                estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DISK)
        ).findFirst();
        EstimationLine diskEstimationLine;
        if (first != null && first.isPresent()) {
            diskEstimationLine = first.get();
        } else {
            diskEstimationLine = new EstimationLine(disk, disk.getComponentDescription());
            estimation.getEstimationTable().get(site).add(diskEstimationLine);
        }

        //Recupero la disk estimation già caricata, se non c'è ne metto una nuova
        EstimationLine driveEnclosureDiskEstimationLine = null;
        Optional<EstimationLine> first1 = estimation.getEstimationTable().get(site).stream().filter(estimationLine ->
                estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.DRIVE_ENCLOSURE_DISK)
        ).findFirst();
        if (first1 != null && first1.isPresent()) {
            driveEnclosureDiskEstimationLine = first1.get();
        } else {
            driveEnclosureDiskEstimationLine = new EstimationLine(driveEnclosureDisk, driveEnclosureDisk.getComponentDescription());
            estimation.getEstimationTable().get(site).add(driveEnclosureDiskEstimationLine);
        }
        int quantityOfDisksBought = 0;
        int quantityOfDriveEnclosure = 0;
        int numberOfSlotAvailable = 0;
        int numberOfFreeSlot = 0;
        
        numberOfSlotAvailable = threePar.getMaxNumberOfDisksHoused(); //Slot disponibili nel 3par
        int gbAvailable = 0;
        
        //CALCOLO DEI GB DISPONIBILI DALLA FOUNDATION
        if(estimation.isFoundation()) {
	        //CALCOLO IL NUMERO DI DISCHI FINO AD ORA ACQUISTATI
	        Optional<EstimationLineDetail> opt = diskEstimationLine.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)).findFirst();
	        if (opt.isPresent()) {
	            quantityOfDisksBought = opt.get().getQuantity()*disk.getNumberOfDisksForBlock();
	        }
	
	        //CALCOLO IL NUMERO DI Drive Enclosure FINO AD ORA ACQUISTATI
	        //TODO quantity of D/E
	        opt = driveEnclosureDiskEstimationLine.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)).findFirst();
	        if (opt.isPresent()) {
	            quantityOfDriveEnclosure = opt.get().getQuantity();
	        }
	
	        numberOfSlotAvailable = numberOfSlotAvailable + (quantityOfDriveEnclosure * driveEnclosureDisk.getMaxNumOfHostableDisks()); //Aggiungo slot disponibili nelle drive enclosure
	        
	        //dischi acquistati totali = dischi acquistati + 1 blocco per ogni drive enclosure
	        quantityOfDisksBought = quantityOfDisksBought + (quantityOfDriveEnclosure*disk.getNumberOfDisksForBlock());
	        
	        Integer gbForInfrastructureManagement = 0;
	        
	        if(estimation.isManagement())
	        	gbForInfrastructureManagement = calculateGbForFoundation(site);
	        else
	        	gbForInfrastructureManagement = estimation.getDefaultFoundationUsedSpace().intValue();
	        
	        int numberofSlotUsed = (int)Math.ceil(new Double(gbForInfrastructureManagement) / new Double(disk.getUsableDiskCapacity())); //Calcolo la quantità di slot occupati
	        numberOfFreeSlot = numberOfSlotAvailable - numberofSlotUsed;
	        
	        //Gb disponibili negli elementi comprati per la foundation
	        gbAvailable = quantityOfDisksBought * disk.getUsableDiskCapacity();
	        gbAvailable-=gbForInfrastructureManagement;
        } else {
        	Double avgReadWrite = estimation.getDefaultAvgReadWrite();
        	Double avgBlockSize = estimation.getDefaultAvgBlockSize();
        	Double avgIops = estimation.getDefaultAvgIops();
        	
        	int diskNeeded = (int)Math.ceil(new Double(estimation.getDefaultFoundationUsedSpace()) / new Double(disk.getUsableDiskCapacity()));
    		int numBlockInFoundation = (int)Math.ceil(new Double(diskNeeded) / new Double(disk.getNumberOfDisksForBlock()));
    		quantityOfDisksBought = numBlockInFoundation*disk.getNumberOfDisksForBlock();
    		
    		int minDisk = filter3ParChar(avgBlockSize, avgReadWrite, avgIops);

            if (minDisk > new Double(quantityOfDisksBought)) {
            	quantityOfDisksBought = minDisk;
            }
            
        	//Caso in cui la foundation non sia presente
        	//Calcolo lo spazio usato dalla foundation come valore di default / capacità di un disco
        	if(estimation.getDefaultFoundationUsedSpace() <= (numberOfSlotAvailable*disk.getUsableDiskCapacity())) {
        		int numDiskIn3Par = threePar.getMaxNumberOfDisksHoused();
        		
        		//Spazio libero nei blocchi di dischi comprati
        		gbAvailable = (int)((quantityOfDisksBought * disk.getUsableDiskCapacity()) - estimation.getDefaultFoundationUsedSpace());
        		//Slot liberi per cui non sono stati comprati dischi
        		if(numDiskIn3Par>=quantityOfDisksBought)
        			numberOfFreeSlot = numDiskIn3Par - quantityOfDisksBought;
        		else {
        			int numberOfDiskOutside3Par = quantityOfDisksBought-numDiskIn3Par;
            		int numberOfEnclosureNeeded = (int)(Math.ceil(new Double(numberOfDiskOutside3Par) / new Double(driveEnclosureDisk.getMaxNumOfHostableDisks()))); //Conto quante D/E servono per contenere i dischi
            		numberOfFreeSlot = driveEnclosureDisk.getMaxNumOfHostableDisks()-(numberOfDiskOutside3Par-((numberOfEnclosureNeeded-1)*driveEnclosureDisk.getMaxNumOfHostableDisks()));
        		}
        	} else {
        		int gbOfDiskOutside3Par = (int)(estimation.getDefaultFoundationUsedSpace() - (numberOfSlotAvailable*disk.getUsableDiskCapacity())); //Conto quanti gb non possono essere messi dentro il 3Par
        		int numberOfDiskOutside3Par = (int)(new Double(gbOfDiskOutside3Par) / new Double(disk.getUsableDiskCapacity()));
        		
        		if(((numberOfSlotAvailable*disk.getUsableDiskCapacity()) + numberOfDiskOutside3Par) < quantityOfDisksBought)
        			numberOfDiskOutside3Par = quantityOfDisksBought - (numberOfSlotAvailable*disk.getUsableDiskCapacity());
        		
        		int numberOfEnclosureNeeded = (int)(Math.ceil(new Double(numberOfDiskOutside3Par) / new Double(driveEnclosureDisk.getMaxNumOfHostableDisks()))); //Conto quante D/E servono per contenere i dischi
        		
        		gbOfDiskOutside3Par = gbOfDiskOutside3Par - ((numberOfEnclosureNeeded-1) * driveEnclosureDisk.getMaxNumOfHostableDisks() * disk.getUsableDiskCapacity());
        		
        		int numDiskInLastEnclosure = (int)Math.ceil(new Double(gbOfDiskOutside3Par) / new Double(disk.getUsableDiskCapacity()));
        		int numBlockInLastEnclosure = (int)Math.ceil(new Double(numDiskInLastEnclosure) / new Double(disk.getNumberOfDisksForBlock()));
        		
        		//Al numero di Gb disponibili nelle enclosure da comprare tolgo il numero di Gb da piazzare, ottenendo il numero di Gb liberi
        		int gbEnclosure = driveEnclosureDisk.getMaxNumOfHostableDisks()*disk.getUsableDiskCapacity();
        		
        		//Spazio libero nei blocchi di dischi comprati
        		gbAvailable = gbEnclosure - gbOfDiskOutside3Par;
        		//Slot liberi per cui non sono stati comprati dischi
        		numberOfFreeSlot = driveEnclosureDisk.getMaxNumOfHostableDisks() - (numBlockInLastEnclosure*disk.getNumberOfDisksForBlock());
        	}
        }

        
        if (gbAvailable < 0) {
            throw new UnexpectedSituationOccurredException("You don't have enough enable slot from foundation");
        }

        int gbAvailableFoundation = gbAvailable;
        int quantityDisksBoughtInitiative = 0;
        
        Map<String, Integer> gbForYearBySiteByYear = calculateGbForYearBySite(site); //Calcolo lo storage necessario per l'iniziativa

        List<EstimationLine> estimationLinesEnclosure = estimation.getEstimationTable().get(site).stream().filter(estimationLine -> estimationLine.getComponent().getType().equalsIgnoreCase(CatalogConstants.ENCLOSURE)).collect(Collectors.toList());
        EstimationLine estimationLineEnclosure = null;
        
        for(EstimationLine el:estimationLinesEnclosure) {
        	for(EstimationLineDetail eld:el.getEstimationLineDetail()) {
        		if(eld.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)) {
        			estimationLineEnclosure = el;
        		}
        	}
        }

        //eld tutte le righe delle enclosure-initive (anno per anno)
        List<EstimationLineDetail> eld = estimationLineEnclosure.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.INITIATIVE)).collect(Collectors.toList());

        Averages avg = estimation.getAverages().stream().filter(av -> av.getSite().equals(site)).findFirst().get();
        
        //Ciclo per anno
        for (EstimationLineDetail el : eld) {
            String year = el.getLineReference();
            Integer gbForThatYear = null;

            //Controllo quanto occupano le blade piazzate quest'anno
            if(gbForYearBySiteByYear.isEmpty())
            	gbForThatYear = 0;
            else
            	gbForThatYear = gbForYearBySiteByYear.get(year);

            int numberOfDiskToBuy = 0;

            int minDisk = filter3ParChar(avg.getAvgBlockSize().get(year), avg.getAvgReadWrite().get(year), avg.getAvgIops().get(year));

            if (gbForThatYear <=  gbAvailable) { //Se gb necessari per anno < gb già disponibili tra foundation e dischi anni prima aggiungo linea
            	int numDisk = 0; //numero dischi da comprare
            	if (minDisk > new Double(quantityOfDisksBought)) { //Se il disco più piccolo va bene
                	numDisk = minDisk-quantityOfDisksBought;
                }
            	
                int numDriveEnclosureToBuy = 0;
                
                if(numberOfFreeSlot < numDisk) {
                	int slotToBuy = (numDisk-numberOfFreeSlot);
                	numDriveEnclosureToBuy = (int) Math.ceil(new Double(slotToBuy) / (driveEnclosureDisk.getMaxNumOfHostableDisks()));
                	numDisk-=numDriveEnclosureToBuy*disk.getNumberOfDisksForBlock(); //tolgo blocchi arrivati con enclosure dai dischi da compare
                	
                	numberOfFreeSlot+=numDriveEnclosureToBuy*driveEnclosureDisk.getMaxNumOfHostableDisks(); //aggiungo agli slot liberi gli slot delle D/E
                	numberOfFreeSlot-=numDisk+(numDriveEnclosureToBuy*disk.getNumberOfDisksForBlock()); //Tolgo i dischi da comprare più i dischi arrivati come omaggio
                }
                else
                	numberOfFreeSlot = numberOfFreeSlot - numDisk;
                
                int numBlockToBuy = (int) Math.ceil(new Double(numDisk) / (disk.getNumberOfDisksForBlock()));
                driveEnclosureDiskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, numDriveEnclosureToBuy));
                diskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, numBlockToBuy));
                
                //aggiorno i valori
                quantityDisksBoughtInitiative+=(numBlockToBuy*disk.getNumberOfDisksForBlock())+(numDriveEnclosureToBuy*disk.getNumberOfDisksForBlock());
                quantityOfDisksBought+=(numBlockToBuy*disk.getNumberOfDisksForBlock())+(numDriveEnclosureToBuy*disk.getNumberOfDisksForBlock());
                gbAvailable=gbAvailableFoundation+(quantityDisksBoughtInitiative*disk.getUsableDiskCapacity()); //per i prossimi anni, saranno disponibili i gb liberi in foundation e i gb dei dischi acquistati fin'ora
            } else {
                //Blocchi da comprare
            	int gbToBuy = gbForThatYear - gbAvailable;
            	
            	numberOfDiskToBuy = (int)Math.ceil(new Double((gbToBuy)) / new Double(disk.getUsableDiskCapacity()));
            	
                if (minDisk > new Double(numberOfDiskToBuy+quantityOfDisksBought)) { //Se il disco più piccolo va bene
                    numberOfDiskToBuy = minDisk-quantityOfDisksBought;
                }

                int numberOfBlockToBuy = (int)Math.ceil(new Double((numberOfDiskToBuy)) / new Double(disk.getNumberOfDisksForBlock()));
                
                if ((numberOfBlockToBuy*disk.getNumberOfDisksForBlock()) <= numberOfFreeSlot) { //Se i dischi da comprare sono meno degli slot disponibili, compro solo i dischi
                    diskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, numberOfBlockToBuy));
                    driveEnclosureDiskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, 0));
                    quantityOfDisksBought+=numberOfBlockToBuy*disk.getNumberOfDisksForBlock();
                    quantityDisksBoughtInitiative+=numberOfBlockToBuy*disk.getNumberOfDisksForBlock();
                    
                    numberOfFreeSlot = numberOfFreeSlot - (numberOfBlockToBuy * disk.getNumberOfDisksForBlock());
                    gbAvailable=gbAvailableFoundation+(quantityDisksBoughtInitiative*disk.getUsableDiskCapacity());
                } else { //Altrimenti calcolo gli slot ancora da comprare
                	int slotsToBuy = (numberOfBlockToBuy*disk.getNumberOfDisksForBlock()) - numberOfFreeSlot;
                	int numberOfDriveEnclosureToBuy = (int) Math.ceil(new Double(slotsToBuy) / (driveEnclosureDisk.getMaxNumOfHostableDisks()));
                    
                	numberOfBlockToBuy-=numberOfDriveEnclosureToBuy; //Tengo conto dei blocchi gratuiti che arrivano con le D/E
                    
                	driveEnclosureDiskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, numberOfDriveEnclosureToBuy));
                    diskEstimationLine.getEstimationLineDetail().add(new EstimationLineDetail(EstimationLineDetail.INITIATIVE, year, numberOfBlockToBuy));
                    
                    quantityOfDisksBought+=numberOfBlockToBuy*disk.getNumberOfDisksForBlock()+(numberOfDriveEnclosureToBuy*disk.getNumberOfDisksForBlock());
                    quantityDisksBoughtInitiative+=numberOfBlockToBuy*disk.getNumberOfDisksForBlock()+(numberOfDriveEnclosureToBuy*disk.getNumberOfDisksForBlock());
                    
                    numberOfFreeSlot+=numberOfDriveEnclosureToBuy * driveEnclosureDisk.getMaxNumOfHostableDisks();
                    numberOfFreeSlot = numberOfFreeSlot - (numberOfBlockToBuy * disk.getNumberOfDisksForBlock());
                    gbAvailable=gbAvailableFoundation+(quantityDisksBoughtInitiative*disk.getUsableDiskCapacity());
                }
            }
        }
    }


    private Map<String, Integer> calculateGbForYearBySite(String site) {
        Map<String, Integer> tbNum = new HashMap<String, Integer>();
        
        List<Cluster> clusterList = estimation.getClusterList().stream().filter(cluster -> !cluster.isFoundation()).collect(Collectors.toList());


        Set<String> yearNames = clusterList.get(0).getPlacementTable().getYear();

        for (String year : yearNames) {
            int hdUsed = 0;

            for (Cluster cluster : clusterList) {
                List<Blade> bladeList = cluster.getPlacementTable().getPlacementForSite(site).get(year);
                if(bladeList!=null && bladeList.size() > 0) {
	                for (Blade blade : bladeList)
	                    hdUsed += blade.getHardDiskUsed();
                }
            }

            tbNum.put(year, hdUsed);
        }

        return tbNum;
    }

    public void setTpcList() throws UnexpectedSituationOccurredException, IllegalCellFormatException, IllegalEmptyCellException, InvalidInputFileException {
        tpcList = Util.readThreeParCharSheet(estimation.getThreeparCharacterizationPath());
    }
    
    private int filter3ParChar(Double avgBlockSize, Double avgReadWrite, Double avgIops) {
    	List<ThreeParCharacterization> filteredList = new ArrayList<ThreeParCharacterization>();
        List<ThreeParCharacterization> filteredListPerIops = new ArrayList<ThreeParCharacterization>();
        filteredList = tpcList.stream().filter(t ->
                new Double(avgBlockSize).equals(new Double(t.getBlockSize())) &&
                        new Double(avgReadWrite).equals(new Double(t.getRead())) &&
                        new Double(estimation.getCatalog().getDisk().getDiskType()).equals(new Double(t.getDiskType())) &&
                        new Double(estimation.getCatalog().getDisk().getThreeParReference()).equals(new Double(t.getId())))
                .collect(Collectors.toList());

        if (filteredList.size() == 0) {
        	logger.warn("Foundation estimation: No line found in the 3ParCharacterization file for the following filters: "
        			+ "Block Size --> " + avgBlockSize
        					+ " | Read value --> " + avgReadWrite
        					+ " | Disk type --> " + estimation.getCatalog().getDisk().getDiskType()
        					+ " | ThreeParId --> " + estimation.getCatalog().getDisk().getThreeParReference());
        }

        filteredListPerIops = filteredList.stream().filter(t -> new Double(avgIops) <= new Double(t.getIops90())).collect(Collectors.toList());

		if (filteredListPerIops.size() == 0) {
        	logger.warn("Foundation estimation: No line found in the 3ParCharacterization file for the following filters: "
        			+ "Block Size --> " + avgBlockSize
        					+ " | Read value --> " + avgReadWrite
        					+ " | Disk type --> " + estimation.getCatalog().getDisk().getDiskType()
        					+ " | ThreeParId --> " + estimation.getCatalog().getDisk().getThreeParReference()
        					+ " | IOPS --> " + avgIops);
        	return 0;
		}
		
		Integer minDisk = null;
		
		for(ThreeParCharacterization tpc:filteredListPerIops) {
			if(minDisk == null)
				minDisk = tpc.getDiskQuantity();
			else if(minDisk > tpc.getDiskQuantity())
				minDisk = tpc.getDiskQuantity();
		}
		
		return minDisk;
    }
    
    private int countEnclosures(EstimationLine enclosure, EstimationLine enclosureHp) {
//    	int numberOfEnclosureForFoundation = enclosure!=null?(enclosure.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)).findFirst().get().getQuantity()):0;
//        numberOfEnclosureForFoundation+= enclosureHp!=null?(enclosureHp.getEstimationLineDetail().stream().filter(estimationLineDetail -> estimationLineDetail.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)).findFirst().get().getQuantity()):0;
    	
    	int num = 0;
    	
    	if(enclosure!=null) {
    		List<EstimationLineDetail> eldList = enclosure.getEstimationLineDetail();
    		for(EstimationLineDetail eld:eldList) {
    			if(eld.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)) {
    				num+=eld.getQuantity();
    			}
    		}
    	}
    	
    	if(enclosureHp!=null) {
    		List<EstimationLineDetail> eldList = enclosure.getEstimationLineDetail();
    		for(EstimationLineDetail eld:eldList) {
    			if(eld.getType().equalsIgnoreCase(EstimationLineDetail.FOUNDATION)) {
    				num+=eld.getQuantity();
    			}
    		}
    	}
    	
    	return num;
    }
}

