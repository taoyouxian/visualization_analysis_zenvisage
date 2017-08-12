package edu.ruc.visualization.hdfs.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.ruc.visualization.hdfs.common.SysConfig;

public class InitDataListenerTest {

	private static Logger log = LoggerFactory.getLogger(InitServiceI.class);

	private static ApplicationContext ctx = null;

	public static void main(String[] args) {
		ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		InitServiceI initService = (InitServiceI) ctx
				.getBean("demoInitService");
		try {
			initService.init();
			log.debug("MetafilelocationDataset Size: {}",
					SysConfig.MetafilelocationDataset.size());
			log.debug("MetatableDataset Size: {}",
					SysConfig.MetatableDataset.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
