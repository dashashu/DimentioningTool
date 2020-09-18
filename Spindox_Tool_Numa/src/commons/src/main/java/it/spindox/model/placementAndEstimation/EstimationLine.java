package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.model.catalog.CatalogEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabrizio.sanfilippo on 12/04/2017.
 */
public class EstimationLine {

    CatalogEntry component;
    String description;

    List<EstimationLineDetail> estimationLineDetail;

    public EstimationLine(CatalogEntry component, String description) {
        this.component = component;
        this.description = description;

        estimationLineDetail = new ArrayList<>();
    }

    public CatalogEntry getComponent() {
        return component;
    }

    public void setComponent(CatalogEntry component) {
        this.component = component;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EstimationLineDetail> getEstimationLineDetail() {
        return estimationLineDetail;
    }

    public void setEstimationLineDetail(List<EstimationLineDetail> estimationLineDetail) {
        this.estimationLineDetail = estimationLineDetail;
    }

    @Override
    public String toString() {
        return "EstimationLine{" +
                "component=" + component +
                ", description='" + description + '\'' +
                ", estimationLineDetail=" + estimationLineDetail +
                '}';
    }
}
