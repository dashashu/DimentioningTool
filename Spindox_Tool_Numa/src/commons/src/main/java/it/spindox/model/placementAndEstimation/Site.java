package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.ArrayList;
import java.util.List;

public class Site {

    private String siteName;
    private List<VirtualMachine> vmList;


    public Site() {
        vmList = new ArrayList<VirtualMachine>();
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<VirtualMachine> getVmList() {
        return vmList;
    }

    public void setVmList(List<VirtualMachine> vmList) {
        this.vmList = vmList;
    }

    public void addVmList(List<VirtualMachine> vms) {
        this.vmList.addAll(vms);
    }

    @Override
    public String toString() {
        return "Site{" +
                "siteName='" + siteName + '\'' +
                ", vmList=" + vmList +
                '}';
    }
}
