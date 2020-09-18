package commons.src.main.java.it.spindox.model.configurations;

public class ClusterSelection {
    private String evaluatedCell;
    private String operator;
    private String matchValue;
    private String workloadType;
    private String destinationClusterOnMatchSuccess;
    private String destinationClusterOnMatchFailure;

    public String getEvaluatedCell() {
        return evaluatedCell;
    }

    public void setEvaluatedCell(String evaluatedCell) {
        this.evaluatedCell = evaluatedCell;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getMatchValue() {
        return matchValue;
    }

    public void setMatchValue(String matchValue) {
        this.matchValue = matchValue;
    }

    public String getWorkloadType() {
        return workloadType;
    }

    public void setWorkloadType(String workloadType) {
        this.workloadType = workloadType;
    }

    public String getDestinationClusterOnMatchSuccess() {
        return destinationClusterOnMatchSuccess;
    }

    public void setDestinationClusterOnMatchSuccess(String destinationClusterOnMatchSuccess) {
        this.destinationClusterOnMatchSuccess = destinationClusterOnMatchSuccess;
    }

    public String getDestinationClusterOnMatchFailure() {
        return destinationClusterOnMatchFailure;
    }

    public void setDestinationClusterOnMatchFailure(String destinationClusterOnMatchFailure) {
        this.destinationClusterOnMatchFailure = destinationClusterOnMatchFailure;
    }
}