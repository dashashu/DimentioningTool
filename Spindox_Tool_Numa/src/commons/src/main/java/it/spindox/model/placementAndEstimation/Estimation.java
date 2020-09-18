package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.model.catalog.Catalog;
import commons.src.main.java.it.spindox.model.vbom.Cluster;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Contains the list of estimationLineList for each Site
 */
public class Estimation {

    /**
     * estimationTable indicate for each Site the list of Estimationline
     */
    private Map<String, List<EstimationLine>> estimationTable;


    @Deprecated
    private PlacementTable serviceCluster;
    @Deprecated
    private PlacementTable managementCluster;
    @Deprecated
    private PlacementTable infrastructureManagement;


    private boolean management;
    private boolean foundation;
    private Double defaultAvgReadWrite;
    private Double defaultAvgBlockSize;
    private Double defaultAvgIops;
    private Double defaultFoundationUsedSpace;
    private Catalog catalog;
    private String threeparCharacterizationPath;


    private List<Cluster> clusterList;
    private List<Averages> averages;
    public Estimation() {
	}
    public Estimation(
            boolean management,
            boolean foundation,
            Double defaultAvgReadWrite,
            Double defaultAvgBlockSize,
            Double defaultAvgIops,
            Double defaultFoundationUsedSpace,
            Catalog catalog,
            String threeparCharacterizationPath,
            List<Cluster> clusterList,
            List<Averages> averages) {

        this.management = management;
        this.foundation = foundation;
        this.defaultAvgBlockSize = defaultAvgBlockSize;
        this.defaultAvgIops = defaultAvgIops;
        this.defaultAvgReadWrite = defaultAvgReadWrite;
        this.defaultFoundationUsedSpace = defaultFoundationUsedSpace;
        this.catalog = catalog;
        this.threeparCharacterizationPath = threeparCharacterizationPath;
        this.clusterList = clusterList;
        this.averages = averages;

        this.estimationTable = new LinkedHashMap<>();


        for (Cluster cluster : clusterList.stream().filter(cluster -> !cluster.isFoundation()).collect(Collectors.toList())) {
            addNewSite(cluster.getPlacementTable().getSite());
        }
//        site.addAll(managementCluster.getSite());
//
//        if (infrastructureManagement != null && foundation == true && management == true)
//            site.addAll(infrastructureManagement.getSite());

//        addNewSite(site);
    }

    @Deprecated
    public Estimation(PlacementTable serviceCluster,
                      PlacementTable managementCluster,
                      PlacementTable infrastructureManagement,
                      boolean foundation,
                      boolean management,
                      Catalog catalog,
                      String vbomCharFilePath,
                      List<Averages> averages) {
        this.foundation = foundation;
        this.management = management;
        this.estimationTable = new LinkedHashMap<>();
        this.serviceCluster = serviceCluster;
        this.managementCluster = managementCluster;
        this.threeparCharacterizationPath = vbomCharFilePath;

        if (management && foundation) {
            this.infrastructureManagement = infrastructureManagement;
        } else {
            this.infrastructureManagement = null;
        }
        this.catalog = catalog;
        this.averages = averages;

        estimationTable();
    }


    @Deprecated
    private void estimationTable() {
        Set<String> site = serviceCluster.getSite();
        site.addAll(managementCluster.getSite());

        if (infrastructureManagement != null && foundation == true && management == true)
            site.addAll(infrastructureManagement.getSite());

        addNewSite(site);
    }


    public void addNewSite(String site, ArrayList<EstimationLine> estimationLineList) {
        estimationTable.put(site, estimationLineList);
    }

    public void addNewSite(String site) {
        addNewSite(site, new ArrayList<EstimationLine>());
    }


    public Map<String, List<EstimationLine>> getEstimationTable() {
        return estimationTable;
    }

    public void setEstimationTable(Map<String, List<EstimationLine>> estimationTable) {
        this.estimationTable = estimationTable;
    }

    public void addNewSite(Set<String> site) {
        for (String s : site) {
            addNewSite(s);
        }
    }


    @Deprecated
    public PlacementTable getServiceCluster() {
        return serviceCluster;
    }

    @Deprecated
    public void setServiceCluster(PlacementTable serviceCluster) {
        this.serviceCluster = serviceCluster;
    }

    @Deprecated
    public PlacementTable getManagementCluster() {
        return managementCluster;
    }

