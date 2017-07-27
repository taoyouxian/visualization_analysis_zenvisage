package edu.uiuc.zenvisage.service.nlp1;

import java.util.ArrayList;
import java.util.List;

public class SegmentsAndZ {
	List<Segment> segments;
	String z;
	double[][] data;
	Tuple[] tuples;
	
	public SegmentsAndZ(List<Segment> segments , Tuple[] tuples , String z ,  double[][] data){
		this.data = data.clone();
		this.segments = new ArrayList<Segment>(segments);
		//TODO : remove tuples
		this.tuples = tuples.clone();
//		for(Segment s : segments) {
//		    this.segments.add(Segment.clone(s, data));
//		}
		this.z = z; 
	}
	
	public SegmentsAndZ(List<Segment> segments , String z ,  double[][] data){
		this.data = data.clone();
		this.segments = new ArrayList<Segment>(segments);
		//TODO : remove tuples
//		for(Segment s : segments) {
//		    this.segments.add(Segment.clone(s, data));
//		}
		this.z = z; 
	}
}
	