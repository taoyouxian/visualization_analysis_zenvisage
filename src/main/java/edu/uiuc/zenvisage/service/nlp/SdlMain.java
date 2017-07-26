package edu.uiuc.zenvisage.service.nlp;


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
import edu.uiuc.zenvisage.model.Sdlquery;

public class SdlMain {
	/*Stores all partitions possible*/
	public static List<Tuple[]> allPartitions = new ArrayList<>();
	
	/*Size of every segment when creating initial list of segments*/
	static int min_size = 2;
	
	/*Number of best visualizations to return */
	static int topK = 10;  

	static String test_z = "0";
	
	public static List<Integer> getIndexes(List<Segment> segment){
		List<Integer> idx = new ArrayList<>();
		
	    for(int i = 0 ; i < segment.size() ; i++){
	    	idx.add(segment.get(i).start_idx);
	    }
	    idx.add(segment.get(segment.size()-1).end_idx);
	    return idx;
	}
	
	/*Casts boolean to int*/
	public static int booleanToInt(boolean b){
		return (b) ? 1 : 0;
	}
	
	/*Takes a shapeSegment and a segment and computes a score*/
	public static double getSingleScoreL1(Segment segment , ShapeSegment shapeSegment , double xRange , double yRange , double data[][]){
		
		int x_s = booleanToInt(!shapeSegment.x_start.equals("")); //0 if x_start is "" else 1
		int x_e = booleanToInt(!shapeSegment.x_end.equals("")); //0 if x_end is "" else 1
		int y_s = booleanToInt(!shapeSegment.y_start.equals("")); //0 if y_start is "" else 1
		int y_e = booleanToInt(!shapeSegment.y_end.equals("")); //0 if y_end is "" else 1
		
		int nb_elements = x_s + x_e + y_s + y_e; // nb of elements not ""
		
		/*score is [(Xs-xs)+(Xe-xe)]/xRange + [(Ys-ys)+(Ye-ye)]/yRange, include only values which are not null */
		if(nb_elements == 0){
			return 0;
		}
		
		if(xRange == 0 ){
			xRange = 1;
		}
		
		if(yRange == 0 ){
			yRange = 1;
		}

		double x_start = shapeSegment.x_start.equals("") ?  Double.MIN_VALUE : Double.parseDouble(shapeSegment.x_start);
		double x_end = shapeSegment.x_end.equals("") ?  Double.MIN_VALUE   : Double.parseDouble(shapeSegment.x_end);
		double y_start = shapeSegment.y_start.equals("") ?  Double.MIN_VALUE  :Double.parseDouble(shapeSegment.y_start);
		double y_end = shapeSegment.y_end.equals("") ?  Double.MIN_VALUE  : Double.parseDouble(shapeSegment.y_end);
		
		double beta;
		if(nb_elements != 0 && shapeSegment.keyword.equals("")){
			 beta = 1;
		}else if((nb_elements != 0 && !shapeSegment.keyword.equals(""))){
			 beta = 0.5;
		}else{
			beta = 0;
		}
		
		double abs_x_s = Math.abs(segment.start_idx - x_start); 
		double abs_x_e = Math.abs(segment.end_idx - x_end);
		double abs_y_s = Math.abs(data[segment.start_idx][1] - y_start);
		double abs_y_e = Math.abs(data[segment.end_idx][1] - y_end);
		
//		
//		return  beta*
//				(
//				(
//				(y_s * abs_y_s)/yRange
//				+ (y_e * abs_y_e)/yRange 
//				)
//				/nb_elements
//				);
		
		return  beta*(( (x_s * abs_x_s)/xRange
				+ (x_e * abs_x_e)/xRange 
				+ (y_s * abs_y_s)/yRange
				+ (y_e * abs_y_e)/yRange )/nb_elements);
	}
	
