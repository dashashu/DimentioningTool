package commons.src.main.java.it.spindox.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;

import commons.src.main.java.it.spindox.configuration.ConfigurationManagement;
import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.catalog.Compute;
import commons.src.main.java.it.spindox.model.configurations.ClusterConfiguration;
import commons.src.main.java.it.spindox.model.configurations.ClusterSelection;
import commons.src.main.java.it.spindox.model.configurations.InputConfiguration;
import commons.src.main.java.it.spindox.model.configurations.VBomPreProcessingRule;
import commons.src.main.java.it.spindox.model.configurations.VBomRules;
import commons.src.main.java.it.spindox.model.placementAndEstimation.BladeFactory;
import commons.src.main.java.it.spindox.model.preprocessing.ClusterRuleApplicationResult;
import commons.src.main.java.it.spindox.model.preprocessing.PreProcessingConstants;
import commons.src.main.java.it.spindox.model.vbom.Cluster;
import commons.src.main.java.it.spindox.model.vbom.VBom;
import commons.src.main.java.it.spindox.model.vbom.VBomGroup;
import commons.src.main.java.it.spindox.model.vbom.VBomYear;
import commons.src.main.java.it.spindox.vfexception.RulesValidationException;
import commons.src.main.java.it.spindox.vfexception.core.MultipleClusterConfigurationDefinedIntoInputConfigJsonException;
import commons.src.main.java.it.spindox.vfexception.core.NoClusterConfigurationDefinedIntoInputConfigJsonException;
import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

public class RulesUtil {
	//private static final Logger logger = LogManager.getLogger(RulesUtil.class);
	final static Logger logger = Logger.getLogger(RulesUtil.class);
	
	public static List<VBom> applyPreProcessingRules(List<VBom> vBomList, List<VBomPreProcessingRule> vBomPreProcessing) throws UnexpectedSituationOccurredException {
		List<VBom> vBomList2 = new ArrayList<VBom>();
		
		for(VBom vBom:vBomList) {
			VBom vBom2 = new VBom(vBom);
			
			for(VBomPreProcessingRule rule:vBomPreProcessing)
				vBom2 = applyPreProcessingRule(vBom2, rule);
			
			vBomList2.add(vBom2);
		}
		
		return vBomList2;
	}
	
	private static VBom applyPreProcessingRule(VBom vBom, VBomPreProcessingRule rule) throws UnexpectedSituationOccurredException {
		VBom vBom2 = new VBom(vBom);
		
		switch(rule.getCellToModify()) {
		case PreProcessingConstants.VNF_NAME:
			vBom2.setVnfName(applyRuleString(vBom, rule, null, vBom2.getVnfName()));
			break;
		case PreProcessingConstants.VM_TYPE_NAME:
			vBom2.setVmTypeName(applyRuleString(vBom, rule, null, vBom2.getVmTypeName()));
			break;
		case PreProcessingConstants.HIGH_THROUGHPUT:
			vBom2.setHighThroughputVswitchResources(applyRuleInteger(vBom, rule, null, vBom2.getHighThroughputVswitchResources()));
			break;
		case PreProcessingConstants.BLOCK_SIZE:
			vBom2.setBlockSize(applyRuleInteger(vBom, rule, null, vBom2.getBlockSize()));
			break;
//		case PreProcessingConstants.VM_WORKLOAD_TYPE:
//			vBom2.setVmWorkloadType(applyRuleString(vBom, rule, null, vBom2.getVmWorkloadType()));
//			break;
			
		case PreProcessingConstants.VNF_PER_SITE:
		case PreProcessingConstants.VNF_PER_INSTANCE:
//		//NUMA
		case PreProcessingConstants.NUMA_FLAG:
		case PreProcessingConstants.SOCKET_FLAG:
			
		case PreProcessingConstants.CPU_NUMBER:
		case PreProcessingConstants.RAM:
		case PreProcessingConstants.STORAGE_DATA_DISK:
		case PreProcessingConstants.STORAGE_OS_DISK:
		case PreProcessingConstants.IOPS_RUNNING:
		case PreProcessingConstants.IOPS_LOADING:
		case PreProcessingConstants.NORTH_SOUTH_BANDWIDTH:
		case PreProcessingConstants.EAST_WEST_BANDWIDTH:
		case PreProcessingConstants.STORAGE_SUM:
		case PreProcessingConstants.BANDWIDTH_SUM:
			vBom2.setvBomYearList(applyRuleYears(vBom, rule));
			break;
		}
		
		return vBom2;
	}
	
