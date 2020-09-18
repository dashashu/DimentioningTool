package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */
public class Blade {

    List<SocketBlade> socketList;
    Integer ram;
    List<VMGroup> vmGroupAssignedToThisBladeList;

    private boolean highThroughputCoreZero;
    private boolean spare;
    private Double maxThroughputSupported;
    private Double maxRunningIopsSupported;
    private String numaSocket;//E,0,1
    private char NumaBlade;//N,Y

	public Blade() {
        vmGroupAssignedToThisBladeList = new ArrayList<>();
        socketList = new ArrayList<>();
        highThroughputCoreZero = false;
        spare = false;
        numaSocket = "";
        NumaBlade = 'N';
    }

    public double getOccupancyPercentage() {
        double core = getCoreAvailable() + getCoreUsed();
        double perc = new Double(getCoreUsed()) / new Double(core) * 100;
        return perc;
    }


    public List<VirtualMachine> getAAFVm() {
        List<VirtualMachine> aaf = new ArrayList<>();
        for (VMGroup gruppo : vmGroupAssignedToThisBladeList) {
            gruppo.getVmList().forEach(virtualMachine -> {
                aaf.addAll(virtualMachine.getAaf());
            });
        }
        return aaf.stream().distinct().collect(Collectors.toList());
    }


    public String print() {

        StringBuilder sb = new StringBuilder();
        int totalRunningIOPS = 0, totalNorthSouthBW = 0, totalEastWestBW = 0;

        sb.append("Blade{(numberOfSockets: " + socketList.size() + ",  RAM available: " + getRamAvailable() + "/" + getRam()
                + ",  core available: " + getCoreAvailable() + "/" + getSocketList().size() * getSocketList().get(0).getCore());


        if (!spare) {
            for (VMGroup vmg : vmGroupAssignedToThisBladeList) {
                totalRunningIOPS += vmg.getVmList().stream().mapToInt(value -> value.getRunningIOPS()).sum();
                totalNorthSouthBW += vmg.getVmList().stream().mapToInt(value -> value.getNorthSouthBW()).sum();
                totalEastWestBW += vmg.getVmList().stream().mapToInt(value -> value.getEastWestBW()).sum();
            }

            int totalNetworkTraffic = totalNorthSouthBW + totalEastWestBW;
            sb.append(",  total running IOPS: " + totalRunningIOPS + ",  total N/S Traffic: " + totalNorthSouthBW
                    + " Mbit/s,  total E/W Traffic: " + totalEastWestBW + " Mbit/s,  total Network traffic: " + totalNetworkTraffic + " Mbit/s)");

            for (VMGroup group : vmGroupAssignedToThisBladeList) {
                sb.append("[" + group.getVmListAsString() + "]");
            }

        } else {
            sb.append(")");
        }

        sb.append("}");

        if (spare)
            sb.append(" SPARE BLADE");

        return sb.toString();
    }


    public String printForSocket() {
        StringBuilder sb = new StringBuilder();
        int totalRunningIOPS = 0, totalNorthSouthBW = 0, totalEastWestBW = 0;

        sb.append("Blade{(numberOfSockets: " + socketList.size() + ",  RAM available: " + getRamAvailable() + "/" + getRam()
                + ",  core available: " + getCoreAvailable() + "/" + getSocketList().size() * getSocketList().get(0).getCore());


        if (spare)
            sb.append(")\n\t\t[SPARE BLADE]\n");
        else {
            for (VMGroup vmg : vmGroupAssignedToThisBladeList) {
                totalRunningIOPS += vmg.getVmList().stream().mapToInt(value -> value.getRunningIOPS()).sum();
                totalNorthSouthBW += vmg.getVmList().stream().mapToInt(value -> value.getNorthSouthBW()).sum();
                totalEastWestBW += vmg.getVmList().stream().mapToInt(value -> value.getEastWestBW()).sum();
            }

            int totalNetworkTraffic = totalNorthSouthBW + totalEastWestBW;
            sb.append(",  total running IOPS/max running IOPS supported: " + totalRunningIOPS + "/" 
	            + this.maxRunningIopsSupported + ",  total N/S Traffic: " + totalNorthSouthBW + " Mbit/s,"
	    		+ "  total E/W Traffic: " + totalEastWestBW + " Mbit/s, total Network traffic/max "
				+ "Network traffic supported: " + totalNetworkTraffic + "/"+ this.maxThroughputSupported 
				+" Mbit/s)");


            if (isHighThroughputCoreZero()) {
                sb.append("\nNote: one of cores that is marked as \"free\" will be reserved because of Core Throughput=0\n");
            }

            for (SocketBlade socketBlade : socketList) {
                sb.append("\n\t\t[" + socketBlade.print() + "]");
            }
        }

        sb.append("\n}");
        return sb.toString();
    }


