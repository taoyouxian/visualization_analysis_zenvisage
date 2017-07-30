package edu.uiuc.zenvisage.service.nlpe;

public class NLPQuery {
	public String X;
	public String Y;
	public String Z;
	public String tableName;
	public String keywords;
	public double max_error;
	public int min_size;
	public int topK;
	
	//Constructor for args
	public NLPQuery (String X, String Y, String Z, String tableName, String keywords , int min_size , double max_error , int topK) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.tableName = tableName;
		this.keywords = keywords; 
		this.max_error = max_error;
		this.min_size = min_size;
		this.topK = topK;
	}
	
	// dummy constructor
	public NLPQuery(){
		
	}
	
	public String getX() {
		return X;
	}


	public void setX(String x) {
		X = x;
	}


	public String getY() {
		return Y;
	}


	public void setY(String y) {
		Y = y;
	}


	public String getZ() {
		return Z;
	}


	public void setZ(String z) {
		Z = z;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getKeywords() {
		return keywords;
	}

	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	
	public double getMax_error() {
		return max_error;
	}


	public void setMax_error(double max_error) {
		this.max_error = max_error;
	}


	public int getMin_size() {
		return min_size;
	}


	public void setMin_size(int min_size) {
		this.min_size = min_size;
	}


	public int getTopK() {
		return topK;
	}


	public void setTopK(int topK) {
		this.topK = topK;
	}

}
