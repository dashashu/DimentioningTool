package core.src.main.java.it.spindox.utils;

import java.util.*;
import java.util.stream.Collectors;

import commons.src.main.java.it.spindox.vfexception.core.InconsistentConstraintsException;
import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.enumeration.Affinity;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Site;
import commons.src.main.java.it.spindox.model.placementAndEstimation.VirtualMachine;
import commons.src.main.java.it.spindox.model.placementAndEstimation.Year;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;

public class VbomManagement {

    /**
     * Ricordati che una riga = vbom
     */
    private List<VBom> vBom;
    private List<Year> yearList;
    private Double storageReservation;
    private boolean isFoundation = false;
    
    //Ashutosh:
    private InputConfiguration inputConfig;
    //Map che linka le virtual machine alla loro vbom.
    private Map<VirtualMachine, VBom> vmVbomMap;

    public VbomManagement(List<VBom> vBom) {
        this.vBom = vBom;
        vmVbomMap = new LinkedHashMap<VirtualMachine, VBom>();
        yearList = new LinkedList<Year>();
        this.storageReservation = 1D;
    }

    public VbomManagement(Cluster cluster) {
    	this.isFoundation = cluster.isFoundation();
        this.vBom = cluster.getVbom();
        vmVbomMap = new LinkedHashMap<VirtualMachine, VBom>();
        yearList = new LinkedList<Year>();
        this.storageReservation = cluster.getClusterConfiguration().getStorageReservation(); 
        this.inputConfig = cluster.getInputConfiguration();
    }

    public void extractDataFromVbom() throws InconsistentConstraintsException {

        checkConstraintsConsistency();//external constraint rule check

        //Scorro righe dell'excel
        for (VBom vbom : vBom) {
            //Scorro anni
            int vbyrListSize = vbom.getvBomYearList().size();
            for (int i = 0; i < vbyrListSize; i++) {
                Year tempYear = new Year();

                if (yearList.size() > i)
                    tempYear = yearList.get(i);

                VBomYear vbomYear = vbom.getvBomYearList().get(i);

                tempYear.setYearName(vbom.getvBomYearList().get(i).getYearName());
                tempYear.setNumOfYear(i);

                int numberOfInstances = vbomYear.getVnfPerSite();
                int numberOfClones = vbomYear.getNumberOfVnfPerTypeAndPerInstance();
                //Ashutos: NUMA
                boolean numaflag = Boolean.parseBoolean(vbomYear.getNumaFlag());
                boolean socketflag = Boolean.parseBoolean(vbomYear.getSocket());
                List<String> siteNameList = vbomYear.getSiteList();

                //Scorro siti
                for (int n = 0; n < siteNameList.size(); n++) {
                    String nomesito = siteNameList.get(n);
                    Site site = tempYear.getSiteList().stream().filter(s -> s.getSiteName().equalsIgnoreCase(nomesito)).findFirst().orElse(null);
                    if (site == null) {
                        site = new Site();
                        site.setSiteName(nomesito);
                        tempYear.getSiteList().add(site);
                    }

                    List<VirtualMachine> vmPerSite = new ArrayList<VirtualMachine>();

                    /*Creo il numero corretto di instanze e cloni e li aggiungo alla lista
                      temporanea di Virtual Machine per quel sito*/
                    for (int k = 1; k <= numberOfInstances; k++) {
                        for (int m = 1; m <= numberOfClones; m++) {

                            VirtualMachine virtualMachine = new VirtualMachine(k, m, vbom, i, storageReservation,inputConfig);
                            
//                            if(((!vbom.getVmWorkloadType().equals(ConfigurationManagement.getVbomConfiguration().getString("foundationWorkloadType"))) 
//                            		&& (!this.isFoundation)) ||
//                            		((vbom.getVmWorkloadType().equals(ConfigurationManagement.getVbomConfiguration().getString("foundationWorkloadType")) 
//                            				&& (this.isFoundation)))) //TODO correggere errore
//                    		{
	                            vmVbomMap.put(virtualMachine, vbom);
	                            
//                    		}
                    		vmPerSite.add(virtualMachine);
                        }
                    }

                    site.addVmList(vmPerSite);
                }

                if (yearList.size() > i)
                    yearList.set(i, tempYear);
                else
                    yearList.add(i, tempYear);
            }
        }
//        return yearList;
    }

