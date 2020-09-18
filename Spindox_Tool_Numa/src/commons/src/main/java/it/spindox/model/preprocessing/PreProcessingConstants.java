package commons.src.main.java.it.spindox.model.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;

public class PreProcessingConstants {
	public static final String INTEGER = "integer";
	public static final String DOUBLE = "double";
	public static final String STRING = "string";
	
	public static final String MULTIPLICATION = "multiplication";
	public static final String OVERWRITE = "overwrite";
	
	public static final String EQUALS = "equals";
	public static final String GREATER = "greater";
	public static final String LESS = "less";
	
	public static final String VNF_NAME = "VNF_NAME";
	public static final String VM_TYPE_NAME = "VM_TYPE_NAME";
	public static final String HIGH_THROUGHPUT = "HIGH_THROUGHPUT";
	public static final String BLOCK_SIZE = "BLOCK_SIZE";
	public static final String VM_WORKLOAD_TYPE = "VM_WORKLOAD_TYPE";
	
	public static final ArrayList<String> VALID_NAMES_CELL_TO_MODIFY = new ArrayList<String>(
		    Arrays.asList("VNF_PER_SITE", "VNF_PER_INSTANCE","NUMA_FLAG","SOCKET_FLAG", "CPU_NUMBER", "RAM", "STORAGE_DATA_DISK", 
					"STORAGE_OS_DISK", "IOPS_RUNNING", "IOPS_LOADING", "NORTH_SOUTH_BANDWIDTH", 
					"EAST_WEST_BANDWIDTH", "STORAGE_SUM", "BANDWIDTH_SUM"));
	
	public static final ArrayList<String> VALID_NAMES_EVALUATED_CELL = new ArrayList<String>(
		    Arrays.asList("VM_WORKLOAD_TYPE", "VNF_PER_SITE", "VNF_PER_INSTANCE","NUMA_FLAG","SOCKET_FLAG", "CPU_NUMBER", "RAM", "STORAGE_DATA_DISK", 
					"STORAGE_OS_DISK", "IOPS_RUNNING", "IOPS_LOADING", "NORTH_SOUTH_BANDWIDTH", 
					"EAST_WEST_BANDWIDTH", "STORAGE_SUM", "BANDWIDTH_SUM"));
	
	public static final ArrayList<String> TEXT_FIELDS = new ArrayList<String>(
		    Arrays.asList("VM_WORKLOAD_TYPE"));
	
	public static final ArrayList<String> VALID_OPERATORS = new ArrayList<String>(
		    Arrays.asList("greater", "less", "equals"));
	
	public static final ArrayList<String> VALID_ACTIONS = new ArrayList<String>(
		    Arrays.asList("multiplication", "overwrite"));
	
	public static final String VNF_PER_SITE = "VNF_PER_SITE";
	public static final String VNF_PER_INSTANCE = "VNF_PER_INSTANCE";
	//NUMA
	public static final String NUMA_FLAG = "NUMA_FLAG";
	public static final String SOCKET_FLAG = "SOCKET_FLAG";
	
	public static final String CPU_NUMBER = "CPU_NUMBER";
	public static final String RAM = "RAM";
	public static final String STORAGE_DATA_DISK = "STORAGE_DATA_DISK";
	public static final String STORAGE_OS_DISK = "STORAGE_OS_DISK";
	public static final String IOPS_RUNNING = "IOPS_RUNNING";
	public static final String IOPS_LOADING = "IOPS_LOADING";
	public static final String NORTH_SOUTH_BANDWIDTH = "NORTH_SOUTH_BANDWIDTH";
	public static final String EAST_WEST_BANDWIDTH = "EAST_WEST_BANDWIDTH";
	public static final String STORAGE_SUM = "STORAGE_SUM";
	public static final String BANDWIDTH_SUM = "BANDWIDTH_SUM";
}
