package edu.ruc.visualization.hdfs.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.SparkSession;

import edu.ruc.visualization.data.Metafilelocation;
import edu.ruc.visualization.data.Metatable;
import edu.ruc.visualization.data.Rowobject;

public class SysConfig {
	public static String Catalog_Project = "G:/zenvisage-0.1/src/main/resources/";
	public static SparkSession Spark_Process = null;

	public static final String Catalog_Cashe = "/hohai/cashe/";
	public static final String Catalog_Common = "/hohai/common/";
	public static final String Catalog_Data = "/hohai/data/";
	public static final String Catalog_Parquet = "/hohai/parquet/";
	public static final String Catalog_Header = "/hohai/header/";
	public static final String Catalog_Sql = "/hohai/sql/";
	public static final String Catalog_Test = "/hohai/test/";

	public static final String RowObject = "visualization_rowobject";
	public static final String METATABLE = "visualization_metatable";
	public static final String METAFILELOCATION = "visualization_metafilelocation";

	public static List<Metafilelocation> MetafilelocationDataset = new ArrayList<Metafilelocation>(); // Metafilelocation
	public static List<Metatable> MetatableDataset = new ArrayList<Metatable>(); // Metatable
	public static List<Rowobject> RowObjectList = new ArrayList<Rowobject>(); // RowObject

	public static String CurAttribute = "";
	public static String CurType = "";
	public static String[] Split_Attribute;
	public static String[] Split_Type;
	public static Metafilelocation CurMetafilelocation = new Metafilelocation();
	public static Metatable CurMetatable = new Metatable();
	public static Map<String, String> Map_Cashe_Sql = new HashMap<String, String>();
}
