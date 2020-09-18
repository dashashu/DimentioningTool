package commons.src.main.java.it.spindox.model.placementAndEstimation;

import commons.src.main.java.it.spindox.vfexception.excelio.UnexpectedSituationOccurredException;

/**
 * Created by fabrizio.sanfilippo on 12/04/2017.
 */
public class EstimationLineDetail {

    public static final String FOUNDATION = "foundation";
    public static final String INITIATIVE = "initiative";

    Cost cost;

    int quantity; // number of blocks (not disks)

    //Used to order the line following an external parameter.
    int priority;

    //FOUNDATION, INITIATIVE
    String type;

    //lineReference =  type == FOUNDATION ? "Foundation" : Name of Year
    String lineReference;

    public EstimationLineDetail(Cost cost, int quantity, int priority, String type, String lineReference) throws UnexpectedSituationOccurredException {
        this.cost = cost;
        this.quantity = quantity;
        this.priority = priority;

        if (type.equals(FOUNDATION) || type.equals(INITIATIVE)) {
            this.type = type;
            this.lineReference = lineReference;
        } else {
            throw new UnexpectedSituationOccurredException("Attribute Type have a not accepted value: " + type);
        }
    }


    public EstimationLineDetail(String type, String lineReference) throws UnexpectedSituationOccurredException {
        this.cost = new Cost();
        this.quantity = 0;
        this.priority = 0;

        if (type.equals(FOUNDATION) || type.equals(INITIATIVE)) {
            this.type = type;
            this.lineReference = lineReference;
        } else {
            throw new UnexpectedSituationOccurredException("Attribute Type have a not accepted value: " + type);
        }
    }

    public EstimationLineDetail(String type, String lineReference, int quantity) throws UnexpectedSituationOccurredException {
        this.cost = new Cost();
        this.quantity = quantity;
        this.priority = 0;

        if (type.equals(FOUNDATION) || type.equals(INITIATIVE)) {
            this.type = type;
            this.lineReference = lineReference;
        } else {
            throw new UnexpectedSituationOccurredException("Attribute Type have a not accepted value: " + type);
        }
    }

    public Cost getCost() {
        return cost;
    }

    public void setCost(Cost cost) {
        this.cost = cost;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLineReference() {
        return lineReference;
    }

    public void setLineReference(String lineReference) {
        this.lineReference = lineReference;
    }


    public static String getFOUNDATION() {
        return FOUNDATION;
    }

    public static String getINITIATIVE() {
        return INITIATIVE;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    @Override
    public String toString() {
        return "EstimationLineDetail{" +
                "cost=" + cost +
                ", quantity=" + quantity +
                ", priority=" + priority +
                ", type='" + type + '\'' +
                ", lineReference='" + lineReference + '\'' +
                '}';
    }
}
