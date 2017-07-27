package edu.uiuc.zenvisage.service.nlpparser;

import java.util.ArrayList;
import java.util.List;

public class SegmentsToZMapping {
	List<Segment> segments;
	String z;
	double[][] data;
	double score;
	Partition[] partitions;

	public SegmentsToZMapping(List<Segment> segments , Partition[] partitions , String z ,  double[][] data , double score){
		this.data = data.clone();
		this.segments = new ArrayList<Segment>(segments);
		//TODO : remove tuples
		this.partitions = partitions.clone();
//		for(Segment s : segments) {
//		    this.segments.add(Segment.clone(s, data));
//		}
		this.z = z; 
		this.score= score;
	}
	
	public SegmentsToZMapping(List<Segment> segments , String z ,  double[][] data , double score){
		this.data = data.clone();
		this.segments = new ArrayList<Segment>(segments);
		//TODO : remove tuples
//		for(Segment s : segments) {
//		    this.segments.add(Segment.clone(s, data));
//		}
		this.z = z; 
		this.score= score;
	}
	
	public static void print(SegmentsToZMapping s){
		System.out.println("z is : "+s.z);
		System.out.println("score is  : "+s.z);
		Segment.printListSegments((s.segments));
		System.out.println("////////////////");
	}
	
}
	