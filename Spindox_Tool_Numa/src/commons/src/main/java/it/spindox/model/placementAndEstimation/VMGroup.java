package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.ArrayList;
import java.util.List;

public class VMGroup {
    private List<VirtualMachine> vmList;

    public VMGroup(List<VirtualMachine> vmList) {
        this.vmList = vmList;
    }

    public VMGroup() {
        vmList = new ArrayList<>();
    }


    public Double getRam() {
        return vmList.stream().mapToDouble(VirtualMachine::getRam).sum();
    }


    public Double getCore() {
        return vmList.stream().mapToDouble(VirtualMachine::getCore).sum() + vmList.stream().mapToDouble(VirtualMachine::getHighThroughputCoreQuantity).sum();
    }

    public Integer getHardDisk() {
        return vmList.stream().mapToInt(VirtualMachine::getHardDisk).sum();
    }

    public Integer getGroupThroughput(){
        return vmList.stream().mapToInt(VirtualMachine::getNSEWThroughput).sum();
    }

    public Integer getGroupIopsRunning(){
        return vmList.stream().mapToInt(VirtualMachine::getRunningIOPS).sum();
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group[" + "(ram: " + getRam() 
        		+ " core: " + getCore() 
        		+ " throughput: " + getGroupThroughput()
        		+ " iops running: " + getGroupIopsRunning()
        		+ " hd: " + getHardDisk() + ")");

        vmList.forEach(
                vm -> {
                    sb.append(vm.getCompleteName());
                    if ((vmList.indexOf(vm) < vmList.size() - 1) && vmList.size() > 1) {
                        sb.append(", ");
                    }
                }
        );

        sb.append("]");
        return sb.toString();
    }


    public String getVmListAsString() {
        StringBuffer r = new StringBuffer();
        boolean first = true;

        for (VirtualMachine vm : vmList) {
            if (!first)
                r.append("|");

            r.append(vm.getCompleteName());
            first = false;
        }

        return r.toString();
    }

    public List<VirtualMachine> getVmList() {
        return vmList;
    }

    public String getVmName() {
        for (VirtualMachine virtualMachine : vmList) {
            return virtualMachine.getVmName();
        }
        return "";
    }


    public void setVmList(List<VirtualMachine> vmList) {
        this.vmList = vmList;
    }


    @Override
    public String toString() {
        return "VMGroup{" +
                "vmList=" + vmList +
                '}';
    }
}
