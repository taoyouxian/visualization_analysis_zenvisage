package edu.uiuc.zenvisage.service.nlpe;

import java.util.List;

public class ShapeSegment {
	String operator;  // NONE,OR, AND, NOT  create enumerations
	List<ShapeSegment> shapeSegment;
	String quantifierType;	// ZEROORMORE, ZERORONE, ATLEAST, ATMOST, BETWEEN
	int minCount;
	int maxCount;
	String modifier;
	String keyword;
	String x_start,x_end;
	String y_start,y_end;
	
	public ShapeSegment(String modifier , String keyword , String x_start , String x_end , String y_start , String y_end){
		this.modifier = modifier;
		this.keyword = keyword;
		this.x_start = x_start;
		this.x_end = x_end;
		this.y_start = y_start;
		this.y_end = y_end;
	}
	
	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getX_start() {
		return x_start;
	}

	public void setX_start(String x_start) {
		this.x_start = x_start;
	}

	public String getX_end() {
		return x_end;
	}

	public void setX_end(String x_end) {
		this.x_end = x_end;
	}

	public String getY_start() {
		return y_start;
	}

	public void setY_start(String y_start) {
		this.y_start = y_start;
	}

	public String getY_end() {
		return y_end;
	}

	public void setY_end(String y_end) {
		this.y_end = y_end;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<ShapeSegment> getShapeSegment() {
		return shapeSegment;
	}

	public void setShapeSegment(List<ShapeSegment> shapeSegment) {
		this.shapeSegment = shapeSegment;
	}

	public String getQuantifierType() {
		return quantifierType;
	}

	public void setQuantifierType(String quantifierType) {
		this.quantifierType = quantifierType;
	}

	public int getMinCount() {
		return minCount;
	}

	public void setMinCount(int minCount) {
		this.minCount = minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

}
