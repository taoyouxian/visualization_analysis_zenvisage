package edu.uiuc.zenvisage.service.nlp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Smoother {
	
	/*Applies bottomUp until max_error is reached*/
	public static List<Segment> Smoothing(int min_size , double max_error , double[][] data){
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
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		Map<double[][],String> map = new HashMap<double[][],String>();
		
		ArrayList<String> zs = Data.getZs("City", "real_estate");
		ArrayList<double[][]> data = Data.fetchAllData("Year", "SoldPrice", "City", "real_estate");
		
		for(int i = 0 ; i < data.size() ; i++){
			map.put(data.get(i),zs.get(i));
		}
		
		String [] pattern = {"down","down","down"};
		
		List<SegmentsZ> bestOfEach = new ArrayList<>();
		
//		Map<List<Segment>,String> bestOfEachZ = new HashMap<List<Segment>,String>();
//		ArrayList<List<Segment>> tmp = new ArrayList<>();
		
		for(int i = 0 ; i < data.size() ; i++){
			bestOfEach.add(new SegmentsZ(SdlMain.getBestPartition(2, 0.00000001, pattern , data.get(i)), zs.get(i), data.get(i)));
//			bestOfEachZ.put(Query.getBestPartition(2, 0.00000001, pattern , d),map.get(d));
//			tmp.add(Query.getBestPartition(2, 0.00000001, pattern , d));
		}
		
		/*Sort list of segments depending on score*/
		Collections.sort(bestOfEach, new Comparator<SegmentsZ>() {
	        @Override public int compare(SegmentsZ s1, SegmentsZ s2) {
	            return (SdlMain.getScore(s1.segments, pattern) >  SdlMain.getScore(s2.segments, pattern) ? 1 : -1); 
	        }
		});
		
//		/*Sort list of segments depending on score*/
//		Collections.sort(tmp, new Comparator<List<Segment>>() {
//	        @Override public int compare(List<Segment> s1, List<Segment> s2) {
//	            return (Query.getScore(s1, pattern) >  Query.getScore(s2, pattern) ? 1 : -1); 
//	        }
//		});
		
		for(int i = bestOfEach.size() - 1 ; i > bestOfEach.size() - 3 - 1  ; i--){
			System.out.println("Place number "+ (bestOfEach.size() - i)+ " with score : "+SdlMain.getScore(bestOfEach.get(i).segments, pattern));
			System.out.println("City is :"+bestOfEach.get(i).z);
			Segment.printListSegments(bestOfEach.get(i).segments);
		}
		
//		for(int i = 0 ; i < 3 ; i++){
//			System.out.println("City is :"+bestOfEachZ.get(tmp.get(i)));
//			Segment.printListSegments(tmp.get(i));
//		}
	}
}
