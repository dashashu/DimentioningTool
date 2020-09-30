package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by fabrizio.sanfilippo on 06/03/2017.
 */
public class SocketBlade {
    private Blade blade; // the blade this socket belongs to
    private Double core; // number of cores this socket has
    private Double esxiCores;
    private Double txrxCores;
    private Double ram;
    //Ashutosh: Numa
    private Double ocupiedCore; // number of cores this socket occupied for numa
    private Double ocupiedRam; 
    private String numaSocket; //
    
    // virtual machines (and their corresponding number of cores) that are currently hosted by this socket
    private Map<VirtualMachine, Double> virtualMachineCoreOccupancy;
    //NUMA
    private Map<VirtualMachine, Double> virtualMachineRamOccupancy;

    public SocketBlade(Blade blade, double core) {
        this.virtualMachineCoreOccupancy = new HashMap<VirtualMachine, Double>();
        this.virtualMachineRamOccupancy = new HashMap<VirtualMachine, Double>();
        this.core = core;
        this.blade = blade;
        this.esxiCores = 0.0;
        this.txrxCores = 0.0;
        this.ocupiedCore = 0.0;
        this.numaSocket = "";
    }


    /**
     * Restituisce il numero di core utilizzati su questo socket
     * virtualMachineCoreOccupancy
     *
     * @return
     */
    public Double getCoreUsed() {
        return virtualMachineCoreOccupancy.keySet().stream().mapToDouble(vm -> virtualMachineCoreOccupancy.get(vm)).sum();
    }

    public Double getCoreAvailable() {
        return core - getCoreUsed() - (this.esxiCores + this.txrxCores+ this.ocupiedCore);
    }

    public Double getEffectiveCore() {
        return core - (this.esxiCores + this.txrxCores);
    }
    
    //NUMA changes
	public Double getRamUsed() {
		return virtualMachineRamOccupancy.keySet().stream().mapToDouble(vm -> virtualMachineRamOccupancy.get(vm)).sum();
	}
    public Double getRamAvailable() {
        return core - getRamUsed() - (this.ocupiedRam);
    }
	
    public Double getCore() {
        return core;
    }

    public void setCore(double core) {
        this.core = core;
    }

    public Blade getBlade() {
        return blade;
    }

    public void setBlade(Blade blade) {
        this.blade = blade;
    }

    public Map<VirtualMachine, Double> getVirtualMachineCoreOccupancy() {
        return virtualMachineCoreOccupancy;
    }

    public void setVirtualMachineCoreOccupancy(Map<VirtualMachine, Double> virtualMachineCoreOccupancy) {
        this.virtualMachineCoreOccupancy = virtualMachineCoreOccupancy;
    }


    public Double getEsxiCores() {
        return esxiCores;
    }

    public void setEsxiCores(Double esxiCores) {
        this.esxiCores = esxiCores;
    }

    public Double getTxrxCores() {
        return txrxCores;
    }

    public void setTxrxCores(Double txrxCores) {
        this.txrxCores = txrxCores;
    }


	public Double getOcupiedCore() {
		return ocupiedCore;
	}


	public void setOcupiedCore(Double ocupiedCore) {
		this.ocupiedCore = ocupiedCore;
	}


	public Double getOcupiedRam() {
		return ocupiedRam;
	}


	public void setOcupiedRam(Double ocupiedRam) {
		this.ocupiedRam = ocupiedRam;
	}


	public String getNumaSocket() {
		return numaSocket;
	}


	public void setNumaSocket(String numaSocket) {
		this.numaSocket = numaSocket;
	}


	public Double getRam() {
		return ram;
	}


	public void setRam(Double ram) {
		this.ram = ram;
	}


	public String print() {

        StringBuilder r = new StringBuilder();
        r.append("Socket[(core available:" + getCoreAvailable() + "|" + getCore() + ")");

        Set<VirtualMachine> vmSet = virtualMachineCoreOccupancy.keySet();

        for (VirtualMachine vm : vmSet) {
            r.append("[" + vm.getCompleteName() + "-coreUsed:" + virtualMachineCoreOccupancy.get(vm) + "|" + (vm.getCore() + vm.getHighThroughputCoreQuantity()) + "]");
        }

        r.append("]");
        return r.toString();
    }
}
