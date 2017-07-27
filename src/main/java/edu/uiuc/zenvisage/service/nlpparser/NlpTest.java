package edu.uiuc.zenvisage.service.nlpparser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.service.ZvMain;

public class NlpTest {
	
	public static void testSdl() throws IOException, InterruptedException, SQLException, ClassNotFoundException{
		String query = "{\"x\": \"x\", \"y\": \"y\", \"z\": \"z\", \"dataset\": \"data1\", \"approach\": \"approach1\", \"sdlsegments\": \"3\" , \"sdltext\":\"[['', 'down', '', '', '', ''],['', '*', '', '', '', '']]\"}";
		ZvMain zv = new ZvMain();
		zv.executeSDL(query);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
//		String jsonstr = "[['', 'up', '5', '10', '', ''], ['', 'down', '15', '20', '', '']]";
//		for(String[] tab : Data.parser(jsonstr)){
//			for(String s : tab){
//				System.out.println(s);
//			}
//		}
		
		long tStart1 = System.currentTimeMillis();		
		
		testSdl();
		
		long tEnd1 = System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME : "+(tEnd1-tStart1)/1000.);

		/* [['', 'up', '', '', '1', '3'], ['', 'down', '', '', '3', '1']*/
//		**********************************************************************
		
//		String jsonstr = "[['', 'up', '5', '10', '', ''], ['', 'down', '15', '20', '', '']]";
//		JSONArray arry = new JSONArray(jsonstr);
//		
//		for (int i = 0 ; i < arry.length(); i++){
//			JSONArray small_array = arry.getJSONArray(i);
//			String modifier = small_array.getString(0);
//			String pattern = small_array.getString(1);
//			String x_start = small_array.getString(2);
//			String x_end = small_array.getString(3);
//			String y_start = small_array.getString(4);
//			String y_end = small_array.getString(5);
//			System.out.println("Segment: No."+i+"\n");
//			System.out.println("M: "+modifier);
//			System.out.println("P: "+pattern);
//			System.out.println("x_start: "+x_start);
//			System.out.println("x_end: "+x_end);
//			System.out.println("y_start: "+y_start);
//			System.out.println("y_end: "+y_end+"\n");
//		}
//		**********************************************************************
	
	
//		double[][] data = new double[2][2];
//		Segment s1 = new Segment(0, 5, data);
//		Segment s2 = new Segment(5, 10, data);
//		Segment s3 = new Segment(10, 12, data);
//		Segment s4 = new Segment(12, 16, data);
//		Segment s5 = new Segment(16, 20, data);	
//		List<Segment> l = new ArrayList<>();
//		
//		l.add(0,s1);
//		l.add(1,s2);
//		l.add(2,s3);
//		l.add(3,s4);
//		l.add(4,s5);
//		
//		System.out.println("START");
//		SdlMain.partition2(l,6, data);
//		
//		for(Tuple[] tuples  : SdlMain.allPartitions){
//			for(Tuple a : tuples){
//				System.out.println("tuple : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("-------------");
//		}
		
//		for(List<Segment> s : SdlMain.partition1(l,2, data)){
//			Segment.printListSegments(s);
//			System.out.println("************");
//		}		
		
//		Segment a1 = new Segment(0, 1, data);
//		Segment a2 = new Segment(1, 2, data);
//		Segment a3 = new Segment(2, 3, data);
//		Segment a4 = new Segment(3, 4, data);
//		Segment a5 = new Segment(4, 5, data);
//
//		
//		List<Segment> l1 = new ArrayList<>();
//		l1.add(0,a1);
//		l1.add(1,a2);
//		l1.add(2,a3);
//		l1.add(3,a4);
//		l1.add(4,a5);
//		

//		
//		System.out.println("LIST 2 **********************");
//		for(List<Segment> s : SdlMain.partition1(l1,3, data)){
//			Segment.printListSegments(s);
//			System.out.println("************");
//		}
//		
//		
//		for(Tuple[] t : SdlMain.allPartitions){
//			for(Tuple a : t){
//				System.out.println("tuple : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("-------------");
//		}
//		
//		System.out.println("END");
		
		
//		**********************************************************************
		
//		String s = " (1,,3,,5,6) ;    (1,2,3,4,5,6)  ; (1,2,3,,,) ; (,,,4,5,6)";
//		for(String[] tab : Data.parser(s)){
//			for(String a : tab){
//				System.out.println(a);
//			}
//			System.out.println("-----------");
//		}
		
//		**********************************************************************	
		
//		Tuple t1 = new Tuple(3,-1);
//		Tuple t2 = new Tuple(-1,-1);
//		Tuple t3 = new Tuple(-1,-1);
//		Tuple t4 = new Tuple(10,-1);
//		Tuple t5 = new Tuple(11,13);
//		
//		Tuple[] tuples = new Tuple[5];
//		tuples[0] = t1;
//		tuples[1] = t2;
//		tuples[2] = t3;
//		tuples[3] = t4;
//		tuples[4] = t5;
//		
//		
//		for(ArrayList<Tuple> t : SdlMain.allTuples(tuples)){
//			for(Tuple a : t){
//			System.out.println("TUPLE : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("------------");	
//		}
//		
//		System.out.println(SdlMain.allTuples(tuples).size());
		
//		for(Integer i : SdlMain.toNewFormat(tuples)){
//			System.out.println(i);
//		}
		
		
//		**********************************************************************
		
//		SdlMain.divide1(1, 3, new Tuple[3] , 5);
//		for(Tuple[] t : SdlMain.allPartitions){
//			for(Tuple a : t){
//				System.out.println("TUPLE : ("+a.start_idx+","+a.end_idx+")");
//			}
//			System.out.println("------------");
//		}
		
//		**********************************************************************		
//		ShapeQuery shapeQuery = new ShapeQuery();
//
//		for(String[] shapeSegment : Data.parser("[['', '*', '', '', '', ''],['', '*', '5', '', '', '']]")){
//			shapeQuery.shapeSegment.add(new ShapeSegment(shapeSegment[0], shapeSegment[1], shapeSegment[2], shapeSegment[3], shapeSegment[4], shapeSegment[5]));
//		}
//		
//		Tuple[] result = SdlMain.xConstraints(shapeQuery);
//		
//		for(Tuple a : result){
//			System.out.println("TUPLE : ("+a.start_idx+","+a.end_idx+")");
//		}
//		
//		System.out.println("**************");	
//		
//		double data[][] = new double [1][2];
//		data[0][0] = 30;
//		data[0][1] = 1;
//		
//		for(ArrayList<Tuple> t : SdlMain.allTuples(result,data)){
//			for(Tuple a : t){
//				System.out.println("TUPLE : ("+a.start_idx+","+a.end_idx+")");
//			}
//		System.out.println("------------");	
//	}
		
	}
}
