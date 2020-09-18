package excelio.src.main.java.it.spindox.excelio.models.exceloutput;

import java.util.*;

import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.SiteDetailsTable;
import excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements.SiteTable;

public class SummaryOutput {
    private List<SiteTable> siteTableList = new ArrayList<SiteTable>();
    private List<SiteDetailsTable> siteDetailsTableList = new ArrayList<SiteDetailsTable>();
    private Set<String> allSites = new HashSet<>();
    private Map<String, List<String>> allSitesAndClusters = new HashMap<>();

    public List<SiteTable> getSiteTableList() {
        return siteTableList;
    }

    public void setSiteTableList(List<SiteTable> siteTableList) {
        this.siteTableList = siteTableList;
    }

    public List<SiteDetailsTable> getSiteDetailsTableList() {
        return siteDetailsTableList;
    }

    public void setSiteDetailsTableList(List<SiteDetailsTable> siteDetailsTableList) {
        this.siteDetailsTableList = siteDetailsTableList;
    }


    // Ashraf
    public void countSitesAndClusters() {
        siteDetailsTableList.forEach(e -> {
            allSites.add(e.getSite());
            List<String> tempList = allSitesAndClusters.get(e.getSite());
            if (tempList == null)
                tempList = new ArrayList<>();
            tempList.add(e.getCluster());
            allSitesAndClusters.put(e.getSite(), tempList);
        });
    }

    /**
     * Make sure to call countSitesAndClusters() method EXACTLY ONCE before calling any of the following two getters
     */
    public Set<String> getAllSites() {
        return allSites;
    }

    public Map<String, List<String>> getAllSitesAndClusters() {
        return allSitesAndClusters;
    }

}