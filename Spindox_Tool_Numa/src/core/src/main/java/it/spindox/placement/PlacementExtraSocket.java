package core.src.main.java.it.spindox.placement;

import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Site;
import commons.src.main.java.it.spindox.model.placementAndEstimation.SocketBlade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VMGroup;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.vfexception.core.InconsistentConstraintsException;
import commons.src.main.java.it.spindox.vfexception.core.NotEnoughResourceAvailableException;






import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */

/**
 * In this class we implement the Placement inserting the vm also between two different sockets
 */
public class PlacementExtraSocket extends Placement {

    //private static final Logger logger = (Logger) LogManager.getLogger(Placement.class);
	final static Logger logger = Logger.getLogger(Placement.class);
	InputConfiguration inputconfig = new InputConfiguration();

    public PlacementExtraSocket(Site site, Cluster cluster) throws InconsistentConstraintsException, NotEnoughResourceAvailableException {

        super(site, cluster);
        toPlace = new ArrayList<>();
        toPlace.addAll(groupList);
    }

    List<VMGroup> alreadyPlaced = new ArrayList<>();
    List<VMGroup> toPlace = new ArrayList<>();

    /**
     * Sceglie un gruppo dalla toPlace
     *
     * @return
     */
    public VMGroup getGroupToInsert() {

        if (toPlace.size() == 0) return null;
        else return toPlace.get(0);

    }

    /**
     * Sceglie i gruppi che hanno delle affinità con il gruppo
     *
     * @param nwGroup
     * @return
     */

    protected List<VMGroup> getAFFGroup(VMGroup nwGroup) {
        List<VMGroup> rList = new ArrayList<>();

        Set<String> affOfOurGroup = new HashSet<>();
        for (VirtualMachine vm : nwGroup.getVmList()) {
            for (VirtualMachine virtualMachine : vm.getAff()) {

                boolean skip = false;

                for (VirtualMachine vm2 : nwGroup.getVmList()) {
                    if (vm2.getCompleteName().equalsIgnoreCase(virtualMachine.getCompleteName())) {
                        skip = true;
                    }
                }
                if (!skip)
                    affOfOurGroup.add(virtualMachine.getCompleteName());
            }
        }


        for (VMGroup vmGroup : toPlace) {

            boolean toAdd = false;

            for (VirtualMachine virtualMachine : vmGroup.getVmList()) {
                if (affOfOurGroup.contains(virtualMachine.getCompleteName())) {
                    toAdd = true;
                }
            }
            if (toAdd)
                rList.add(vmGroup);
        }

        return rList.stream().distinct().filter(vmGroup ->
                !vmGroup.getVmName().equalsIgnoreCase(nwGroup.getVmName())
        ).collect(Collectors.toList());
    }


    protected List<VMGroup> filterForVnName(List<VMGroup> vmGroupList) {

        Set<String> alreadyConsidered = new HashSet<>();
        List<VMGroup> rList = new ArrayList<>();

        for (VMGroup vmGroup : vmGroupList) {

            boolean canAdd = true;

            for (VirtualMachine virtualMachine : vmGroup.getVmList()) {
                if (alreadyConsidered.contains(virtualMachine.getVmName())) {
                    canAdd = false;
                }
            }
            if (canAdd) {
                rList.add(vmGroup);
                for (VirtualMachine virtualMachine : vmGroup.getVmList()) {
                    alreadyConsidered.add(virtualMachine.getVmName());
                }
            }
        }


        return rList;

    }

    private void allocateVmGroupAndItsPossibleAffinity(VMGroup nwGroup) {
        Blade choosenBlade = cluster.getBladeFactory().createBlade();
        allocate(choosenBlade, nwGroup, null);
        bladeList.add(choosenBlade);
    }

    protected boolean canAllocate(Blade blade, VMGroup nwGroup) {
        boolean zero = false;
        Double totalCores = nwGroup.getCore();
        //Ashutosh Numa blade check- Extra socket flow will not have NUMA
//        if(nwGroup.getVmList().stream().anyMatch(vm -> vm.isNumaflag())) {	
//        for(VirtualMachine vm: nwGroup.getVmList()) {      
//        	if((Double.compare((blade.getSocketList().get(0).getEffectiveCore()),(vm.getCore()))>=0 && (Double.compare((blade.getSocketList().get(0).getRam()),(vm.getRam()))>=0) )
//        	|| (Double.compare((blade.getSocketList().get(1).getEffectiveCore()),(vm.getCore()))>=0 && (Double.compare((blade.getSocketList().get(1).getRam()),(vm.getRam()))>=0))){ 
//        	
//        		return true;
//        	}else {
//        		logger.error(" Numa VM cannot be consider for placement for the VM " + nwGroup.getVmList().get(0).getVmName());
//        		System.exit(0);
//        		return false;
//        		
//        	}   	
//        }}
//         else {
        for (VirtualMachine vm : nwGroup.getVmList()) {
            if (vm.getHighThroughputCore()== 0)
                zero = true;
        }
        
        if(blade.getBladeThroughput()+nwGroup.getGroupThroughput() > blade.getMaxThroughputSupported()){
            return false;
        }

        if(blade.getBladeIopsRunning()+nwGroup.getGroupIopsRunning() > blade.getMaxRunningIopsSupported()){
            return false;
        }

        if (zero && !blade.isHighThroughputCoreZero())
            totalCores++;
        
        if (totalCores > blade.getCoreAvailable()) {
//            logger.debug("Pass to new Blade for Core full");
            return false;
        }

        if (nwGroup.getRam() > blade.getRamAvailable()) {
//            logger.debug("Pass to new Blade for Ram full");
            return false;
        }

        if (blade.checkIfNewGroupIsAAF(nwGroup)) {
//            logger.debug("Pass to new Blade for AAF: " + nwGroup.getVmListAsString());
            return false;
        }
//        }
		return true;
    }

