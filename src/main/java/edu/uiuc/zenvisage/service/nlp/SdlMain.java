package edu.uiuc.zenvisage.service.nlp;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.google.common.collect.Lists;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.Sdlquery;

public class SdlMain {
	/*Stores all partitions possible*/
	public static List<Tuple[]> allPartitions = new ArrayList<>();
	static int min_size = 2;
	/*static int max_error = 2;*/
	static int topK = 10;  

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
	public static List<Segment> getBestPartition(int min_size , int nb_segments/*double max_error */, String[] pattern , double[][] data){
		List<List<Segment>> result = SdlMain.partition(Segment.smoothing(min_size,nb_segments,data),pattern.length, data);
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
		String X = sdlquery.x;
		String Y = sdlquery.y; 
		String Z = sdlquery.z;
		String tableName = sdlquery.dataset;
		String keywords = sdlquery.sdltext;  
		int nb_segments = Integer.parseInt(sdlquery.sdlsegments);
		
		long tStart1 = System.currentTimeMillis();
		ArrayList<String> zs = Data.getZs(Z, tableName);
		long tEnd1 = System.currentTimeMillis();
		System.out.println("Elapsed time for getting all Zs "+(tEnd1-tStart1));
		long tStart2 = System.currentTimeMillis();
		ArrayList<double[][]> data = Data.fetchAllData(X, Y, Z, tableName);
		long tEnd2 = System.currentTimeMillis();
		System.out.println("Elapsed time to fetch all data "+(tEnd2-tStart2));
		List<SegmentsAndZ> bestOfEachZ = new ArrayList<>();

		/*Get the pattern*/
		long tStart3 = System.currentTimeMillis();
		String [] pattern = Data.toKeywords(keywords);
		long tEnd3 = System.currentTimeMillis();
		System.out.println("Elapsed time toKeywords "+(tEnd3-tStart3));
		
		/*Store every best representation of the visualization along with its z value*/
		long tStart4 = System.currentTimeMillis();
		for(int i = 0 ; i < data.size() ; i++){
			bestOfEachZ.add(new SegmentsAndZ(SdlMain.getBestPartition(min_size, nb_segments, pattern , data.get(i)), zs.get(i), data.get(i)));
		}
		long tEnd4 = System.currentTimeMillis();
		System.out.println("Elapsed time to get best partitions"+(tEnd4-tStart4));
		
		long tStart5 = System.currentTimeMillis();		
		/*Sort list of segments depending on score*/
		Collections.sort(bestOfEachZ, new Comparator<SegmentsAndZ>() {
	        @Override public int compare(SegmentsAndZ s1, SegmentsAndZ s2) {
	            return (SdlMain.getScore(s1.segments, pattern) <  SdlMain.getScore(s2.segments, pattern) ? 1 : -1); 
	        }
		});
		long tEnd5 = System.currentTimeMillis();
		System.out.println("Elapsed time to sort "+(tEnd5-tStart5));
		
		long tStart6 = System.currentTimeMillis();
		/*Returns the top k visualizations*/
		for(int i = 0 ; i < topK  ; i++){
			System.out.println("Place number "+ (i+1) + " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
			System.out.println("City is : "+bestOfEachZ.get(i).z);
			Segment.printListSegments(bestOfEachZ.get(i).segments);
			System.out.println("////////////////");
		}
		long tEnd6 = System.currentTimeMillis();
		System.out.println("Elapsed time to get top K "+(tEnd6-tStart6));
		
		List<SegmentsAndZ> result = bestOfEachZ.subList(0, topK);
		System.out.println("Size of sublist is : "+result.size());
		return convertOutputtoVisualization(result,sdlquery);
	}
	
	static public Result convertOutputtoVisualization(List<SegmentsAndZ> top_k_results , Sdlquery sdlquery){
		Result result = new Result();
		for(int i = 0 ; i < topK ; i++){
			result.outputCharts.add(i,new Chart());
			result.outputCharts.get(i).setxData(Data.doubleToString(Data.getColumn(top_k_results.get(i).data,1)));// change to  actual data
			result.outputCharts.get(i).setyData(Data.doubleToString(Data.getColumn(top_k_results.get(i).data,2)));// change to actual data
			result.outputCharts.get(i).setRank(i+1);
			result.outputCharts.get(i).setxType(sdlquery.x);
			result.outputCharts.get(i).setyType(sdlquery.y);
			result.outputCharts.get(i).setzType(sdlquery.z);
			result.outputCharts.get(i).setConsiderRange(false);
			result.outputCharts.get(i).setDistance(1);
			result.outputCharts.get(i).setNormalizedDistance(1);
			result.outputCharts.get(i).xRange = new float[]{0,120};
			result.outputCharts.get(i).title = (top_k_results.get(i).z);
			result.setMethod("");
			result.setxUnit("");
			result.setyUnit("");	
		}
		return result;
	}

}