	private static List<VBomYear> applyRuleYears(VBom vBom, VBomPreProcessingRule rule) throws UnexpectedSituationOccurredException {
		List<VBomYear> yearList = new ArrayList<VBomYear>();
		
		for(VBomYear y:vBom.getvBomYearList()) {
			if(y.isYearEmpty()) {
				yearList.add(y);
				continue;
			}
			
			switch(rule.getCellToModify()) {
			case PreProcessingConstants.VNF_PER_SITE:
				y.setVnfPerSite(applyRuleInteger(vBom, rule, y, y.getVnfPerSite()));
				break;
			case PreProcessingConstants.VNF_PER_INSTANCE:
				y.setNumberOfVnfPerTypeAndPerInstance(applyRuleInteger(vBom, rule, y, y.getNumberOfVnfPerTypeAndPerInstance()));
				break;
			//NUMA
			case PreProcessingConstants.NUMA_FLAG:
				y.setNumaFlag(applyRuleString(vBom, rule, y, y.getNumaFlag()));
				break;
			case PreProcessingConstants.SOCKET_FLAG:
				y.setSocket(applyRuleString(vBom, rule, y, y.getSocket()));
				break;
				
			case PreProcessingConstants.CPU_NUMBER:
				y.setNumberOfVcpuPerVm(applyRuleDouble(vBom, rule, y, y.getNumberOfVcpuPerVm()));
				break;
			case PreProcessingConstants.RAM:
				y.setRamPerVmInGB(applyRuleDouble(vBom, rule, y, y.getRamPerVmInGB()));
				break;
			case PreProcessingConstants.STORAGE_DATA_DISK:
				y.setStoragePerVmDataDisk(applyRuleInteger(vBom, rule, y, y.getStoragePerVmDataDisk()));
				break;
			case PreProcessingConstants.STORAGE_OS_DISK:
				y.setStoragePerVmOsDisk(applyRuleInteger(vBom, rule, y, y.getStoragePerVmOsDisk()));
				break;
			case PreProcessingConstants.IOPS_RUNNING:
				y.setStorageIopsPerVmRunning(applyRuleInteger(vBom, rule, y, y.getStorageIopsPerVmRunning()));
				break;
			case PreProcessingConstants.IOPS_LOADING:
				y.setStorageIopsPerVmLoading(applyRuleInteger(vBom, rule, y, y.getStorageIopsPerVmLoading()));
				break;
			case PreProcessingConstants.NORTH_SOUTH_BANDWIDTH:
				y.setNorthSouthBandwidthRequirementPerVm(applyRuleInteger(vBom, rule, y, y.getNorthSouthBandwidthRequirementPerVm()));
				break;
			case PreProcessingConstants.EAST_WEST_BANDWIDTH:
				y.setEastWestBandwidthRequirementPerVm(applyRuleInteger(vBom, rule, y, y.getEastWestBandwidthRequirementPerVm()));
				break;
			case PreProcessingConstants.STORAGE_SUM:
				y.setEastWestBandwidthRequirementPerVm(applyRuleInteger(vBom, rule, y, y.getStoragePerVmDataDisk() + y.getStoragePerVmOsDisk()));
				break;
			case PreProcessingConstants.BANDWIDTH_SUM:
				y.setEastWestBandwidthRequirementPerVm(applyRuleInteger(vBom, rule, y, y.getEastWestBandwidthRequirementPerVm() + y.getNorthSouthBandwidthRequirementPerVm()));
				break;
			}
			
			yearList.add(y);
		}
		
		return yearList;
	}
	