	/*Takes shapeSegments and segments and returns score based on these*/
	public static double getScoreL1(List<Segment> segments , ShapeQuery shapeQuery , double data[][]){
		double score = 0 ;
		
		double xRange = data[data.length-1][0]-data[0][0];
		double yRange = Data.findRange(Data.getColumn(data,2));

		int overall_length = 0 ;
		
		int[] segment_length = new int[segments.size()];
		
		for(int i = 0 ; i < segments.size() ; i++){
			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
			overall_length += current_length;
			segment_length[i] = current_length;
		}

		for(int i = 0 ; i < segments.size() ; i++){
			score += getSingleScoreL1(segments.get(i), shapeQuery.shapeSegment.get(i), xRange, yRange, data)*(segment_length[i])/overall_length;
		}
		return score;
	}
	
	/*Takes a pattern and a list of segments and returns a score based on these */
	public static double getScoreKeywords1(List<Segment> segments , String[] pattern , ShapeQuery shapeQuery){
		List<Segment> s1 = new ArrayList<>(segments);

		double score = 0 ;
		
		int overall_length = 0 ;
		
		int[] segment_length = new int[segments.size()];
		
		for(int i = 0 ; i < segments.size() ; i++){
			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
			
			if(!shapeQuery.shapeSegment.get(i).keyword.equals("*")){
				overall_length += current_length;	
			}
			
			segment_length[i] = current_length;
		}
		
		int nb_stars = 0 ;
		for(String keyword : pattern){
			if(keyword.equals("*")){
				nb_stars += 1;
			}
		}
		
		int j = 0;
		for(int i = 0 ; i < s1.size() ; i++){
			double alpha;
			if(shapeQuery.shapeSegment.get(i).keyword.equals("")){
				continue;
			}
						
			int x_s = booleanToInt(!shapeQuery.shapeSegment.get(i).x_start.equals(""));
			int x_e = booleanToInt(!shapeQuery.shapeSegment.get(i).x_end.equals(""));
			int y_s = booleanToInt(!shapeQuery.shapeSegment.get(i).y_start.equals("")); 
			int y_e = booleanToInt(!shapeQuery.shapeSegment.get(i).y_end.equals(""));
			
			int nb_elements = x_s + x_e + y_s + y_e;

			if(nb_elements != 0 && shapeQuery.shapeSegment.get(i).keyword.equals("")){
				 alpha = 0;
			}else if((nb_elements != 0 && !shapeQuery.shapeSegment.get(i).keyword.equals(""))){
				 alpha = 0.5;
			}else{
				alpha = 1;
			}
			
				switch(pattern[j]){
				case "up" : score += alpha*(((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
							break;
				case "flat" : score += alpha*(((1-Math.abs(Math.atan(s1.get(i).slope)/(Math.PI/4)))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
							  break;
				case "down" : score -= alpha*(((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length*(pattern.length - nb_stars)));
							  break;
				case "*"  : break;		  
				}	
				j++;
			}
		return score;
	}
	
	/*Takes pattern,shapeSegments,segments,constant alpha and returns overall score */
	public static double getOverallScore1(List<Segment> segments , ShapeQuery shapeQuery , String[] pattern  , double normalized_data[][] , double raw_data[][]){
		return getScoreKeywords1(segments,pattern,shapeQuery)+(1-getScoreL1(segments,shapeQuery,raw_data));
	}
	
	/*Takes a list of segments and gives all possible partitions */
	public static List<List<Segment>> partition1(List<Segment> segments , int nb_partitions , double[][] data){
		List<List<Segment>> result = new ArrayList<List<Segment>>();
		
		if(nb_partitions == 0){
			SdlMain.divide1(0, nb_partitions, new Tuple[1], segments.size());
		}else{
			if(segments.size() < nb_partitions){
				result.add(segments);
				return result;
			}
			SdlMain.divide1(0, nb_partitions, new Tuple[nb_partitions], segments.size());
		}
	
		for(Tuple[] tuples : allPartitions){
			result.add(Segment.createListSegment(Tuple.toRealIndexes(tuples, segments), data));
		}
		
		allPartitions.clear();
		return result;
	}
	   
	/*Dividing from "start" ending at "end" into all possible "nb_partitions" partitions*/
	public static void divide1(int start , int nb_partitions , Tuple[] tuples , int end){
			if(nb_partitions == 1){
				tuples[0] = new Tuple(start , end); 
				Collections.reverse(Arrays.asList(tuples));
				allPartitions.add(tuples);
			}else{
				for(int i = start + 1 ; i < end - nb_partitions + 2 ; i++){
					tuples[nb_partitions-1] = new Tuple(start,i);
					divide1(i,nb_partitions-1,tuples.clone(),end);
				}
			}
		}
	
	/*Returns the visualization with the highest score*/
	public static List<Segment> getBestPartition1(int min_size , int nb_segments , String[] pattern , ShapeQuery shapeQuery , double[][] normalized_data , double[][] raw_data){
		/*Smooth our segments then give all partitions possible*/
		/*OLD APPROACH 1 */
//		List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);
		
		
		/*HARD CONSTRAINT ON X-VALUES*/
		List<List<Segment>> result = new ArrayList<>();
		for(ArrayList<Tuple> tuples : SdlMain.allTuples(SdlMain.xConstraints(shapeQuery),raw_data)){
			Tuple[] tmp = tuples.toArray(new Tuple[0]);
			result.add(Segment.createListSegment(tmp, normalized_data));
		}
		
		List<Double> scores = new ArrayList<>();
		/*Score and rank*/
		for(List<Segment> segments : result){
			scores.add(SdlMain.getOverallScore1(segments, shapeQuery, pattern , normalized_data,raw_data));
		}

		int max_idx = -1;
		double max_score = Double.NEGATIVE_INFINITY;
		
		for(int i = 0 ; i < scores.size() ; i++){
			if(scores.get(i) > max_score){
				max_idx = i;
				max_score = scores.get(i);
			}
		}
		return result.get(max_idx);
	}
	
	/*Takes a pattern and a list of segments and returns a score based on these */
	public static double getScoreKeywords2(List<Segment> segments , String[] pattern , Tuple[] tuples , ShapeQuery shapeQuery){
		double score = 0;
		
		if(pattern.length == 0){
			return score;
		}
		
//		int nb_stars = 0 ;
//		for(String keyword : pattern){
//			if(keyword.equals("*")){
//				nb_stars += 1;
//			}
//		}
		double[] scores = new double[pattern.length] ;

		int overall_length = 0 ;
		
		int[] segment_length = new int[segments.size()];
		
		for(int i = 0 ; i < segments.size() ; i++){
			int current_length = segments.get(i).end_idx - segments.get(i).start_idx;
			overall_length += current_length;	
			segment_length[i] = current_length;
		}
				
		int j = 0 ;
		
		for(int i = 0 ; i < segments.size() ; i++){
			if(shapeQuery.shapeSegment.get(j).keyword.equals("")){
				continue;
			}
			
			double alpha;
			
			int x_s = booleanToInt(!shapeQuery.shapeSegment.get(j).x_start.equals(""));
			int x_e = booleanToInt(!shapeQuery.shapeSegment.get(j).x_end.equals(""));
			int y_s = booleanToInt(!shapeQuery.shapeSegment.get(j).y_start.equals("")); 
			int y_e = booleanToInt(!shapeQuery.shapeSegment.get(j).y_end.equals(""));
			
			int nb_elements = x_s + x_e + y_s + y_e;

			if(nb_elements != 0 && shapeQuery.shapeSegment.get(j).keyword.equals("")){
				 alpha = 0;
			}else if((nb_elements != 0 && !shapeQuery.shapeSegment.get(j).keyword.equals(""))){
				 alpha = 0.5;
			}else{
				alpha = 1;
			}
			
				switch(pattern[j]){
				case "up" : scores[j] += alpha*(((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length));
							break;
				case "flat" : scores[j] += alpha*(((1-Math.abs(Math.atan(segments.get(i).slope)/(Math.PI/4)))*segment_length[i])/(overall_length));
							break;
				case "down" : scores[j] -= alpha*(((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length[i])/(overall_length));
							break;
				case "*" : break;
				}

			if(segments.get(i).end_idx == tuples[j].end_idx){
				j++;
			}
			
			
		}
		
		for(int k = 0 ; k < scores.length ; k++){
			score += scores[k]/(pattern.length);
		}

		return score;
	}
	
	/*Takes pattern,locations,segments,constant alpha and returns overall score */
	public static double getOverallScore2(List<Segment> segments , ShapeQuery shapequery , String[] pattern , Tuple[] tuples ,double raw_data[][]){	
//		List<Segment> s = Segment.smoothing2(min_size, shapequery.shapeSegment.size(), shapequery.shapeSegment.size() , raw_data);
		return getScoreKeywords2(segments,pattern,tuples, shapequery)+(1-getScoreL1(Segment.createListSegment(tuples, raw_data),shapequery,raw_data));
	}
	
	/*Takes a list of segments and gives all possible partitions */
	public static void partition2(List<Segment> segments , int nb_partitions , double[][] data){
		if(segments.size() < nb_partitions){
			Tuple[] tuples = Tuple.toTuple(SdlMain.getIndexes(segments));
			allPartitions.add(tuples);
		}else{
			SdlMain.divide2(segments,0, nb_partitions, new Tuple[nb_partitions], segments.size());	
		}
	}
	
	/*Dividing from "start" ending at "end" into all possible "nb_partitions" partitions*/
	public static void divide2(List<Segment> segment , int start , int nb_partitions , Tuple[] tuples , int end){
			if(nb_partitions == 1){
				tuples[0] = new Tuple(start , end); 
				Collections.reverse(Arrays.asList(tuples));
				allPartitions.add(Tuple.toRealIndexes(tuples,segment));
			}else{
				for(int i = start + 1 ; i < end - nb_partitions + 2 ; i++){
					tuples[nb_partitions-1] = new Tuple(start,i);
					divide2(segment,i,nb_partitions-1,tuples.clone(),end);
				}
			}
		}

	/*Returns the partition that maximizes the score*/
	public static Tuple[] getBestPartition2(List<Segment> smooth_segments , int min_size , int nb_segments , String[] pattern , ShapeQuery shapeQuery , double[][] normalized_data , double [][] raw_data){
		/*Without constraints*/
		
		/*Give all partitions possible of smooth version of segments*/
//		SdlMain.partition2(smooth_segments,shapeQuery.shapeSegment.size(), normalized_data); 
//		List<Double> scores = new ArrayList<>();
//		
//		/*Score and rank*/
//		for(Tuple[] tuples  : allPartitions){
//			scores.add(SdlMain.getOverallScore2(smooth_segments , shapeQuery, pattern ,tuples , raw_data));
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
//		return allPartitions.get(max_idx);
		
//		************************
		
		/*WITH HARD CONSTRAINT*/
		List<Double> scores = new ArrayList<>();
	
		ArrayList<ArrayList<Tuple>> result = SdlMain.allTuples(SdlMain.xConstraints(shapeQuery),normalized_data);
		for(ArrayList<Tuple> tuples : result){
			Tuple[] tmp = tuples.toArray(new Tuple[0]);
			scores.add(SdlMain.getOverallScore2(smooth_segments , shapeQuery, pattern ,tmp , raw_data));
		}
		
		int max_idx = -1;
		double max_score = Double.NEGATIVE_INFINITY;
		
		for(int i = 0 ; i < scores.size() ; i++){
			if(scores.get(i) > max_score){
				max_idx = i;
				max_score = scores.get(i);
			}
		}

		Tuple[] max_tuple = result.get(max_idx).toArray(new Tuple[0]);
        return max_tuple;	
        
	}
	
	public static Tuple[] xConstraints(ShapeQuery shapeQuery){
		
		Tuple[] constraints = new Tuple[shapeQuery.shapeSegment.size()];
		int i = 0 ;
		
		for(ShapeSegment shapeSegment : shapeQuery.shapeSegment){
			if(shapeSegment.x_start.equals("") && shapeSegment.x_end.equals("")){
				constraints[i] = new Tuple(-1,-1);
			}else if(!shapeSegment.x_start.equals("") && shapeSegment.x_end.equals("")){
				constraints[i] = new Tuple(Integer.parseInt(shapeSegment.x_start),-1);
			}else if(shapeSegment.x_start.equals("") && !shapeSegment.x_end.equals("")){
				constraints[i] = new Tuple(-1,Integer.parseInt(shapeSegment.x_end));
			}else{
				constraints[i] = new Tuple(Integer.parseInt(shapeSegment.x_start),Integer.parseInt(shapeSegment.x_end));
			}
			i++;
		}
		return constraints;
	}	
	
	public static ArrayList<ArrayList<Tuple>> allTuples(Tuple[] tuples , double[][] data){
		
		ArrayList<ArrayList<Tuple>> result = new ArrayList<>();
		int x_max = (int)(data[data.length-1][0]-1);
		
		/*Case where first x value is 0*/
		if(data[0][0] == 0){
			 x_max = (int)(data[data.length-1][0]);
		}
		
		ArrayList<Integer> newFormat = toNewFormat(tuples , x_max);
		boolean firstRun = true;
		int j = 1 ;
		
		result.add(0,new ArrayList<>());

		for(int i = 0 ; i < newFormat.size() ; i++){
			if(i == newFormat.size()-1){
				return result;
			}
			
			if(newFormat.get(i) >= 0 && newFormat.get(i+1) >= 0){
				for(int k = 0 ; k < j ; k++){
					result.get(k).add(new Tuple((int)newFormat.get(i),(int)newFormat.get(i+1)));
				}
				i++;
				firstRun = false;
			}else{//case where newFormat.get(i+1) < 0
				int last_idx = newFormat.get(i);
				int partitions = 0 ;
				while(newFormat.get(i+1) < 0){
					partitions++;
					i++;
				}
				
				partitions = (partitions/2) + 1;
				divide1(last_idx, partitions, new Tuple[partitions], newFormat.get(i+1));
				i+=1;
				
				int old_j = j;
				
				j *= allPartitions.size();
				
				for(int k = old_j ; k < j ; k++){
					result.add(k,new ArrayList<>());
				}
				
				ArrayList<ArrayList<Tuple>> tmp = new ArrayList<>();
				
				if(!firstRun){
					for(int n = 0 ; n < old_j ; n++){
						tmp.add(n,Tuple.clone(result.get(n)));
					}
				}

				int start = 0 ;
				
				for(int l = 0 ; l < tmp.size() ; l++){
					for(int idx = start ; idx < start + allPartitions.size() && idx < j; idx ++){
						result.get(idx).clear();
						for(Tuple t : tmp.get(l)){
							result.get(idx).add(t);	
						}
					}
					start += allPartitions.size();
				}
			
//				boolean reversed = false ;
				
				for(int k = 0 ; k < j ; k+=0){
					for(int l = 0 ; l < allPartitions.size() ; l++){
//						if(!reversed){
//							Collections.reverse(Arrays.asList(allPartitions.get(l)));
//						}
						for(Tuple t : allPartitions.get(l)){
							result.get(k).add(t);	
						}
						k++;
					}
//					reversed = true;
				}
				allPartitions.clear();
				firstRun = false;
			}
		}
		return result;
	}
	
	public static ArrayList<Integer> toNewFormat(Tuple[] tuples , int x_max){
		ArrayList<Integer> result = new ArrayList<>();
		
		int j = 0 ;
		for(int i = 0 ; i < tuples.length ; i++){
			if(tuples[i].start_idx >= 0){
				result.add(j,(tuples[i].start_idx));
				j++;
			}else{
				if(i == 0){
					result.add(j,0); //change to smallest x value
					j++;
				}else{
					result.add(j,tuples[i-1].end_idx);
					j++;
				}
			}
			
		    if(tuples[i].end_idx >= 0){
				result.add(j,(tuples[i].end_idx));
				j++;
			}else{
				if(i == tuples.length-1 ){
					result.add(j,x_max);
					j++;
				}else{
					result.add(j,(tuples[i+1].start_idx));
					j++;
				}	
			}
		}
		return result;
	}

	
	/*Prints top K best visualizations */
	public static Result executeSdlQuery(Sdlquery sdlQuery) throws ClassNotFoundException, SQLException, JsonParseException, JsonMappingException, IOException{
		/*Getting all values needed for query*/
		String X = sdlQuery.x;
		String Y = sdlQuery.y; 
		String Z = sdlQuery.z;
		String tableName = sdlQuery.dataset;
		String keywords = sdlQuery.sdltext;  
		String approach = sdlQuery.approach;
		int nb_segments = Integer.parseInt(sdlQuery.sdlsegments);
		
		long tStart1 = System.currentTimeMillis();
		
		/*z values*/
		ArrayList<String> zs = Data.allZs;

		long tEnd1 = System.currentTimeMillis();
		System.out.println("Elapsed time for getting all Zs "+(tEnd1-tStart1));
	
		long tStart2 = System.currentTimeMillis();
		
		ArrayList<double[][]> data = Data.fetchAllData(X, Y, Z, tableName);
		
		/*Clone data (the original will be normalized)*/
		ArrayList<double[][]> data1 = new ArrayList<>();
		for(double[][] d : data){
		    double[][] result = new double[d.length][];
		    for (int r = 0; r < d.length; r++) {
		        result[r] = d[r].clone();
		    }
			data1.add(result);
		}
		
		/*Z-Normalize data(Y values)*/
		for(double[][] single_data : data){
			double[] normalized = Data.zNormalize(Data.getColumn(single_data,2));
			for(int i = 0 ; i < single_data.length ; i++){
				 single_data[i][1] = normalized[i]; 
			}
		}
		
		
		long tEnd2 = System.currentTimeMillis();
		System.out.println("Elapsed time to fetch all data "+(tEnd2-tStart2));
		
		/*a list storing the best visualization for every z-value*/
		List<SegmentsAndZ> bestOfEachZ = new ArrayList<>();

		/*Get the pattern*/
		long tStart3 = System.currentTimeMillis();
				
		ShapeQuery shapeQuery = new ShapeQuery();
		
		/*Parsing shapeQuery*/ 
		for(String[] shapeSegment : Data.parser(keywords)){
			if(shapeSegment[1].equals("") && (shapeSegment[4].equals("") || shapeSegment[5].equals(""))){
				throw new IOException("---------------ERROR : INCOMPLETE INFORMATION---------------");
			}
			shapeQuery.shapeSegment.add(new ShapeSegment(shapeSegment[0], shapeSegment[1], shapeSegment[2], shapeSegment[3], shapeSegment[4], shapeSegment[5]));
		}
		
		ArrayList<String> keywrds = new ArrayList<>();
		
		for(ShapeSegment shapeSegment : shapeQuery.shapeSegment){
			if(!shapeSegment.keyword.equals("")){
				keywrds.add(shapeSegment.keyword);
			}
		}
		
		/*Stores the pattern in the query*/
		String[] pattern = new String[keywrds.size()];
		for(int i = 0 ; i < pattern.length ; i++){
			pattern[i] = keywrds.get(i);
		}
		
		long tEnd3 = System.currentTimeMillis();
		System.out.println("Elapsed time toKeywords "+(tEnd3-tStart3));
		
		
		long tStart4 = System.currentTimeMillis();
		
		if(approach.equals("approach1")){
			/*Store best representation with its z value*/
			for(int i = 0 ; i < data.size() ; i++){
				/*Test if we can have at least one segment*/
				if(data.get(i).length > 1){
						bestOfEachZ.add(new SegmentsAndZ(SdlMain.getBestPartition1(min_size, nb_segments, pattern , shapeQuery , data.get(i) , data1.get(i)), zs.get(i), data.get(i)));
					}
				}
				allPartitions.clear();
		}else{
			for(int i = 0 ; i < data.size() ; i++){
				/*Test if we can have at least one segment*/
				if(data.get(i).length > 1){
					List<Segment> smooth_segments = Segment.initialize(data.get(i),min_size);	
					bestOfEachZ.add(new SegmentsAndZ(smooth_segments,SdlMain.getBestPartition2(smooth_segments , min_size, nb_segments, pattern , shapeQuery  , data.get(i) , data1.get(i)), zs.get(i), data.get(i)));
					}
				}
				allPartitions.clear();
		}
		
		long tEnd4 = System.currentTimeMillis();
		System.out.println("Elapsed time to get best partitions "+(tEnd4-tStart4));
		
		long tStart5 = System.currentTimeMillis();
		
		if(approach.equals("approach1")){
			/*Sort list of segments depending on score*/
			Collections.sort(bestOfEachZ, new Comparator<SegmentsAndZ>() {
		        @Override public int compare(SegmentsAndZ s1, SegmentsAndZ s2) {
		        	if(SdlMain.getOverallScore1(s1.segments, shapeQuery, pattern,s1.data , data1.get(zs.indexOf(s1.z))) <  SdlMain.getOverallScore1(s2.segments, shapeQuery, pattern,s2.data,data1.get(zs.indexOf(s2.z)))){
		        		return 1;
		        	}else if(SdlMain.getOverallScore1(s1.segments, shapeQuery, pattern,s1.data,data1.get(zs.indexOf(s1.z))) >  SdlMain.getOverallScore1(s2.segments, shapeQuery, pattern,s2.data,data1.get(zs.indexOf(s2.z)))){
		        		return -1;
		        	}else{
		        		return 0;
		        	}
		        }
			});
		}else{
			/*Sort list of segments depending on score*/
			Collections.sort(bestOfEachZ, new Comparator<SegmentsAndZ>() {
		        @Override public int compare(SegmentsAndZ s1, SegmentsAndZ s2) {
		        	if(SdlMain.getOverallScore2(s1.segments, shapeQuery, pattern,s1.tuples, data1.get(zs.indexOf(s1.z))) <  SdlMain.getOverallScore2(s2.segments, shapeQuery, pattern,s2.tuples, data1.get(zs.indexOf(s2.z)))){
		        		return 1;
		        	}else if(SdlMain.getOverallScore2(s1.segments, shapeQuery, pattern,s1.tuples, data1.get(zs.indexOf(s1.z))) >  SdlMain.getOverallScore2(s2.segments, shapeQuery, pattern,s2.tuples, data1.get(zs.indexOf(s2.z)))){
		        		return -1;
		        	}else{
		        		return 0;
		        	}
		        }
			});
		}
		
		long tEnd5 = System.currentTimeMillis();
		System.out.println("Elapsed time to sort "+(tEnd5-tStart5));
		
		long tStart6 = System.currentTimeMillis();
		
		/*Check the details about a specific z(test_z)*/
//		 for(int i = 0 ; i < bestOfEachZ.size(); i++){
//			 if(bestOfEachZ.get(i).z.equals(test_z)){
//				 System.out.println("z : "+bestOfEachZ.get(i).z);
//				 System.out.println("it's : "+i);
//				 System.out.println("z is : "+bestOfEachZ.get(i).z);
//					System.out.println("score is  : "+SdlMain.getOverallScore1(bestOfEachZ.get(i).segments, shapeQuery, pattern,bestOfEachZ.get(i).data,data1.get(zs.indexOf(bestOfEachZ.get(i).z))));
//					Segment.printListSegments(bestOfEachZ.get(i).segments);
//					System.out.println("////////////////");
//			 }
//		 }

		/*Returns the top k visualizations*/
		int top_K = Math.min(topK,bestOfEachZ.size());
		if(approach.equals("approach1")){
			for(int i = 0 ; i < top_K  ; i++){
				System.out.println("z is : "+bestOfEachZ.get(i).z);
				System.out.println("score is  : "+SdlMain.getOverallScore1(bestOfEachZ.get(i).segments, shapeQuery, pattern,bestOfEachZ.get(i).data,data1.get(zs.indexOf(bestOfEachZ.get(i).z))));
				Segment.printListSegments((bestOfEachZ.get(i).segments));
				System.out.println("////////////////");
			}
			
			for(int i = 0 ; i < top_K  ; i++){
				System.out.println("z is : "+bestOfEachZ.get(i).z);
			}
		}else{
			for(int i = 0 ; i < top_K  ; i++){
				System.out.println("z is : "+bestOfEachZ.get(i).z);
				System.out.println("overall score is  : "+SdlMain.getOverallScore2(bestOfEachZ.get(i).segments, shapeQuery, pattern,bestOfEachZ.get(i).tuples,data1.get(zs.indexOf(bestOfEachZ.get(i).z))));
				System.out.println("score K : "+SdlMain.getScoreKeywords2(bestOfEachZ.get(i).segments, pattern, bestOfEachZ.get(i).tuples, shapeQuery));
				System.out.println("score L : "+SdlMain.getScoreL1(Segment.createListSegment(bestOfEachZ.get(i).tuples, data1.get(zs.indexOf(bestOfEachZ.get(i).z))),shapeQuery,data1.get(zs.indexOf(bestOfEachZ.get(i).z))));
				Segment.printListSegments(bestOfEachZ.get(i).segments);
				System.out.println("////////////////");
			}
			

			for(int i = 0 ; i < top_K  ; i++){
				System.out.println("z is : "+bestOfEachZ.get(i).z);
			}
			
		}

		
		long tEnd6 = System.currentTimeMillis();
		System.out.println("Elapsed time to get top K "+(tEnd6-tStart6));
		
		long tStart7 = System.currentTimeMillis();
		
		List<SegmentsAndZ> result = bestOfEachZ.subList(0,top_K);
		
		long tEnd7 = System.currentTimeMillis();
		System.out.println("Elapsed time to get sublist "+(tEnd7-tStart7));

		/*Send normalized data to front-end
		return convertOutputtoVisualization(result,data,zs,sdlquery,top_K);
		*/
		
		/*Send raw data to front-end*/
		return convertOutputtoVisualization(result,data1,zs,sdlQuery,top_K);
	}
	
	/*Converts the top K List<Segment> to Result format*/
	 public static Result convertOutputtoVisualization(List<SegmentsAndZ> top_k_results , ArrayList<double[][]> data ,ArrayList<String> zs , Sdlquery sdlQuery , int topK) throws SQLException, ClassNotFoundException{
		long tStart7 = System.currentTimeMillis();
		Result result = new Result();
		
		for(int i = 0 ; i < topK ; i++){
			result.outputCharts.add(i,new Chart());

			result.outputCharts.get(i).setxData(Data.doubleToString(Data.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),1)));
			result.outputCharts.get(i).setyData(Data.doubleToString(Data.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),2)));
			
			result.outputCharts.get(i).setRank(i+1);
			result.outputCharts.get(i).setxType(sdlQuery.x);
			result.outputCharts.get(i).setyType(sdlQuery.y);
			result.outputCharts.get(i).setzType(sdlQuery.z);
			result.outputCharts.get(i).setConsiderRange(false);
			result.outputCharts.get(i).setDistance(1);
			result.outputCharts.get(i).setNormalizedDistance(1);
			result.outputCharts.get(i).xRange = new float[]{0,120};
			result.outputCharts.get(i).title = (top_k_results.get(i).z);
			result.setMethod("");
			result.setxUnit("");
			result.setyUnit("");	
		}
		
		long tEnd7 = System.currentTimeMillis();
		System.out.println("Elapsed time to convert output "+(tEnd7-tStart7));
		return result;
	}

}
