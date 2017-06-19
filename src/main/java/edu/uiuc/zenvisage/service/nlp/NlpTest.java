package edu.uiuc.zenvisage.service.nlp;

import java.io.IOException;
import java.sql.SQLException;

import edu.uiuc.zenvisage.service.ZvMain;

public class NlpTest {
	
	public static void testSdl() throws IOException, InterruptedException, SQLException, ClassNotFoundException{
		String query = "{\"x\": \"month\", \"y\": \"soldpricepersqft\", \"z\": \"city\", \"dataset\": \"real_estate\", \"sdlsegments\": \"2\" , \"sdltext\":\"up down\"}";
		ZvMain zv = new ZvMain();
		String output = zv.executeSDL(query);
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, SQLException {
		testSdl();
	}
}
