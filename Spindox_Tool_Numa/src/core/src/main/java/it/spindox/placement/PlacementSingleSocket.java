package core.src.main.java.it.spindox.placement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import commons.src.main.java.it.spindox.vfexception.core.InconsistentConstraintsException;
import org.apache.log4j.Logger;

import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Site;
import commons.src.main.java.it.spindox.model.placementAndEstimation.SocketBlade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VMGroup;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import core.src.main.java.it.spindox.utils.Utils;
import commons.src.main.java.it.spindox.vfexception.core.NotEnoughResourceAvailableException;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */
public class PlacementSingleSocket extends Placement {

    //private static final Logger logger = (Logger) LogManager.getLogger(Placement.class);
	final static Logger logger = Logger.getLogger(Placement.class);
	
	
    public PlacementSingleSocket(Site site, Cluster cluster) throws InconsistentConstraintsException, NotEnoughResourceAvailableException {

        super(site, cluster);

        toPlace = new ArrayList<>();
        toPlace.addAll(groupList);
        
    }

    @Override
    public void place() {
        VMGroup nwGroup;

        nwGroup = getGroupToInsert();
        while (nwGroup != null) {
            allocateVmGroupAndItsPossibleAffinity(nwGroup);
            nwGroup = getGroupToInsert();
        }
    }

    private void allocateVmGroupAndItsPossibleAffinity(VMGroup nwGroup) {
        Blade chosenBlade = cluster.getBladeFactory().createBlade();
        allocate(chosenBlade, nwGroup, null);
        bladeList.add(chosenBlade);
    }

    private void allocate(Blade blade, VMGroup nwGroup, String vmName) {
        boolean canAllocate = canAllocate(blade, nwGroup);

        if (canAllocate) {
            List<VirtualMachine> vmList = nwGroup.getVmList();
            placeVms(vmList, blade);

            blade.getVmGroupAssignedToThisBladeList().add(nwGroup);

            int indexToRemove = 0;
            for (int j = 0; j < toPlace.size(); j++) {
                if (toPlace.get(j).getVmListAsString().equalsIgnoreCase(nwGroup.getVmListAsString())) {
                    indexToRemove = j;
                }
            }

            toPlace.remove(indexToRemove);
            alreadyPlace.add(nwGroup);

            for (VMGroup vmGroup : filterForVnName(getAFFGroup(nwGroup)))
                allocate(blade, vmGroup, nwGroup.getVmList().get(0).getCompleteName());
        } else {
            logger.warn("Cannot satisfy soft affinity between VM " + vmName + " and VM " + nwGroup.getVmList().get(0).getVmName());
        }
    }

