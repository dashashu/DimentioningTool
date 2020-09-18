package excelio.src.main.java.it.spindox.excelio.models.exceloutput.elements;

import java.util.*;

public class SiteTable {
    private String rowName;
    private String site;
    private Map<String, YearSummary> yearSummaryList = new HashMap<String, YearSummary>();

    public String getRowName() {
        return rowName;
    }

    public void setRowName(String rowName) {
        this.rowName = rowName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Map<String, YearSummary> getYearSummaryList() {
        return yearSummaryList;
    }

    public void setYearSummaryList(Map<String, YearSummary> yearSummaryList) {
        this.yearSummaryList = yearSummaryList;
    }

    //Ashraf
    public List<String> getSortedYearNames() {
        List<String> sortedYearNames = new ArrayList<>(yearSummaryList.keySet());
        Collections.sort(sortedYearNames);
        return sortedYearNames;
    }
}
