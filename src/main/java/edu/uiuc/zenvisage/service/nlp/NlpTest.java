package edu.uiuc.zenvisage.service.nlp;

import java.io.IOException;
import java.sql.SQLException;

import edu.uiuc.zenvisage.service.ZvMain;

public class NlpTest {
	
	public static void testSdl() throws IOException, InterruptedException, SQLException, ClassNotFoundException{
		String query = "{\"x\": \"x\", \"y\": \"y\", \"z\": \"z\", \"dataset\": \"test\", \"sdlsegments\": \"3\" , \"sdltext\":\"up down up\"}";
		ZvMain zv = new ZvMain();
		String output = zv.executeSDL(query);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
		long tStart1 = System.currentTimeMillis();		
		
		testSdl();
		
		long tEnd1 = System.currentTimeMillis();
		System.out.println("TOTAL EXECUTION TIME : "+(tEnd1-tStart1)/1000.);	

	}
}
