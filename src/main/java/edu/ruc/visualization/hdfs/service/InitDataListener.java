package edu.ruc.visualization.hdfs.service;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.ruc.visualization.hdfs.common.SysConfig;

public class InitDataListener implements ServletContextListener {

	private static Logger log = LoggerFactory.getLogger(InitServiceI.class);

	private static ApplicationContext ctx = null;

	public InitDataListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		log.debug("销毁spark进程");
		if (SysConfig.Spark_Process != null)
			SysConfig.Spark_Process.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		log.debug("加载数据");
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		InitServiceI initService = (InitServiceI) ctx
				.getBean("demoInitService");
		try {
			initService.init();
			// File file = new File(this.getClass().getClassLoader()
			// .getResource(("applicationContext.xml")).getFile());
			// SysConfig.Catalog_Project = file.getAbsolutePath().replace(
			// "applicationContext.xml", "");
			SysConfig.Spark_Process = SparkSession.builder()
					.appName("spark_visualization")
					.config("spark.some.config.option", "some-value")
					.getOrCreate();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