    protected boolean canAllocate(Blade blade, VMGroup nwGroup) {
    	
        if (cluster.getClusterConfiguration().isNumaFlag()) {
            int socketNeeded = 0;
            for (VirtualMachine vm : nwGroup.getVmList()) {
                socketNeeded += (int) Math.ceil((double) vm.getTotalCores() / blade.getSocketList().get(0).getCoreAvailable());
            }
            if (socketNeeded > blade.getSocketList().size())
                return false;
        }//This will not execute at all.As the cluster NUMA flag is not going to be in use.
        if(nwGroup.getVmList().stream().anyMatch(vm -> vm.isNumaflag())) {	
            for(VirtualMachine vm: nwGroup.getVmList()) {      
            	if((Double.compare((blade.getSocketList().get(0).getEffectiveCore()),(vm.getCore()))>=0 && (Double.compare((blade.getSocketList().get(0).getRam()),(vm.getRam()))>=0) )
            	|| (Double.compare((blade.getSocketList().get(1).getEffectiveCore()),(vm.getCore()))>=0 && (Double.compare((blade.getSocketList().get(1).getRam()),(vm.getRam()))>=0))){ 
            	
            		return true;
            	}else {
            		logger.error(" Numa VM cannot be consider for placement for the VM " + nwGroup.getVmList().get(0).getVmName());
            		System.exit(0);
            		return false;
            		
            	}   	
            }}
             else {
        boolean highTroughputConsidered = false; //It will be true if we have left a core free for highTroughput, or if the HT is never at 0 in the VMs
        boolean highTroughputToBeConsidered = blade.isHighThroughputCoreZero(); //L'HT è 0 in almeno una VM? Lo inizializziamo considerando il valore indicato dalla blade

        for (VirtualMachine vm : nwGroup.getVmList()) //I scroll through the VMs and if any have HT to 0 I update the value of highTroughputToBeConsidered
        	//changes 3.1.5
        	vm.setHighThroughputCore(cluster.getInputConfiguration().getDefaultHighThroughput());
        	if(cluster.getInputConfiguration().getDefaultHighThroughput() ==0) {
        		highTroughputToBeConsidered = true;
        	}
//        	if (vm.gethigetHighThroughputCore() == 0) {
//                highTroughputToBeConsidered = true;
//        	}
    		

        // I order VMs from largest to smallest by number of cores to optimize the space occupied inside the sockets
        nwGroup.setVmList(nwGroup.getVmList().stream().sorted((o1, o2) ->
                (int) (o2.getCore() + (cluster.getInputConfiguration().getDefaultHighThroughput() == -1 ? 0 : cluster.getInputConfiguration().getDefaultHighThroughput()))
                        - (int) (o1.getCore() + (cluster.getInputConfiguration().getDefaultHighThroughput() == -1 ? 0 : cluster.getInputConfiguration().getDefaultHighThroughput()))).collect(Collectors.toList()));


        List<VirtualMachine> toPlaceList = Utils.copyVmList(nwGroup.getVmList());
        for (SocketBlade s : blade.getSocketList()) {
            if (cluster.getClusterConfiguration().isNumaFlag() && !s.getVirtualMachineCoreOccupancy().isEmpty())
                continue;
            
            List<VirtualMachine> placedList = new ArrayList<VirtualMachine>(); //List of VMs inserted in this socket
            Double coresAvailableInThisSocket = s.getCoreAvailable();

            for (VirtualMachine vm : toPlaceList) {
                if (vm.getCore() + vm.getHighThroughputCoreQuantity() > s.getEffectiveCore()) { //Caso in cui si può dividere una VM su più socket
                    if (vm.getCore() - coresAvailableInThisSocket <= 0) { /*Se i core della vm sono meno dei core disponibili, allora in questo socket posso piazzare tutti i
                                                                        core, e parte dell'HT*/
                        vm.setCore(0.0); //I core della VM da piazzare ora sono 0
                        coresAvailableInThisSocket -= vm.getCore();
                        cluster.getInputConfiguration().setDefaultHighThroughput((int) (cluster.getInputConfiguration().getDefaultHighThroughput() - coresAvailableInThisSocket));
                        //vm.setHighThroughputCore((int) (vm.getHighThroughputCore() - coresAvailableInThisSocket)); //L'HT della VM da piazzare viene aggiornato
                    } else { //Se i core della VM sono più dei core disponibili, allora devo piazzare in questo socket parte dei core
                        vm.setCore(vm.getCore() - coresAvailableInThisSocket);
                    }
                    coresAvailableInThisSocket = 0.0;
                } else if (vm.getCore() + vm.getHighThroughputCoreQuantity() > coresAvailableInThisSocket) //Se invece non posso dividere per socket
                    continue; //Se questa vm non si può inserire dentro questo socket per mancanza di spazio, provo a inserire la prossima VM
                else {
                    coresAvailableInThisSocket -= vm.getCore() + vm.getHighThroughputCoreQuantity(); //Se posso inserire la vm, decremento il contatore dei core disponibili nel socket
                    placedList.add(vm);

                    if (cluster.getClusterConfiguration().isNumaFlag())
                        break;
                }
            	
            }

            if (!highTroughputToBeConsidered || coresAvailableInThisSocket > 0) //Se l'HT non va considerato oppure se è rimasto almeno 1 core libero nella blade, considero l'HT ok
                highTroughputConsidered = true;

            for (VirtualMachine vm : placedList) //Rimuovo le vm inserite dalla lista prima di passare al prossimo socket
                toPlaceList.remove(vm);
        }
        

        if (blade.getBladeThroughput()+nwGroup.getGroupThroughput() > blade.getMaxThroughputSupported()) {
            return false;
        }

        if (blade.getBladeIopsRunning()+nwGroup.getGroupIopsRunning() > blade.getMaxRunningIopsSupported()) {
            return false;
        }

        if (!highTroughputConsidered || !toPlaceList.isEmpty()) //Se non c'è spazio per l'HT o se non ho piazzato tutte le VM allora non posso allocare il gruppo
            return false;

        if (nwGroup.getRam() > blade.getRamAvailable()) {
//            logger.debug("Pass to new Blade for Ram full");
            return false;
        }

        if (blade.checkIfNewGroupIsAAF(nwGroup)) {
//            logger.debug("Pass to new Blade for AAF: " + nwGroup.getVmListAsString());
            return false;
        }
         }
        return true;
        
    }

