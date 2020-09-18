package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.ArrayList;
import java.util.List;

public class Year {
    private int numOfYear;
    private String yearName;

    private List<Site> siteList;

    public Year() {
        siteList = new ArrayList<Site>();
    }


    public String getYearName() {
        return yearName;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public int getNumOfYear() {
        return numOfYear;
    }

    public void setNumOfYear(int numOfYear) {
        this.numOfYear = numOfYear;
    }

    public List<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

}
