package edu.ruc.visualization.hdfs.remotedb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.parse.HiveParser.ifExists_return;
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

import edu.ruc.visualization.data.Metatable;
import edu.ruc.visualization.data.Rowobject;
import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.ruc.visualization.hdfs.utils.ParquetUtils;

public class SchemeToHDFS {

	static SchemeToHDFS _sHdfs = null;

	public static SchemeToHDFS getSchemeToHDFS() {
		if (_sHdfs == null) {
			_sHdfs = new SchemeToHDFS();
		}
		return _sHdfs;
	}

	private static Logger log = LoggerFactory.getLogger(SchemeToHDFS.class);

	public void schemeFileToHDFSStream(String filePath, String tablename,
			boolean overwrite) throws IOException {
		if (overwrite) {
			for (Metatable me : SysConfig.MetatableDataset) {
				if (me.getTablename().equals(tablename)) { 
					SysConfig.MetatableDataset.remove(me);
					break;
				}
			}
		}
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		String sCurrentLine;
		String attribute = "";
		String type = "";
		while ((sCurrentLine = br.readLine()) != null) {
			String split1[] = sCurrentLine.split(":");
			String split2[] = split1[1].split(",");
			Metatable metatable = new Metatable(tablename, split1[0]
					.toLowerCase().replaceAll("-", ""), split2[0]);
			attribute += split1[0].toLowerCase() + ",";
			type += split2[0].toLowerCase() + ",";
			SysConfig.MetatableDataset.add(metatable);
		}
		attribute = attribute.substring(0, attribute.length() - 1);
		type = type.substring(0, type.length() - 1);
		Rowobject rowobject = new Rowobject(tablename, attribute, type);
		if (overwrite) {
			for (Rowobject me : SysConfig.RowObjectList) {
				if (me.getTablename().equals(tablename)) { 
					SysConfig.RowObjectList.remove(me);
					break;
				}
			}
		}
		SysConfig.RowObjectList.add(rowobject);
		if (hdfsUtil.isTableExists(SysConfig.Catalog_Parquet + tablename)) {
			hdfsUtil.delFile(SysConfig.Catalog_Parquet + tablename);
		} else {

		}
		if (SysConfig.Spark_Process == null) {
			SysConfig.Spark_Process = SparkSession.builder()
					.appName("spark_temp")
					.config("spark.some.config.option", "some-value")
					.getOrCreate();
		}
		ParquetUtils.csvFileToHDFSParquet(tablename, attribute, type);
		br.close();
	}

	public static void main(String[] args) throws IOException {
		String tablename = "flight";
		SysConfig.CurAttribute = "year,month,day,weekday,carrier,origin,destination,arrivaldelay,departuredelay,weatherdelay,distance";
		SysConfig.CurType = "int,int,int,int,string,string,string,float,float,float,float";
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		if (hdfsUtil.isTableExists(SysConfig.Catalog_Parquet + tablename)) {
			hdfsUtil.delFile(SysConfig.Catalog_Parquet + tablename);
		} else {

		}
		SysConfig.Spark_Process = SparkSession.builder().appName("a")
				.config("spark.some.config.option", "some-value").getOrCreate();
		ParquetUtils.csvFileToHDFSParquet(tablename, SysConfig.CurAttribute,
				SysConfig.CurType);
		SysConfig.Spark_Process.stop();
	}
}
