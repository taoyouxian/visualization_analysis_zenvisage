package edu.uiuc.zenvisage.service.nlpparser;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Segment{
	
	 int start_idx , end_idx;
	 double slope , beta , error;
	 
	/*Constructor of segment*/
	public Segment(int start_idx,int end_idx , double[][] data){
		this.start_idx = start_idx;
		this.end_idx = end_idx;
		SimpleRegression regression = new SimpleRegression();	
		regression.addData(DataService.getPart(start_idx, end_idx, data)); 
		this.slope = regression.getSlope();
		this.beta = regression.getIntercept();
		this.error = regression.getSumSquaredErrors();
	}
	
	/*Create Segment from a partition*/
	public static Segment createSegment(Partition partition , double[][] data){
		return new Segment(partition.start_idx,partition.end_idx,data);
	}
		
	/*Creates a list of segments from a list of partitions*/
	public static List<Segment> createListSegment(Partition[] partitions , double[][] data){
		List<Segment> result = new ArrayList<>();
		for(Partition partition : partitions){
			result.add(createSegment(partition, data));
		}
		return result;
	}
	
	/*Deep copy segment*/
	public static Segment clone(Segment old_segment,double[][] data){
		return new Segment(old_segment.start_idx,old_segment.end_idx,data);
	}
	
	/*Print a single segment(values)*/
	public static void printSegment(Segment segment){
		System.out.println("The start index is : " +segment.start_idx);
		System.out.println("The end index is : " +segment.end_idx);
		System.out.println("The slope of the segment is : " +segment.slope);
		System.out.println("The intercept of segment is : " +segment.beta);
		System.out.println("The error of segment is : " +segment.error);
		System.out.println("---------------------------------");
	}

	/*Print a list of segments*/
	public static void printListSegments(List<Segment> segments){
		for(Segment s : segments){
			printSegment(s);
		}
	}
	
	/*Merge 2 segments*/
	public static Segment mergeSegments(Segment segment1 , Segment segment2 , double[][] data){
		return new Segment(segment1.start_idx,segment2.end_idx,data);
	}
	
	/*Create list of segments of length min_size each from data*/
	public static List<Segment> initialize(double[][] data , int min_size){
		List<Segment> segments = new ArrayList<Segment>(); 
	
		for(int i = 0 ; i < data.length  ; i+= min_size-1){
			if(i+min_size >= data.length){
				segments.add(new Segment(i,data.length-1,data));
				break;
			}else{
				segments.add(new Segment(i,i+min_size-1,data));	
			}
		} 	
		return segments;
	}
	
	/*Computes total error of list of segments*/
	public static double getError(List<Segment> segments){
		double total_error = segments.get(0).error;
		
		for(int i = 1 ; i < segments.size() ; i++){
			total_error += segments.get(i).error;	
		}
		return total_error;
	}
	
	/*Merges the 2 segments that costs the least */
	public static List<Segment> bottomUp(List<Segment> segments , double[][] data){
		double current_error = Double.POSITIVE_INFINITY;//Merge cost at merge_idx
	    int nb_segments = segments.size();
	    int merge_idx = 0 ; //Where to merge
	    List<Segment> result = new ArrayList<>(nb_segments - 1);
	    
	    for(int i = 0; i < nb_segments - 1 ; i++){
	    	if(mergeSegments(segments.get(i),segments.get(i+1),data).error < current_error){
	    		current_error =  mergeSegments(segments.get(i),segments.get(i+1),data).error;
	    		merge_idx = i ;
	    	}
	    }
	    
	    if(nb_segments == 1){
	    	result.add(segments.get(0));
	    }else{
		    for(int i = 0 ; i < nb_segments ; i++){
		    	if(i == merge_idx){
		    		result.add(mergeSegments(segments.get(i),segments.get(i+1),data));
		    		++i;
		    	}else{
		    		result.add(segments.get(i));
		    	}
		    }
	    }
		return result;
	}

	/*Applies bottomUp until a number of segments is reached*/
	public static List<Segment> smoothing(int min_size , int nb_segments , int query_length, double[][] data){
		/*smooth size is data.length-1*/ 
		List<Segment> smooth = Segment.initialize(data,min_size);	
		
		int max = (query_length > nb_segments) ? query_length : nb_segments;
		
		/*smooth size is max(nb_segments,query_length)*/
		while(smooth.size() > max){
			smooth = Segment.bottomUp(smooth,data);
		}
		
		return smooth;
	}
}