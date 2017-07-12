package edu.uiuc.zenvisage.service.nlp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.service.ZvMain;

public class NlpTest {
	
	public static void testSdl() throws IOException, InterruptedException, SQLException, ClassNotFoundException{
		String query = "{\"x\": \"x\", \"y\": \"y\", \"z\": \"z\", \"dataset\": \"data1\", \"approach\": \"approach1\", \"sdlsegments\": \"4\" , \"sdltext\":\" (,up,,,1,2);(,down,,,2,1);(,up,,,1,2);(,flat,,,2,2)\"}";
		ZvMain zv = new ZvMain();
		String output = zv.executeSDL(query);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
		
		long tStart1 = System.currentTimeMillis();		
		
		testSdl();
		
		long tEnd1 = System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME : "+(tEnd1-tStart1)/1000.);
//		**********************************************************************
//	
//		double[][] data = new double[2][2];
//		Segment s1 = new Segment(0, 5, data);
//		Segment s2 = new Segment(5, 10, data);
//		Segment s3 = new Segment(10, 12, data);
//		Segment s4 = new Segment(12, 16, data);
//		Segment s5 = new Segment(16, 20, data);
//
//		
//		List<Segment> l = new ArrayList<>();
//		l.add(0,s1);
//		l.add(1,s2);
//		l.add(2,s3);
//		l.add(3,s4);
//		l.add(4,s5);
//
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
//		System.out.println("START");
//		
//		for(List<Segment> s : SdlMain.partition1(l,3, data)){
//			Segment.printListSegments(s);
//			System.out.println("************");
//		}
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
	}
}
