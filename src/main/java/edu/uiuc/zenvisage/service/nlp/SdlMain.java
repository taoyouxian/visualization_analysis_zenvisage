package edu.uiuc.zenvisage.service.nlp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.Lists;

import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.Sdlquery;

public class SdlMain {
	/*Stores all partitions possible*/
	public static List<Tuple[]> allPartitions = new ArrayList<>();
	static int min_size=2;
	static int max_error=2;
	static int topK=10;  

	/*Takes a pattern and a list of segments and gives a score */
	public static double getScore(List<Segment> s1 , String[] pattern){
		//Assume s1.size()  == pattern.length
		double score = 0 ;
		
		for(int i = 0 ; i < s1.size() ; i++){
			
			switch(pattern[i]){
			
			case "up" : score += s1.get(i).slope;
						break;
			case "flat" : score += 1-Math.abs(s1.get(i).slope);
						break;
			case "down" : score -= s1.get(i).slope;
						break;
						
			}
		}
		return score;
	}
	
	/*Takes a list of segments and gives all possible partitions */
	public static List<List<Segment>> partition(List<Segment> s1 , int partitions , double[][] data){
		List<List<Segment>> result = new ArrayList<List<Segment>>();
		SdlMain.divide(0, partitions, new Tuple[partitions], s1.size());
		int i = 0;
		
		for(Tuple[] tuples : allPartitions){
			result.add(i,Segment.createListSegment(Tuple.toRealIndexes(tuples, s1), data));
			i++;
		}
		return result;
	}
	
	/*Creates all partitions possible of a set*/
	public static void divide(int start , int partition , Tuple[] tab , int size){
		if(partition == 1){
			tab[0] = new Tuple(start , size); 
			allPartitions.add(tab);
		}else{
			for(int i = start + 1 ; i < size - partition + 2 ; i++){
				tab[partition-1] = new Tuple(start,i);
				divide(i,partition-1,tab.clone(),size);
			}
		}
	}
   
	/*Makes an array of all segments' indexes*/
	public static List<Integer> getIndexes(List<Segment> s1){
		List<Integer> idx = new ArrayList<>();
		idx.add(0);
	    for(int i = 1 ; i < s1.size() ; i++){
	    	idx.add(s1.get(i).start_idx);
	    }
	    idx.add(s1.get(s1.size() - 1).end_idx);
	    return idx;
	}
	
	/*Returns the partition that maximizes the score*/
	public static List<Segment> getBestPartition(int min_size , double max_error , String[] pattern , double[][] data){
		List<List<Segment>> result = SdlMain.partition(Segment.smoothing(min_size,max_error,data),pattern.length, data);
		List<Double> scores = new ArrayList<>();
		
		for(List<Segment> segments : result){
//			System.out.println("//////////////////////////////////////////");
			List<Segment> reversed = Lists.reverse(segments);
//			Segment.printListSegments(reversed);
			scores.add(SdlMain.getScore(reversed,pattern));
//			System.out.println("SCORE OF LIST IS  "+ Query.getScore(reversed,pattern));
//			System.out.println("//////////////////////////////////////////\n");
		}
//		System.out.println("Number of possibities is "+result.size()+"\n");
		
		int max_idx = -1;
		double max_score = Double.NEGATIVE_INFINITY;
		for(int i = 0 ; i < scores.size() ; i++){
			if(scores.get(i) > max_score){
				max_idx = i;
				max_score = scores.get(i);
			}
		}
		
		return Lists.reverse(result.get(max_idx));
//		System.out.println("The partition with the best score : " +max_score+" is : \n");
//		Segment.printListSegments(Lists.reverse(result.get(max_idx)));
	}
	
	/*Prints top K best visualizations */
	public static Result executeSdlQuery(Sdlquery sdlquery) throws ClassNotFoundException, SQLException{
	
		String X=sdlquery.x;
		String Y=sdlquery.y; 
		String Z=sdlquery.z;
		String tableName=sdlquery.dataset;
		String keywords=sdlquery.sdltext;  
		// int segments=
		
		ArrayList<String> zs = Data.getZs(Z, tableName);
		ArrayList<double[][]> data = Data.fetchAllData(X, Y, Z, tableName);
		List<SegmentsAndZ> bestOfEachZ = new ArrayList<>();

		/*Get the pattern*/
		String [] pattern = Data.toKeywords(keywords);
		
		/*Store every best representation of the visualization along with its z value*/
		for(int i = 0 ; i < data.size() ; i++){
			bestOfEachZ.add(new SegmentsAndZ(SdlMain.getBestPartition(min_size, max_error, pattern , data.get(i)), zs.get(i), data.get(i)));
		}
		
		/*Sort list of segments depending on score*/
		Collections.sort(bestOfEachZ, new Comparator<SegmentsAndZ>() {
	        @Override public int compare(SegmentsAndZ s1, SegmentsAndZ s2) {
	            return (SdlMain.getScore(s1.segments, pattern) >  SdlMain.getScore(s2.segments, pattern) ? 1 : -1); 
	        }
		});
		
		/*Returns the top k visualizations*/
		for(int i = bestOfEachZ.size() - 1 ; i > bestOfEachZ.size() - topK - 1  ; i--){
			System.out.println("Place number "+ (bestOfEachZ.size() - i)+ " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
			System.out.println("City is : "+bestOfEachZ.get(i).z);
			Segment.printListSegments(bestOfEachZ.get(i).segments);
			System.out.println("////////////////");
		}
		
		//Fix this
		return convertOutputtoVisualization();
	}
	
	static public Result convertOutputtoVisualization(){
		//Fix this
		return null;
	}

}