    public void applyConstraints() throws InconsistentConstraintsException {
        try {
            yearList.forEach((Year y) -> {

                y.getSiteList().forEach((Site s) -> {
                    try {
                        applyConstraints(s.getVmList(), false);
                    } catch (InconsistentConstraintsException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof InconsistentConstraintsException) {
                throw (InconsistentConstraintsException) e.getCause();
            } else {
                throw e;
            }
        }
    }
    
    public void applyConstraints(boolean aoptpi) throws InconsistentConstraintsException {
        try {
            yearList.forEach((Year y) -> {

                y.getSiteList().forEach((Site s) -> {
                    try {
                        applyConstraints(s.getVmList(), aoptpi);
                    } catch (InconsistentConstraintsException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        } catch (RuntimeException e) {
            if (e.getCause() != null && e.getCause() instanceof InconsistentConstraintsException) {
                throw (InconsistentConstraintsException) e.getCause();
            } else {
                throw e;
            }
        }
    }


    public static boolean notContains(VirtualMachine vm, List<VirtualMachine> vmList) {
        for (VirtualMachine virtualMachine : vmList) {
            if (virtualMachine.getCompleteName().equalsIgnoreCase(vm.getCompleteName())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Interpreta le regole di affinità dal file di Vbom e le applica
     * alla vmList del sito
     * 
     * @param aoptpi -> "affinity only per type per instance ", if true, self affinities will be assigned only
		between clones of the same instance
     */
    private void applyConstraints(List<VirtualMachine> vmList, boolean aoptpi) throws InconsistentConstraintsException {
        for (VirtualMachine vm : vmList) {

            // External constraints
            List<VirtualMachine> aff = new ArrayList<VirtualMachine>();
            vmVbomMap.get(vm)
                    .getExternalConstraintAffinity()
                    .forEach(
                            str -> {

                            	List<VirtualMachine> collect = getVirtualMachine(vmList, str) //return list of VM with same name
	                                        .stream().filter(vmTmp -> !vmTmp
	                                                .getCompleteName().equalsIgnoreCase(
	                                                        vm.getCompleteName())).distinct().collect(Collectors.toList());
                                
                                collect.forEach(vm2 -> {

                                    if (notContains(vm, vm2.getAff()))
                                        vm2.getAff().add(vm);
                                });
                                aff.addAll(collect);

                            });


            List<VirtualMachine> aaf = new ArrayList<VirtualMachine>();
            vmVbomMap.get(vm)
                    .getExternalConstraintAntiAffinity()
                    .forEach(
                            str -> {

                                List<VirtualMachine> collect = getVirtualMachine(vmList, str)
	                                        .stream().filter(vmTmp -> !vmTmp
	                                                .getCompleteName().equalsIgnoreCase(
	                                                        vm.getCompleteName())).distinct().collect(Collectors.toList());

                                collect.forEach(vm2 -> {
                                    if (notContains(vm, vm2.getAaf()))
                                        vm2.getAaf().add(vm);
                                });
//                                aff.addAll(collect);

                                aaf.addAll(collect);

                            });

            // Self constraints
            Affinity sc = vmVbomMap.get(vm).getSelfConstraint();
            if (sc != null) {
                switch (sc) {
                    case AFF:
                    	if(aoptpi) {
	                        aff.addAll(Arrays.asList(getVirtualMachineForInstance(
	                                vmList, vm.getVmName(), vm.getInstance())
	                                .stream()
	                                .filter(vmTmp -> !vmTmp.getCompleteName()
	                                        .equalsIgnoreCase(vm.getCompleteName())).distinct()
	                                .toArray(VirtualMachine[]::new)));
                    	} else {
                    		aff.addAll(Arrays.asList(getVirtualMachine(
	                                vmList, vm.getVmName())
	                                .stream()
	                                .filter(vmTmp -> !vmTmp.getCompleteName()
	                                        .equalsIgnoreCase(vm.getCompleteName())).distinct()
	                                .toArray(VirtualMachine[]::new)));
                    	}
                        break;
                    case AAF:
                    	if(aoptpi) { //Se affinity only per type per instance
	                    	aaf.addAll(Arrays.asList(getVirtualMachineForInstance(
	                        		vmList, vm.getVmName(), vm.getInstance())
	                                .stream()
	                                .filter(vmTmp -> !vmTmp.getCompleteName()
	                                        .equalsIgnoreCase(vm.getCompleteName())).distinct()
	                                .toArray(VirtualMachine[]::new)));
                    	} else {
	                        aaf.addAll(Arrays.asList(getVirtualMachine(vmList, vm.getVmName())
	                                .stream()
	                                .filter(vmTmp -> !vmTmp.getCompleteName()
	                                        .equalsIgnoreCase(vm.getCompleteName())).distinct()
	                                .toArray(VirtualMachine[]::new)));
                    	}
                        break;
                    case AMS:
                        vm.setAms(true);
                }
            }

//            vm.setAff(aff.stream().distinct().collect(Collectors.toList()));
//            vm.setAaf(aaf.stream().distinct().collect(Collectors.toList()));

            vm.setAff(aff);
            vm.setAaf(aaf);
        }


        //APPLY THE AAF rules for vmAMS
        /**For each vmAMS we tie that virtual machine
		to another virtual machine of the same type
		if the "affinity only per type per instance" flag is true,
		then we tie that virtual machine
		to another virtual machine of the same type / instance but different clone
         */

        List<VirtualMachine> vmAMS =
                Arrays.asList(vmList.stream().filter(vm -> vm.isAms()).toArray(VirtualMachine[]::new));

        if (vmAMS.size() % 2 != 0) {
            throw new InconsistentConstraintsException("The number of the virtual machine "+vmAMS.get(0).getCompleteName()+" is odd, but the VM is in AMS.");
        }
        Set<String> yetUsed = new HashSet<>();

        for (VirtualMachine vm : vmAMS) {
            if (!yetUsed.contains(vm.getCompleteName())) {
                yetUsed.add(vm.getCompleteName());

                for (VirtualMachine vmTmp : vmAMS) {
                    if (!yetUsed.contains(vmTmp.getCompleteName())) {
                        if (vmTmp.getVmName().equalsIgnoreCase(vm.getVmName())) {
                        	if(aoptpi) {
                        		if(vmTmp.getInstance() == vm.getInstance()) {
                        			yetUsed.add(vmTmp.getCompleteName());
                                    vmTmp.getAaf().add(vm);
                                    vm.getAaf().add(vmTmp);
                                    break;
                        		}
                        	} else {
                        		yetUsed.add(vmTmp.getCompleteName());
                                vmTmp.getAaf().add(vm);
                                vm.getAaf().add(vmTmp);
                                break;
                        	}
                        }  
                    }
                }
            }
        }

//        applyBivalenceContraintsAAF(vmList);
//        applyBivalenceContraintsAFF(vmList);
    }

    private void applyBivalenceContraintsAFF(List<VirtualMachine> vmList) {
        for (VirtualMachine vm : vmList) {
            List<VirtualMachine> affList = vm.getAff();

            for (VirtualMachine affVm : affList) {

                VirtualMachine tmpVM = vmList.get(vmList.indexOf(affVm));

                List<VirtualMachine> affToAffList = tmpVM.getAff();

                boolean alreadyInsert = false;

                for (VirtualMachine affToAff : affToAffList) {
                    if (affToAff.equals(vm)) {
                        alreadyInsert = true;
                    }
                }

                if (!alreadyInsert) {
                    vmList.get(vmList.indexOf(affVm)).getAff().add(vm);
                }

            }


        }
    }

    private void applyBivalenceContraintsAAF(List<VirtualMachine> vmList) {
        for (VirtualMachine vm : vmList) {
            List<VirtualMachine> aafList = vm.getAaf();

            for (VirtualMachine aafVm : aafList) {
                VirtualMachine tmpVM = vmList.get(vmList.indexOf(aafVm));
                List<VirtualMachine> aafToAffList = tmpVM.getAaf();

                boolean alreadyInsert = false;

                for (VirtualMachine aafToAaf : aafToAffList) {
                    if (aafToAaf.equals(vm)) {
                        alreadyInsert = true;
                    }
                }

                if (!alreadyInsert) {
                    vmList.get(vmList.indexOf(aafVm)).getAaf().add(vm);
                }

            }


        }
    }

    public List<Year> getYearList() {
        return yearList;
    }

    public void setYearList(List<Year> yearList) {
        this.yearList = yearList;
    }

    /**
     * Returns the list of Virtual Machines with the same vmName
     *
     * @param vmName
     * @return
     */
    public List<VirtualMachine> getVirtualMachine(List<VirtualMachine> vmList, String vmName) {
        return Arrays.asList(vmList.stream()
                .filter(vm -> vm.getVmName().equals(vmName))
                .toArray(VirtualMachine[]::new));
    }

    /**
     * Restituisce le virtual machine filtrate per instanza
     *
     * @param vmName
     * @param instance
     * @return
     */
    public List<VirtualMachine> getVirtualMachineForInstance(List<VirtualMachine> vmList, String vmName,
                                                             int instance) {
        return Arrays.asList(vmList
                .stream()
                .filter(vm -> {
                    return vm.getVmName().equals(vmName)
                            && (vm.getInstance() == instance);
                })
                .toArray(VirtualMachine[]::new));
    }

    private void checkConstraintsConsistency() throws InconsistentConstraintsException {
        for (VBom vbom : vBom) {
            for (String aff : vbom.getExternalConstraintAffinity()) {
                //For each line of the vbom, first check that the VMs in affinity are not also in antiaffinity
                for (String aaf : vbom.getExternalConstraintAntiAffinity()) {
                    if (aff.equalsIgnoreCase(aaf)) {
                        throw new InconsistentConstraintsException(
                                "Inconsistent constraints for virtual machine " + vbom.getVnfName() + "." + vbom.getVmTypeName());
                    }
                }

                //Then check that for each VM in affinity, it is not in antiaffinity with the VM 
                for (VBom vbom2 : vBom) {
                    if ((vbom2.getVnfName() + "." + vbom2.getVmTypeName()).equals(aff)) {
                        for (String aaf : vbom2.getExternalConstraintAntiAffinity()) {
                            if (aaf.equals(vbom.getVnfName() + "." + vbom.getVmTypeName()))
                                throw new InconsistentConstraintsException(
                                        "Inconsistent constraints between virtual machine " + vbom.getVnfName() + "." + vbom.getVmTypeName() + " and " + vbom2.getVnfName() + "." + vbom2.getVmTypeName());
                        }
                    }
                }
            }
        }
    }
}
