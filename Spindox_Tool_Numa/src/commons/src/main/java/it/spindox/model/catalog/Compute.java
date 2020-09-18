package commons.src.main.java.it.spindox.model.catalog;

/**
 * Created by Ashraf Uz Zaman on 23/03/2017.
 */
public class Compute extends CatalogEntry {

    private double numberOfCores;
    private int numberOfSockets;
    private int ramInGb;
    private int maxThroughputSupported;

    private int maxRunningIopsSupported;

    public int getMaxThroughputSupported() {
        return maxThroughputSupported;
    }

    public void setMaxThroughputSupported(int maxThroughputSupported) {
        this.maxThroughputSupported = maxThroughputSupported;
    }

    public int getMaxRunningIopsSupported() {
        return maxRunningIopsSupported;
    }

    public void setMaxRunningIopsSupported(int maxRunningIopsSupported) {
        this.maxRunningIopsSupported = maxRunningIopsSupported;
    }

    public double getNumberOfCores() {
        return numberOfCores;
    }

    public int getNumberOfSockets() {
        return numberOfSockets;
    }

    public int getRamInGb() {
        return ramInGb;
    }

    public void setNumberOfCores(double numberOfCore) {
        this.numberOfCores = numberOfCore;
    }

    public void setNumberOfSockets(int numberOfSocket) {
        this.numberOfSockets = numberOfSocket;
    }

    public void setRamInGb(int ramInGb) {
        this.ramInGb = ramInGb;
    }
}
