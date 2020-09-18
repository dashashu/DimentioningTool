package commons.src.main.java.it.spindox.model.placementAndEstimation;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by fabrizio.sanfilippo on 27/03/2017.
 */
public class PlacementTable {

    List<PlacementResult> placementResultList;

    public PlacementTable() {
        placementResultList = new ArrayList<>();
    }


    public String printSummaryPlacement() {
        StringBuilder sb = new StringBuilder();

     //   placementResultList.sort((o1, o2) -> o1.getSite().compareTo(o2.getSite()));
        placementResultList.sort(Comparator.comparing(PlacementResult::getSite));
        placementResultList.forEach(placementResult -> {
            sb.append("Year: " + placementResult.getYear() + " Site: " + placementResult.getSite() + " Number Of Blades: " + placementResult.getPlacement().size());

            long notActive = placementResult.getPlacement().stream().filter(bl -> bl.isSpare()).count();
            long active = placementResult.getPlacement().size() - notActive;
            sb.append(" (Active Blades:" + active + ", Spare Blades:" + notActive + ")\n");
        });
        return sb.toString();
    }


    public List<PlacementResult> getPlacementResultList() {
        return placementResultList;
    }

    public void setPlacementResultList(List<PlacementResult> placementResultList) {
        this.placementResultList = placementResultList;
    }

    /**
     * Return all the placement for a Singular Year
     *
     * @param site
     * @return
     */
    public Map<String, List<Blade>> getPlacementForSite(String site) {
        List<PlacementResult> placementOfOneSite = placementResultList.stream().filter(pl -> pl.getSite().equalsIgnoreCase(site)).collect(Collectors.toList());
        Map<String, List<Blade>> placementForSiteMap = new LinkedHashMap<>();

        for (PlacementResult placementResult : placementOfOneSite) {
            placementForSiteMap.put(placementResult.getYear(), placementResult.getPlacement());
        }
        return placementForSiteMap;
    }


    /**
     * Return all the placement for a Singular Year
     *
     * @param year
     * @return
     */
    public Map<String, List<Blade>> getPlacementForYear(String year) {

        List<PlacementResult> placementOfOneYear = placementResultList.stream().filter(pl -> pl.getYear().equalsIgnoreCase(year)).collect(Collectors.toList());

        Map<String, List<Blade>> placementForYearMap = new LinkedHashMap<>();

        for (PlacementResult placementResult : placementOfOneYear) {
            placementForYearMap.put(placementResult.getSite(), placementResult.getPlacement());
        }
        return placementForYearMap;
    }

    public Set<String> getYear() {
        Set<String> yearSet = new LinkedHashSet<>();
        placementResultList.forEach(placementResult -> yearSet.add(placementResult.getYear())
        );
        return yearSet;
    }

    public Set<String> getSite() {
        Set<String> siteSet = new LinkedHashSet<>();
        placementResultList.forEach(placementResult -> siteSet.add(placementResult.getSite())
        );
        return siteSet;
    }

    public int getOccupancy(String site, String year) {
        int occupancy = 0;
        Map<String, List<Blade>> tmp = this.getPlacementForSite(site);
        List<Blade> bladeList = tmp.get(year);
        for (Blade tmpBlade : bladeList) {
            occupancy += tmpBlade.getHardDiskUsed();
        }
        return occupancy;
    }

}
