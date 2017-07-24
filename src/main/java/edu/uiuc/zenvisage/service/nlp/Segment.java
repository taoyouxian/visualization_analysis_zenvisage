package edu.uiuc.zenvisage.service.nlp;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import com.google.common.collect.Lists;

public class Segment{
	
	 int start_idx , end_idx;
	 double slope , beta , error;
	 
	/*Constructor of segment*/
	public Segment(int start_idx,int end_idx , double[][] data){
		this.start_idx = start_idx;
		this.end_idx = end_idx;
		SimpleRegression regression = new SimpleRegression();	
		regression.addData(Data.getPart(start_idx, end_idx, data)); 
		this.slope = regression.getSlope();
		this.beta = regression.getIntercept();
		this.error = regression.getSumSquaredErrors();
	}
	
	/*Create Segment from a tuple*/
	public static Segment createSegment(Tuple tuple , double[][] data){
		return new Segment(tuple.start_idx,tuple.end_idx,data);
	}
		
	/*Creates a list of segments from a list of tuples*/
	public static List<Segment> createListSegment(Tuple[] tuples , double[][] data){
		List<Segment> result = new ArrayList<>();
		for(Tuple tuple : tuples){
			result.add(createSegment(tuple, data));
		}
		return result;
	}
	
	/*Deep copy segment*/
	public static Segment clone(Segment s,double[][] data){
		return new Segment(s.start_idx,s.end_idx,data);
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
	public static Segment mergeSegments(Segment seg1 , Segment seg2 , double[][] data){
		return new Segment(seg1.start_idx,seg2.end_idx,data);

	}
	
	/*Create list of segments of length min_size each from data*/
	public static List<Segment> initialize(double[][] data , int min_size){
		List<Segment> segments = new ArrayList<Segment>(); 
		
//		for(int i = 0 ; i < data.length - min_size ; i++){
//				segments.add(new Segment(i,i+min_size-1,data));
//		}
//		if(!(data.length > 2)){
//			segments.add(new Segment(0,1,data));
//			return segments;
//		}
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
		double total_error = segments.get(0).error ;
		
		for(int i = 1 ; i < segments.size() ; i++){
			total_error += segments.get(i).error;	
		}
		return total_error;
	}
	
	/*Merges the 2 segments that costs the least */
	public static List<Segment> bottomUp(List<Segment> segments , /* double max_error , */ double[][] data){
//		for(Segment s : segments){
//			result.add(clone(s,data)); //Goal is to deep copy segments
//		}
		
		double current_error = Double.POSITIVE_INFINITY;//Merge cost at merge_idx
	    int nb_segments = segments.size();
	    int merge_idx = 0 ; //Where to merge
	    List<Segment> result = new ArrayList<>(nb_segments - 1);
	    
	    for(int i = 0; i < nb_segments - 1 ; i++){//Make sure that cost of merging is less than the current error and less than maximal error
	    	//System.out.print("Step number : " +i+ "     ");
	    	//System.out.println(mergeSegments(segments.get(i),segments.get(i+1),data).error);
	    	if( mergeSegments(segments.get(i),segments.get(i+1),data).error < current_error /*&& mergeSegments(segments.get(i),segments.get(i+1),data).error < max_error*/){
	    		//System.out.println("Merging segment " +i+ " and segment " +(i+1));
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
	public static List<Segment> smoothing1(int min_size , int nb_segments , int query_length, double[][] data){
		/*smooth size is data.length - 1 */ 
		List<Segment> smooth = Segment.initialize(data,min_size);	
		
		/*smooth size is max(nb_segments,query_length)*/
		while(smooth.size() != nb_segments && smooth.size() > query_length){;
			smooth = Segment.bottomUp(smooth,data);
		}
		return smooth;
	}
	
	/*Applies bottomUp until a number of segments is reached*/
	public static List<Segment> smoothing2(int min_size , int nb_segments , int query_length, double[][] data){
		List<Segment> smooth = Segment.initialize(data,min_size);	
		
		int max = (query_length > nb_segments) ? query_length : nb_segments;
		
		while(smooth.size() > max){;
			smooth = Segment.bottomUp(smooth,data);
		}
		return smooth;
	}
	
}