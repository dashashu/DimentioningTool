package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.List;

import commons.src.main.java.it.spindox.model.catalog.Container;

/**
 * Created by fabrizio.sanfilippo on 27/03/2017.
 */
public class PlacementResult {
    String year;
    String site;
    List<Blade> placement;


    public PlacementResult(String year, String site, List<Blade> placement) {
        this.year = year;
        this.site = site;
        this.placement = placement;
    }

    public PlacementResult() {
		// TODO Auto-generated constructor stub
	}
    
    
	public String getYear() {
        return year;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();

        int totalRunningIOPS = 0, totalNorthSouthBW = 0, totalEastWestBW = 0, totalHardDisk = 0, totalVM = 0;
        String modelName;
        
        double totalVcpuUsed = placement.stream().mapToDouble(value -> value.getCoreUsed()).sum();
        double totalVramUsed = placement.stream().mapToDouble(value -> value.getRamUsed()).sum();


        int totalBlades = placement.size();
        int activeBlades = totalBlades - ((int) placement.stream().filter(Blade::isSpare).count());
      /*  int enclouser = 0;
        Container enclouser1;
        //For BOS 40 GB // Ashutosh dash
        if(totalBlades>0 & totalBlades<= 128){
        	modelName = "Small";
        }
        	else if (totalBlades>128 & totalBlades<= 272){
        		modelName = "Medium";
        		Estimation estimation = new Estimation();
        		enclouser = estimation.getCatalog().getEnclosure()
        		//enclouser= totalBlades/( estimation.getCatalog().getEnclosure().getMaxNumOfHostableUnit()); // Rule: No of blades/ Unithouse
        		//enclouser1= estimation.getCatalog().getEnclosure();
        	}
        	else if (totalBlades>272 & totalBlades<= 640){
        		modelName = "Large";
        	}
        	else modelName = "Undefined";
        
        //For BOB 40 GB
        if(totalBlades>0 & totalBlades<= 128){
        	modelName = "Small";
        }
        	else if (totalBlades>128 & totalBlades<= 272){
        		modelName = "Medium";
        	}
        	else if (totalBlades>272 & totalBlades<= 640){
        		modelName = "Large";
        	}
        	else modelName = "Undefined";
        
        /////////////////////////////////////////////////////////////////////////
    */
        for (Blade a : placement) {
            for (VMGroup b : a.getVmGroupAssignedToThisBladeList()) {
                totalVM += b.getVmList().size(); // VM
                totalRunningIOPS += b.getVmList().stream().mapToInt(value -> value.getRunningIOPS()).sum();
                totalNorthSouthBW += b.getVmList().stream().mapToInt(value -> value.getNorthSouthBW()).sum();
                totalEastWestBW += b.getVmList().stream().mapToInt(value -> value.getEastWestBW()).sum();
                totalHardDisk += b.getVmList().stream().mapToInt(value -> value.getHardDisk()).sum();
            }
        }

        Double avgNetworkThroughputPerBlade = 0.0;
        if (activeBlades != 0)
            avgNetworkThroughputPerBlade = (totalNorthSouthBW + totalEastWestBW) / (double) activeBlades;

        sb.append("********************************************************************************\n");
        sb.append("Year: " + year + "  Site: " + site + "  Number of Blades: " + totalBlades + "  Number of VMs: " + totalVM
                + "\n(total vCPU used: " + totalVcpuUsed + ", total vRAM used: " + totalVramUsed
                + ", total running IOPS: " + totalRunningIOPS + ", total disk space needed: " + totalHardDisk
                + " GB, total N/S Traffic: " + totalNorthSouthBW + " Mbit/s, total E/W Traffic: " + totalEastWestBW
                + " Mbit/s, Average Network Throughput Per Blade: " + avgNetworkThroughputPerBlade + ")\n");

        sb.append("********************************************************************************");


        placement.forEach(blade -> {
            sb.append("\t\n" + blade.printForSocket());
        });
        sb.append("\n");

        return sb.toString();
    }

  

    public void setYear(String year) {
        this.year = year;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public List<Blade> getPlacement() {
        return placement;
    }

    public void setPlacement(List<Blade> placement) {
        this.placement = placement;
    }
}