    public boolean checkIfNewGroupIsAAF(VMGroup toInsertGroup) {

        List<VirtualMachine> aafToInsertGroup = new LinkedList<>();


        for (VirtualMachine virtualMachine : toInsertGroup.getVmList()) {
            aafToInsertGroup.addAll(virtualMachine.getAaf()); //metto tutte le AAF del gruppo in una lista
        }

        List<VirtualMachine> aafBlade = getAAFVm(); //metto le AAF della blade in una lista

        for (VirtualMachine virtualMachine : aafBlade) { //scorro le AAF della blade
            for (VirtualMachine machine : toInsertGroup.getVmList()) { //per ognuna scorro le vm del gruppo da inserire
                if (machine.getCompleteName().equalsIgnoreCase(virtualMachine.getCompleteName())) {
                    return true; //ritorno true se una delle vm del gruppo è tra le AAF della blade
                }
            }
        }

        //creo una lista di tutte le VMs dentro questa blade
        List<VirtualMachine> allVmInMyBlade = new LinkedList<>();
        for (VMGroup group : getVmGroupAssignedToThisBladeList()) {
            allVmInMyBlade.addAll(group.getVmList());
        }

        for (VirtualMachine virtualMachine : aafToInsertGroup) {
            for (VirtualMachine machine : allVmInMyBlade) { //scorro la lista della AAF del gruppo da inserire e quella delle VMs nella blade
                if (machine.getCompleteName().equalsIgnoreCase(virtualMachine.getCompleteName())) {
                    return true; //se due elementi combaciano, blade e gruppo sono in AAF
                }
            }
        }

        return false;
    }
    
    public String getVmNames() {
    	String nameList = "";
    	
    	for(SocketBlade s:socketList) {
			for (Map.Entry<VirtualMachine, Double> entry : s.getVirtualMachineCoreOccupancy().entrySet())
			{
				nameList+=entry.getKey().getVmName()+"_"+entry.getKey().getInstance()+"_"+entry.getKey().getClone();
			}
    	}
    	
    	return nameList;
    }

    public Integer getBladeThroughput() {
        return vmGroupAssignedToThisBladeList.stream().mapToInt(VMGroup::getGroupThroughput).sum();
    }

    public Integer getBladeIopsRunning() {
        return vmGroupAssignedToThisBladeList.stream().mapToInt(VMGroup::getGroupIopsRunning).sum();
    }


    public Integer getHardDiskUsed() {
        return vmGroupAssignedToThisBladeList.stream().mapToInt(VMGroup::getHardDisk).sum();
    }

    public Double getRamUsed() {
        return vmGroupAssignedToThisBladeList.stream().mapToDouble(VMGroup::getRam).sum();
    }

    public Double getCoreUsed() {
        return vmGroupAssignedToThisBladeList.stream().mapToDouble(VMGroup::getCore).sum();
    }

    public Double getRamAvailable() {
        return ram - getRamUsed();
    }

    public Double getCoreAvailable() {
        return socketList.stream().mapToDouble(SocketBlade::getCoreAvailable).sum() - (this.highThroughputCoreZero ? 1 : 0);
    }

    public List<SocketBlade> getSocketList() {
        return socketList;
    }

    public void setSocketList(List<SocketBlade> socketList) {
        this.socketList = socketList;
    }


    public Integer getRam() {
        return ram;
    }

    public void setRam(Integer ram) {
        this.ram = ram;
    }

    public List<VMGroup> getVmGroupAssignedToThisBladeList() {
        return vmGroupAssignedToThisBladeList;
    }

    public void setVmGroupAssignedToThisBladeList(List<VMGroup> vmGroupAssignedToThisBladeList) {
        this.vmGroupAssignedToThisBladeList = vmGroupAssignedToThisBladeList;
    }

    public boolean isHighThroughputCoreZero() {
        return highThroughputCoreZero;
    }

    public void setHighThroughputCoreZero(boolean highThroughputCoreZero) {
        this.highThroughputCoreZero = highThroughputCoreZero;
    }

    public Double getMaxThroughputSupported() {
        return maxThroughputSupported;
    }

    public void setMaxThroughputSupported(Double maxThroughputSupported) {
        this.maxThroughputSupported = maxThroughputSupported;
    }

    public Double getMaxRunningIopsSupported() {
        return maxRunningIopsSupported;
    }

    public void setMaxRunningIopsSupported(Double maxRunningIopsSupported) {
        this.maxRunningIopsSupported = maxRunningIopsSupported;
    }

    /* Another implementation of the isHighThroughputCoreZero method. It does not return the value of highThroughputCoreZero
     * but actually scrolls each VMs of this blade and checks their highThroughputCore value.
	 * It is not used for now because it decreases performances, even if it is more secure, and lifts us from manually
	 * setting the highThroughputCoreZero variable of this object.
	 * If performance are not an issue it would be preferable to use this method: in that case, it's possible to delete
	 * the getter and setter above (isHighThroughputCoreZero and setHighThroughputCoreZero), along with the variable
	 * highThroughputCoreZero and remove from the placement algorithm classes every call at its setter.

	public boolean isHighThroughputCoreZero() {
		for(SocketBlade sb:socketList)
			for (Map.Entry<VirtualMachine, Integer> entry : sb.getVirtualMachineCoreOccupancy().entrySet())
			    if(entry.getKey().getHighThroughputCore() == 0)
			    	return true;
		
		return false;
	}*/

    public boolean isSpare() {
        return spare;
    }

    public void setSpare(boolean spare) {
        this.spare = spare;
    }


    @Override
    public String toString() {
        return "Blade{" +
                "vmGroupAssignedToThisBladeList=" + vmGroupAssignedToThisBladeList +
                '}';
    }

	public char getNumaBlade() {
		return NumaBlade;
	}

	public void setNumaBlade(char numaBlade) {
		NumaBlade = numaBlade;
	}
    public String getNumaSocket() {
		return numaSocket;
	}

	public void setNumaSocket(String numaSocket) {
		this.numaSocket = numaSocket;
	}

}