	private static ClusterRuleApplicationResult applyClusterSelectionRule(VBom vBom, ClusterSelection rule) throws UnexpectedSituationOccurredException {
		ClusterRuleApplicationResult returnObject = new ClusterRuleApplicationResult();
		boolean success = false;
		
		switch(rule.getEvaluatedCell()) {
		case PreProcessingConstants.VNF_NAME:
			success = evaluateClusterWithString(rule, vBom.getVnfName());
			break;
		case PreProcessingConstants.VM_TYPE_NAME:
			success = evaluateClusterWithString(rule, vBom.getVmTypeName());
			break;
		case PreProcessingConstants.VM_WORKLOAD_TYPE:
			success = evaluateClusterWithString(rule, vBom.getVmWorkloadType());
			break;
		case PreProcessingConstants.VNF_PER_SITE:
			boolean tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getVnfPerSite())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.VNF_PER_INSTANCE:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getNumberOfVnfPerTypeAndPerInstance())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		//NUMA
		case PreProcessingConstants.NUMA_FLAG:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithString(rule, vBom.getvBomYearList().get(i).getNumaFlag()))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.SOCKET_FLAG:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithString(rule,  vBom.getvBomYearList().get(i).getSocket()))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
			
		case PreProcessingConstants.CPU_NUMBER:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getNumberOfVcpuPerVm())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.RAM:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getRamPerVmInGB())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.STORAGE_DATA_DISK:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getStoragePerVmDataDisk())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.STORAGE_OS_DISK:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getStoragePerVmOsDisk())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.IOPS_RUNNING:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getStorageIopsPerVmRunning())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.IOPS_LOADING:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getStorageIopsPerVmLoading())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.NORTH_SOUTH_BANDWIDTH:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getNorthSouthBandwidthRequirementPerVm())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.EAST_WEST_BANDWIDTH:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getEastWestBandwidthRequirementPerVm())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.STORAGE_SUM:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getStoragePerVmDataDisk() +
							vBom.getvBomYearList().get(i).getStoragePerVmOsDisk())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		case PreProcessingConstants.BANDWIDTH_SUM:
			tempSuccess = false;
			for(int i=0; i<vBom.getvBomYearList().size(); i++) {
				if(vBom.getvBomYearList().get(i) != null && !vBom.getvBomYearList().get(i).isYearEmpty()) {
					if(evaluateClusterWithNumber(rule, new Double(vBom.getvBomYearList().get(i).getEastWestBandwidthRequirementPerVm() +
							vBom.getvBomYearList().get(i).getNorthSouthBandwidthRequirementPerVm())))
						tempSuccess = true;
				}
			}
			success = tempSuccess;
			break;
		}
		
		returnObject.setvBom(vBom);
		returnObject.setSuccess(success);
		if(success)
			returnObject.setClusterName(rule.getDestinationClusterOnMatchSuccess());
		else
			returnObject.setClusterName(rule.getDestinationClusterOnMatchFailure());
		
		return returnObject;
	}
	
	private static boolean evaluateClusterWithString(ClusterSelection rule, String value) throws UnexpectedSituationOccurredException {
		switch(rule.getOperator()) {
		case PreProcessingConstants.EQUALS:
			if(value.equals(rule.getMatchValue()))
				return true;
			else
				return false;
		case PreProcessingConstants.GREATER:
		case PreProcessingConstants.LESS:
			throw new UnexpectedSituationOccurredException("Cluster Selection: Not expecting to have to do a majority/minority comparison on the field "+rule.getEvaluatedCell());
		
		}
		
		throw new UnexpectedSituationOccurredException("Cluster Selection: Operator not valid: " + rule.getOperator());
	}
	
	private static boolean evaluateClusterWithNumber(ClusterSelection rule, Double value) throws UnexpectedSituationOccurredException {
		switch(rule.getOperator()) {
		case PreProcessingConstants.EQUALS:
			if(value.equals(new Double(rule.getMatchValue())))
				return true;
			else
				return false;
		case PreProcessingConstants.GREATER:
			if(value > new Double(rule.getMatchValue()))
				return true;
			else
				return false;
		case PreProcessingConstants.LESS:
			if(value < new Double(rule.getMatchValue()))
				return true;
			else
				return false;
		default:
			throw new UnexpectedSituationOccurredException("Cluster Selection: invalid rule operator: " + rule.getOperator());
		}
	}
	
	private static Integer applyRuleInteger(VBom vBom, VBomPreProcessingRule rule, VBomYear year, Integer oldValue) throws UnexpectedSituationOccurredException {
		Object cellToModifyValue = null;
		Object evaluatedCellValue = null;
		
		evaluatedCellValue = getValueFromVBom(rule.getEvaluatedCell(), vBom, year);
		cellToModifyValue = getValueFromVBom(rule.getCellToModify(), vBom, year);
		
		boolean controlMatch = false;
		
		cellToModifyValue = new Double((int)cellToModifyValue);
		controlMatch = controlMatchNumber(rule, year, Double.valueOf(evaluatedCellValue.toString()));
		
		if(controlMatch) {
			if(rule.getAction().equals(PreProcessingConstants.MULTIPLICATION)) {
				return (multiplicate(vBom, rule, year, (Double)cellToModifyValue)).intValue();
			} else if(rule.getAction().equals(PreProcessingConstants.OVERWRITE)) {
				return Integer.parseInt(rule.getActionArgument());
			} else throw new UnexpectedSituationOccurredException("Pre Processing: Action not valid: " + rule.getOperator() 
																+ ". Expected " + PreProcessingConstants.MULTIPLICATION + 
																" or " + PreProcessingConstants.OVERWRITE + ".");
		} else return oldValue;
	}
	
	private static Double applyRuleDouble(VBom vBom, VBomPreProcessingRule rule, VBomYear year, Double oldValue) throws UnexpectedSituationOccurredException {
		Object cellToModifyValue = null;
		Object evaluatedCellValue = null;
		
		evaluatedCellValue = getValueFromVBom(rule.getEvaluatedCell(), vBom, year);
		cellToModifyValue = getValueFromVBom(rule.getCellToModify(), vBom, year);
		
		boolean controlMatch = false;
		
		controlMatch = controlMatchNumber(rule, year, Double.valueOf(evaluatedCellValue.toString()));
		
		if(controlMatch) {
			if(rule.getAction().equals(PreProcessingConstants.MULTIPLICATION)) {
				return multiplicate(vBom, rule, year, Double.valueOf(cellToModifyValue.toString()));
			} else if(rule.getAction().equals(PreProcessingConstants.OVERWRITE)) {
				return Double.parseDouble(rule.getActionArgument());
			} else throw new UnexpectedSituationOccurredException("Pre Processing: Action not valid: " + rule.getOperator() 
																+ ". Expected " + PreProcessingConstants.MULTIPLICATION + 
																" or " + PreProcessingConstants.OVERWRITE + ".");
		} else return oldValue;
	}
	
	private static String applyRuleString(VBom vBom, VBomPreProcessingRule rule, VBomYear year, String oldValue) throws UnexpectedSituationOccurredException {
		Object evaluatedCellValue = null;
		
		evaluatedCellValue = getValueFromVBom(rule.getEvaluatedCell(), vBom, year);
		
		boolean controlMatch = false;
		
		controlMatch = controlMatchString(rule, year, evaluatedCellValue.toString());
		
		if(controlMatch) {
			if(rule.getAction().equals(PreProcessingConstants.MULTIPLICATION)) {
				throw new UnexpectedSituationOccurredException("Pre Processing: Multiplication required for a text field.");
			} else if(rule.getAction().equals(PreProcessingConstants.OVERWRITE)) {
				return rule.getActionArgument();
			} else throw new UnexpectedSituationOccurredException("Pre Processing: Action not valid: " + rule.getOperator() 
																+ ". Expected " + PreProcessingConstants.MULTIPLICATION + 
																" or " + PreProcessingConstants.OVERWRITE + ".");
		} else return oldValue;
	}
	
	private static boolean controlMatchNumber(VBomPreProcessingRule rule, VBomYear year, Double evaluatedValue) throws UnexpectedSituationOccurredException {
		switch(rule.getOperator()) {
		case PreProcessingConstants.EQUALS:
			if(evaluatedValue.equals(new Double(rule.getMatchValue())))
				return true;
			else
				return false;
		case PreProcessingConstants.GREATER:
			if(evaluatedValue > new Double(rule.getMatchValue()))
				return true;
			else
				return false;
		case PreProcessingConstants.LESS:
			if(evaluatedValue < new Double(rule.getMatchValue()))
				return true;
			else
				return false;
		}
		
		throw new UnexpectedSituationOccurredException("Pre Processing: invalid rule operator: " + rule.getOperator());
	}
	
	private static Object getValueFromVBom(String cellName, VBom vBom, VBomYear year) {
		switch(cellName) {
		case PreProcessingConstants.VNF_NAME:
			return vBom.getVnfName();
		case PreProcessingConstants.VM_TYPE_NAME:
			return vBom.getVmTypeName();
		case PreProcessingConstants.HIGH_THROUGHPUT:
			return vBom.getHighThroughputVswitchResources();
		case PreProcessingConstants.BLOCK_SIZE:
			return vBom.getBlockSize();
		case PreProcessingConstants.VM_WORKLOAD_TYPE:
			return vBom.getVmWorkloadType();
		case PreProcessingConstants.VNF_PER_SITE:
			return year.getVnfPerSite();
		case PreProcessingConstants.VNF_PER_INSTANCE:
			return year.getNumberOfVnfPerTypeAndPerInstance();
			
		//NUMA
		case PreProcessingConstants.NUMA_FLAG:
			return year.getNumaFlag();
		case PreProcessingConstants.SOCKET_FLAG:
			return year.getSocket();
			
		case PreProcessingConstants.CPU_NUMBER:
			return year.getNumberOfVcpuPerVm();
		case PreProcessingConstants.RAM:
			return year.getRamPerVmInGB();
		case PreProcessingConstants.STORAGE_DATA_DISK:
			return year.getStoragePerVmDataDisk();
		case PreProcessingConstants.STORAGE_OS_DISK:
			return year.getStoragePerVmOsDisk();
		case PreProcessingConstants.IOPS_RUNNING:
			return year.getStorageIopsPerVmRunning();
		case PreProcessingConstants.IOPS_LOADING:
			return year.getStorageIopsPerVmLoading();
		case PreProcessingConstants.NORTH_SOUTH_BANDWIDTH:
			return year.getNorthSouthBandwidthRequirementPerVm();
		case PreProcessingConstants.EAST_WEST_BANDWIDTH:
			return year.getEastWestBandwidthRequirementPerVm();
		case PreProcessingConstants.STORAGE_SUM:
			return year.getStoragePerVmDataDisk()+year.getStoragePerVmOsDisk();
		case PreProcessingConstants.BANDWIDTH_SUM:
			return year.getEastWestBandwidthRequirementPerVm()+year.getNorthSouthBandwidthRequirementPerVm();
		}
		
		return null;
	}
	
	private static boolean controlMatchString(VBomPreProcessingRule rule, VBomYear year, String evaluatedValue) throws UnexpectedSituationOccurredException {
		switch(rule.getOperator()) {
			case PreProcessingConstants.EQUALS:
				if(evaluatedValue.equals(rule.getMatchValue()))
					return true;
				else
					return false;
			case PreProcessingConstants.GREATER:
			case PreProcessingConstants.LESS:
				throw new UnexpectedSituationOccurredException("Pre Processing: Not expecting to have to do a majority/minority comparison on the field "+rule.getEvaluatedCell());
		}
		
		throw new UnexpectedSituationOccurredException("Pre Processing: Operator not valid: " + rule.getOperator());
	}
	
	private static Double multiplicate(VBom vBom, VBomPreProcessingRule rule, VBomYear year, Double evaluatedValue) {
		return new Double(rule.getActionArgument())*evaluatedValue;
	}
	
	public static List<Cluster> dividePerCluster(List<VBom> vBomCustomerListInput, VBomRules vBomRules,
			InputConfiguration inputConfig, Catalog catalog) throws UnexpectedSituationOccurredException, NoClusterConfigurationDefinedIntoInputConfigJsonException, MultipleClusterConfigurationDefinedIntoInputConfigJsonException {
		List<VBom> vBomCustomerList = new ArrayList<VBom>(vBomCustomerListInput);
		List<Cluster> clusterList = new ArrayList<Cluster>();
		List<VBomGroup> vBomGroups = new ArrayList<VBomGroup>();
		VBomGroup tempGroup = new VBomGroup();
		List<VBom> foundationGroup = new ArrayList<VBom>();
		
		for(VBom temp:vBomCustomerList) {
			if(temp.getVmWorkloadType().equals(ConfigurationManagement.getVbomConfiguration().getString("foundationWorkloadType"))) {
				foundationGroup.add(temp);
			}
		}
		
		for(VBom temp:foundationGroup)
			vBomCustomerList.remove(temp);
		
		while(!vBomCustomerList.isEmpty()) { //Fino a che il vbom non è vuoto
			tempGroup = new VBomGroup();
			tempGroup.addAll(getAVBomGroupFromList(new HashSet<VBom>(vBomCustomerList))); //Divido le VM in affinità per gruppi
			vBomGroups.add(tempGroup);
			
			for(VBom temp:tempGroup.getVBomList())
				vBomCustomerList.remove(temp);
		}
		
		for(VBomGroup t:vBomGroups) { //scorro i gruppi
			String clusterForGroup = null;
			List<ClusterRuleApplicationResult> groupRuleResult = new ArrayList<ClusterRuleApplicationResult>();
			
			
			for(VBom vbc:t.getVBomList()) {
				for(ClusterSelection clusterSelectionRule:vBomRules.getClusterSelectRules())
					if(clusterSelectionRule.getWorkloadType().equals(vbc.getVmWorkloadType()))
						groupRuleResult.add(applyClusterSelectionRule(vbc, clusterSelectionRule));
			}
			
			boolean isSuccess = false; //Se è true vuol dire che per questo gruppo almeno una vm ha success
			for(ClusterRuleApplicationResult crar:groupRuleResult) { //scorro gli elementi del gruppo
				if(crar.isSuccess()) { //Per ogni success
					if(!isSuccess) { //Se nessuno era ancora success
						isSuccess = true; //Segno il successo
						clusterForGroup = crar.getClusterName(); //E il nome del cluster
					} else {
						if(!crar.getClusterName().equals(clusterForGroup)) //Se qualcosa era già success, ma il nome del cluster è cambiato allora è un errore
							throw new UnexpectedSituationOccurredException("The evaluation of the cluster rule would put the VMs in affinity or with the same"
									+ " VNF Name in different clusters.");
					}
				}
			}
			
			if ((foundationGroup.isEmpty()) ||
                    (!foundationGroup.isEmpty() && inputConfig.isFoundationFlag() && inputConfig.isManagementFlag())) { //Se c'è una foundation ma non era previsto restituisco errore, altrimenti vado avanti
				if(isSuccess) { //Se almeno per una VM era success, allora vanno tutte nello stesso cluster
					final String clusterForGroup2 = clusterForGroup;
					
					Cluster tempCluster = null;
					if(clusterList.stream().filter(c -> c.getSheetLabel().equals(clusterForGroup2)).findAny().isPresent())
						tempCluster = clusterList.stream().filter(c -> c.getSheetLabel().equals(clusterForGroup2)).findFirst().get();
					
					List<ClusterConfiguration> tmpClusterConfigurationList = inputConfig.getClusterConfiguration().stream().filter(clusterConfiguration -> clusterConfiguration.getSheetLabel().equalsIgnoreCase(clusterForGroup2)).collect(Collectors.toList());
	                
					if (tmpClusterConfigurationList.size() == 1) { //Se non trovo una configuration per questo cluster sollevo errore.
	                    Compute blade;
	
	                    ClusterConfiguration clusterConfiguration = tmpClusterConfigurationList.get(0);
	                    if (!clusterConfiguration.isHighPerformanceBladeFlag()) {
	                        blade = catalog.getBlade();
	                    } else {
	                        blade = catalog.getBladeHighPerformance();
	                    }
	                    BladeFactory bladeFactory = new BladeFactory(blade, clusterConfiguration, inputConfig.getEsxiCores(), inputConfig.getTxrxCores());	                    

	                    if(tempCluster!=null) {
	                    	List<VBom> tempList = tempCluster.getVbom();
	                    	tempList.addAll(t.getVBomList());
	                    	tempCluster.setVbom(tempList);
	                    } else
	                    	clusterList.add(new Cluster(clusterForGroup, t.getVBomList(), bladeFactory, inputConfig));
	                } else {
	                    if (tmpClusterConfigurationList.size() == 0) {
	                        throw new NoClusterConfigurationDefinedIntoInputConfigJsonException(clusterForGroup);
	                    } else {
	                        throw new MultipleClusterConfigurationDefinedIntoInputConfigJsonException(clusterForGroup);
	                    }
	                }
				} else { //Se nessuna VM nel gruppo ha restituito success.
					for(ClusterRuleApplicationResult crar:groupRuleResult) {
						final String clusterForGroup2 = crar.getClusterName();
						
						Cluster tempCluster = null;
						if(clusterList.stream().filter(c -> c.getSheetLabel().equals(clusterForGroup2)).findAny().isPresent())
							tempCluster = clusterList.stream().filter(c -> c.getSheetLabel().equals(clusterForGroup2)).findFirst().get();
						
						List<ClusterConfiguration> tmpClusterConfigurationList = inputConfig.getClusterConfiguration().stream().filter(clusterConfiguration -> clusterConfiguration.getSheetLabel().equalsIgnoreCase(clusterForGroup2)).collect(Collectors.toList());
						
						if (tmpClusterConfigurationList.size() == 1) { //Se non trovo una configuration per questo cluster sollevo errore.
		                    Compute blade;

		                    ClusterConfiguration clusterConfiguration = tmpClusterConfigurationList.get(0);
		                    if (!clusterConfiguration.isHighPerformanceBladeFlag()) {
		                        blade = catalog.getBlade();
		                    } else {
		                        blade = catalog.getBladeHighPerformance();
		                    }
		                    BladeFactory bladeFactory = new BladeFactory(blade, clusterConfiguration, inputConfig.getEsxiCores(), inputConfig.getTxrxCores());	                    

		                    if(tempCluster!=null) {
		                    	List<VBom> tempList = tempCluster.getVbom();
		                    	tempList.add(crar.getvBom());
		                    	tempCluster.setVbom(tempList);
		                    } else {
		                    	List<VBom> tempList = new ArrayList<VBom>();
		                    	tempList.add(crar.getvBom());
		                    	clusterList.add(new Cluster(crar.getClusterName(), tempList, bladeFactory, inputConfig));
		                    }
		                } else {
		                    if (tmpClusterConfigurationList.size() == 0) {
		                        throw new NoClusterConfigurationDefinedIntoInputConfigJsonException(clusterForGroup);
		                    } else {
		                        throw new MultipleClusterConfigurationDefinedIntoInputConfigJsonException(clusterForGroup);
		                    }
		                }
					}
				}
			} else {
                throw new UnexpectedSituationOccurredException("Found a foundation sheet, but management is disabled");
            }
		}
		
		if(!foundationGroup.isEmpty()) {
			String foundationLabel = ConfigurationManagement.getVbomConfiguration().getString("foundationWorkloadType");
			ClusterConfiguration foundationConfiguration = inputConfig.getClusterConfiguration().stream().filter(clusterConfiguration -> clusterConfiguration.getSheetLabel().equalsIgnoreCase(foundationLabel)).findFirst().get();
			Compute blade = new Compute();
			if (!foundationConfiguration.isHighPerformanceBladeFlag()) {
                blade = catalog.getBlade();
            } else {
                blade = catalog.getBladeHighPerformance();
            }
			BladeFactory bladeFactory = new BladeFactory(blade, foundationConfiguration, inputConfig.getEsxiCores(), inputConfig.getTxrxCores());
			
			Cluster foundationCluster = new Cluster(foundationLabel, foundationGroup, bladeFactory, inputConfig);
			clusterList.add(foundationCluster);
		}
		
		return clusterList;
	}
	
	private static Set<VBom> getAVBomGroupFromList(Set<VBom> vBomCustomerList) {
		Set<VBom> returnList = new HashSet<VBom>();
		
		VBom tempVBom = vBomCustomerList.iterator().next(); //prendo un vbom
		returnList.add(tempVBom); //lo metto nel gruppo
		
		returnList.addAll(recursiveCreateGroup(vBomCustomerList, tempVBom, returnList));
		
		return returnList;
	}

	/**
	 * @param originalVBomCustomerList Lista completa dei vbom rimanenti
	 * @param baseVBom VBom che stiamo usando a questo giro
	 * @param groupList Lista dei VBom raggruppati a questo giro
	 * @return un gruppo di vm raggruppate per vmname e affinità
	 */
	private static Set<VBom> recursiveCreateGroup(Set<VBom> originalVBomCustomerList, VBom baseVBom, Set<VBom> groupList) {
		Set<VBom> newVBomList = new HashSet<VBom>();
		
		for(VBom tempVBom:originalVBomCustomerList) {
			if((tempVBom.getVnfName().equals(baseVBom.getVnfName()) && !doesListContains(groupList, tempVBom.getCompleteName())) ||
				tempVBom.getExternalConstraintAffinity().contains(baseVBom.getVnfName()+"."+baseVBom.getVmTypeName()))	{
				newVBomList.add(tempVBom);
				groupList.add(tempVBom);
			}
		}
		
		for(String aff:baseVBom.getExternalConstraintAffinity()) {
			for(VBom tempVBom:originalVBomCustomerList) {
				if(tempVBom.getCompleteName().equals(aff) && !doesListContains(groupList, tempVBom.getCompleteName())) {
					newVBomList.add(tempVBom);
					groupList.add(tempVBom);
				}
			}
		}
		
		List<VBom> tempList = new ArrayList<VBom>();
		for(VBom tempVBom:newVBomList) {
			tempList.addAll(recursiveCreateGroup(originalVBomCustomerList, tempVBom, groupList));
		}
		
		newVBomList.addAll(tempList);
		
		return newVBomList;
	}
	
	private static boolean doesListContains(Set<VBom> vBomCustomerList, String name) {
		for(VBom temp:vBomCustomerList)
			if((temp.getVnfName()+"."+temp.getVmTypeName()).equals(name))
				return true;
		
		return false;
	}
	
	/*This commented function is probably useless. If the tool is already loading VBomCustomers, and correctly
	doing the cluster selection, delete it.*/
