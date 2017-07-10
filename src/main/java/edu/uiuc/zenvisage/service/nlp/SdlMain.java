package edu.uiuc.zenvisage.service.nlp;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
	/*Size of every segment when creating initial list of segments*/
	static int min_size = 2;
	/*static int max_error = 2;*/
	/*Number of best visualizations to return */
	static int topK = 10;  
	
	/*Casts boolean to int*/
	public static int booleanToInt(boolean b){
		return (b) ? 1 : 0;
	}
	
	/*Takes a location and a segment and computes a score*/
	public static double getSingleScoreL(Segment segment , ShapeSegment location , double xRange , double yRange , double data[][]){
		int nb_elements = 	booleanToInt(!location.x_start.equals("")) + booleanToInt(!location.x_end.equals("")) + booleanToInt(!location.y_start.equals("")) + booleanToInt(!location.y_end.equals(""));
		
		/*score is [(Xs-xs)+(Xe-xe)]/xRange + [(Ys-ys)+(Ye-ye)]/yRange, include only values which are not null */
		
		return ( (booleanToInt(!location.x_start.equals(""))*(segment.start_idx - Double.parseDouble(location.x_start)))/xRange
				+ (booleanToInt(!location.x_end.equals(""))*(segment.end_idx - Double.parseDouble(location.x_end)))/xRange 
				+ (booleanToInt(!location.y_start.equals(""))*(data[segment.start_idx][1] - Double.parseDouble(location.y_start)))/yRange
				+ (booleanToInt(!location.y_end.equals(""))*(data[segment.end_idx][1] - Double.parseDouble(location.y_end)))/yRange )/nb_elements;
	}
	
	/*Takes locations and segments and returns score based on these*/
	public static double getScoreL(List<Segment> segments , ShapeQuery shapequery , double data[][]){
		double score = 0 ;
		
		double xRange = data[data.length-1][0]-data[0][0];
		double yRange = data[data.length-1][1]-data[0][1];
		
		for(int i = 0 ; i < segments.size() ; i++){
			score += getSingleScoreL(segments.get(i), shapequery.shapeSegment.get(i), xRange, yRange, data);
		}
		
		return score;
	}
	
	/*Takes a pattern and a list of segments and returns a score based on these */
	public static double getScoreK1(List<Segment> segments , String[] pattern){
		List<Segment> s1 = new ArrayList<>(segments);
		
		if(segments.get(0).start_idx != 0){
			s1 = Lists.reverse(segments);
		}
		
		double score = 0 ;
		int overall_length = s1.get(s1.size()-1).end_idx-s1.get(0).start_idx;
		
//		System.out.println("******* IN SCORE *******");
//		Segment.printListSegments(s1);
//		System.out.println("NON REVERSED");
//		Segment.printListSegments(s);
		
		for(int i = 0 ; i < s1.size() ; i++){
			int segment_length = s1.get(i).end_idx-s1.get(i).start_idx;
			//TODO : Leave this check?
			if(i < pattern.length){
				switch(pattern[i]){
				case "up" : score += ((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length)/(overall_length*pattern.length);
							break;
				case "flat" : score += ((1-Math.abs(Math.atan(s1.get(i).slope)/(Math.PI/2)))*segment_length)/(overall_length*pattern.length);
							break;
				case "down" : score -= ((Math.atan(s1.get(i).slope)/(Math.PI/2))*segment_length)/(overall_length*pattern.length);
							break;		
				}
			}
		}
		return score;
	}
	
	/*Takes a pattern and a list of segments and returns a score badsed on these */
	public static double getScoreK2(List<Segment> segments , String[] pattern , Tuple[] tuples){
		double score = 0;
		double[] scores = new double[pattern.length] ;
		
		
		int tuples_size = tuples.length;
		
		int overall_length = segments.get(segments.size()-1).end_idx-segments.get(0).start_idx;
		
		int j = 0 ;
		
		for(int i = 0 ; i < segments.size() ; i++){
			int segment_length = segments.get(i).end_idx-segments.get(i).start_idx;
			
			//TODO : Leave this check?
//			if(i < pattern.length){
				switch(pattern[j]){
				case "up" : scores[j] += ((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length)/(overall_length);
							break;
				case "flat" : scores[j] += ((1-Math.abs(Math.atan(segments.get(i).slope)/(Math.PI/2)))*segment_length)/(overall_length);
							break;
				case "down" : scores[j] -= ((Math.atan(segments.get(i).slope)/(Math.PI/2))*segment_length)/(overall_length);
							break;		
				}
//			}
//			System.out.println(s1.get(i).end_idx+" and " +tuples[j].end_idx);
//			System.out.println("before " +j);
			if(segments.get(i).end_idx == tuples[tuples_size-j-1].end_idx){
				j++;
			}
		}
		
		for(int k = 0 ; k < scores.length ; k++){
			score += scores[k]/pattern.length;
		}
		
		return score;
	}
	/* TODO : ADD GET PATTERN LENGTH AND SEG OVERALL LENGTH*/
	
	/*Takes pattern,locations,segments,constant alpha and returns overall score */
	public static double getOverallScore(List<Segment> segments , ShapeQuery shapequery , String[] pattern , double alpha , double data[][]){
		return alpha*getScoreK1(segments,pattern)+(1-alpha)*getScoreL(segments,shapequery,data);
	}
	
	/*Takes a list of segments and gives all possible partitions */
	public static List<List<Segment>> partition1(List<Segment> segments , int partitions , double[][] data){
		List<List<Segment>> result = new ArrayList<List<Segment>>();
		
		if(segments.size() < partitions){
			result.add(segments);
			return result;
		}
		
		SdlMain.divide1(0, partitions, new Tuple[partitions], segments.size());
		
		for(Tuple[] tuples : allPartitions){
			Collections.reverse(Arrays.asList(tuples));
//			for(Tuple a : tuples){
//				System.out.println("tuple : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("-------------");
			result.add(Segment.createListSegment(Tuple.toRealIndexes(tuples, segments), data));
		}
		
//		for(Tuple[] t : SdlMain.allPartitions){
//			for(Tuple a : t){
//				System.out.println("tuple : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("-------------");
//		}
		
		allPartitions.clear();
		return result;
	}
	
	/*Takes a list of segments and gives all possible partitions */
	public static void partition2(List<Segment> segments , int partitions , double[][] data){
		if(segments.size() < partitions){
			 allPartitions.add(Tuple.toTuple(SdlMain.getIndexes(segments)));
		}
		SdlMain.divide2(segments,0, partitions, new Tuple[partitions], segments.size());
	}
	
	/*Creates all partitions possible of a set*/
	public static void divide1(int start , int partition , Tuple[] tab , int size){
//		if(size < partition){
//			tab[0] = new Tuple(start , size);
//			allPartitions.add(tab);
//		}else{
			if(partition == 1){
				tab[0] = new Tuple(start , size); 
				allPartitions.add(tab);
			}else{
				for(int i = start + 1 ; i < size - partition + 2 ; i++){
					tab[partition-1] = new Tuple(start,i);
					divide1(i,partition-1,tab.clone(),size);
				}
			}
		}
	
	/*Creates all partitions possible of a set*/
	public static void divide2(List<Segment> s1 ,int start , int partition , Tuple[] tab , int size){
//		if(size < partition){
//			tab[0] = new Tuple(start , size);
//			allPartitions.add(tab);
//		}else{
			if(partition == 1){
				tab[0] = new Tuple(start , size); 
				allPartitions.add(Tuple.toRealIndexes(tab,s1));
			}else{
				for(int i = start + 1 ; i < size - partition + 2 ; i++){
					tab[partition-1] = new Tuple(start,i);
					divide2(s1,i,partition-1,tab.clone(),size);
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
	public static List<Segment> getBestPartition1(int min_size , int nb_segments/*double max_error */, String[] pattern , double[][] data){
		/*Smooth our segments then give all partitions possible*/
		List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,pattern.length,data),pattern.length, data);
//		System.out.println("***** RESULT OF PARTITION *****");
//		for(List<Segment>  s : result){
//			System.out.println("********************************************");
//			Segment.printListSegments(s);
//			System.out.println("********************************************");
//		}
		
		List<Double> scores = new ArrayList<>();
		/*Score and rank*/
		for(List<Segment> segments : result){
//			System.out.println("//////////////////////////////////////////");
//			List<Segment> reversed = Lists.reverse(segments);
//			Segment.printListSegments(segments);
			scores.add(SdlMain.getScoreK1(segments,pattern));
//			System.out.println("SCORE OF LIST IS  "+ Query.getScore(reversed,pattern));
//			System.out.println("//////////////////////////////////////////\n");

		}
//		System.out.println("Number of possibities is "+result.size()+"\n");
//		System.out.println("result size  : "+ result.size());
//		Segment.printListSegments(Lists.reverse(result.get(0)));
//		System.out.println(getScore1(result.get(0),pattern));
		
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

	/*Returns the partition that maximizes the score*/
	public static Tuple[] getBestPartition2(int min_size , int nb_segments/*double max_error */, String[] pattern , double[][] data){
		/*Smooth our segments then give all partitions possible*/
		List<Segment> segments = Segment.smoothing(min_size,nb_segments,pattern.length,data);
		SdlMain.partition2(segments,pattern.length, data); 
		List<Double> scores = new ArrayList<>();
//		Segment.printListSegments(segments);
		/*Score and rank*/
		for(Tuple[] tuples  : allPartitions){
//			System.out.println("new tuple : ");
//			System.out.println("//////////////////////////////////////////");
//			for(Tuple a : tuples){
//				System.out.println("Tuple : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("score of this tuple ^ is : " +SdlMain.getScore(segments,pattern,tuples));
			scores.add(SdlMain.getScoreK2(segments,pattern,tuples));
//			System.out.println("SCORE OF LIST IS  "+ SdlMain.getScore(reversed,pattern));
//			System.out.println("//////////////////////////////////////////\n");
		}
//		System.out.println("Number of possibities is "+result.size()+"\n");
		int max_idx = -1;
		double max_score = Double.NEGATIVE_INFINITY;
		
//		for(double score : scores){
//			System.out.println(score);
//		}
		
		for(int i = 0 ; i < scores.size() ; i++){
			if(scores.get(i) > max_score){
				max_idx = i;
				max_score = scores.get(i);
			}
		}
//		System.out.println("max score " +max_score);
//		System.out.println("------------------------------------");
		return allPartitions.get(max_idx);
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
		String approach = sdlquery.approach;
		int nb_segments = Integer.parseInt(sdlquery.sdlsegments);
		
		long tStart1 = System.currentTimeMillis();
		
//		/* Get list of Zs*/  //TODO:Get it from fetchAllData
//		ArrayList<String> zs = Data.getZs(Z, tableName);
//		Collections.sort(zs);
		
		ArrayList<String> zs = Data.allZs;

		long tEnd1 = System.currentTimeMillis();
		System.out.println("Elapsed time for getting all Zs "+(tEnd1-tStart1));
	
		long tStart2 = System.currentTimeMillis();
		
		ArrayList<double[][]> data = Data.fetchAllData(X, Y, Z, tableName);
		
		/*Clone data*/
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
			for(int i = 0 ; i < single_data.length ; i++){
				 single_data[i][1] = Data.zNormalize(Data.getColumn(single_data,2))[i]; 
			}
		}
		
		
		long tEnd2 = System.currentTimeMillis();
		System.out.println("Elapsed time to fetch all data "+(tEnd2-tStart2));
		
		List<SegmentsAndZ> bestOfEachZ = new ArrayList<>();

		/*Get the pattern*/
		long tStart3 = System.currentTimeMillis();
		
//		String [] pattern = Data.toKeywords(keywords);
		
		ShapeQuery shapeQuery = new ShapeQuery();
		
		for(String[] shapeSegment : Data.parser(keywords)){
			if(shapeSegment[1].equals("") && (shapeSegment[4].equals("") || shapeSegment[5].equals(""))){
				System.out.println("---------------ERROR : INCOMPLETE INFORMATION---------------");
				System.exit(1);
			}
			shapeQuery.shapeSegment.add(new ShapeSegment(shapeSegment[0], shapeSegment[1], shapeSegment[2], shapeSegment[3], shapeSegment[4], shapeSegment[5]));
		}
		
		String keywrds = "";
		
		for(ShapeSegment shapeSegment : shapeQuery.shapeSegment){
			keywrds += shapeSegment.keyword+" ";
		}
		
		String[] pattern = Data.toKeywords(keywrds);
		
//		for(String s : pattern){
//			System.out.println(s);
//		}
		
		long tEnd3 = System.currentTimeMillis();
		System.out.println("Elapsed time toKeywords "+(tEnd3-tStart3));
		
		
		long tStart4 = System.currentTimeMillis();
		
		if(approach.equals("approach1")){
			/*Store best representation with its z value*/
			for(int i = 0 ; i < data.size() ; i++){
				/*Test if we can have at least one segment*/
				if(data.get(i).length > 1){
						bestOfEachZ.add(new SegmentsAndZ(SdlMain.getBestPartition1(min_size, nb_segments, pattern , data.get(i)), zs.get(i), data.get(i)));
					}
					//bestOfEachZ.add(new SegmentsAndZ(Segment.initialize(data.get(i), 2), zs.get(i), data.get(i)));
				}
				allPartitions.clear();
		}else{
			for(int i = 0 ; i < data.size() ; i++){
				/*Test if we can have at least one segment*/
				if(data.get(i).length > 1){
					bestOfEachZ.add(new SegmentsAndZ(Segment.smoothing(min_size,nb_segments,pattern.length,data.get(i)),SdlMain.getBestPartition2(min_size, nb_segments, pattern , data.get(i)), zs.get(i), data.get(i)));
					}
					//bestOfEachZ.add(new SegmentsAndZ(Segment.initialize(data.get(i), 2), zs.get(i), data.get(i)));
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
		        	if(SdlMain.getScoreK1(s1.segments, pattern) <  SdlMain.getScoreK1(s2.segments, pattern)){
		        		return 1;
		        	}else if(SdlMain.getScoreK1(s1.segments, pattern) >  SdlMain.getScoreK1(s2.segments, pattern)){
		        		return -1;
		        	}else{
		        		return 0;
		        	}
	//	            return (SdlMain.getScore(s1.segments, pattern) <  SdlMain.getScore(s2.segments, pattern) ? 1 : -1); 
		        }
			});
		}else{
			/*Sort list of segments depending on score*/
			Collections.sort(bestOfEachZ, new Comparator<SegmentsAndZ>() {
		        @Override public int compare(SegmentsAndZ s1, SegmentsAndZ s2) {
		        	if(SdlMain.getScoreK2(s1.segments, pattern , s1.tuples) <  SdlMain.getScoreK2(s2.segments, pattern,s2.tuples)){
		        		return 1;
		        	}else if(SdlMain.getScoreK2(s1.segments, pattern , s1.tuples) >  SdlMain.getScoreK2(s2.segments, pattern,s2.tuples)){
		        		return -1;
		        	}else{
		        		return 0;
		        	}
	//	            return (SdlMain.getScore(s1.segments, pattern) <  SdlMain.getScore(s2.segments, pattern) ? 1 : -1); 
		        }
			});
		}
		long tEnd5 = System.currentTimeMillis();
		System.out.println("Elapsed time to sort "+(tEnd5-tStart5));
		
		long tStart6 = System.currentTimeMillis();
		
		/*
		 for(int i = 0 ; i < bestOfEachZ.size(); i++){
			 if(bestOfEachZ.get(i).z.equals("22")){
				 System.out.println("it's : "+i);
			 }
		 }
		 */
//		System.out.println("z is : "+bestOfEachZ.get(31).z);
//		System.out.println("score is  : "+SdlMain.getScore1(bestOfEachZ.get(31).segments, pattern));
//		Segment.printListSegments(bestOfEachZ.get(31).segments);
//		System.out.println("////////////////");
		
		/*Returns the top k visualizations*/
		int top_K = Math.min(topK,bestOfEachZ.size());
		if(approach.equals("approach1")){
			for(int i = 0 ; i < top_K  ; i++){
				//System.out.println("Place number "+ (i+1) + " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
				System.out.println("z is : "+bestOfEachZ.get(i).z);
				System.out.println("score is  : "+SdlMain.getScoreK1(bestOfEachZ.get(i).segments, pattern));
				Segment.printListSegments(Lists.reverse(bestOfEachZ.get(i).segments));
				System.out.println("////////////////");
			}
			
			for(int i = 0 ; i < top_K  ; i++){
				//System.out.println("Place number "+ (i+1) + " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
				System.out.println("z is : "+bestOfEachZ.get(i).z);
			}
		}else{
			for(int i = 0 ; i < top_K  ; i++){
				//System.out.println("Place number "+ (i+1) + " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
				System.out.println("z is : "+bestOfEachZ.get(i).z);
				System.out.println("score is  : "+SdlMain.getScoreK2(bestOfEachZ.get(i).segments, pattern,bestOfEachZ.get(i).tuples));
				Segment.printListSegments(bestOfEachZ.get(i).segments);
				System.out.println("////////////////");
			}
			for(int i = 0 ; i < top_K  ; i++){
				//System.out.println("Place number "+ (i+1) + " with score : "+SdlMain.getScore(bestOfEachZ.get(i).segments, pattern));
				System.out.println("z is : "+bestOfEachZ.get(i).z);
			}
		}

		
		
		long tEnd6 = System.currentTimeMillis();
		System.out.println("Elapsed time to get top K "+(tEnd6-tStart6));
		
		long tStart7 = System.currentTimeMillis();
		
		List<SegmentsAndZ> result = bestOfEachZ.subList(0,top_K);
		
		long tEnd7 = System.currentTimeMillis();
		System.out.println("Elapsed time to get sublist "+(tEnd7-tStart7));
		
		return convertOutputtoVisualization(result,data,zs,sdlquery,top_K);
	}
	
	/*Converts the top K List<Segment> to Result format*/
	 public static Result convertOutputtoVisualization(List<SegmentsAndZ> top_k_results , ArrayList<double[][]> data ,ArrayList<String> zs , Sdlquery sdlquery , int topK) throws SQLException, ClassNotFoundException{
		long tStart7 = System.currentTimeMillis();
		Data queryExecutor = new Data();
		Result result = new Result();
		
		for(int i = 0 ; i < topK ; i++){
			result.outputCharts.add(i,new Chart());
			/*Change to actual data
			result.outputCharts.get(i).setxData(Data.doubleToString(Data.getColumn(top_k_results.get(i).data,1)));
			result.outputCharts.get(i).setyData(Data.doubleToString(Data.getColumn(top_k_results.get(i).data,2)));

			/*To optimize :*/
			result.outputCharts.get(i).setxData(Data.doubleToString(Data.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),1)));
			result.outputCharts.get(i).setyData(Data.doubleToString(Data.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),2)));
			
			/*
			result.outputCharts.get(i).setxData(Data.doubleToString(Data.getColumn(Data.fetchSingleData(sdlquery.x,sdlquery.y,sdlquery.z, top_k_results.get(i).z, sdlquery.dataset, queryExecutor),1)));
			result.outputCharts.get(i).setyData(Data.doubleToString(Data.getColumn(Data.fetchSingleData(sdlquery.x,sdlquery.y,sdlquery.z, top_k_results.get(i).z, sdlquery.dataset, queryExecutor),2)));
			*/
			
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
		
		long tEnd7 = System.currentTimeMillis();
		System.out.println("Elapsed time to convert output "+(tEnd7-tStart7));
		return result;
	}

}