    @Deprecated
    public void setManagementCluster(PlacementTable managementCluster) {
        this.managementCluster = managementCluster;
    }

    @Deprecated
    public PlacementTable getInfrastructureManagement() {
        return infrastructureManagement;
    }

    @Deprecated
    public void setInfrastructureManagement(PlacementTable infrastructureManagement) {
        this.infrastructureManagement = infrastructureManagement;
    }

    public boolean isManagement() {
        return management;
    }

    public void setManagement(boolean management) {
        this.management = management;
    }

    public boolean isFoundation() {
        return foundation;
    }

    public void setFoundation(boolean foundation) {
        this.foundation = foundation;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }


    public String getThreeparCharacterizationPath() {
        return threeparCharacterizationPath;
    }

    public void setThreeparCharacterizationPath(String threeparCharacterizationPath) {
        this.threeparCharacterizationPath = threeparCharacterizationPath;
    }

    public List<Averages> getAverages() {
        return averages;
    }

    public void setAverages(List<Averages> averages) {
        this.averages = averages;
    }

    public Double getDefaultAvgReadWrite() {
		return defaultAvgReadWrite;
	}

	public void setDefaultAvgReadWrite(Double defaultAvgReadWrite) {
		this.defaultAvgReadWrite = defaultAvgReadWrite;
	}

	public Double getDefaultAvgBlockSize() {
		return defaultAvgBlockSize;
	}

	public void setDefaultAvgBlockSize(Double defaultAvgBlockSize) {
		this.defaultAvgBlockSize = defaultAvgBlockSize;
	}

	public Double getDefaultAvgIops() {
		return defaultAvgIops;
	}

	public void setDefaultAvgIops(Double defaultAvgIops) {
		this.defaultAvgIops = defaultAvgIops;
	}

	public Double getDefaultFoundationUsedSpace() {
		return defaultFoundationUsedSpace;
	}

	public void setDefaultFoundationUsedSpace(Double defaultFoundationUsedSpace) {
		this.defaultFoundationUsedSpace = defaultFoundationUsedSpace;
	}

	@Override
    public String toString() {
        return "Estimation{" +
                "estimationTable=" + estimationTable +
                ", serviceCluster=" + serviceCluster +
                ", managementCluster=" + managementCluster +
                ", infrastructureManagement=" + infrastructureManagement +
                ", management=" + management +
                ", foundation=" + foundation +
                ", catalog=" + catalog +
                '}';
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    public void setClusterList(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
//


        Set<String> siteList = estimationTable.keySet();

        for (String site : siteList) {


            sb.append("\n***************************************************************");
            sb.append("\n\t ESTIMATION FOR SITE: " + site + " ");
            sb.append("\n***************************************************************");


            StringBuilder sbFoundation = new StringBuilder();
            StringBuilder sbInitiative = new StringBuilder();


            List<EstimationLine> estimationLineList = estimationTable.get(site);
            for (EstimationLine estimationLine : estimationLineList) {
                List<EstimationLineDetail> estimationLineDetailList = estimationLine.getEstimationLineDetail();

                boolean tmpInitiative = false;
                boolean tmpFoundation = false;


                for (EstimationLineDetail estimationLineDetail : estimationLineDetailList) {
                    if (estimationLineDetail.getType().equals(EstimationLineDetail.FOUNDATION)) {

                        if (!tmpFoundation) {
                            sbFoundation.append("\n" + estimationLine.getDescription() + "\n\t");
                            tmpFoundation = true;
                        }

                        sbFoundation.append("||Foundation" + "|Q:" + estimationLineDetail.getQuantity() + "|| ");
                    }
//
                    if (estimationLineDetail.getType().equals(EstimationLineDetail.INITIATIVE)) {

                        if (!tmpInitiative) {
                            sbInitiative.append("\n\n" + estimationLine.getDescription() + "\n\t");
                            tmpInitiative = true;
                        }

                        sbInitiative.append("||Y:" + estimationLineDetail.getLineReference() + "|Q:" + estimationLineDetail.getQuantity() + "|| ");

                    }
                }
//                sbInitiative.append("||");
            }

            sb.append("\n#####Foundation#####\n");
            sb.append(sbFoundation.toString());

            sb.append("\n\n#####Initiative#####");
            sb.append(sbInitiative.toString());

            sb.append("\n\n***************************************************************");
        }


        return sb.toString();
    }
}
