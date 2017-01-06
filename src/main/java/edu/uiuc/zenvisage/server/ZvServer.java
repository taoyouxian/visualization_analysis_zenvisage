package edu.uiuc.zenvisage.server;
import java.sql.SQLException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;

public class ZvServer {

	private Server server;
	private static int port = 8080;
	private static String metatable="zenvisage_metatable";
	private static String metafilelocation="zenvisage_metafilelocation";
	

	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws Exception {	
		server = new Server(port);
		
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("zenvisage.war");
		webAppContext.setParentLoaderPriority(true);
		webAppContext.setServer(server);
		webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
		webAppContext.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webAppContext);	
		server.start();
//		ZvMain zvMain = (ZvMain) SpringApplicationContext.getBean("zvMain");
//		zvMain.loadData();
	
	}
	
	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		createMetaTables();
		loadDemoDatasets();
		ZvServer zvServer = new ZvServer();
		zvServer.start();	
	}
	
	public  static void createMetaTables() throws SQLException{
		SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();
		if(!sqlQueryExecutor.isTableExists(metatable)){
			String dropPublicSchemaSQL = "DROP schema public cascade;";
			String createPublicSchemaSQL = "CREATE schema public;";
			String createMetaTableSQL = "CREATE TABLE zenvisage_metatable (tablename TEXT,attribute TEXT, type TEXT);";
			sqlQueryExecutor.executeUpdate(dropPublicSchemaSQL);
			sqlQueryExecutor.executeUpdate(createPublicSchemaSQL);
			sqlQueryExecutor.createTable(createMetaTableSQL);			
		}
		
		if(!sqlQueryExecutor.isTableExists(metafilelocation)){
			String createMetaFileLocationSQL ="CREATE TABLE zenvisage_metafilelocation (database TEXT, metafilelocation TEXT, csvfilelocation TEXT);";
			sqlQueryExecutor.createTable(createMetaFileLocationSQL);		
		}
	}
	
	
	public static void loadDemoDatasets(){
		
	}

}
