package commons.src.main.java.it.spindox.model.configurations;

public class VBomPreProcessingRule {
	private String evaluatedCell;
	private String operator;
	private String matchValue;
	private String matchValueType;
	private String action;
	private String actionArgument;
	private String actionArgumentType;
	private String cellToModify;
	
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
	public String getMatchValueType() {
		return matchValueType;
	}
	public void setMatchValueType(String matchValueType) {
		this.matchValueType = matchValueType;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getActionArgument() {
		return actionArgument;
	}
	public void setActionArgument(String actionArgument) {
		this.actionArgument = actionArgument;
	}
	public String getActionArgumentType() {
		return actionArgumentType;
	}
	public void setActionArgumentType(String actionArgumentType) {
		this.actionArgumentType = actionArgumentType;
	}
	public String getCellToModify() {
		return cellToModify;
	}
	public void setCellToModify(String cellToModify) {
		this.cellToModify = cellToModify;
	}
	
	
}