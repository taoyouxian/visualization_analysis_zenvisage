package edu.uiuc.zenvisage.service.nlpe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.service.nlpe.Partition;
import edu.uiuc.zenvisage.service.nlpe.Segment;
import edu.uiuc.zenvisage.service.nlpe.ShapeSegment;

public class ShapeQueryExecutor {

/*Casts boolean to int*/
public static int booleanToInt(boolean b){
	return (b) ? 1 : 0;
}
	
public static ArrayList<Integer> toNewFormat(Partition[] partitions ,int x_min , int x_max){
	ArrayList<Integer> result = new ArrayList<>();
	
	int j = 0 ;
	for(int i = 0 ; i < partitions.length ; i++){
		if(partitions[i].start_idx >= 0){
			result.add(j,(partitions[i].start_idx));
			j++;
		}else{
			if(i == 0){
				result.add(j,x_min); 
				j++;
			}else{
				result.add(j,partitions[i-1].end_idx);
				j++;
			}
		}
		
	    if(partitions[i].end_idx >= 0){
			result.add(j,(partitions[i].end_idx));
			j++;
		}else{
			if(i == partitions.length-1 ){
				result.add(j,x_max);
				j++;
			}else{
				result.add(j,(partitions[i+1].start_idx));
				j++;
			}	
		}
	}
	return result;
}

public static ArrayList<ArrayList<Partition>> allPartitions(Partition[] partitions , double[][] data, Partition partition){
		
		ArrayList<ArrayList<Partition>> result = new ArrayList<>();
		
//		int x_min = (int)(data[0][0]);
//
//		int x_max = (int)(data[data.length-1][0]-1);
//		
//		/*Case where first x value is 0*/
//		if(data[0][0] == 0){
//			 x_max = (int)(data[data.length-1][0]);
//		}
		
		ArrayList<Integer> newFormat = toNewFormat(partitions , partition.start_idx , partition.end_idx);
		boolean firstRun = true;
		int j = 1 ;
		
		result.add(0,new ArrayList<>());

		for(int i = 0 ; i < newFormat.size() ; i++){
			if(i == newFormat.size()-1){
				return result;
			}
			
			if(newFormat.get(i) >= 0 && newFormat.get(i+1) >= 0){
				for(int k = 0 ; k < j ; k++){
					result.get(k).add(new Partition((int)newFormat.get(i),(int)newFormat.get(i+1)));
				}
				i++;
				firstRun = false;
			}else{//case where newFormat.get(i+1) < 0
				int last_idx = newFormat.get(i);
				int nb_partitions = 0 ;
				while(newFormat.get(i+1) < 0){
					nb_partitions++;
					i++;
				}
				
				nb_partitions = (nb_partitions/2) + 1;
				divide1(last_idx, nb_partitions, new Partition[nb_partitions], newFormat.get(i+1));
				i+=1;
				
				int old_j = j;
				
				j *= allPartitions.size();
				
				for(int k = old_j ; k < j ; k++){
					result.add(k,new ArrayList<>());
				}
				
				ArrayList<ArrayList<Partition>> tmp = new ArrayList<>();
				
				if(!firstRun){
					for(int n = 0 ; n < old_j ; n++){
						tmp.add(n,Partition.clone(result.get(n)));
					}
				}

				int start = 0 ;
				
				for(int l = 0 ; l < tmp.size() ; l++){
					for(int idx = start ; idx < start + allPartitions.size() && idx < j; idx ++){
						result.get(idx).clear();
						for(Partition t : tmp.get(l)){
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
						for(Partition t : allPartitions.get(l)){
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
	
public static Partition[] xConstraints(ShapeSegment shapeSegment){
	
	Partition[] constraints = new Partition[shapeSegment.getShapeSegments().size()];
	int i = 0 ;

	for(ShapeSegment shapeSeg : shapeSegment.getShapeSegments()){
		if(shapeSeg.getX_start() == null && shapeSeg.getX_end() == null){
			constraints[i] = new Partition(-1,-1);
		}else if(shapeSeg.getX_start() != null && shapeSeg.getX_end() == null){
			constraints[i] = new Partition(shapeSeg.getX_start(),-1);
		}else if(shapeSeg.getX_start() == null && shapeSeg.getX_end() != null){
			constraints[i] = new Partition(-1,shapeSeg.getX_end());
		}else{
			constraints[i] = new Partition(shapeSeg.getX_start(),shapeSeg.getX_end());
		}
		i++;
	}
	return constraints;
}	


/*Takes shapeSegments and segments and returns score based on these*/
public static double getScoreL1(List<Segment> segments , ShapeQuery shapeQuery , double data[][]){
	double score = 0 ;
	
	double xRange = data[data.length-1][0]-data[0][0];
	double yRange = DataService.findRange(DataService.getColumn(data,2));

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
public static double getOverallScore1(List<Segment> segments ,ShapeSegment shapeSegment , String[] pattern  , double normalized_data[][] , double raw_data[][]){
	return getScoreKeywords1(segments,pattern,shapeQuery)+(1-getScoreL1(segments,shapeQuery,raw_data));
}


public static double getScoreKeywordLeaf(Partition partition ,ShapeSegment shapeSegment , double[][] normalized_viz){
	Segment segment = Segment.createSegment(partition, normalized_viz);
	
	int x_s = booleanToInt(shapeSegment.getX_start() != null); //0 if x_start is "" else 1
	int x_e = booleanToInt(shapeSegment.getX_end() != null); //0 if x_end is "" else 1
	int y_s = booleanToInt(shapeSegment.getY_start() != null); //0 if y_start is "" else 1
	int y_e = booleanToInt(shapeSegment.getY_end() != null); //0 if y_end is "" else 1
	
	int nb_elements = x_s + x_e + y_s + y_e; // nb of elements not ""
	
	double alpha;
	if(nb_elements != 0 && shapeSegment.getPattern()..equals("")){
		 alpha = 0;
	}else if((nb_elements != 0 && !shapeSegment.getPattern()..equals(""))){
		 alpha = 0.5;
	}else{
		alpha = 1;
	}
	
	double score = 0 ;
	switch(shapeSegment.getPattern().){
	case "up" : score = Math.atan(segment.slope)/(Math.PI/2);
				break;
	case "flat" : score = (1-Math.abs(Math.atan(segment.slope)/(Math.PI/4)));
				break;
	case "down" : score = Math.atan(segment.slope)/(Math.PI/2);
				break;
	case "*"  : score = 0 ;		 
				break;
	}	
	return alpha*score;
}


public static double getScoreL1Leaf(Partition partition , ShapeSegment shapeSegment , double data[][]){
	Segment segment = Segment.createSegment(partition, data);
	
	double xRange = DataService.findRange(DataService.getColumn(data, 1));
	double yRange = DataService.findRange(DataService.getColumn(data, 2));

	int x_s = booleanToInt(shapeSegment.getX_start() != null); //0 if x_start is "" else 1
	int x_e = booleanToInt(shapeSegment.getX_end() != null); //0 if x_end is "" else 1
	int y_s = booleanToInt(shapeSegment.getY_start() != null); //0 if y_start is "" else 1
	int y_e = booleanToInt(shapeSegment.getY_end() != null); //0 if y_end is "" else 1
	
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

	double x_start = shapeSegment.getX_start() == null ?  Double.MIN_VALUE : shapeSegment.getX_start();
	double x_end = shapeSegment.getX_end() == null  ?  Double.MIN_VALUE   : shapeSegment.getX_end();
	double y_start = shapeSegment.getY_start() == null  ?  Double.MIN_VALUE  : shapeSegment.getY_start();
	double y_end = shapeSegment.getY_end() == null ?  Double.MIN_VALUE  : shapeSegment.getY_end();
	
	double beta;
	if(nb_elements != 0 && shapeSegment.getPattern()..equals("")){
		 beta = 1;
	}else if((nb_elements != 0 && !shapeSegment.getPattern()..equals(""))){
		 beta = 0.5;
	}else{
		beta = 0;
	}
	
	double abs_x_s = Math.abs(segment.start_idx - x_start); 
	double abs_x_e = Math.abs(segment.end_idx - x_end);
	double abs_y_s = Math.abs(data[segment.start_idx][1] - y_start);
	double abs_y_e = Math.abs(data[segment.end_idx][1] - y_end);
	
//	
//	return  beta*
//			(
//			(
//			(y_s * abs_y_s)/yRange
//			+ (y_e * abs_y_e)/yRange 
//			)
//			/nb_elements
//			);
	
	return  beta*(( (x_s * abs_x_s)/xRange
			+ (x_e * abs_x_e)/xRange 
			+ (y_s * abs_y_s)/yRange
			+ (y_e * abs_y_e)/yRange )/nb_elements);
}

 


/*Returns the highest score for a visualization using [TODO: describe the approach]*/
public static double getScoreForPartition(ShapeSegment shapeSegment, Partition partition, double[][] normalized_viz , double[][] original_viz){
	/*Smooth our segments then give all partitions possible*/
	/*OLD APPROACH 1 */
   //	List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);
	
	
	/*HARD CONSTRAINT ON X-VALUES*/

	
	if(shapeSegment.isHasChildren()){
		//TODO 

		List<Double> scores = new ArrayList<>();
		// create all partitions
		//Fix
		for(ArrayList<Partition> partitions : allPartitions(xConstraints(shapeSegment),original_viz,partition)){
			Partition[] tmp = partitions.toArray(new Partition[0]);
			double score = 0 ;
			int i = 0 ; 
			int overall_length = 0 ;
			for(ShapeSegment childSegment : shapeSegment.getShapeSegments()){
				 int currentChildSegmentLength = tmp[i].end_idx - tmp[i].start_idx;
				 overall_length += currentChildSegmentLength;
				 score += getScoreForPartition(childSegment, tmp[i], normalized_viz , original_viz)*(currentChildSegmentLength);
				 i++;
			}
			score /= overall_length;
			scores.add(score);
			// getScoreForPartition
			// calculate score for one partition, based on the operation and normalization: segment_length[i])/(overall_length*(pattern.length - nb_stars)))
			// add the score to scores
		}
		int max_idx = -1;
		double max_score = Double.NEGATIVE_INFINITY;
		
		for(int i = 0 ; i < scores.size() ; i++){
			if(scores.get(i) > max_score){
				max_idx = i;
				max_score = scores.get(i);
			}
		}
		return scores.get(max_idx);
		// return max score		
	}
	else{
		return getScoreKeywordLeaf(partition,shapeSegment.getPattern().,normalized_viz)+(1-getScoreL1Leaf(partition, shapeSegment, original_viz));
		// calculate score for partition
	   //	getOverallScore1(segments, shapeQuery, pattern , normalized_viz,original_viz);
	   //	return the score for one match
	}
	
	
	//Raw material
	List<List<Segment>> result = new ArrayList<>();
	for(ArrayList<Partition> partitions : allPartitions(xConstraints(shapeQuery.getShapeSegment()),original_viz,partition)){
		Partition[] tmp = partitions.toArray(new Partition[0]);
		result.add(Segment.createListSegment(tmp, normalized_viz));
	}
			
//  *************************************
	
	List<Double> scores = new ArrayList<>();
	/*Score and rank*/
	for(List<Segment> segments : result){
		scores.add(SdlMain.getOverallScore1(segments, shapeQuery, pattern , normalized_viz,original_viz));
	}

	int max_idx = -1;
	double max_score = Double.NEGATIVE_INFINITY;
	
	for(int i = 0 ; i < scores.size() ; i++){
		if(scores.get(i) > max_score){
			max_idx = i;
			max_score = scores.get(i);
		}
	}

}



	/*Returns the highest score for a visualization using [TODO: describe the approach]*/
	public static SegmentsToZMapping getBestFitScoreForViz(String[] pattern , ShapeQuery shapeQuery , String z , Partition partition, double[][] normalized_viz , double[][] original_viz){
		/*Smooth our segments then give all partitions possible*/
		/*OLD APPROACH 1 */
//		List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);
		
		
		/*HARD CONSTRAINT ON X-VALUES*/
		// create a partition with starting index and end index
		Partition partition = new Partition(original_viz[0][0] , original_viz[original_viz.length-1][0]);
	    double vizscore = getScoreForPartition(shapeQuery.getShapeSegment(),partition , original_viz,normalized_viz);
	//	return vizscore;
	//	return new SegmentsToZMapping(result.get(max_idx), z, normalized_viz, scores.get(max_idx));
		
		
	
	}
	
	
	

  public Result execute(ShapeQuery shapeQuery){
		/*Getting all values needed for query*/
		String X = shapeQuery.x;
		String Y = shapeQuery.y; 
		String Z = shapeQuery.z;
		String tableName = shapeQuery.dataset;
		String keywords = shapeQuery.nltext;  
		String approach = shapeQuery.approach;
		int nb_segments = Integer.parseInt(shapeQuery.regex);
		
		/*Object for storing z values*/
		ArrayList<String> zs =  new ArrayList<>();
	
		/*Fetch all data*/
		ArrayList<double[][]> originalVisualizations = DataService.fetchAllData(X, Y, Z, tableName,zs);
		
		/*Normalized version of original data(y values)*/
		ArrayList<double[][]> normalizedVisualizations = DataService.normalizeData(DataService.cloneData(originalVisualizations));

		/*Parsing shapeQuery*/
//		NlpRegexParser.parse(shapeQuery);
		
		/*Getting the pattern from the shape query*/
//		String[] pattern = DataService.getPatternFromShapeQuery(shapeQuery);
	
		/*A list storing the best visualization for every z-value*/
		List<SegmentsToZMapping> scores = new ArrayList<>();
		
		/*Store best representation with its z value*/
		for(int i = 0 ; i < normalizedVisualizations.size() ; i++){
			/*Test if we can have at least one segment*/
			if(normalizedVisualizations.get(i).length > 1){
				if(approach.equals("approach1")){
					scores.add(SdlMain.getBestFitScoreForViz(min_size, nb_segments, pattern , shapeQuery , zs.get(i), normalizedVisualizations.get(i) , originalVisualizations.get(i)));
				}else{
					List<Segment> smooth_segments = Segment.initialize(normalizedVisualizations.get(i),min_size);	
					scores.add(SdlMain.getBestPartition2(smooth_segments , min_size, nb_segments, pattern , shapeQuery , zs.get(i), normalizedVisualizations.get(i) , originalVisualizations.get(i)));
				}

			}
		}
	
		
		
		
		/*Sort list of segments depending on score*/
		Collections.sort(scores, new Comparator<SegmentsToZMapping>() {
			@Override public int compare(SegmentsToZMapping s1, SegmentsToZMapping s2) {
				if(s1.score < s2.score){
					return 1;
				}else if(s1.score > s2.score){
					return -1;
				}else{
					return 0;
				}
			}
		});
		
		/*Print the top k visualizations*/
		int top_K = Math.min(shapeQuery.topk,scores.size());
	
		for(int i = 0 ; i < top_K  ; i++){
			SegmentsToZMapping.print(scores.get(i));
		}

		/*Indexes of top k visualizations*/
		for(int i = 0 ; i < top_K  ; i++){
			System.out.println("z is : "+scores.get(i).z);
		}
		
/*		Check the details about a specific z(test_z)
		 for(int i = 0 ; i < scores.size(); i++){
			 if(scores.get(i).z.equals(test_z)){
				 System.out.println("z : "+scores.get(i).z);
				 System.out.println("its rank is : "+i);
				 SegmentsMappedWithZ.print(scores.get(i));
			 }
		 }
		*/
		
		
		List<SegmentsToZMapping> bestVisualizations = scores.subList(0,top_K);
		// 	return convertOutputtoVisualization(bestVisualizations,normalizedData,zs,shapeQuery,top_K);	
	
		return convertOutputtoVisualization(bestVisualizations,originalVisualizations,zs,shapeQuery,top_K);	
		
	}


	

	
	
	
/*Converts the top K List<Segment> to Result format*/
 public static Result convertOutputtoVisualization(List<SegmentsToZMapping> top_k_results , ArrayList<double[][]> data ,ArrayList<String> zs , ShapeQuery shapeQuery , int topK) throws SQLException, ClassNotFoundException{
		long tStart7 = System.currentTimeMillis();
		Result result = new Result();
		
		for(int i = 0 ; i < topK ; i++){
			result.outputCharts.add(i,new Chart());

			result.outputCharts.get(i).setxData(DataService.doubleToString(DataService.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),1)));
			result.outputCharts.get(i).setyData(DataService.doubleToString(DataService.getColumn(data.get(zs.indexOf(top_k_results.get(i).z)),2)));
			
			result.outputCharts.get(i).setRank(i+1);
			result.outputCharts.get(i).setxType(shapeQuery.x);
			result.outputCharts.get(i).setyType(shapeQuery.y);
			result.outputCharts.get(i).setzType(shapeQuery.z);
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