    //Ricorsiva
    private void allocate(Blade blade, VMGroup nwGroup, String vmName) {

        boolean canAllocate = canAllocate(blade, nwGroup);

        if (canAllocate) {
            List<VirtualMachine> vmList = nwGroup.getVmList();
            for (VirtualMachine vm : vmList) {
                //Ashutosh NUMA
//            	if(vm.isNumaflag()) {
//            		placeNumaVm(vm, blade);
//            	}else
            		placeVm(vm, blade);
                if (vm.getHighThroughputCore() == 0)
                    blade.setHighThroughputCoreZero(true);
            }

            blade.getVmGroupAssignedToThisBladeList().add(nwGroup);

            int indexToRemove = 0;
            List<Integer> removelist = new ArrayList<Integer>(); 
            for (int j = 0; j < toPlace.size(); j++) {
                if (toPlace.get(j).getVmListAsString().equalsIgnoreCase(nwGroup.getVmListAsString())) {
                    indexToRemove = j;
                    removelist.add(indexToRemove);
                }
            }
            for(int index : removelist) {
            	toPlace.remove(index);
            }
            alreadyPlaced.add(nwGroup);

            List<VMGroup> affGroup = filterForVnName(getAFFGroup(nwGroup));

            //We filter the affgroup by eliminating the affinities that have already been inserted into the blade

            affGroup = affGroup.stream().filter(vmGroup -> {
                long count = blade.getVmGroupAssignedToThisBladeList().stream().filter(tmp -> tmp.getVmName().equalsIgnoreCase(nwGroup.getVmName())).count();
                return count == 0;
            }).collect(Collectors.toList());

            for (VMGroup vmGroup : affGroup) {
            	allocate(blade, vmGroup, nwGroup.getVmList().get(0).getCompleteName());
            }
        } else {
            logger.warn("Cannot satisfy soft affinity between VM " + vmName + " and VM " + nwGroup.getVmList().get(0).getVmName());
        }
    }


    /**
     * @param blade
     * @param nwGroup
     * @return true iif i can place the group false otherwise
     */
    protected boolean compressionAllocate(Blade blade, VMGroup nwGroup) {

        boolean canAllocate = canAllocate(blade, nwGroup);

        if (canAllocate) {
            List<VirtualMachine> vmList = nwGroup.getVmList();
            for (VirtualMachine vm : vmList) {
                placeVm(vm, blade);
                if (vm.getHighThroughputCore() == 0)
                    blade.setHighThroughputCoreZero(true);
                	System.out.println("High: "+(blade.isHighThroughputCoreZero()));
            }

            blade.getVmGroupAssignedToThisBladeList().add(nwGroup);
        }

        return canAllocate;
    }
    //Ashutosh NUMA
    private void placeNumaVm(VirtualMachine vm, Blade blade) {
        List<SocketBlade> socketList = blade.getSocketList();
        blade.setNumaBlade('Y');
        Double vmCore = vm.getCore() + (vm.getHighThroughputCoreQuantity());
        int index = 0;
        for (SocketBlade s : socketList) {//get 1st socket
        	if(vm.getSocketflag()== "") {//check if VM socket is empty
        		if (s.getCoreAvailable() > 0 && vmCore > 0) {
        			s.getVirtualMachineCoreOccupancy().put(vm, s.getCoreAvailable()); //as this socket is not going to use: s.getCoreAvailable()
                    vmCore = 0.0;
                    blade.setNumaSocket(vm.getSocketflag());//00 is empty
//                    blade.getSocketList().get(index).setCore(0);
//                    blade.getSocketList().get(index).setRam(0.0);
                    break;
        		}
        	}else {
    			s.getVirtualMachineCoreOccupancy().put(vm, s.getCoreAvailable()); //as this socket is not going to use: s.getCoreAvailable()
                vmCore = 0.0;
                blade.setNumaSocket(vm.getSocketflag());
//                blade.getSocketList().get(index).setCore(0);
//                blade.getSocketList().get(index).setRam(0.0);
                break;
        	}
        	index = +1;
        }
    }
    private void placeVm(VirtualMachine vm, Blade blade) {
        List<SocketBlade> socketList = blade.getSocketList();

        Double vmCore = vm.getCore() + (vm.getHighThroughputCoreQuantity());

        for (SocketBlade s : socketList) {


            if (s.getCoreAvailable() > 0 && vmCore > 0) {
            	if (vmCore < s.getCoreAvailable()) {
                    s.getVirtualMachineCoreOccupancy().put(vm, vmCore);
                    vmCore = 0.0;
                    break;
                } else {
                    vmCore = vmCore - s.getCoreAvailable();
                    s.getVirtualMachineCoreOccupancy().put(vm, s.getCoreAvailable());
                }

            }
        }
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
}
