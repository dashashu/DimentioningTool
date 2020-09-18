package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomConstants;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Averages {
    private String site;

    private Map<String, Integer> initiativeVMNumber;
    private Map<String, Double> avgBlockSize;
    private Map<String, Double> avgIops;
    private Map<String, Double> avgReadWrite;
    private Integer foundationVMNumber;
    private Double avgBlockSizeFoundation = 0.0D;
    private Double totalIopsFoundation = 0.0D;
    private Double avgIopsFoundation = 0.0D;
    private Double avgReadWriteFoundation = 0.0D;

    public Averages(String site, Set<String> yearNames, List<Cluster> clusterList) throws UnexpectedSituationOccurredException {
    	Cluster foundationCluster = null;;
    	for(Cluster x:clusterList) {
    		if(x.isFoundation()) {
    			foundationCluster = x;
    			break;
    		}
    	}
    	if(foundationCluster != null) {
	    	List<VBom> foundationVBomList = foundationCluster.getVbom();
	    	setAveragesFoundation(site, foundationVBomList);
    	}
    	
        this.site = site;
        Map<String, Double> avgBlockSize = new HashMap<String, Double>();
        Map<String, Double> avgIops = new HashMap<String, Double>();
        Map<String, Double> totalIops = new HashMap<String, Double>();
        Map<String, Double> totalIops2 = new HashMap<String, Double>();
        Map<String, Double> avgReadWrite = new HashMap<String, Double>();
        Map<String, Integer> vmPerYear = new HashMap<String, Integer>();

        for (String yearName : yearNames) {
            avgBlockSize.put(yearName, 0D); //blocksize
            avgIops.put(yearName, 0D); //iops running average
            totalIops.put(yearName, 0D); //total iops for readwriteavg
            totalIops2.put(yearName, 0D); //total iops for avg read/write calculation
            avgReadWrite.put(yearName, 0D); //readwrite
            vmPerYear.put(yearName, 0);
        }

        //sum(iops:read * IOPS)/iopstotal
        for(Cluster c:clusterList) {
        	if(!c.isFoundation()) {
	        	for (VBom v : c.getVbom()) {
	                for (VBomYear y : v.getvBomYearList()) {
	                    if (y.getSiteList().contains(site)) {
	                        if (!y.isYearEmpty()) {
	                        	int numOfVM = y.getVnfPerSite()*y.getNumberOfVnfPerTypeAndPerInstance();
	                            vmPerYear.put(y.getYearName(), vmPerYear.get(y.getYearName()) + numOfVM);
	                            avgBlockSize.put(y.getYearName(), avgBlockSize.get(y.getYearName()) + (v.getBlockSize() * numOfVM));
	                            totalIops.put(y.getYearName(), totalIops.get(y.getYearName()) + ((y.getStorageIopsPerVmRunning()*v.getBlockSize()) * numOfVM));
	                            totalIops2.put(y.getYearName(), totalIops2.get(y.getYearName()) + (y.getStorageIopsPerVmRunning() * numOfVM));
	                            avgReadWrite.put(y.getYearName(), avgReadWrite.get(y.getYearName()) + ((new Double(y.getStorageIopsPerVmRunning() * y.getStorageVmWorkloadForRead()) / 100) * numOfVM));
	                        }
	                    }
	                }
	            }
	        	
	        	if(foundationCluster!=null)
		        	for (VBomYear y : c.getVbom().get(0).getvBomYearList()) {
			        	vmPerYear.put(y.getYearName(), vmPerYear.get(y.getYearName()) + 1);
		                avgBlockSize.put(y.getYearName(), avgBlockSize.get(y.getYearName()) + (avgBlockSizeFoundation));
		                totalIops.put(y.getYearName(), totalIops.get(y.getYearName()) + avgIopsFoundation);
		                totalIops2.put(y.getYearName(), totalIops2.get(y.getYearName()) + totalIopsFoundation);
		                avgReadWrite.put(y.getYearName(), avgReadWrite.get(y.getYearName()) + avgReadWriteFoundation);
		        	}
        	}
        }
        
        

        for (String yearName : yearNames) {
            int numOfVm = vmPerYear.get(yearName);
            
            if(numOfVm > 0) {
	            Double tempAvgBlockSize = avgBlockSize.get(yearName) / numOfVm;
	
	            int index = -1;
	            for (int i = 0; i < VBomConstants.BLOCK_SIZE_VALUES.length; i++) {
	                if (tempAvgBlockSize >= VBomConstants.BLOCK_SIZE_VALUES[i] && tempAvgBlockSize <= VBomConstants.BLOCK_SIZE_VALUES[i + 1])
	                    index = i;
	            }
	
	            if (index == -1 && tempAvgBlockSize!=0)
	                throw new UnexpectedSituationOccurredException("The average value of block size isn't into the expected range for year: " + yearName);
	
	            Double blockSizeAvg = (new Double(VBomConstants.BLOCK_SIZE_VALUES[index] + VBomConstants.BLOCK_SIZE_VALUES[index + 1])) / 2;
	            if (tempAvgBlockSize < blockSizeAvg)
	                avgBlockSize.put(yearName, new Double(VBomConstants.BLOCK_SIZE_VALUES[index]));
	            else
	                avgBlockSize.put(yearName, new Double(VBomConstants.BLOCK_SIZE_VALUES[index + 1]));
	
	            avgIops.put(yearName, totalIops2.get(yearName));
	            Double toBeRoundedReadWrite = (avgReadWrite.get(yearName) / totalIops2.get(yearName)) * 100;
	            avgReadWrite.put(yearName, Double.parseDouble(5 * (Math.round(toBeRoundedReadWrite / 5)) + ""));
            }
        }
        this.avgBlockSize = avgBlockSize;
        this.avgIops = avgIops;
        this.avgReadWrite = avgReadWrite;
        this.initiativeVMNumber = vmPerYear;
    }

    private void setAveragesFoundation(String site, List<VBom> foundation) throws UnexpectedSituationOccurredException {
        Double avgBlockSizeFoundation = 0D;
        Double totalIopsFoundation = 0D;
        Double totalIopsFoundation2 = 0D;
        Double avgReadWriteFoundation = 0D;
        Integer numVmFoundation = 0;
        foundationVMNumber = numVmFoundation;

        if(foundation.size() < 1)
        	return;
        //sum(iops:read * IOPS)/iopstotal
        for (VBom v : foundation) {
            for (VBomYear y : v.getvBomYearList()) {
                if (y.getSiteList().contains(site)) {
                    if (!y.isYearEmpty()) {
                        int numOfVM = y.getVnfPerSite()*y.getNumberOfVnfPerTypeAndPerInstance();
                        numVmFoundation+=numOfVM;
                        avgBlockSizeFoundation += (v.getBlockSize() * numOfVM);
                        totalIopsFoundation += (y.getStorageIopsPerVmRunning()*v.getBlockSize()) * numOfVM;
                        totalIopsFoundation2 += (y.getStorageIopsPerVmRunning() * numOfVM);
                        avgReadWriteFoundation += (((y.getStorageIopsPerVmRunning() * y.getStorageVmWorkloadForRead()) / 100)) * numOfVM;
                    }
                }
            }
        }
        
        foundationVMNumber = numVmFoundation;

        avgBlockSizeFoundation = avgBlockSizeFoundation / numVmFoundation;
        Double tempAvgBlockSizeFoundation = avgBlockSizeFoundation;

        int index = -1;
        for (int i = 0; i < VBomConstants.BLOCK_SIZE_VALUES.length; i++) {
            if (tempAvgBlockSizeFoundation >= VBomConstants.BLOCK_SIZE_VALUES[i] && tempAvgBlockSizeFoundation <= VBomConstants.BLOCK_SIZE_VALUES[i + 1])
                index = i;
        }

        if (index == -1)
            throw new UnexpectedSituationOccurredException("The average value of block size is not in the expected range.");

        Double blockSizeAvg = (new Double(VBomConstants.BLOCK_SIZE_VALUES[index] + VBomConstants.BLOCK_SIZE_VALUES[index + 1])) / 2;
        if (tempAvgBlockSizeFoundation < blockSizeAvg)
            avgBlockSizeFoundation = new Double(VBomConstants.BLOCK_SIZE_VALUES[index]);
        else
            avgBlockSizeFoundation = new Double(VBomConstants.BLOCK_SIZE_VALUES[index + 1]);

        avgIopsFoundation = totalIopsFoundation / avgBlockSizeFoundation;
        Double toBeRoundedReadWrite = (avgReadWriteFoundation / totalIopsFoundation2) * 100;
        avgReadWriteFoundation = Double.parseDouble(5 * (Math.round(toBeRoundedReadWrite / 5)) + "");

        this.avgBlockSizeFoundation = avgBlockSizeFoundation;
        this.avgIopsFoundation = totalIopsFoundation2;
        this.totalIopsFoundation = totalIopsFoundation;
        this.avgReadWriteFoundation = avgReadWriteFoundation;
    }

    public String getSite() {
        return site;
    }

    public Map<String, Double> getAvgBlockSize() {
        return avgBlockSize;
    }

    public void setAvgBlockSize(Map<String, Double> avgBlockSize) {
        this.avgBlockSize = avgBlockSize;
    }

    public Map<String, Double> getAvgIops() {
        return avgIops;
    }

    public void setAvgIops(Map<String, Double> avgIops) {
        this.avgIops = avgIops;
    }

    public Map<String, Double> getAvgReadWrite() {
        return avgReadWrite;
    }

    public void setAvgReadWrite(Map<String, Double> avgReadWrite) {
        this.avgReadWrite = avgReadWrite;
    }

    public Double getAvgBlockSizeFoundation() {
        return avgBlockSizeFoundation;
    }

    public void setAvgBlockSizeFoundation(Double avgBlockSizeFoundation) {
        this.avgBlockSizeFoundation = avgBlockSizeFoundation;
    }

    public Double getAvgIopsFoundation() {
        return avgIopsFoundation;
    }

    public void setAvgIopsFoundation(Double avgIopsFoundation) {
        this.avgIopsFoundation = avgIopsFoundation;
    }

    public Double getAvgReadWriteFoundation() {
        return avgReadWriteFoundation;
    }

    public void setAvgReadWriteFoundation(Double avgReadWriteFoundation) {
        this.avgReadWriteFoundation = avgReadWriteFoundation;
    }
}
