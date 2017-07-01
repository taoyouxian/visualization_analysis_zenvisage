package edu.uiuc.zenvisage.service.nlp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import edu.uiuc.zenvisage.service.ZvMain;

public class NlpTest {
	
	public static void testSdl() throws IOException, InterruptedException, SQLException, ClassNotFoundException{
		String query = "{\"x\": \"year\", \"y\": \"soldpricepersqft\", \"z\": \"city\", \"dataset\": \"real_estate\", \"approach\": \"approach2\", \"sdlsegments\": \"4\" , \"sdltext\":\"flat up down flat\"}";
		ZvMain zv = new ZvMain();
		String output = zv.executeSDL(query);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
		
		long tStart1 = System.currentTimeMillis();		
		
		testSdl();
		
		long tEnd1 = System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME : "+(tEnd1-tStart1)/1000.);	
	
		
		/*
		double[][] data = new double[2][2];
		Segment s1 = new Segment(0, 5, data);
		Segment s2 = new Segment(5, 10, data);
		Segment s3 = new Segment(10, 12, data);
		Segment s4 = new Segment(12, 16, data);
		
		List<Segment> l = new ArrayList<>();
		l.add(0,s1);
		l.add(1,s2);
		l.add(2,s3);
		l.add(3,s4);
		
		SdlMain.partition(l,1 , data);
		
		for(Tuple[] t : SdlMain.allPartitions){
			for(Tuple a : t){
				System.out.println("tuple : ("+a.start_idx+","+a.end_idx+")");
			}
			System.out.println("-------------");
		}
		*/
	}
}
