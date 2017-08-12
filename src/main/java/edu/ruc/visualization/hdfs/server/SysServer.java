package edu.ruc.visualization.hdfs.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.service.SysMain;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.ruc.visualization.utils.DateUtil;
import edu.ruc.visualization.utils.FileUtil;

public class SysServer {

	private static Logger log = LoggerFactory.getLogger(SysServer.class);

	static SysServer _sysServer = null;

	public static SysServer getSysServer() {
		if (_sysServer == null) {
			_sysServer = new SysServer();
		}
		return _sysServer;
	}

	@SuppressWarnings({ "resource", "unchecked" })
	public List<Row> executeQuery(String sql, String databaseName)
			throws IOException {
		// SparkSession spark = SparkSession.builder()
		// .appName("executeQuery_ReadParquet")
		// .config("spark.some.config.option", "some-value").getOrCreate();
		List<Row> parquetList = new ArrayList<Row>();
		SysMain sysMain = SysMain.getSysMain();
		String filename = null;
		if (SysConfig.Map_Cashe_Sql == null) {
			String aJson = FileUtil.readFile(SysConfig.Catalog_Project
					+ "cashe/cashe.txt");
			if (aJson.length() > 0)
				SysConfig.Map_Cashe_Sql = (Map<String, String>) JSON
						.parse(aJson);
		}
		if (!SysConfig.Map_Cashe_Sql.containsKey(sql)) {
			Dataset<Row> parquetDF = SysConfig.Spark_Process.read().parquet(
					SysConfig.Catalog_Parquet + databaseName);
			parquetDF.createOrReplaceTempView(databaseName);
			Dataset<Row> resDF = SysConfig.Spark_Process.sql(sql);
			parquetList = resDF.collectAsList(); 
			filename = DateUtil.mkTime(new Date());

			SysConfig.Map_Cashe_Sql.put(sql, filename);

			String sourcePath = SysConfig.Catalog_Project + "sql/" + filename
					+ ".txt";
			for (Row r : parquetList) {
				FileUtil.appendFile(
						r.toString().substring(1, r.toString().length() - 1),
						sourcePath);
				// System.out.println(r.toString());
				// log.debug("parquetList info: \n{}" + r.toString());
			}
			FileUtil.writeFile(JSON.toJSONString(SysConfig.Map_Cashe_Sql),
					SysConfig.Catalog_Project + "cashe/cashe.txt");

			sysMain.uploadCashetoHDFS(sourcePath, filename);
		} else {
			filename = SysConfig.Map_Cashe_Sql.get(sql);
			filename = SysConfig.Catalog_Project + "sql/" + filename + ".txt";
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					filename));
			String line;
			String[] terms;
			while ((line = bufferedReader.readLine()) != null) {
				terms = line.split(",");
				Row row = RowFactory.create(terms[0], terms[1], terms[2]);
				parquetList.add(row);
			}
		}
		// spark.stop();
		return parquetList;
	}

	public static void main(String[] args) throws IOException {
		SysServer server = SysServer.getSysServer();
		String sql = "SELECT origin, year, avg(arrivaldelay) as temp FROM flights where origin = 'EWR' GROUP BY origin, year ORDER BY year";
		String database = "flight";

		SysConfig.Spark_Process = SparkSession.builder().appName("a")
				.config("spark.some.config.option", "some-value").getOrCreate();
		// String sql =
		// "SELECT location, month, avg(temperature) as temp FROM weather where location = 'BRBRGTWN' GROUP BY location, month ORDER BY month";
		// String database = "weather";
		List<Row> parquetList = server.executeQuery(sql, database);
		String aJson = JSONArray.toJSONString(parquetList);
		log.debug("parquetList to json: {}", aJson);
	}
}
