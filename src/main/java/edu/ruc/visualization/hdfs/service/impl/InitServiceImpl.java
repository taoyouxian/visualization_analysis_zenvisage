package edu.ruc.visualization.hdfs.service.impl;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PreDestroy;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;

import edu.ruc.visualization.data.Metafilelocation;
import edu.ruc.visualization.data.Metatable;
import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.service.InitServiceI;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.ruc.visualization.utils.FileUtil;

@Service("demoInitService")
public class InitServiceImpl implements InitServiceI {

	@SuppressWarnings("unchecked")
	synchronized public void init() throws IOException {

		HdfsUtil hUtil = HdfsUtil.getHdfsUtil();
		String aJson = "";
		if (SysConfig.MetafilelocationDataset.size() == 0) {
			aJson = hUtil.readContent(SysConfig.Catalog_Common
					+ SysConfig.METAFILELOCATION);
			SysConfig.MetafilelocationDataset = JSON.parseArray(aJson,
					Metafilelocation.class);
		}
		if (SysConfig.MetatableDataset.size() == 0) {
			aJson = hUtil.readContent(SysConfig.Catalog_Common
					+ SysConfig.METATABLE);
			SysConfig.MetatableDataset = JSON
					.parseArray(aJson, Metatable.class);
		}

		aJson = FileUtil
				.readFile(SysConfig.Catalog_Project + "cashe/cashe.txt");
		if (aJson.length() > 0)
			SysConfig.Map_Cashe_Sql = (Map<String, String>) JSON.parse(aJson);

		hUtil.copyDirectory(SysConfig.Catalog_Project + "sql",
				SysConfig.Catalog_Sql);

		hUtil.copyFile(SysConfig.Catalog_Project + "cashe/cashe.txt",
				SysConfig.Catalog_Cashe + "cashe.txt");
	}

	/**
	 * 在web结束时执行
	 */
	@PreDestroy
	public void applicationEnd() {
		if (SysConfig.Spark_Process != null)
			SysConfig.Spark_Process.stop();
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		String aJson = FileUtil.readFile(SysConfig.Catalog_Project
				+ "cashe/cashe.txt");
		if (aJson.length() > 0)
			SysConfig.Map_Cashe_Sql = (Map<String, String>) JSON.parse(aJson);
	}
}
