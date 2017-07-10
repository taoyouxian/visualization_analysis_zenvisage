package edu.uiuc.zenvisage.service.nlp;

import java.util.ArrayList;
import java.util.List;

public class ShapeQuery {

	List<ShapeSegment> shapeSegment;
	
	public ShapeQuery(){
		this.shapeSegment = new ArrayList<>();
	}
	
	public ShapeQuery(List<ShapeSegment> shapeQuery){
		this.shapeSegment = new ArrayList<>();
		for(ShapeSegment location : shapeQuery){
			this.shapeSegment.add(new ShapeSegment(location.modifier,location.keyword,location.x_start,location.x_end,location.y_start,location.y_end));
		}
	}
}
