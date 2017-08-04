package edu.uiuc.zenvisage.service.nlpe;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.service.nlp.Sdlquery;

public class SdlMain {
//	/*Stores all partitions possible*/
//	public static List<Partition[]> allPartitions = new ArrayList<>();
//	
//	/*Size of every segment when creating initial list of segments*/
//	static int min_size = 2;
//	
//	/*Number of best visualizations to return */
//	static int topK = 10;  
//
//	static String test_z = "0";
//	
	public static List<Integer> getIndexes(List<Segment> segment){
		List<Integer> idx = new ArrayList<>();
		
	    for(int i = 0 ; i < segment.size() ; i++){
	    	idx.add(segment.get(i).start_idx);
	    }
	    idx.add(segment.get(segment.size()-1).end_idx);
	    return idx;
	}
//	
//	/*Casts boolean to int*/
//	public static int booleanToInt(boolean b){
//		return (b) ? 1 : 0;
//	}
//	
//	/*Takes a shapeSegment and a segment and computes a score*/
//	public static double getSingleScoreL1(Segment segment , ShapeSegment shapeSegment , double xRange , double yRange , double data[][]){
//		
//		int x_s = booleanToInt(!shapeSegment.x_start.equals("")); //0 if x_start is "" else 1
//		int x_e = booleanToInt(!shapeSegment.x_end.equals("")); //0 if x_end is "" else 1
//		int y_s = booleanToInt(!shapeSegment.y_start.equals("")); //0 if y_start is "" else 1
//		int y_e = booleanToInt(!shapeSegment.y_end.equals("")); //0 if y_end is "" else 1
//		
//		int nb_elements = x_s + x_e + y_s + y_e; // nb of elements not ""
//		
//		/*score is [(Xs-xs)+(Xe-xe)]/xRange + [(Ys-ys)+(Ye-ye)]/yRange, include only values which are not null */
//		if(nb_elements == 0){
//			return 0;
//		}
//		
//		if(xRange == 0 ){
//			xRange = 1;
//		}
//		
//		if(yRange == 0 ){
//			yRange = 1;
//		}
//
//		double x_start = shapeSegment.x_start.equals("") ?  Double.MIN_VALUE : Double.parseDouble(shapeSegment.x_start);
//		double x_end = shapeSegment.x_end.equals("") ?  Double.MIN_VALUE   : Double.parseDouble(shapeSegment.x_end);
//		double y_start = shapeSegment.y_start.equals("") ?  Double.MIN_VALUE  :Double.parseDouble(shapeSegment.y_start);
//		double y_end = shapeSegment.y_end.equals("") ?  Double.MIN_VALUE  : Double.parseDouble(shapeSegment.y_end);
//		
//		double beta;
//		if(nb_elements != 0 && shapeSegment.keyword.equals("")){
//			 beta = 1;
//		}else if((nb_elements != 0 && !shapeSegment.keyword.equals(""))){
//			 beta = 0.5;
//		}else{
//			beta = 0;
//		}
//		
//		double abs_x_s = Math.abs(segment.start_idx - x_start); 
//		double abs_x_e = Math.abs(segment.end_idx - x_end);
//		double abs_y_s = Math.abs(data[segment.start_idx][1] - y_start);
//		double abs_y_e = Math.abs(data[segment.end_idx][1] - y_end);
//		
////		
////		return  beta*
////				(
////				(
////				(y_s * abs_y_s)/yRange
////				+ (y_e * abs_y_e)/yRange 
////				)
////				/nb_elements
////				);
//		
//		return  beta*(( (x_s * abs_x_s)/xRange
//				+ (x_e * abs_x_e)/xRange 
//				+ (y_s * abs_y_s)/yRange
//				+ (y_e * abs_y_e)/yRange )/nb_elements);
//	}
//	
//	/*Takes shapeSegments and segments and returns score based on these*/
//	public static double getScoreL1(List<Segment> segments , ShapeQuery shapeQuery , double data[][]){
//		double score = 0 ;
//		
//		double xRange = data[data.length-1][0]-data[0][0];
//		double yRange = DataService.findRange(DataService.getColumn(data,2));
//
//		int overall_length = 0 ;
//		
//		int[] segment_length = new int[segments.size()];
//		
//		for(int i = 0 ; i < segments.size() ; i++){
//			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
//			overall_length += current_length;
//			segment_length[i] = current_length;
//		}
//
//		for(int i = 0 ; i < segments.size() ; i++){
//			score += getSingleScoreL1(segments.get(i), shapeQuery.shapeSegment.get(i), xRange, yRange, data)*(segment_length[i])/overall_length;
//		}
//		return score;
//	}
//	
//	/*Takes a pattern and a list of segments and returns a score based on these */
//	public static double getScoreKeywords1(List<Segment> segments , String[] pattern , ShapeQuery shapeQuery){
//		List<Segment> s1 = new ArrayList<>(segments);
//
//		double score = 0 ;
//		
//		int overall_length = 0 ;
//		
//		int[] segment_length = new int[segments.size()];
//		
//		for(int i = 0 ; i < segments.size() ; i++){
//			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
//			
//			if(!shapeQuery.shapeSegment.get(i).keyword.equals("*")){
//				overall_length += current_length;	
//			}
//			
//			segment_length[i] = current_length;
//		}
//		
//		int nb_stars = 0 ;
//		for(String keyword : pattern){
//			if(keyword.equals("*")){
//				nb_stars += 1;
//			}
//		}
//		
//		int j = 0;
//		for(int i = 0 ; i < s1.size() ; i++){
//			double alpha;
//			if(shapeQuery.shapeSegment.get(i).keyword.equals("")){
//				continue;
//			}
//						
//			int x_s = booleanToInt(!shapeQuery.shapeSegment.get(i).x_start.equals(""));
//			int x_e = booleanToInt(!shapeQuery.shapeSegment.get(i).x_end.equals(""));
//			int y_s = booleanToInt(!shapeQuery.shapeSegment.get(i).y_start.equals("")); 
//			int y_e = booleanToInt(!shapeQuery.shapeSegment.get(i).y_end.equals(""));
//			
//			int nb_elements = x_s + x_e + y_s + y_e;
//
//			if(nb_elements != 0 && shapeQuery.shapeSegment.get(i).keyword.equals("")){
//				 alpha = 0;
//			}else if((nb_elements != 0 && !shapeQuery.shapeSegment.get(i).keyword.equals(""))){
//				 alpha = 0.5;
//			}else{
//				alpha = 1;
//			}
//			
//				switch(pattern[j]){
//				case "up" : score += alpha*(((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
//							break;
//				case "flat" : score += alpha*(((1-Math.abs(Math.atan(s1.get(i).slope)/(Math.PI/4)))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
//							  break;
//				case "down" : score -= alpha*(((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
//							  break;
//				case "*"  : break;		  
//				}	
//				j++;
//			}
//		return score;
//	}
//	
//	/*Takes pattern,shapeSegments,segments,constant alpha and returns overall score */
//	public static double getOverallScore1(List<Segment> segments , ShapeQuery shapeQuery , String[] pattern  , double normalized_data[][] , double raw_data[][]){
//		return getScoreKeywords1(segments,pattern,shapeQuery)+(1-getScoreL1(segments,shapeQuery,raw_data));
//	}
//	
//	/*Takes a list of segments and gives all possible partitions */
//	public static List<List<Segment>> partition1(List<Segment> segments , int nb_partitions , double[][] data){
//		List<List<Segment>> result = new ArrayList<List<Segment>>();
//		
//		if(nb_partitions == 0){
//			SdlMain.divide1(0, nb_partitions, new Partition[1], segments.size());
//		}else{
//			if(segments.size() < nb_partitions){
//				result.add(segments);
//				return result;
//			}
//			SdlMain.divide1(0, nb_partitions, new Partition[nb_partitions], segments.size());
//		}
//	
//		for(Partition[] partitions : allPartitions){
//			result.add(Segment.createListSegment(Partition.toRealIndexes(partitions, segments), data));
//		}
//		
//		allPartitions.clear();
//		return result;
//	}
//	   
//	/*Dividing from "start" ending at "end" into all possible "nb_partitions" partitions*/
//	public static void divide1(int start , int nb_partitions , Partition[] partitions , int end){
//			if(nb_partitions == 1){
//				partitions[0] = new Partition(start , end); 
//				Collections.reverse(Arrays.asList(partitions));
//				allPartitions.add(partitions);
//			}else{
//				for(int i = start + 1 ; i < end - nb_partitions + 2 ; i++){
//					partitions[nb_partitions-1] = new Partition(start,i);
//					divide1(i,nb_partitions-1,partitions.clone(),end);
//				}
//			}
//		}
//	
//	/*Returns the visualization with the highest score*/
//	public static SegmentsToZMapping getBestPartition1(int min_size , int nb_segments , String[] pattern , ShapeQuery shapeQuery , String z , double[][] normalized_data , double[][] raw_data){
//		/*Smooth our segments then give all partitions possible*/
//		/*OLD APPROACH 1 */
////		List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);
//		
//		
//		/*HARD CONSTRAINT ON X-VALUES*/
//		
//		List<List<Segment>> result = new ArrayList<>();
//		for(ArrayList<Partition> partitions : SdlMain.allPartitions(SdlMain.xConstraints(shapeQuery),raw_data)){
//			Partition[] tmp = partitions.toArray(new Partition[0]);
//			result.add(Segment.createListSegment(tmp, normalized_data));
//		}
//				
////      *************************************
//		
//		List<Double> scores = new ArrayList<>();
//		/*Score and rank*/
//		for(List<Segment> segments : result){
//			scores.add(SdlMain.getOverallScore1(segments, shapeQuery, pattern , normalized_data,raw_data));
//		}
//
//		int max_idx = -1;
//		double max_score = Double.NEGATIVE_INFINITY;
//		
//		for(int i = 0 ; i < scores.size() ; i++){
//			if(scores.get(i) > max_score){
//				max_idx = i;
//				max_score = scores.get(i);
//			}
//		}
//		return new SegmentsToZMapping(result.get(max_idx), z, normalized_data, scores.get(max_idx));
//	}
//	
//	/*Takes a pattern and a list of segments and returns a score based on these */
//	public static double getScoreKeywords2(List<Segment> segments , String[] pattern , Partition[] partitions , ShapeQuery shapeQuery){
//		double score = 0;
//		
//		if(pattern.length == 0){
//			return score;
//		}
//		
////		int nb_stars = 0 ;
////		for(String keyword : pattern){
////			if(keyword.equals("*")){
////				nb_stars += 1;
////			}
////		}
//		double[] scores = new double[pattern.length] ;
//
//		int overall_length = 0 ;
//		
//		int[] segment_length = new int[segments.size()];
//		
//		for(int i = 0 ; i < segments.size() ; i++){
//			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
//			overall_length += current_length;	
//			segment_length[i] = current_length;
//		}
//				
//		int j = 0 ;
//		
//		for(int i = 0 ; i < segments.size() ; i++){
//			if(shapeQuery.shapeSegment.get(j).keyword.equals("")){
//				continue;
//			}
//			
//			double alpha;
//			
//			int x_s = booleanToInt(!shapeQuery.shapeSegment.get(j).x_start.equals(""));
//			int x_e = booleanToInt(!shapeQuery.shapeSegment.get(j).x_end.equals(""));
//			int y_s = booleanToInt(!shapeQuery.shapeSegment.get(j).y_start.equals("")); 
//			int y_e = booleanToInt(!shapeQuery.shapeSegment.get(j).y_end.equals(""));
//			
//			int nb_elements = x_s + x_e + y_s + y_e;
//
//			if(nb_elements != 0 && shapeQuery.shapeSegment.get(j).keyword.equals("")){
//				 alpha = 0;
//			}else if((nb_elements != 0 && !shapeQuery.shapeSegment.get(j).keyword.equals(""))){
//				 alpha = 0.5;
//			}else{
//				alpha = 1;
//			}
//			
//				switch(pattern[j]){
//				case "up" : scores[j] += alpha*(((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length));
//							break;
//				case "flat" : scores[j] += alpha*(((1-Math.abs(Math.atan(segments.get(i).slope)/(Math.PI/4)))*segment_length[i])/(overall_length));
//							break;
//				case "down" : scores[j] -= alpha*(((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length));
//							break;
//				case "*" : break;
//				}
//
//			if(segments.get(i).end_idx == partitions[j].end_idx){
//				j++;
//			}
//			
//			
//		}
//		
//		for(int k = 0 ; k < scores.length ; k++){
//			score += scores[k]/(pattern.length);
//		}
//
//		return score;
//	}
//	
//	/*Takes pattern,locations,segments,constant alpha and returns overall score */
//	public static double getOverallScore2(List<Segment> segments , ShapeQuery shapequery , String[] pattern , Partition[] partitions ,double raw_data[][]){	
////		List<Segment> s = Segment.smoothing2(min_size, shapequery.shapeSegment.size(), shapequery.shapeSegment.size() , raw_data);
//		return getScoreKeywords2(segments,pattern,partitions, shapequery)+(1-getScoreL1(Segment.createListSegment(partitions, raw_data),shapequery,raw_data));
//	}
//	
//	/*Takes a list of segments and gives all possible partitions */
//	public static void partition2(List<Segment> segments , int nb_partitions , double[][] data){
//		if(segments.size() < nb_partitions){
//			Partition[] partitions = Partition.toPartition(SdlMain.getIndexes(segments));
//			allPartitions.add(partitions);
//		}else{
//			SdlMain.divide2(segments,0, nb_partitions, new Partition[nb_partitions], segments.size());	
//		}
//	}
//	
//	/*Dividing from "start" ending at "end" into all possible "nb_partitions" partitions*/
//	public static void divide2(List<Segment> segment , int start , int nb_partitions , Partition[] partitions , int end){
//			if(nb_partitions == 1){
//				partitions[0] = new Partition(start , end); 
//				Collections.reverse(Arrays.asList(partitions));
//				allPartitions.add(Partition.toRealIndexes(partitions,segment));
//			}else{
//				for(int i = start + 1 ; i < end - nb_partitions + 2 ; i++){
//					partitions[nb_partitions-1] = new Partition(start,i);
//					divide2(segment,i,nb_partitions-1,partitions.clone(),end);
//				}
//			}
//		}
//
//	/*Returns the partition that maximizes the score*/
//	public static SegmentsToZMapping getBestPartition2(List<Segment> smooth_segments , int min_size , int nb_segments , String[] pattern , ShapeQuery shapeQuery , String z , double[][] normalized_data , double [][] raw_data){
//		/*Without constraints*/
//		
//		/*Give all partitions possible of smooth version of segments*/
////		SdlMain.partition2(smooth_segments,shapeQuery.shapeSegment.size(), normalized_data); 
////		List<Double> scores = new ArrayList<>();
////		
////		/*Score and rank*/
////		for(Partition[] partitions  : allPartitions){
////			scores.add(SdlMain.getOverallScore2(smooth_segments , shapeQuery, pattern ,partitions , raw_data));
////		}
////		
////		int max_idx = -1;
////		double max_score = Double.NEGATIVE_INFINITY;
////		
////		for(int i = 0 ; i < scores.size() ; i++){
////			if(scores.get(i) > max_score){
////				max_idx = i;
////				max_score = scores.get(i);
////			}
////		}
////		return new SegmentsMappedWithZ(smooth_segments,allPartitions.get(max_idx),z,normalized_data,scores.get(max_idx));
//		
////		************************
//		
//		/*WITH HARD CONSTRAINT*/
//		List<Double> scores = new ArrayList<>();
//	
//		ArrayList<ArrayList<Partition>> result = SdlMain.allPartitions(SdlMain.xConstraints(shapeQuery),normalized_data);
//		for(ArrayList<Partition> partitions : result){
//			Partition[] tmp = partitions.toArray(new Partition[0]);
//			scores.add(SdlMain.getOverallScore2(smooth_segments , shapeQuery, pattern ,tmp , raw_data));
//		}
//		
//		int max_idx = -1;
//		double max_score = Double.NEGATIVE_INFINITY;
//		
//		for(int i = 0 ; i < scores.size() ; i++){
//			if(scores.get(i) > max_score){
//				max_idx = i;
//				max_score = scores.get(i);
//			}
//		}
//
//		Partition[] max_partition = result.get(max_idx).toArray(new Partition[0]);
//        return new SegmentsToZMapping(smooth_segments, max_partition, z, normalized_data,scores.get(max_idx));	
//        
//	}
//	
//	public static Partition[] xConstraints(ShapeQuery shapeQuery){
//		
//		Partition[] constraints = new Partition[shapeQuery.shapeSegment.size()];
//		int i = 0 ;
//		
//		for(ShapeSegment shapeSegment : shapeQuery.shapeSegment){
//			if(shapeSegment.x_start.equals("") && shapeSegment.x_end.equals("")){
//				constraints[i] = new Partition(-1,-1);
//			}else if(!shapeSegment.x_start.equals("") && shapeSegment.x_end.equals("")){
//				constraints[i] = new Partition(Integer.parseInt(shapeSegment.x_start),-1);
//			}else if(shapeSegment.x_start.equals("") && !shapeSegment.x_end.equals("")){
//				constraints[i] = new Partition(-1,Integer.parseInt(shapeSegment.x_end));
//			}else{
//				constraints[i] = new Partition(Integer.parseInt(shapeSegment.x_start),Integer.parseInt(shapeSegment.x_end));
//			}
//			i++;
//		}
//		return constraints;
//	}	
//	
//	public static ArrayList<ArrayList<Partition>> allPartitions(Partition[] partitions , double[][] data){
//		
//		ArrayList<ArrayList<Partition>> result = new ArrayList<>();
//		int x_max = (int)(data[data.length-1][0]-1);
//		
//		/*Case where first x value is 0*/
//		if(data[0][0] == 0){
//			 x_max = (int)(data[data.length-1][0]);
//		}
//		
//		ArrayList<Integer> newFormat = toNewFormat(partitions , x_max);
//		boolean firstRun = true;
//		int j = 1 ;
//		
//		result.add(0,new ArrayList<>());
//
//		for(int i = 0 ; i < newFormat.size() ; i++){
//			if(i == newFormat.size()-1){
//				return result;
//			}
//			
//			if(newFormat.get(i) >= 0 && newFormat.get(i+1) >= 0){
//				for(int k = 0 ; k < j ; k++){
//					result.get(k).add(new Partition((int)newFormat.get(i),(int)newFormat.get(i+1)));
//				}
//				i++;
//				firstRun = false;
//			}else{//case where newFormat.get(i+1) < 0
//				int last_idx = newFormat.get(i);
//				int nb_partitions = 0 ;
//				while(newFormat.get(i+1) < 0){
//					nb_partitions++;
//					i++;
//				}
//				
//				nb_partitions = (nb_partitions/2) + 1;
//				divide1(last_idx, nb_partitions, new Partition[nb_partitions], newFormat.get(i+1));
//				i+=1;
//				
//				int old_j = j;
//				
//				j *= allPartitions.size();
//				
//				for(int k = old_j ; k < j ; k++){
//					result.add(k,new ArrayList<>());
//				}
//				
//				ArrayList<ArrayList<Partition>> tmp = new ArrayList<>();
//				
//				if(!firstRun){
//					for(int n = 0 ; n < old_j ; n++){
//						tmp.add(n,Partition.clone(result.get(n)));
//					}
//				}
//
//				int start = 0 ;
//				
//				for(int l = 0 ; l < tmp.size() ; l++){
//					for(int idx = start ; idx < start + allPartitions.size() && idx < j; idx ++){
//						result.get(idx).clear();
//						for(Partition t : tmp.get(l)){
//							result.get(idx).add(t);	
//						}
//					}
//					start += allPartitions.size();
//				}
//			
////				boolean reversed = false ;
//				
//				for(int k = 0 ; k < j ; k+=0){
//					for(int l = 0 ; l < allPartitions.size() ; l++){
////						if(!reversed){
////							Collections.reverse(Arrays.asList(allPartitions.get(l)));
////						}
//						for(Partition t : allPartitions.get(l)){
//							result.get(k).add(t);	
//						}
//						k++;
//					}
////					reversed = true;
//				}
//				allPartitions.clear();
//				firstRun = false;
//			}
//		}
//		return result;
//	}
//	
//	public static ArrayList<Integer> toNewFormat(Partition[] partitions , int x_max){
//		ArrayList<Integer> result = new ArrayList<>();
//		
//		int j = 0 ;
//		for(int i = 0 ; i < partitions.length ; i++){
//			if(partitions[i].start_idx >= 0){
//				result.add(j,(partitions[i].start_idx));
//				j++;
//			}else{
//				if(i == 0){
//					result.add(j,0); //change to smallest x value
//					j++;
//				}else{
//					result.add(j,partitions[i-1].end_idx);
//					j++;
//				}
//			}
//			
//		    if(partitions[i].end_idx >= 0){
//				result.add(j,(partitions[i].end_idx));
//				j++;
//			}else{
//				if(i == partitions.length-1 ){
//					result.add(j,x_max);
//					j++;
//				}else{
//					result.add(j,(partitions[i+1].start_idx));
//					j++;
//				}	
//			}
//		}
//		return result;
//	}
//
//	/*Prints top K best visualizations */
//	public static Result executeSdlQuery(Sdlquery sdlQuery) throws ClassNotFoundException, SQLException, JsonParseException, JsonMappingException, IOException{
//		/*Getting all values needed for query*/
//		String X = sdlQuery.x;
//		String Y = sdlQuery.y; 
//		String Z = sdlQuery.z;
//		String tableName = sdlQuery.dataset;
//		String keywords = sdlQuery.sdltext;  
//		String approach = sdlQuery.approach;
//		int nb_segments = Integer.parseInt(sdlQuery.sdlsegments);
//		
//		/*Get all z values*/
//		ArrayList<String> zs = DataService.allZs;
//
//		/*Fetch all data*/
//		ArrayList<double[][]> originalData = DataService.fetchAllData(X, Y, Z, tableName);
//		
//		/*Normalized version of original data(y values)*/
//		ArrayList<double[][]> normalizedData = DataService.normalizeData(DataService.cloneData(originalData));
//
//		/*Parsing shapeQuery*/
//		ShapeQuery shapeQuery = DataService.parser(keywords);
//		
//		/*Getting the pattern from the shape query*/
//		String[] pattern = DataService.getPatternFromShapeQuery(shapeQuery);
//	
//		/*A list storing the best visualization for every z-value*/
//		List<SegmentsToZMapping> scores = new ArrayList<>();
//		
//		/*Store best representation with its z value*/
//		for(int i = 0 ; i < normalizedData.size() ; i++){
//			/*Test if we can have at least one segment*/
//			if(normalizedData.get(i).length > 1){
//				if(approach.equals("approach1")){
//					scores.add(SdlMain.getBestPartition1(min_size, nb_segments, pattern , shapeQuery , zs.get(i), normalizedData.get(i) , originalData.get(i)));
//				}else{
//					List<Segment> smooth_segments = Segment.initialize(normalizedData.get(i),min_size);	
//					scores.add(SdlMain.getBestPartition2(smooth_segments , min_size, nb_segments, pattern , shapeQuery , zs.get(i), normalizedData.get(i) , originalData.get(i)));
//				}
//
//			}
//		}
//		allPartitions.clear();
//		
//		/*Sort list of segments depending on score*/
//		Collections.sort(scores, new Comparator<SegmentsToZMapping>() {
//			@Override public int compare(SegmentsToZMapping s1, SegmentsToZMapping s2) {
//				if(s1.score < s2.score){
//					return 1;
//				}else if(s1.score > s2.score){
//					return -1;
//				}else{
//					return 0;
//				}
//			}
//		});
//		
//		/*Print the top k visualizations*/
//		int top_K = Math.min(topK,scores.size());
//	
//		for(int i = 0 ; i < top_K  ; i++){
//			SegmentsToZMapping.print(scores.get(i));
//		}
//
//		/*Indexes of top k visualizations*/
//		for(int i = 0 ; i < top_K  ; i++){
//			System.out.println("z is : "+scores.get(i).z);
//		}
//		
//		/*Check the details about a specific z(test_z)*/
////		 for(int i = 0 ; i < scores.size(); i++){
////			 if(scores.get(i).z.equals(test_z)){
////				 System.out.println("z : "+scores.get(i).z);
////				 System.out.println("its rank is : "+i);
////				 SegmentsMappedWithZ.print(scores.get(i));
////			 }
////		 }
//		
//		List<SegmentsToZMapping> bestVisualizations = scores.subList(0,top_K);
//		return convertOutputtoVisualization(bestVisualizations,originalData/*normalizedData*/,zs,sdlQuery,top_K);
//	}
//	
//	/*Converts the top K List<Segment> to Result format*/
//	 public static Result convertOutputtoVisualization(List<SegmentsToZMapping> top_k_results , ArrayList<double[][]> data ,ArrayList<String> zs , Sdlquery sdlQuery , int topK) throws SQLException, ClassNotFoundException{
//		long tStart7 = System.currentTimeMillis();
//		Result result = new Result();
//		
//		for(int i = 0 ; i < topK ; i++){
//			result.outputCharts.add(i,new Chart());
//
//			result.outputCharts.get(i).setxData(DataService.doubleToString(DataService.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),1)));
//			result.outputCharts.get(i).setyData(DataService.doubleToString(DataService.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),2)));
//			
//			result.outputCharts.get(i).setRank(i+1);
//			result.outputCharts.get(i).setxType(sdlQuery.x);
//			result.outputCharts.get(i).setyType(sdlQuery.y);
//			result.outputCharts.get(i).setzType(sdlQuery.z);
//			result.outputCharts.get(i).setConsiderRange(false);
//			result.outputCharts.get(i).setDistance(1);
//			result.outputCharts.get(i).setNormalizedDistance(1);
//			result.outputCharts.get(i).xRange = new float[]{0,120};
//			result.outputCharts.get(i).title = (top_k_results.get(i).z);
//			result.setMethod("");
//			result.setxUnit("");
//			result.setyUnit("");	
//		}
//		
//		long tEnd7 = System.currentTimeMillis();
//		System.out.println("Elapsed time to convert output "+(tEnd7-tStart7));
//		return result;
//	}

}