    private void placeVms(List<VirtualMachine> vmList, Blade blade) {
        //	I order VMs from largest to smallest by number of cores to optimize the space occupied inside the sockets
        vmList = vmList.stream().sorted((o1, o2) -> o2.getTotalCores().compareTo(o1.getTotalCores())).collect(Collectors.toList());
        List<VirtualMachine> toPlaceList = Utils.copyVmList(vmList);

        for (SocketBlade s : blade.getSocketList()) {
            if (cluster.getClusterConfiguration().isNumaFlag() && !s.getVirtualMachineCoreOccupancy().isEmpty())
                continue;
            List<VirtualMachine> placedList = new ArrayList<VirtualMachine>();

            for (VirtualMachine vm : toPlaceList) {

            		if (cluster.getInputConfiguration().getDefaultHighThroughput() == 0)
                        blade.setHighThroughputCoreZero(true);
                    if (vm.getTotalCores() > s.getEffectiveCore()) { //Only case where it can be divided by socket
                    	
                        logger.warn("The Virtual Machine " + vm.getVmName() + " have " + vm.getTotalCores() + ". Since a socket can contain max " + s.getCore() + " cores, the VM will be split between sockets.");
                        int index = toPlaceList.indexOf(vm);
                        Double coreAvailable = s.getCoreAvailable();
                        s.getVirtualMachineCoreOccupancy().put(vmList.get(index), s.getCoreAvailable()); //This VM will occupy all the remaining cores in the socket
                        if (vm.getCore() - s.getCoreAvailable() <= 0) { /* If the vm cores are less than the available cores, then I can place all the in this socket
                                                                        core, and part of the HT*/
                            //if numa is on
                        	//if socket no.
                        	//if blade socket is empty.
                        	//if 

                        	coreAvailable -= vm.getCore();
                            vm.setCore(0.0); //VM cores to be placed are now 0

                            //Note: in this point high throughput will always be positive or 0
                            cluster.getInputConfiguration().setDefaultHighThroughput((int) (cluster.getInputConfiguration().getDefaultHighThroughput()  - coreAvailable)); //The High Throuhgput of the VM to be placed is updated
                        } else {
                            vm.setCore(vm.getCore() - coreAvailable); //If the VM cores are more than the available cores, then I have to place part of the cores in this socket
                        }
                    } else if (vm.getTotalCores() > s.getCoreAvailable()) //If instead I can't divide by socket
                        continue; //If this vm cannot be inserted into this socket due to lack of space, I try to insert the next VM
                    else {
                        int index = toPlaceList.indexOf(vm);
                        s.getVirtualMachineCoreOccupancy().put(vmList.get(index), vm.getTotalCores());
                        placedList.add(vm);

                        if (cluster.getClusterConfiguration().isNumaFlag())
                            break;
                    }
            	
            
            }
            	for (VirtualMachine vm1 : placedList) //Rimuovo le vm inserite dalla lista prima di passare al prossimo socket
                    toPlaceList.remove(vm1);
           //}
        }
    }
   

    protected boolean compressionAllocate(Blade blade, VMGroup nwGroup) {
        boolean canAllocate = canAllocate(blade, nwGroup);

        if (canAllocate) {
            placeVms(nwGroup.getVmList(), blade);
            blade.getVmGroupAssignedToThisBladeList().add(nwGroup);
        } else {
//        	logger.info("WE CAN'T ALLOCATE: " + blade.print() + "<------" + nwGroup.print());
        }

        return canAllocate;
    }
}