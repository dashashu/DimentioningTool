package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.model.catalog.Compute;
import commons.src.main.java.it.spindox.model.configurations.ClusterConfiguration;

/**
 * Created by fabrizio.sanfilippo on 09/03/2017.
 */
public class BladeFactory {

    private Double coreAvailableForSocket;
    private Integer ramAvailable;
    private Integer numberOfSocket;
    private Double esxiCores;
    private Double txrxCores;
    private Double maxThrougputSupported;
    private Double maxRunningIopsSupported;
    
    private ClusterConfiguration clusterconfiguration;

    public BladeFactory(Double coreAvailableForSocket, Integer ramAvailable, Integer numberOfSocket, 
    		double esxi, double txrx, Double maxThrougputSupported, Double maxRunningIopsSupported) {
        this.coreAvailableForSocket = coreAvailableForSocket;
        this.ramAvailable = ramAvailable;
        this.numberOfSocket = numberOfSocket;
        this.esxiCores = esxi;
        this.txrxCores = txrx;
        this.maxRunningIopsSupported = maxRunningIopsSupported;
        this.maxThrougputSupported = maxThrougputSupported;
    }

    public BladeFactory(Double coreAvailableForSocket, Integer ramAvailable, Integer numberOfSocket) {
        this.coreAvailableForSocket = coreAvailableForSocket;
        this.ramAvailable = ramAvailable;
        this.numberOfSocket = numberOfSocket;
    }

    public BladeFactory(Compute blade, ClusterConfiguration clusterConfiguration, double esxi, double txrx) {
    	//Ashutosh: Numa
    	clusterconfiguration = clusterConfiguration;
    	if (clusterConfiguration.isEsxi()) {
    		blade.setNumberOfCores(blade.getNumberOfCores()-clusterConfiguration.getEsxi_core_blade());
    		blade.setRamInGb(blade.getRamInGb()-clusterConfiguration.getEsxi_mem_blade());
    	}
    	else if(clusterConfiguration.isNsx()){
    		blade.setNumberOfCores(blade.getNumberOfCores()-clusterConfiguration.getNsx_core_blade());
    		blade.setRamInGb(blade.getRamInGb()-clusterConfiguration.getNsx_mem_blade());
    	}

        numberOfSocket = blade.getNumberOfSockets();
        coreAvailableForSocket = blade.getNumberOfCores() * ((clusterConfiguration.getCpuOverProvisioning() == 0.0 ? 1.0 : clusterConfiguration.getCpuOverProvisioning()));
        ramAvailable = (int) Math.ceil(blade.getRamInGb() * ((clusterConfiguration.getRamOverProvisioning() == 0.0 ? 1.0 : clusterConfiguration.getRamOverProvisioning())));
        this.maxThrougputSupported = blade.getMaxThroughputSupported() * ((clusterConfiguration.getMaxThroughputPerBladeOverprovisioning() == 0.0 ? 1.0 : clusterConfiguration.getMaxThroughputPerBladeOverprovisioning()));
        this.maxRunningIopsSupported = blade.getMaxRunningIopsSupported() * ((clusterConfiguration.getMaxRunningIOPSPerBladeOverprovisioning() == 0.0 ? 1.0 : clusterConfiguration.getMaxRunningIOPSPerBladeOverprovisioning()));
        this.esxiCores = esxi;
        this.txrxCores = txrx;
    	
    }


    public Blade createBlade() {
        Blade newBlade = new Blade();
        newBlade.setRam(ramAvailable);
        newBlade.setMaxThroughputSupported(maxThrougputSupported);
        newBlade.setMaxRunningIopsSupported(maxRunningIopsSupported);
        int ramAvailable0 = 0;
        int ramAvailable1 = 0;
        	//Ashutosh Numa
        	if (clusterconfiguration.isEsxi()) {
                for (int i = 0; i < numberOfSocket; i++) {
        		if (i==0) {
        			coreAvailableForSocket=coreAvailableForSocket-clusterconfiguration.getEsxi_core_socket_0();
        			ramAvailable0 = (int) ((ramAvailable/2.0)-clusterconfiguration.getEsxi_core_socket_0());
        		}else if(i==1) {
        			coreAvailableForSocket=coreAvailableForSocket-clusterconfiguration.getEsxi_core_socket_1();
        			ramAvailable1 =(int) ((ramAvailable/2.0)-clusterconfiguration.getEsxi_core_socket_1());
        		}
                }
                ramAvailable=(ramAvailable0+ramAvailable1); 
        	}
        	if(clusterconfiguration.isNsx()){

                for (int i = 0; i < numberOfSocket; i++) {
        		if (i==0) {
        			coreAvailableForSocket=coreAvailableForSocket-clusterconfiguration.getNsx_core_socket_0();
        			ramAvailable0 = (int) ((ramAvailable/2.0)-clusterconfiguration.getNsx_core_socket_0());
        		}else if(i==1) {
        			coreAvailableForSocket=coreAvailableForSocket-clusterconfiguration.getNsx_core_socket_1();
        			ramAvailable1 =(int) ((ramAvailable/2.0)-clusterconfiguration.getNsx_mem_socket_1());
        		}
                }
                ramAvailable = (ramAvailable0+ramAvailable1); 
        		
        	}
        		for (int i = 0; i < numberOfSocket; i++) {
		//        	SocketBlade sock = new SocketBlade(newBlade, coreAvailableForSocket, overprovisioning);
		            SocketBlade sock = new SocketBlade(newBlade, coreAvailableForSocket);
		            sock.setEsxiCores(esxiCores);//not in use
		            sock.setTxrxCores(txrxCores);//not in use
		            sock.setRam(ramAvailable/2.0);
		            newBlade.getSocketList().add(sock);
        			}
        return newBlade;
    }

    public Blade createSpareBlade() {
        Blade newBlade = new Blade();
        newBlade.setRam(ramAvailable);

        for (int i = 0; i < numberOfSocket; i++) {
            newBlade.getSocketList().add(new SocketBlade(newBlade, coreAvailableForSocket));
        }

        newBlade.setSpare(true);

        return newBlade;
    }
    public Blade createActiveBlade() {
        Blade newBlade = new Blade();
        newBlade.setRam(ramAvailable);

        for (int i = 0; i < numberOfSocket; i++) {
            newBlade.getSocketList().add(new SocketBlade(newBlade, coreAvailableForSocket));
        }

        newBlade.setSpare(false);

        return newBlade;
    }

    public String print() {
        return "BladeFactory[(ram: " + ramAvailable 
        		+ " core: " + (numberOfSocket * coreAvailableForSocket) 
        		+ " max throughput supported: " + maxThrougputSupported
        		+ " max iops running supported: " + maxRunningIopsSupported
        		+ ")]";
    }

    public int getTotalCores() {
    	return (int)((this.numberOfSocket*this.coreAvailableForSocket)-(this.esxiCores+this.txrxCores));
    }

    public Double getCoreAvailableForSocket() {
        return coreAvailableForSocket;
    }

    public void setCoreAvailableForSocket(Double coreAvailableForSocket) {
        this.coreAvailableForSocket = coreAvailableForSocket;
    }

    public Integer getRamAvailable() {
        return ramAvailable;
    }

    public void setRamAvailable(Integer ramAvailable) {
        this.ramAvailable = ramAvailable;
    }

    public Integer getNumberOfSocket() {
        return numberOfSocket;
    }

    public void setNumberOfSocket(Integer numberOfSocket) {
        this.numberOfSocket = numberOfSocket;
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
}
