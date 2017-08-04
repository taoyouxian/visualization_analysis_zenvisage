package edu.uiuc.zenvisage.service.nlpe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.SystemPropertyUtils;

import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.service.nlpe.Partition;
import edu.uiuc.zenvisage.service.nlpe.Segment;
import edu.uiuc.zenvisage.service.nlpe.ShapeSegment;

public class ShapeQueryExecutor {
	
	
static int min_size = 2;
public static List<Partition[]> allPartitions = new ArrayList<>();

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
	
	/*Dividing from "start" ending at "end" into all possible "nb_partitions" partitions*/
	public static void divide1(int start , int nb_partitions , Partition[] partitions , int end){
			if(nb_partitions == 1){
				partitions[0] = new Partition(start , end); 
				Collections.reverse(Arrays.asList(partitions));
				allPartitions.add(partitions);
			}else{
				for(int i = start + 1 ; i < end - nb_partitions + 2 ; i++){
					partitions[nb_partitions-1] = new Partition(start,i);
					divide1(i,nb_partitions-1,partitions.clone(),end);
				}
			}
		}
	
	public static ArrayList<ArrayList<Partition>> allPartitions(Partition[] partitions , double[][] data, Partition partition){
			
			ArrayList<ArrayList<Partition>> result = new ArrayList<>();
			
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
				
					
					for(int k = 0 ; k < j ; k+=0){
						for(int l = 0 ; l < allPartitions.size() ; l++){
							for(Partition t : allPartitions.get(l)){
								result.get(k).add(t);	
							}
							k++;
						}
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

	/*Creates a Segment from the "partition" and returns a score comparing the slope to the pattern type*/
	public static double getScoreKeywordLeaf(Partition partition ,ShapeSegment shapeSegment , double[][] normalized_viz){
		Segment segment = Segment.createSegment(partition, normalized_viz);
		
		double score = 0 ;
		switch(shapeSegment.getPattern().getType()){
		case "up" : score = Math.atan(segment.slope)/(Math.PI/2);
					break;
		case "flat" : score = (1-Math.abs(Math.atan(segment.slope)/(Math.PI/4)));
					break;
		case "down" : score = -Math.atan(segment.slope)/(Math.PI/2);
					break;
		case "*"  : score = 0 ;		 
					break;
		}	
		
//		System.out.println("Partition is : ("+partition.start_idx+","+partition.end_idx+")");
//		System.out.println("segment slope is : " +segment.slope);
//		System.out.println("pattern is : " +shapeSegment.getPattern().getType());
//		System.out.println("score given is : " +score);
//		System.out.println("***********************************************");

		return score;
	}
	
	/*Creates a Segment from the "partition" and returns a score comparing the real x,y values to the user's x,y inputs*/
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
		
		double abs_x_s = Math.abs(segment.start_idx - x_start); 
		double abs_x_e = Math.abs(segment.end_idx - x_end);
		double abs_y_s = Math.abs(data[segment.start_idx][1] - y_start);
		double abs_y_e = Math.abs(data[segment.end_idx][1] - y_end);


		return  (( (x_s * abs_x_s)/xRange
				+ (x_e * abs_x_e)/xRange 
				+ (y_s * abs_y_s)/yRange
				+ (y_e * abs_y_e)/yRange )/nb_elements);
	}
	
	/*Returns the highest score for a visualization following these steps:
	 * Partition "partition" into the number of child segments.
	 * For every child segment,assign a partition and create a Segment with it and give the child segment a score depending on its pattern type and x,y constraints.
	 * Return the partition that maximizes the score.
	 */
	public static double getScoreForPartition(ShapeSegment shapeSegment , Partition partition, double[][] normalized_viz , double[][] original_viz){
		/*Smooth our segments then give all partitions possible*/
		/*OLD APPROACH 1 */
	   //	List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);

		
		/*HARD CONSTRAINT ON X-VALUES*/
		if(shapeSegment.isHasChildren()){
			
			if(partition.end_idx-partition.start_idx < shapeSegment.getShapeSegments().size()){
				System.out.println("Cannot partition");
				return Double.MIN_VALUE;
			}
			
			List<Double> scores = new ArrayList<>();
			for(ArrayList<Partition> partitions : allPartitions(xConstraints(shapeSegment),original_viz,partition)){
				Partition[] tmp = partitions.toArray(new Partition[0]);
				
				for(Partition p : tmp){
					System.out.println("Partition : ("+p.start_idx+","+p.end_idx+")");
				}
				System.out.println("******************");
				
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
		}
		else{
			int x_s = booleanToInt(shapeSegment.getX_start() != null); //0 if x_start is "" else 1
			int x_e = booleanToInt(shapeSegment.getX_end() != null); //0 if x_end is "" else 1
			int y_s = booleanToInt(shapeSegment.getY_start() != null); //0 if y_start is "" else 1
			int y_e = booleanToInt(shapeSegment.getY_end() != null); //0 if y_end is "" else 1
			
			int nb_elements = x_s + x_e + y_s + y_e; // nb of elements not ""
			
			double alpha;
			if(nb_elements != 0 && shapeSegment.getPattern().getType().equals("")){
				 alpha = 0;
			}else if((nb_elements != 0 && !shapeSegment.getPattern().getType().equals(""))){
				 alpha = 0.5;
			}else{
				alpha = 1;
			}
		
			return alpha*getScoreKeywordLeaf(partition,shapeSegment,normalized_viz)+(1-alpha)*(1-getScoreL1Leaf(partition, shapeSegment, original_viz));
		}
		
		
	//	//Raw material
	//	List<List<Segment>> result = new ArrayList<>();
	//	for(ArrayList<Partition> partitions : allPartitions(xConstraints(shapeQuery.getShapeSegment()),original_viz,partition)){
	//		Partition[] tmp = partitions.toArray(new Partition[0]);
	//		result.add(Segment.createListSegment(tmp, normalized_viz));
	//	}
	//			
	////  *************************************
	//	
	//	List<Double> scores = new ArrayList<>();
	//	/*Score and rank*/
	//	for(List<Segment> segments : result){
	//		scores.add(SdlMain.getOverallScore1(segments, shapeQuery, pattern , normalized_viz,original_viz));
	//	}
	//
	//	int max_idx = -1;
	//	double max_score = Double.NEGATIVE_INFINITY;
	//	
	//	for(int i = 0 ; i < scores.size() ; i++){
	//		if(scores.get(i) > max_score){
	//			max_idx = i;
	//			max_score = scores.get(i);
	//		}
	//	}
	
	}

	/*Returns the highest score for a visualization*/
	public static SegmentsToZMapping getBestFitScoreForViz(String[] pattern , ShapeQuery shapeQuery , String z , double[][] normalized_viz , double[][] original_viz){
			/*Smooth our segments then give all partitions possible*/
			/*OLD APPROACH 1 */
	//		List<List<Segment>> result = SdlMain.partition1(Segment.smoothing(min_size,nb_segments,shapeQuery.shapeSegment.size(),normalized_data),shapeQuery.shapeSegment.size(), normalized_data);
			
			
			/*HARD CONSTRAINT ON X-VALUES*/
			//Create a partition with starting index and end index
			Partition rootPartition = new Partition((int)original_viz[0][0] , (int)original_viz[original_viz.length-1][0]);
			
		    double vizscore = getScoreForPartition(shapeQuery.getShapeSegment() , rootPartition ,  normalized_viz  , original_viz);
			return new SegmentsToZMapping(Segment.initialize(normalized_viz, min_size), z, normalized_viz, vizscore);
		}
		
	public static Result execute(ShapeQuery shapeQuery) throws ClassNotFoundException, SQLException{
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
//			NlpRegexParser.parse(shapeQuery);

			ArrayList<String> patternTypes = new ArrayList<>();
			
			/*Getting the pattern from the shape query*/
			DataService.getPatternFromShapeQuery(shapeQuery.getShapeSegment(),patternTypes);
	
			String[] pattern = new String[patternTypes.size()];
			int j = 0 ;
			for(String s : patternTypes){
				pattern[j] = s;
				j++;
			}
			
			/*A list storing the best visualization for every z-value*/
			List<SegmentsToZMapping> scores = new ArrayList<>();
			
			/*Store best representation with its z value*/
			for(int i = 0 ; i < normalizedVisualizations.size() ; i++){
				/*Test if we can have at least one segment*/
				if(normalizedVisualizations.get(i).length > 1){
					if(approach.equals("approach1")){
						scores.add(getBestFitScoreForViz(pattern,shapeQuery, zs.get(i), normalizedVisualizations.get(i) , originalVisualizations.get(i)));
					}else{
						//Approach 2 
//						List<Segment> smooth_segments = Segment.initialize(normalizedVisualizations.get(i),min_size);	
//						scores.add(SdlMain.getBestPartition2(smooth_segments , min_size, nb_segments, pattern , shapeQuery , zs.get(i), normalizedVisualizations.get(i) , originalVisualizations.get(i)));
					}
	
				}
			}
		
			/*Sort depending on score*/
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
	
			/*Z values of top k visualizations*/
			for(int i = 0 ; i < top_K  ; i++){
				System.out.println("z is : "+scores.get(i).z);
			}
			
			/*Check the details about a specific z(test_z)
			 for(int i = 0 ; i < scores.size(); i++){
				 if(scores.get(i).z.equals(test_z)){
					 System.out.println("z : "+scores.get(i).z);
					 System.out.println("its rank is : "+i);
					 SegmentsMappedWithZ.print(scores.get(i));
				 }
			 }
			*/
			
			/*Get top K best visualizations*/
			List<SegmentsToZMapping> bestVisualizations = scores.subList(0,top_K);
			
			/*Returning normalized visualizations to front end*/
			// 	return convertOutputtoVisualization(bestVisualizations,normalizedData,zs,shapeQuery,top_K);	
			
			
			/*Returning original visualizations to front end*/
			return convertOutputtoVisualization(bestVisualizations,originalVisualizations,zs,shapeQuery,top_K);	
			
		}
	
	/*Converts the top K List<Segment> to Result format*/
	 public static Result convertOutputtoVisualization(List<SegmentsToZMapping> top_k_results , ArrayList<double[][]> data , ArrayList<String> zs , ShapeQuery shapeQuery , int topK) throws SQLException, ClassNotFoundException{
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
