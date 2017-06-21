package edu.uiuc.zenvisage.service.nlp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/* data is data[n][2]*/
public class Data{
	private String database = "postgres";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "postgres";
	private String password = "zenvisage";
	Connection c = null;

	// Initialize connection
	public Data() {
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
	
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
	      ResultSet ret = stmt.executeQuery(sQLQuery);;
	      return ret;
	}
	
	/*Gets the data of a single z value from Postgres and stores it in a 2d array*/
	public static double[][]  fetchSingleData(String X , String Y ,String Z , String singleValue , String tableName , Data executor) throws SQLException, ClassNotFoundException{
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
		
//		String sql =  "SELECT Year,avg(SoldPrice) FROM real_estate WHERE City = 'Chicago' GROUP BY Year ORDER BY Year;";
//		System.out.println("Current city : "+singleValue);
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
		
		Data queryExecutor = new Data();
		
		
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
				result.add(i, new ArrayList<>());
				z = rows.getString(Z);
				j = 0 ;
			}
			double[] row = {rows.getDouble(X),rows.getDouble("AVG")};
			result.get(i).add(j,row);
			j++;
		}
		
		/*
		while(rows.next()){
			long tStart7 = System.currentTimeMillis();
			result.add(fetchSingleData(X,Y,Z,rows.getString(1),tableName , queryExecutor));
			long tEnd7 = System.currentTimeMillis();
			System.out.println("Elapsed time fetch single data "+(tEnd7-tStart7));
		}
		*/
		return toArrayListDouble(result);
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
		 Data queryExecutor = new Data();
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
		return pattern.split("\\s+"); // change to , 
	}
	
	/*Converts  array of double to array of strings*/
	public static ArrayList<String> doubleToString(double[] data){
		ArrayList<String> dataString = new ArrayList<>();
		for(int i = 0 ; i < data.length ; i++){
			dataString.add(i,Double.toString(data[i]));
		}
		return dataString;
	}
	
	/*Returns column: index_column of data*/
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
			/*System.out.println(list.get(i).size());*/
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
        return tmp/(data.length-1);
    }
	
	/*Computes standard deviation of data*/
	public static double getStdDev(double data[]){
        return Math.sqrt(getVariance(data));
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
	
}