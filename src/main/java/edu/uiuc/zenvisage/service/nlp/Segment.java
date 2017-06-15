package edu.uiuc.zenvisage.service.nlp;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.stat.regression.SimpleRegression;

public class Segment{
	
	 int start_idx , end_idx;
	 double slope , beta , error;
	 
	/*Constructor of segment*/
	public  Segment(int start_idx,int end_idx , double[][] data){
		
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
	
	/*Create List of segments from array of tuples*/
	public static List<Segment> createListSegment(Tuple[] tuples , double[][] data){
		List<Segment> result = new ArrayList<>();
		int i = 0;
		
		for(Tuple tuple : tuples){
			result.add(i,createSegment(tuple, data));
			i++;
		}
		
		return result;
	}
	
	/*Deep copy segment*/
	public static Segment clone(Segment s,double[][] data){
		return new Segment(s.start_idx,s.end_idx,data);
	}
	
	/*Print value of a segment*/
	public static void printSegment(Segment segment){
		System.out.println("The start index is : " +segment.start_idx);
		System.out.println("The end index is : " +segment.end_idx);
		System.out.println("The slope of the segment is : " +segment.slope);
		System.out.println("The intercept of segment is : " +segment.beta);
		System.out.println("The error of segment is : " +segment.error);
		System.out.println("---------------------------------");
	}
	
	/*Print value of list of segments*/
	public static void printListSegments(List<Segment> segments){
		for(Segment s : segments){
			printSegment(s);
		}
	}
	
	/*Merge 2 segments*/
	public static Segment mergeSegments(Segment seg1 , Segment seg2 , double[][] data){
		int result_start_idx = -1 , result_end_idx = -1;//Assign dummy value
		
//		if(seg1.end_idx == (seg2.start_idx-1)){
			result_start_idx = seg1.start_idx;
			result_end_idx = seg2.end_idx;   
//		}
		
		Segment result = new Segment(result_start_idx,result_end_idx,data);
		return result;
	}
	
	/*Create list of segments of length min_size each from data*/
	public static List<Segment> initialize(double[][] data , int min_size){
		List<Segment> segments = new ArrayList<Segment>(); 
		
//		for(int i = 0 ; i < data.length - min_size ; i++){
//				segments.add(new Segment(i,i+min_size-1,data));
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
	
	/*Gives total error of a list of segments*/
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
	    
	    for(int i = 0 ; i < nb_segments ; i++){
	    	if(i == merge_idx){
	    		result.add(mergeSegments(segments.get(i),segments.get(i+1),data));
	    		++i;
	    	}else{
	    		result.add(segments.get(i));
	    	}
	    }
	    
		return result;
	}
	
	/*Applies bottomUp until max_error is reached      TODO: ADD DESIRED_SIZE*/
	public static List<Segment> smoothing(int min_size , double max_error , double[][] data){
		List<Segment> smooth = Segment.initialize(data,min_size);
		
		List<Segment> smooth_test1 = Segment.initialize(data,min_size);
		List<Segment> smooth_test2 = Segment.initialize(data,min_size);
		
		int desired_size = 5;//if desired_size == max number of segments set error to 0 (no smoothing)
		
		while(smooth_test1.size() != desired_size + 1){
			smooth_test1 = Segment.bottomUp(smooth_test1,data);
		}
		
		while(smooth_test2.size() != desired_size){
			smooth_test2 = Segment.bottomUp(smooth_test2,data);
		}
		
		//that's the error to get desired_size
//		System.out.println("TO GET "+desired_size+ " SEGMENTS IN SMOOTH VERSION SET ERROR BIGGER THAN " +Segment.getError(smooth_test1)+" AND SMALLER THAN " +Segment.getError(smooth_test2)+ "\n"); 

		while(Segment.getError(smooth) < max_error && smooth.size() > 1){;
			smooth = Segment.bottomUp(smooth,data);//error here is maximal error permitted per merge / shallow copy of smooth
		}
//		System.out.println("NUMBER OF SEGMENTS OF SMOOTH VERSION IS : "+smooth.size()+"\n");
		return smooth;
	}
	
}