//	private static List<VBom> convertVBomCustomer(List<VBom> vbcList, InputConfiguration inputConfig) {
//    	List<VBom> newList = new ArrayList<VBom>();
//    	
//    	for(VBom vbc:vbcList) {
//    		VBom vb = new VBom();
//    		vb.setVnfName(vbc.getVnfName());
//    		vb.setVmTypeName(vbc.getVmTypeName());
//    		vb.setSelfConstraint(vbc.getSelfConstraint());
//    		vb.setExternalConstraintAffinity(vbc.getExternalConstraintAffinity());
//    		vb.setExternalConstraintAntiAffinity(vbc.getExternalConstraintAntiAffinity());
//    		vb.setHighThroughputVswitchResources(inputConfig.getDefaultHighThroughput());
//    		vb.setBlockSize(inputConfig.getDefaultBlockSize());
//    		vb.setvBomYearList(vbc.getvBomYearList());
//    		
//    		newList.add(vb);
//    	}
//    	
//    	return newList;
//    }
	
	/**
	 * 
	 * @param vBomRules
	 * @throws RulesValidationException
	 */
	public static void validateRules(VBomRules vBomRules, boolean isCustomer) throws RulesValidationException {
		List<VBomPreProcessingRule> preProcessRules = vBomRules.getPreProcessRules();
		List<ClusterSelection> clusterSelectionRules = vBomRules.getClusterSelectRules();
		
		Integer index = 0;
		for(VBomPreProcessingRule rule:preProcessRules) {
			index++;
			
			//Checking if there are empty parameters in a rule
			if(rule.getEvaluatedCell() == null || rule.getEvaluatedCell().equals("") || rule.getOperator() == null ||
					rule.getOperator().equals("") || rule.getMatchValue() == null || rule.getMatchValue().equals("") ||
					rule.getAction() == null || rule.getAction().equals("") || rule.getActionArgument() == null ||
					rule.getActionArgument().equals("") || rule.getCellToModify() == null || rule.getCellToModify().equals("")) {
				throw new RulesValidationException("One or more parameters are empty in the pre processing rule n. " + index);
			}
			
			//Check if the operator contains a valid value
			if(!PreProcessingConstants.VALID_OPERATORS.contains(rule.getOperator()))
				throw new RulesValidationException("The value contained in the 'Operator' parameter in the pre processing "
						+ "rule n. " + index + " is not allowed. The allowed operators are 'greater', 'equals' and 'less'.");
			
			//Check if the action contains a valid value
			if(!PreProcessingConstants.VALID_ACTIONS.contains(rule.getAction()))
				throw new RulesValidationException("The key contained in the 'Action' parameter in the pre processing "
						+ "rule n. " + index + " is not valid. The allowed actions are 'overwrite' and 'multiplication'.");
			
			//Check if the evaluated cell contains a valid key
			if(!PreProcessingConstants.VALID_NAMES_EVALUATED_CELL.contains(rule.getEvaluatedCell()))
				throw new RulesValidationException("The key contained in the 'Evaluated Cell' parameter in the pre processing "
						+ "rule n. " + index + " is not valid.");
			
			//Check if the cell to modify contains a valid key
			if(!PreProcessingConstants.VALID_NAMES_CELL_TO_MODIFY.contains(rule.getCellToModify()))
				throw new RulesValidationException("The key contained in the 'Cell to Modify' parameter in the pre processing "
						+ "rule n. " + index + " is not valid.");
			
			//If the match value is not numeric the operator can only be equals
			if(!NumberUtils.isNumber(rule.getMatchValue()) && !rule.getOperator().equals(PreProcessingConstants.EQUALS))
				throw new RulesValidationException("The value contained in the 'Match Argument' parameter in the pre processing "
						+ "rule n. " + index + " is a text but the operator is not equals.");
			
			if(rule.getEvaluatedCell().equals(PreProcessingConstants.VM_WORKLOAD_TYPE) && !isCustomer)
				throw new RulesValidationException("'Evaluated Cell' refers to the column 'VNF Workload Type' but the VBom is not a VBom customer.");
			
			/*If the evaluated cell is not numeric the operator can only be equals - not yet needed, all the possible fields are numeric*/
			if(PreProcessingConstants.TEXT_FIELDS.contains(rule.getEvaluatedCell()) && !rule.getOperator().equals(PreProcessingConstants.EQUALS))
				throw new RulesValidationException("The value contained in the 'Evaluated Cell' parameter in the pre processing "
						+ "rule n. " + index + " is a text but the operator is not equals.");
			
			//All the field are numeric, so if the action argument is a text, it can't be neither overwritten nor multiplied 
			if(!NumberUtils.isNumber(rule.getActionArgument())) {
				if(rule.getAction().equals(PreProcessingConstants.OVERWRITE))
					throw new RulesValidationException("The rule n. " + index + " would overwrite a text value on a numeric field.");
				else if(rule.getAction().equals(PreProcessingConstants.MULTIPLICATION))
					throw new RulesValidationException("The rule n. " + index + " would multiply a number for a text value.");
			}
		}
		
		index = 0;
		List<String> ruleNames = new ArrayList<String>();
		for(ClusterSelection rule:clusterSelectionRules) {
			index++;
			
			//Checking if there are empty parameters in a rule
			if(rule.getEvaluatedCell() == null || rule.getEvaluatedCell().equals("") || rule.getOperator() == null ||
					rule.getOperator().equals("") || rule.getMatchValue() == null || rule.getMatchValue().equals("") ||
					rule.getWorkloadType() == null || rule.getWorkloadType().equals("") || 
					rule.getDestinationClusterOnMatchSuccess() == null || rule.getDestinationClusterOnMatchSuccess().equals("") ||
					rule.getDestinationClusterOnMatchFailure() == null || rule.getDestinationClusterOnMatchFailure().equals("")) {
				throw new RulesValidationException("One or more parameters are empty in the cluster selection rule n. " + index);
			}
			
			//Check if the evaluated cell contains a valid key
			if(!PreProcessingConstants.VALID_NAMES_EVALUATED_CELL.contains(rule.getEvaluatedCell()))
				throw new RulesValidationException("The key contained in the 'Evaluated Cell' parameter in the cluster selection "
						+ "rule n. " + index + " is not valid.");
			
			//Check if the operator contains a valid value
			if(!PreProcessingConstants.VALID_OPERATORS.contains(rule.getOperator()))
				throw new RulesValidationException("The value contained in the 'Operator' parameter in the cluster selection "
						+ "rule n. " + index + " is not allowed. The allowed operators are 'greater', 'equals' and 'less'.");
			
			//If the match value is not numeric the operator can only be equals
			if(!NumberUtils.isNumber(rule.getMatchValue()) && !rule.getOperator().equals(PreProcessingConstants.EQUALS))
				throw new RulesValidationException("The value contained in the 'Match Argument' parameter in the cluster selection "
						+ "rule n. " + index + " is a text but the operator is not equals.");
			
			/*If the evaluated cell is not numeric the operator can only be equals - not yet needed, all the possible fields are numeric*/
			if(PreProcessingConstants.TEXT_FIELDS.contains(rule.getEvaluatedCell()) && !rule.getOperator().equals(PreProcessingConstants.EQUALS))
				throw new RulesValidationException("The value contained in the 'Evaluated Cell' parameter in the cluster selection "
						+ "rule n. " + index + " is a text but the operator is not equals.");
			
			if(ruleNames.contains(rule.getWorkloadType()))
				throw new RulesValidationException("Found more than one rule under with the valued " + rule.getWorkloadType() + " in the Workload Type cell.");
			
			ruleNames.add(rule.getWorkloadType());
		}
	}
}