package edu.uiuc.zenvisage.service.nlpe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;


/*data is data[n][2]*/
public class DataService{
	private String database = "postgres";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "postgres";
	private String password = "zenvisage";
	Connection c = null;
	
	/*Stores list of Z values*/
	public static ArrayList<String> allZs = new ArrayList<>();

	/*Initialize connection*/
	public DataService() {
	      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection(host, username, password);
		      } catch (Exception e) {
		    	 System.out.println("Connection Failed! Check output console");
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
	      System.out.println("Opened database successfully");
	      try {
	    	  Statement s = c.createStatement();
	    	  s.execute("SET SESSION work_mem = '200MB'");
	      } catch (SQLException e) {
	    	  System.out.println("Cannot change work_mem!");
	    	  e.printStackTrace();
	    	  System.exit(0);
	      }
	}
	
	/*Given a SQL query returns a ResultSet*/
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	      ResultSet ret = stmt.executeQuery(sQLQuery);;
	      return ret;
	}
	
	/*Gets the data of a single z value from Postgres and stores it in a 2D array*/
	public static double[][]  fetchSingleData(String X , String Y ,String Z , String singleValue , String tableName , DataService executor) throws SQLException, ClassNotFoundException{
//		SQLQueryExecutor queryExecutor = new SQLQueryExecutor();
		
//		String sql = "SELECT " + Z + "," + X + "," + "avg(" + Y + ")"
//				+ " FROM " + tableName
//				+ " GROUP BY " + Z + ", "+ X
//				+ " ORDER BY " + Z + ", "+ X;
			
		String sql = "SELECT " + X+ "," + "avg(" + Y + ")"
		+ " FROM " + tableName
		+ " WHERE " + Z + " = '" + singleValue.replaceAll("'","''")  //escaping single quotes
		+ "' GROUP BY " + X
		+ " ORDER BY "+ X;
		/*+ " LIMIT 25000";*/

		ResultSet rows = executor.query(sql);
		
		/*Finds the length of the table(ResultSet)*/
		int rowcount = 0;
		
		/*Get number of rows*/
		if (rows.last()) {
			  rowcount = rows.getRow();
			  rows.beforeFirst(); 
		}
		
		double[][] result = new double[rowcount][2];
		
		/*Print table*/
//		while(rows.next()){
//			System.out.println("The city is " +rows.getString("City"));
//			System.out.println("The year is " +rows.getDouble(X));
//			System.out.println("The sold price is " +rows.getDouble("AVG"));
//		}
		
		rows.beforeFirst(); 
		 
		/*Store data in double[][]}*/
		int i = 0 ;
		while(rows.next()){
			result[i][0] = rows.getDouble(X); // Store value of X
			result[i][1] = rows.getDouble("AVG"); // Store value of Y
			i++;
		}
		return result;
	}	
	
	/*Gets the data of a all z values from Postgres and stores it in a list*/
	public static ArrayList<double[][]> fetchAllData(String X , String Y , String Z , String tableName ) throws SQLException, ClassNotFoundException{
		/*ArrayList<double[][]> result = new ArrayList<>();*/
		ArrayList<ArrayList<double[]>> result = new ArrayList<>();
		DataService queryExecutor = new DataService();
		
		/*String sql = "SELECT DISTINCT " + Z + " FROM " + tableName;*/
		String sql = "SELECT " + Z + "," + X + "," +"avg(" + Y + ")"
				+ " FROM " + tableName 
				+ " GROUP BY " + Z + "," + X
				+ " ORDER BY "+ Z + "," + X;
				
		ResultSet rows = queryExecutor.query(sql);
		String z = "" ;
		int i = -1;
		int j = 0;
		
		while(rows.next()){		
			if(!z.equals(rows.getString(Z))){
				i++;
				allZs.add(i,rows.getString(Z));
				result.add(i, new ArrayList<>());
				z = rows.getString(Z);
				j = 0 ;
			}
			double[] row = {rows.getDouble(X),rows.getDouble("AVG")};
			result.get(i).add(j,row);
			j++;
		}
		return toArrayListDouble(result);
	}
	
	/*Extract the pattern from the shape query*/
	public static String[] getPatternFromShapeQuery(ShapeQuery shapeQuery){
		ArrayList<String> keywrds = new ArrayList<>();
		
		for(ShapeSegment shapeSegment : shapeQuery.shapeSegment){
			if(!shapeSegment.keyword.equals("")){
				keywrds.add(shapeSegment.keyword);
			}
		}
		
		String[] pattern = new String[keywrds.size()];
		for(int i = 0 ; i < pattern.length ; i++){
			pattern[i] = keywrds.get(i);
		}
		
		return pattern;
	}

	/*Returns a clone of data*/
	public static ArrayList<double[][]> cloneData(ArrayList<double[][]> originalData){
		ArrayList<double[][]> clone = new ArrayList<>();
		for(double[][] d : originalData){
		    double[][] result = new double[d.length][];
		    for (int r = 0; r < d.length; r++) {
		        result[r] = d[r].clone();
		    }
		    clone.add(result);
		}
		return clone;
	}
	
	/*Returns a normalized version of data*/
	public static ArrayList<double[][]> normalizeData(ArrayList<double[][]> originalData){
		for(double[][] single_data : originalData){
			double[] normalized = DataService.zNormalize(DataService.getColumn(single_data,2));
			for(int i = 0 ; i < single_data.length ; i++){
				 single_data[i][1] = normalized[i]; 
			}
		}
		return originalData;
	}

	/*Gives a slice of the data*/
	public static double[][] getPart(int start_idx , int end_idx , double[][] data){
		 double[][] result = new double[end_idx - start_idx + 1][2];
		 for(int i = start_idx ; i < end_idx + 1 ; i++ ){
			 result[i-start_idx][0] = data[i][0];
			 result[i-start_idx][1] = data[i][1];
		 }
		 return result;
	 } 
	 
	/*Returns the list of z values*/
	public static ArrayList<String> getZs(String Z , String tableName) throws SQLException{
		 DataService queryExecutor = new DataService();
		 ArrayList<String> result = new ArrayList<>();
		 String sql = "SELECT DISTINCT " + Z + " FROM " + tableName;
		 ResultSet rows = queryExecutor.query(sql);
		 while(rows.next()){
				result.add(rows.getString(1));
		}
		 return result;
	 }
	
	/*Returns the keywords in the pattern*/
	public static String[] toKeywords(String pattern){
		return (pattern.trim()).split("\\P{L}+"); 
	}
	
	
	public static ShapeQuery parser(String shapeQueryString) throws JsonParseException, JsonMappingException, IOException{
		ShapeQuery shapeQuery = new ShapeQuery();
		
		shapeQueryString = shapeQueryString.replaceAll("'", "\"");
		
		ArrayList<String[]> arrayOfShapeSegments = new ArrayList<>();
		
		ObjectMapper mapper = new ObjectMapper();
		String[][] big_array = mapper.readValue(shapeQueryString,String[][].class);
		
		for (int i = 0 ; i < big_array.length; i++){
			String[] small_array = big_array[i];
			String modifier = small_array[0];
			String pattern = small_array[1];
			String x_start = small_array[2];
			String x_end = small_array[4];
			String y_start = small_array[3];
			String y_end = small_array[5];
			String[] tuple = {modifier,pattern,x_start,x_end,y_start,y_end};
			arrayOfShapeSegments.add(tuple);
		}
		
		for(String[] shapeSegment : arrayOfShapeSegments){
			if(shapeSegment[1].equals("") && (shapeSegment[4].equals("") || shapeSegment[5].equals(""))){
				throw new IOException("---------------ERROR : INCOMPLETE INFORMATION---------------");
			}
			shapeQuery.shapeSegment.add(new ShapeSegment(shapeSegment[0], shapeSegment[1], shapeSegment[2], shapeSegment[3], shapeSegment[4], shapeSegment[5]));
		}
		
		return shapeQuery;
	}
		
	/*Converts  array of double to array of strings*/
	public static ArrayList<String> doubleToString(double[] data){
		ArrayList<String> dataString = new ArrayList<>();
		for(int i = 0 ; i < data.length ; i++){
			dataString.add(i,Double.toString(data[i]));
		}
		return dataString;
	}
	
	/*Returns column "index_column" of data*/
	public static double[] getColumn(double[][] data , int index_column){
		double[] column = new double[data.length];
		for(int i = 0 ; i < data.length ; i++){
			column[i]  = data[i][index_column-1];
		}
		return column;
	}
	
	/*Converts ArrayList<ArrayList<double[]>> to ArrayList<double[][]>*/
	public static ArrayList<double[][]> toArrayListDouble(ArrayList<ArrayList<double[]>> list){
		ArrayList<double[][]> result = new ArrayList<>();
		for(int i = 0 ; i < list.size() ; i++){
			double[][] data = new double[list.get(i).size()][2];
			for(int j = 0 ; j < data.length ; j++){
				data[j][0] = list.get(i).get(j)[0];
				data[j][1] = list.get(i).get(j)[1];
			}
			result.add(i,data);
		}
		return result;
	}
	
	/*Computes mean of data*/
	public static double getMean(double data[]){
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.length;
    }
	
	/*Computes variance of data*/
	public static double getVariance(double data[]){
        double mean = getMean(data);
        double tmp = 0;
        for(double a :data)
            tmp += (a-mean)*(a-mean);
        return tmp/(data.length);
    }
	
	/*Computes standard deviation of data*/
	public static double getStdDev(double data[]){
        return Math.sqrt(getVariance(data));
    }
	
	/*Normalize a single point*/
	public static double singleNormalize(double x , double data[]){
		double mean = getMean(data);
		double sigma = getStdDev(data);
		
		if(sigma == 0){
			return 0 ;
		}else{
			return (x-mean)/sigma;
		}
	}
	
	/*zNormalizes the data*/
	public static double[] zNormalize(double data[]){
		
		double[] result = new double[data.length];
		double mu = getMean(data);
		double sigma = getStdDev(data);
		for(int i = 0 ; i < data.length ; i++){
			if(sigma == 0){
				result[i] = 0 ;
			}else{
				result[i] = (data[i]-mu)/sigma;
			}
		}
		return result;
	}
	
	/*Computes the range of some data (max value - min value)*/
	public static double findRange(double[] data){
		double max = Double.NEGATIVE_INFINITY;
		double min = Double.POSITIVE_INFINITY;
	
		for(int i = 0; i < data.length; i++){
			if(data[i] < min){
				min = data[i];
			}
		    if(data[i] > max){
		    	max = data[i];
		    }
		}
		return max-min;
	}
	
}