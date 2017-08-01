package edu.uiuc.zenvisage.service.nlpe;

import java.util.List;

public class ShapeSegment {

	private boolean hasChildren=false;
	private LogicalOperation operation;
	private List<ShapeSegment> shapeSegments;	
	private Pattern pattern;
	private Integer x_start,x_end;
	private Integer y_start,y_end;
	

	
	public boolean isHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	public LogicalOperation getOperation() {
		return operation;
	}
	public void setOperation(LogicalOperation operation) {
		this.operation = operation;
	}
	public List<ShapeSegment> getShapeSegments() {
		return shapeSegments;
	}
	public void setShapeSegments(List<ShapeSegment> shapeSegments) {
		this.shapeSegments = shapeSegments;
	}
	public Pattern getPattern() {
		return pattern;
	}
	public void setPattern(Pattern pattern) {
		this.pattern = pattern;
	}
	public void setX_start(Integer x_start) {
		this.x_start = x_start;
	}
	public void setX_end(Integer x_end) {
		this.x_end = x_end;
	}
	public void setY_start(Integer y_start) {
		this.y_start = y_start;
	}
	public void setY_end(Integer y_end) {
		this.y_end = y_end;
	}
	public Integer getX_start() {
		return x_start;
	}
	public Integer getX_end() {
		return x_end;
	}
	public Integer getY_start() {
		return y_start;
	}
	public Integer getY_end() {
		return y_end;
	}
}