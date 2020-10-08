package core.src.main.java.it.spindox.placement;

import commons.src.main.java.it.spindox.model.placementAndEstimation.Blade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Site;
import commons.src.main.java.it.spindox.model.placementAndEstimation.SocketBlade;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VMGroup;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import core.src.main.java.it.spindox.utils.Utils;
import commons.src.main.java.it.spindox.vfexception.core.InconsistentConstraintsException;
import commons.src.main.java.it.spindox.vfexception.core.NotEnoughResourceAvailableException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import java.net.Socket;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public abstract class Placement {

    private static final Logger logger = (Logger) LogManager
            .getLogger(Placement.class);

    protected List<Blade> bladeList;

    //    @Deprecated
//    protected BladeFactory bladeFactory;
    protected Site site;
    protected Cluster cluster;

    List<VMGroup> alreadyPlace;
    List<VMGroup> toPlace;

    /**
     * IDENTIFICA TUTTE LE VIRTUAL MACHINES CHE NON SI POSSONO DIVIDERE TRA DI
     * LORO
     */
    protected List<VMGroup> groupList;
    

    protected List<VirtualMachine> vmList;

    public Placement(Site site, Cluster cluster)
            throws InconsistentConstraintsException, NotEnoughResourceAvailableException {
        bladeList = new ArrayList<>();
        groupList = new ArrayList<>();
        alreadyPlace = new ArrayList<>();
        toPlace = new ArrayList<>();
        this.site = site;
        vmList = site.getVmList();
        this.cluster = cluster;
//        this.bladeFactory = bladeFactory;

        checkConsistencyConstraints();//Check the aff/aaf rule
        divideVmWithHardConstraintsInGroupList();

    }

    /**
     * Sceglie un gruppo dalla toPlace
     *
     * @return
     */
    protected VMGroup getGroupToInsert() {
        if (toPlace.size() == 0)
            return null;
        else
            return toPlace.get(0);
    }

    protected void checkConsistencyConstraints()
            throws InconsistentConstraintsException {
        for (VirtualMachine vm : site.getVmList()) {
            List<VirtualMachine> aaf = vm.getAaf();
            List<VirtualMachine> aff = vm.getAff();
            for (VirtualMachine vma : aaf) {
                for (VirtualMachine vmb : aff) {
                    if (vma.getCompleteName().equalsIgnoreCase(
                            vmb.getCompleteName())) {
                        throw new InconsistentConstraintsException(
                                "Inconsistent constraints between "
                                        + vma.getVmName() + " and "
                                        + vmb.getVmName());
                    }
                }
            }
        }
    }

    protected void divideVmWithHardConstraintsInGroupList()
            throws NotEnoughResourceAvailableException {

        Set<String> wasteVM = new HashSet<>();
        //VMGroup vmGroupnuma = new VMGroup();

        for (VirtualMachine vm : vmList) {
        	//Ashutosh : NUMA
        	
    		if(vm.isNumaflag()) { 
    			VMGroup vmGroupnuma = new VMGroup();
    			vmGroupnuma.getVmList().add(vm);
    			groupList.add(vmGroupnuma);//adding a separate group for each Numa VMs.As there will be no affinity rule applicable
    			continue;
    		}
            if (!wasteVM.contains(vm.getCompleteName())) {
                List<VirtualMachine> aff = vm.selfAFF();

                // FILTER THE VMS THAT HAVE NOT ALREADY BEEN INSERTED IN A GROUP
                List<VirtualMachine> noWasteAff = Arrays
                        .asList(vm
                                .selfAFF()
                                .stream()
                                .filter(tmp -> !wasteVM.contains(tmp
                                        .getCompleteName()))
                                .toArray(VirtualMachine[]::new));

        		wasteVM.add(vm.getCompleteName());
                // THIS VM MUST STAY IN AFFINITY WITH SOMEONE
                if (aff.size() > 0) {
                    if (noWasteAff.size() > 0) {
                        // If there are still VMs to be paired
                        VMGroup vmGroup = new VMGroup();
                        vmGroup.getVmList().add(vm);
                        vmGroup.getVmList().add(noWasteAff.get(0));
                        wasteVM.add(noWasteAff.get(0).getCompleteName());

                        if (!canAllocate(cluster.getBladeFactory().createBlade(), vmGroup)) {
                            throw new NotEnoughResourceAvailableException("Not enough resources to place group: "

                                    + vmGroup.print() + " into the blade chosen: "
                                    + cluster.getBladeFactory().print());
                        }

                        groupList.add(vmGroup);
                    } else {

						//CASE where there are no VMs to pair,
						//therefore we add to an existing group the
						//  virtual machine under analysis
                        VMGroup vmGroup = null;
                        for (VMGroup vmgTmp : groupList) {
                            for (VirtualMachine vmTmp : vmgTmp.getVmList()) {
                                if (vmTmp.getVmName().equalsIgnoreCase(
                                        vm.getVmName())) {
                                    vmGroup = vmgTmp;
                                    break;
                                }
                            }
                        }
                        vmGroup.getVmList().add(vm);

                        if (!canAllocate(cluster.getBladeFactory().createBlade(), vmGroup)) {
                            throw new NotEnoughResourceAvailableException("Not enough resources to place group: "

                                    + vmGroup.print() + " into the blade chosen: "
                                    + cluster.getBladeFactory().print());
                        }


                    }
                } else {

                    // HAS NO AFFINITY RULES 
                    VMGroup vmGroup = new VMGroup();
                    vmGroup.getVmList().add(vm);
                    
                    
                    if (!canAllocate(cluster.getBladeFactory().createBlade(), vmGroup)) {
                    	
                        throw new NotEnoughResourceAvailableException("Not enough resources to place group: "
                                + vmGroup.print() + " into the blade chosen: "
                                + cluster.getBladeFactory().print());
                        
                    }
                    groupList.add(vmGroup);
                }
                wasteVM.add(vm.getCompleteName());
            }
        }
    }

    protected List<VMGroup> getAFFGroup(VMGroup nwGroup) {
        List<VMGroup> rList = new ArrayList<>();

        Set<String> affOfOurGroup = new HashSet<>();
        for (VirtualMachine vm : nwGroup.getVmList()) {
            for (VirtualMachine virtualMachine : vm.getAff()) {

                boolean skip = false;

                for (VirtualMachine vm2 : nwGroup.getVmList()) {
                    if (vm2.getCompleteName().equalsIgnoreCase(
                            virtualMachine.getCompleteName())) {
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

        return rList;
    }

    /**
     * Sceglie i gruppi che hanno delle affinità con il gruppo
     *
     * @param vmGroupList
     * @return
     */

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

    Set<Blade> bladeAlreadyVisited;


    private List<VirtualMachine> filteredAAFVM(Blade blade) {
        Set<VirtualMachine> virtualMachineToNotConsiderForNextBlade = new LinkedHashSet<>();

        List<Blade> bladeFull = bladeList.stream().filter(fullBlade -> {
//            int core = fullBlade.getCoreAvailable() + fullBlade.getCoreUsed();
//            double perc = new Double(fullBlade.getCoreUsed()) / new Double(core) * 100;

            return fullBlade.getOccupancyPercentage() > 95;
        }).collect(Collectors.toList());

        bladeFull.stream().forEach(bl -> {
            bl.getVmGroupAssignedToThisBladeList().stream().forEach(vmGroup -> {
                virtualMachineToNotConsiderForNextBlade.addAll(vmGroup.getVmList());
            });
        });

        List<VirtualMachine> aafVm;
        aafVm = blade.getAAFVm().stream().filter(virtualMachine -> !virtualMachineToNotConsiderForNextBlade.contains(virtualMachine)).collect(Collectors.toList());

        return aafVm;
    }

    private static int checkAAFForThisList(Blade blade, List<Blade> listOfBlade) {
        int valueAAF = 0;

        for (Blade tocheck : listOfBlade) {
            for (VMGroup vmGroup : blade.getVmGroupAssignedToThisBladeList()) {
                if (tocheck.checkIfNewGroupIsAAF(vmGroup)) {
                    valueAAF++;
                }
            }
        }
        return valueAAF;
    }

    private Blade nextBlade() {
        List<Blade> sortedBladeList = bladeList.stream()
           //     .sorted((o1, o2) -> filteredAAFVM(o2).size() - filteredAAFVM(o1).size())
                .filter(blade -> blade.getVmGroupAssignedToThisBladeList().size() > 0)
                .filter(blade -> !bladeAlreadyVisited.contains(blade)).collect(Collectors.toList());

        List<Blade> tmpSortedBladeList = new ArrayList<>(sortedBladeList);
        sortedBladeList.sort((o1, o2) -> {
            int cmp;

            cmp = Double.compare(o2.getCoreUsed(), o1.getCoreUsed());
            
            cmp = checkAAFForThisList(o2, tmpSortedBladeList) - checkAAFForThisList(o1, tmpSortedBladeList);
            if (cmp == 0)
                cmp = Double.compare(o2.getRamUsed(), o1.getRamUsed());
            if (cmp == 0)
                cmp = Integer.compare(o2.getBladeThroughput(), o1.getBladeThroughput());
            if (cmp == 0)
                cmp = Integer.compare(o2.getBladeIopsRunning(), o1.getBladeIopsRunning());


            return cmp;
        });

        if (sortedBladeList.size() > 0) {
            bladeAlreadyVisited.add(sortedBladeList.get(0));
            return sortedBladeList.get(0);
        } else return null;
    }

    public void bladeCompression() {

        bladeAlreadyVisited = new LinkedHashSet<>();

        Blade bladeToCompress;
        
      //Ashutosh NUMA
    	ArrayList<Blade> emptySocketBladelist = new ArrayList<Blade>();
    	ArrayList<Blade> zeroSocketBladelist = new ArrayList<Blade>();
    	ArrayList<Blade> Socket0Bladelist = new ArrayList<Blade>();
    	ArrayList<Blade> Socket1Bladelist = new ArrayList<Blade>();
    	
    	ArrayList<Blade> zeroSocketMergedBlades = new ArrayList<Blade>();
    	ArrayList<Blade> emptySocketBladelistToRemove = new ArrayList<Blade>();
    	
    	for(Blade blade:bladeList) {
    		if(blade.getNumaBlade() == 'Y') {
    			if(blade.getNumaSocket()=="") {//if socket E/0/1 choosen
    				//add to empty socket bladelist
    				HashMap<VirtualMachine, Double> map= new HashMap<VirtualMachine, Double>();
					map.put(blade.getVmGroupAssignedToThisBladeList().get(0).getVmList().get(0),(blade.getSocketList().get(0).getCore()));
					blade.getSocketList().get(0).setVirtualMachineCoreOccupancy(map);
					blade.getSocketList().get(0).setOcupiedCore(blade.getSocketList().get(0).getCore());
    				emptySocketBladelist.add(blade);
    			}else {
    				//add to 0/1 blade socket list
					HashMap<VirtualMachine, Double> map= new HashMap<VirtualMachine, Double>();
					map.put(blade.getVmGroupAssignedToThisBladeList().get(0).getVmList().get(0),(blade.getSocketList().get(0).getCore()));//VM & full core of a socket
					blade.getSocketList().get(Integer.parseInt(blade.getNumaSocket())).setVirtualMachineCoreOccupancy(map);
					blade.getSocketList().get(Integer.parseInt(blade.getNumaSocket())).setOcupiedCore(blade.getSocketList().get(0).getCore());//occuped core set to full.
					blade.getSocketList().get(Integer.parseInt(blade.getNumaSocket())).setNumaSocket(blade.getNumaSocket());//set numa socket
					zeroSocketBladelist.add(blade);
    			}
    		}
    	}	
    	
    	////merge the numa Vms to reduce the blades 
//		Collections.sort(emptySocketBladelist, (Blade o1,Blade o2) -> o1.getCoreAvailable().compareTo(o2.getCoreAvailable()));
//		Collections.sort(zeroSocketBladelist, (Blade o1,Blade o2) -> o1.getCoreAvailable().compareTo(o2.getCoreAvailable()));
		//1.considering 0,1 marked VMs & Empty socket VMs
		if(zeroSocketBladelist.size()>1 && emptySocketBladelist.size()>1) {
		for(Blade blade01: zeroSocketBladelist) {
			for( Blade bladeE : emptySocketBladelist) {
				if(!emptySocketBladelistToRemove.contains(bladeE)) {
					for ( SocketBlade socket : blade01.getSocketList()) {
						if(socket.getVirtualMachineCoreOccupancy().isEmpty()) {
							if (socket.getNumaSocket().equalsIgnoreCase(" ")) {
								socket.setNumaSocket((blade01.getNumaSocket().equalsIgnoreCase("0"))? "0":"1");
							}
							HashMap<VirtualMachine, Double> map= new HashMap<VirtualMachine, Double>();
							map.put(bladeE.getVmGroupAssignedToThisBladeList().get(0).getVmList().get(0),(bladeE.getSocketList().get(0).getEffectiveCore()));
							socket.setVirtualMachineCoreOccupancy(map);
							socket.setOcupiedCore(Math.floor(blade01.getSocketList().get(0).getEffectiveCore()));
							blade01.getVmGroupAssignedToThisBladeList().add(bladeE.getVmGroupAssignedToThisBladeList().get(0));
							emptySocketBladelistToRemove.add(bladeE);
							bladeList.remove(bladeE);
						}}
					}
				}
			}
		}//2.Considering only Numa-empty Socket blades
		else if(emptySocketBladelist.size()>1){
			for (int i = 0; i < emptySocketBladelist.size(); i++) {
				  for (int j = i+1; j < emptySocketBladelist.size(); j++) {
					  if(!emptySocketBladelistToRemove.contains(emptySocketBladelist.get(j)) && !emptySocketBladelistToRemove.contains(emptySocketBladelist.get(i))) {
					  for ( SocketBlade socket : emptySocketBladelist.get(i).getSocketList()) {
						  if(socket.getVirtualMachineCoreOccupancy().isEmpty()) {
							emptySocketBladelistToRemove.add(emptySocketBladelist.get(i));
							emptySocketBladelistToRemove.add(emptySocketBladelist.get(j));
						  }}
					 }
				 }
			}
			for (int i = 0; i < emptySocketBladelistToRemove.size()-1;i = i+2) {
				int j=i+1;
				  //for (int j = i+1; j < emptySocketBladelistToRemove.size()) {
					  for ( SocketBlade socket : emptySocketBladelistToRemove.get(i).getSocketList()) {
						  if(socket.getVirtualMachineCoreOccupancy().isEmpty()) {
								HashMap<VirtualMachine, Double> map= new HashMap<VirtualMachine, Double>();
								map.put(emptySocketBladelistToRemove.get(j).getVmGroupAssignedToThisBladeList().get(0).getVmList().get(0),(emptySocketBladelistToRemove.get(j).getSocketList().get(0).getEffectiveCore()));
								socket.setVirtualMachineCoreOccupancy(map);
								socket.setOcupiedCore(Math.floor(emptySocketBladelistToRemove.get(j).getSocketList().get(0).getEffectiveCore()));
								//socket.setNumaSocket("0");//just to identify in future
								emptySocketBladelistToRemove.get(i).getVmGroupAssignedToThisBladeList().add(emptySocketBladelistToRemove.get(j).getVmGroupAssignedToThisBladeList().get(0));
								zeroSocketMergedBlades.add(emptySocketBladelistToRemove.get(i));
								ArrayList<String> socketlist = new ArrayList<String>( Arrays.asList("1","0")); 	
								emptySocketBladelistToRemove.get(i).getSocketList().get(0).setNumaSocket("0");
								emptySocketBladelistToRemove.get(i).getSocketList().get(1).setNumaSocket("1");
								bladeList.remove(emptySocketBladelistToRemove.get(j));
						  }
					  }
				  //}
			}
		}
		//3.Considering only 0,1 Socket blades
		else if(zeroSocketBladelist.size()>1){
			//devide the blades to 0 & 1 lists
			for(Blade blade: zeroSocketBladelist) {
				if (blade.getNumaSocket().equals("1") ){
					blade.getSocketList().get(1).setNumaSocket("1");
					Socket1Bladelist.add(blade);
				}else if(blade.getNumaSocket().equals("0")) {
					blade.getSocketList().get(0).setNumaSocket("0");
					Socket0Bladelist.add(blade);
				}
			}
			//merging both lists
			if(Socket0Bladelist.size()>1 && Socket1Bladelist.size()>1) {
			for(Blade blade0: Socket0Bladelist) {
				for( Blade blade1 : Socket1Bladelist) {
					if(!emptySocketBladelistToRemove.contains(blade1)) {
						for ( SocketBlade socket : blade0.getSocketList()) {
							
							if(socket.getVirtualMachineCoreOccupancy().isEmpty()) {
								HashMap<VirtualMachine, Double> map= new HashMap<VirtualMachine, Double>();
								map.put(blade1.getVmGroupAssignedToThisBladeList().get(0).getVmList().get(0),(blade1.getSocketList().get(0).getEffectiveCore()));
								socket.setVirtualMachineCoreOccupancy(map);
								socket.setOcupiedCore(Math.floor(blade1.getSocketList().get(0).getEffectiveCore()));
								blade0.getVmGroupAssignedToThisBladeList().add(blade1.getVmGroupAssignedToThisBladeList().get(0));
								emptySocketBladelistToRemove.add(blade1);
								bladeList.remove(blade1);
							}}
						}
					}
				}
			}
		}

		//4.considering one empty socket VM
//		else if(emptySocketBladelist.size()==1){
//			for (Blade blade: emptySocketBladelist) {
//				for ( SocketBlade socket : blade.getSocketList()) {
//					if(!socket.getVirtualMachineCoreOccupancy().isEmpty()) {
//						socket.setOcupiedCore(Math.floor(blade.getSocketList().get(0).getEffectiveCore()));
//					}
//				}
//			}
//		}
//		//5.considering one empty socket VM
//		else if(zeroSocketBladelist.size()==1){
//			for (Blade blade: zeroSocketBladelist) {
//				for ( SocketBlade socket : blade.getSocketList()) {
//					if(!socket.getVirtualMachineCoreOccupancy().isEmpty()) {
//						socket.setOcupiedCore(Math.floor(blade.getSocketList().get(0).getEffectiveCore()));
//					}
//				}
//			}
//		}
		
		//remove the blades which can merge
		bladeList.removeAll(emptySocketBladelistToRemove);
		//add merged VM blades- case2: Numa empty sockets
		bladeList.addAll(zeroSocketMergedBlades);  
		
    	//ignore the blade with both socket numa for now
    	ArrayList<Blade> removeBladeList = new ArrayList<Blade>();
    	for(Blade blade :bladeList) {
    		if(!blade.getSocketList().get(0).getVirtualMachineCoreOccupancy().isEmpty() && !blade.getSocketList().get(1).getVirtualMachineCoreOccupancy().isEmpty()) {	
    			removeBladeList.add(blade);
    		}	}
    	bladeList.removeAll(removeBladeList);
    	ArrayList<Blade> removeblades = new ArrayList<Blade>();
        while ((bladeToCompress = nextBlade()) != null) {
        	//old Numa logic : cluster level
            if (cluster.getClusterConfiguration().isNumaFlag()) {
                boolean emptySocket = false;

                for (SocketBlade sb : bladeToCompress.getSocketList())
                    if (sb.getVirtualMachineCoreOccupancy().isEmpty())
                        emptySocket = true;

                if (!emptySocket)
                    continue;
            }
            //new NUMa logic : VM level
            if (bladeToCompress.getNumaBlade()=='Y') {
                boolean emptySocket = false;

                for (SocketBlade sb : bladeToCompress.getSocketList())
                    if (sb.getVirtualMachineCoreOccupancy().isEmpty())
                        emptySocket = true;

                if (!emptySocket)
                    continue;
            }

            List<Blade> tmpBladeList = new ArrayList<>(bladeList);
            bladeList.sort((o1, o2) -> {
                int cmp;
                cmp = checkAAFForThisList(o2, tmpBladeList) - checkAAFForThisList(o1, tmpBladeList);

                if (cmp == 0)
                	cmp = Double.compare(o2.getCoreUsed(), o1.getCoreUsed());
                if (cmp == 0)
                    cmp = Double.compare(o2.getRamUsed(), o1.getRamUsed());
                if (cmp == 0)
                    cmp = Integer.compare(o2.getBladeThroughput(), o1.getBladeThroughput());
                if (cmp == 0)
                    cmp = Integer.compare(o2.getBladeIopsRunning(), o1.getBladeIopsRunning());

                return cmp;
            });

            //FOR ALL THE Non- NUMA BLADES THAT ARE STILL FULL
            for (Blade analyzeBlade : bladeList) {

                if (!analyzeBlade.equals(bladeToCompress)) {
                	if(analyzeBlade.getNumaBlade()!='Y' && bladeToCompress.getNumaBlade()!='Y') {
                    VMGroup tryToPutGroup = new VMGroup();
                    List<VirtualMachine> vmInAnalyzeBlade = new ArrayList<VirtualMachine>();

                    for (VMGroup vmGroup : analyzeBlade.getVmGroupAssignedToThisBladeList()) {
                        vmInAnalyzeBlade.addAll(vmGroup.getVmList());
                    }
                    tryToPutGroup.setVmList(vmInAnalyzeBlade);

                    if (canAllocate(bladeToCompress, tryToPutGroup)) {

                        for (VMGroup vmGroup : analyzeBlade
                                .getVmGroupAssignedToThisBladeList()) {
                            compressionAllocate(bladeToCompress, vmGroup);
                        }

                        analyzeBlade.getVmGroupAssignedToThisBladeList().clear();
                        for (SocketBlade socketBlade : analyzeBlade.getSocketList()) {
                            socketBlade.getVirtualMachineCoreOccupancy()
                                    .clear();
                        }
                    }
                	}
                }
            }

        bladeList.removeAll(bladeList
                .stream()
                .filter(blade2 -> blade2.getVmGroupAssignedToThisBladeList()
                        .size() == 0).collect(Collectors.toList()));
       
    }
    bladeList.addAll(removeBladeList);   
		
    }
    
    
    private List<String> getSortingOrder(List<Blade> bladeList) {
		Map<String, Double> order = new HashMap<String, Double>();
		List<String> orderList = new ArrayList<String>();

    	Double totalCores = 0.0D;
        Double totalRam = 0.0D;
		Double totalThroughput = 0.0D;
		Double totalIOPS = 0.0D;
		
        int vmNumber = 0;
        for(Blade b:bladeList) {
        	for(VMGroup g:b.getVmGroupAssignedToThisBladeList()) {
        		for(VirtualMachine v:g.getVmList()) {
        			vmNumber++;
        			totalCores+=v.getTotalCores();
        			totalRam+=v.getRam();
					totalThroughput+=v.getNSEWThroughput();
					totalIOPS+=v.getRunningIOPS();
        		}
        	}
        }
        Double mediumCores = totalCores/vmNumber;
        Double mediumRam = totalRam/vmNumber;
		Double mediumThroughput = totalThroughput/vmNumber;
		Double mediumIOPS = totalIOPS/vmNumber;
        Double coresProportion = mediumCores / cluster.getBladeFactory().getTotalCores();
        Double ramProportion = mediumRam / cluster.getBladeFactory().getRamAvailable();
		Double throughputProportion = mediumThroughput / cluster.getBladeFactory().getRamAvailable();
		Double IOPSProportion = mediumIOPS / cluster.getBladeFactory().getRamAvailable();
		
		if(coresProportion.isNaN())
			coresProportion = 0.0D;
		if(ramProportion.isNaN())
			ramProportion = 0.0D;
		if(throughputProportion.isNaN())
			throughputProportion = 0.0D;
		if(IOPSProportion.isNaN())
			IOPSProportion = 0.0D;
		
		order.put("cores", coresProportion);
		order.put("ram", ramProportion);
		order.put("throughput", throughputProportion);
		order.put("IOPS", IOPSProportion);
		
		for(int i=0; i<4; i++) {
			String biggest = getBiggest(order);
			order.remove(biggest);
			orderList.add(0, biggest);
		}
		
        return orderList;
    }
	
	private String getBiggest(Map<String, Double> orderMap) {
		String result = "";
		Double max = 0.0D;
		
		for (Map.Entry<String, Double> entry : orderMap.entrySet())
		{
			if(entry.getValue() >= max) {
				result = entry.getKey();
				max = entry.getValue();
			}
		}
		
		return result;
	}

    protected abstract boolean compressionAllocate(Blade blade, VMGroup nwGroup);

    protected abstract boolean canAllocate(Blade blade, VMGroup nwGroup);

    public void addSpareBlades(int spareNumber) {
        int spareBladesNumber = Utils.calcolateSpare(bladeList.size(), spareNumber);
        List<Blade> spareBlades = new ArrayList<Blade>();

        for (int i = 0; i < spareBladesNumber; i++)
            spareBlades.add(cluster.getBladeFactory().createSpareBlade());

        bladeList.addAll(spareBlades);
    }

    public abstract void place();

    public List<Blade> getBladeList() {
        return bladeList;
    }

    public void setBladeList(List<Blade> bladeList) {
        this.bladeList = bladeList;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public List<VMGroup> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<VMGroup> groupList) {
        this.groupList = groupList;
    }

    public List<VirtualMachine> getVmList() {
        return vmList;
    }

    public void setVmList(List<VirtualMachine> vmList) {
        this.vmList = vmList;
    }

    public Cluster getCluster() {
        return cluster;
    }

    public void setCluster(Cluster cluster) {
        this.cluster = cluster;
    }

}
