package commons.src.main.java.it.spindox.model.vbom;

public class VBomConstants {
    public static final String VNF_NAME = "VNF Name";
    public static final String VM_TYPE_NAME = "VM Type Name";
    public static final String SELF_CONSTRAINTS = "Self Constraints";
    public static final String EXTERNAL_CONSTRAINTS = "External Constraints";
    public static final String HIGH_THROUGHPUT = "High throughput vSwitch resources";
    public static final String NAME_OF_SITES = "Name of sites";
    public static final String VNF_INSTANCES_NUMBER = "VNF per site";
    public static final String VNF_CLONES_NUMBER = "Number of VMs per type and per VNF instance";
    public static final String VNF_NUMA_FLAG = "NUMA\r\n" + "(Accepted Vlaues: TRUE, FLASE, empty cell)";
    public static final String VNF_SOCKET_FLAG = "Socket\r\n" + "(Wil only consider if NUMA is TRUE)";
    public static final String VM_CPU_NUMBER = "Number of vCPU per VM";
    public static final String VM_RAM = "RAM (GB) per VM";
    public static final String VM_STORAGE_DATA_DISK = "Storage (GB) per VM  - Data Disk";
    public static final String VM_STORAGE_OSDISK = "Storage (GB) per VM - OS Disk";
    public static final String VM_IOPS_RUNNING = "Storage IOPS per VM - Running";
    public static final String VM_IOPS_LOADING = "Storage IOPS per VM - Loading";
    public static final String NORTH_SOUTH_BANDWIDTH = "North/South bandwidth requirement per VM (Mbit/s)";
    public static final String EAST_WEST_BANDWIDTH = "East/West  bandwidth requirement per VM (Mbit/s)";
    public static final String STORAGE_READ_WRITE = "Storage Read/Write VM workload distribution (%/%)";
    public static final String BLOCK_SIZE = "blockSize";

    public static final int[] BLOCK_SIZE_VALUES = {4, 8, 16, 32, 64, 128};
    public static final String VM_WORKLOAD_TYPE = "VM Workload Type"; // vBOM Customer
}
