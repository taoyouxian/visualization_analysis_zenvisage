package edu.ruc.visualization.hdfs.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitDataListener implements ServletContextListener {

	private static Logger log = LoggerFactory.getLogger(InitServiceI.class);

	private static ApplicationContext ctx = null;

	public InitDataListener() {
	}

	@Override
	public void contextDestroyed(ServletContextEvent evt) {
		log.debug("销毁spark进程");
	}

	@Override
	public void contextInitialized(ServletContextEvent evt) {
		log.debug("加载数据");
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
	}

}
