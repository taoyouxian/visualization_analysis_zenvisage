package edu.ruc.visualization.hdfs.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ruc.visualization.hdfs.common.SysConfig;

public class ParquetUtils {

	private static Logger log = LoggerFactory.getLogger(ParquetUtils.class);

	public static String attributes = "class,ip,ea,vis,bp,fp,li,lio2,o2m,o2";
	public static String types = "string,float,float,float,float,float,float,float,float,float";

	public static boolean index_flag = false;

	public static void main(String[] args) throws IOException {
		String tablename = "cmu_clean";
		SysConfig.CurType = types;
		SysConfig.CurAttribute = attributes;
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		if (hdfsUtil.isTableExists(SysConfig.Catalog_Parquet + tablename)) {
			hdfsUtil.delFile(SysConfig.Catalog_Parquet + tablename);
		} else {

		}
		SysConfig.Spark_Process = SparkSession.builder().appName("a").config("spark.some.config.option", "some-value")
				.getOrCreate();
		ParquetUtils.csvFileToHDFSParquet(tablename, SysConfig.CurAttribute, SysConfig.CurType);
		SysConfig.Spark_Process.stop();
	}

	@SuppressWarnings("serial")
	public static void csvFileToHDFSParquet(String tablename, String attribute, String type) {
		// init default value
		SysConfig.CurAttribute = "";
		SysConfig.CurType = "";
		SysConfig.CurAttribute = attribute;
		SysConfig.CurType = type;

		String csvPath = SysConfig.Catalog_Data + tablename + ".csv";
		List<StructField> fields = new ArrayList<StructField>();
		StructField field = new StructField();
		String[] Split_Attribute = SysConfig.CurAttribute.split(",");
		SysConfig.Split_Type = SysConfig.CurType.split(",");
		int i = 0;
		for (String fieldName : Split_Attribute) {
			if (SysConfig.Split_Type[i].equals("string") || SysConfig.Split_Type[i].equals("timestamp")) {
				field = DataTypes.createStructField(fieldName, DataTypes.StringType, true);
			} else if (SysConfig.Split_Type[i].equals("float") || SysConfig.Split_Type[i].equals("double")) {
				field = DataTypes.createStructField(fieldName, DataTypes.FloatType, true);
			} else if (SysConfig.Split_Type[i].equals("int")) {
				field = DataTypes.createStructField(fieldName, DataTypes.IntegerType, true);
			}
			fields.add(field);
			i++;
		}
		StructType schema = DataTypes.createStructType(fields);
		JavaRDD<String> jRDD = SysConfig.Spark_Process.read().textFile(csvPath).toJavaRDD()
				.filter(new Function<String, Boolean>() {
					public Boolean call(String s) throws Exception {
						if (!index_flag) {
							index_flag = true;
							return false;
						}
						return true;
					}
				});

		JavaRDD<Row> javaRDD = jRDD.map(new Function<String, Row>() {
			public Row call(String s) throws Exception {
				String[] attributes = s.split(",");
				Object[] o = new Object[attributes.length];
				// if (index_flag) {
				int i = 0;
				for (String value : attributes) {
					if (SysConfig.Split_Type[i].equals("string") || SysConfig.Split_Type[i].equals("timestamp")) {
						o[i] = value.trim();
					} else if (SysConfig.Split_Type[i].equals("float") || SysConfig.Split_Type[i].equals("double")) {
						o[i] = Float.valueOf(value.trim());
					} else if (SysConfig.Split_Type[i].equals("int")) {
						o[i] = Integer.valueOf(value.trim());
					}
					i++;
				}
				return RowFactory.create(o);
				// } else {
				// index_flag = true;
				// return RowFactory.create(o);
				// }
			}
		});
		index_flag = false;

		Dataset<Row> dataFrame = SysConfig.Spark_Process.createDataFrame(javaRDD, schema);
		try {
			dataFrame.write().parquet(SysConfig.Catalog_Parquet + tablename);
		} catch (Exception er) {
			System.out.println("Error Info: " + er.getMessage());
		}
	}
}
