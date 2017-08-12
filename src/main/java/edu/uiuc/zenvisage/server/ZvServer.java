package edu.uiuc.zenvisage.server;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import edu.ruc.visualization.data.Metafilelocation;
import edu.ruc.visualization.data.Metatable;
import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.service.SysMain;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.uiuc.zenvisage.service.ZvMain;

public class ZvServer {

	private static Logger log = LoggerFactory.getLogger(ZvServer.class);
	private Server server;
	private static int port = 8080;

	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws Exception {
		server = new Server(port);  
		
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("G:/zenvisage-0.1/target/zenvisage.war");
		webAppContext.setParentLoaderPriority(true);
		webAppContext.setServer(server);
		webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
		webAppContext.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webAppContext);
		server.start();
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
//		createMetaTables();
		ZvServer zvServer = new ZvServer();
//		zvServer.loadDemoDatasets();
		zvServer.start();
	}

	public static void createMetaTables() throws SQLException {
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		try {
			if (!hdfsUtil.isTableExists(SysConfig.METATABLE)) {
				log.debug(SysConfig.METATABLE + " create");
				String createMetaTableSQL = SysConfig.Catalog_Common
						+ SysConfig.METATABLE;
				hdfsUtil.createTable(createMetaTableSQL);
			}
		} catch (IOException e) {
			log.debug(SysConfig.METATABLE + " error: {}", e.getMessage());
		}
		try {
			if (!hdfsUtil.isTableExists(SysConfig.METAFILELOCATION)) {
				log.debug(SysConfig.METAFILELOCATION + " create");
				String createMetaFileLocationSQL = SysConfig.Catalog_Common
						+ SysConfig.METAFILELOCATION;
				hdfsUtil.createTable(createMetaFileLocationSQL);
			}
		} catch (IOException e) {
			log.debug(SysConfig.METAFILELOCATION + " error: {}", e.getMessage());
		}
	}

	// public static void createMetaTables() throws SQLException {
	// SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
	// if (!sqlQueryExecutor.isTableExists(metatable)) {
	// String dropPublicSchemaSQL = "DROP schema public cascade;";
	// String createPublicSchemaSQL = "CREATE schema public;";
	// String createMetaTableSQL =
	// "CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT, min FLOAT, max FLOAT);";
	// sqlQueryExecutor.executeUpdate(dropPublicSchemaSQL);
	// sqlQueryExecutor.executeUpdate(createPublicSchemaSQL);
	// sqlQueryExecutor.createTable(createMetaTableSQL);
	// }
	//
	// if (!sqlQueryExecutor.isTableExists(metafilelocation)) {
	// String createMetaFileLocationSQL =
	// "CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);";
	// sqlQueryExecutor.createTable(createMetaFileLocationSQL);
	// }
	// }

	public void loadDemoDatasets() throws SQLException, IOException,
			InterruptedException {
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		SysMain sysMain = SysMain.getSysMain();

		Metafilelocation metafilelocation = new Metafilelocation();

		metafilelocation.setDatabase("real_estate"); // real_estate
		File file = new File(this.getClass().getClassLoader()
				.getResource(("data/real_estate.csv")).getFile());
		metafilelocation.setCsvfilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "real_estate.csv");
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/real_estate.txt")).getFile());
		metafilelocation.setMetafilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "real_estate.txt");
		SysConfig.MetafilelocationDataset.add(metafilelocation);
		sysMain.uploadDatasettoHDFS(metafilelocation, false);
		metafilelocation = new Metafilelocation();

		metafilelocation.setDatabase("weather"); // weather
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/weather.csv")).getFile());
		metafilelocation.setCsvfilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "weather.csv");
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/weather.txt")).getFile());
		metafilelocation.setMetafilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "weather.txt");
		SysConfig.MetafilelocationDataset.add(metafilelocation);
		sysMain.uploadDatasettoHDFS(metafilelocation, false);
		metafilelocation = new Metafilelocation();

		metafilelocation.setDatabase("flights"); // flights
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/flights.csv")).getFile());
		metafilelocation.setCsvfilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "flights.csv");
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/flights.txt")).getFile());
		metafilelocation.setMetafilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "flights.txt");
		SysConfig.MetafilelocationDataset.add(metafilelocation);
		sysMain.uploadDatasettoHDFS(metafilelocation, false);
		metafilelocation = new Metafilelocation();

		metafilelocation.setDatabase("cmu"); // cmu
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/cmu.csv")).getFile());
		metafilelocation.setCsvfilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "cmu.csv");
		file = new File(this.getClass().getClassLoader()
				.getResource(("data/cmu.txt")).getFile());
		metafilelocation.setMetafilelaction(file.getAbsolutePath());
		hdfsUtil.upFile(file.getAbsolutePath(), SysConfig.Catalog_Data
				+ "cmu.txt");
		SysConfig.MetafilelocationDataset.add(metafilelocation);
		sysMain.uploadDatasettoHDFS(metafilelocation, false);
		metafilelocation = new Metafilelocation();

		sysMain.uploadDatatoHDFS();

		// ZvMain.uploadDatasettoDB(dataset1,false);
		// ZvMain.uploadSysConfig.datasettoDB(SysConfig.dataset2,false);
		// ZvMain.uploadSysConfig.datasettoDB(SysConfig.dataset3,false);
		// ZvMain.uploadSysConfig.datasettoDB(SysConfig.dataset4,false);
	}

}
