package edu.ruc.visualization.hdfs.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.HashBiMap;

import edu.ruc.visualization.data.Metafilelocation;
import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.remotedb.HDFSQueryExecutor;
import edu.ruc.visualization.hdfs.remotedb.SchemeToHDFS;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.uiuc.zenvisage.data.Query;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.data.roaringdb.db.Database;
import edu.uiuc.zenvisage.model.BaselineQuery;
import edu.uiuc.zenvisage.model.Chart;
import edu.uiuc.zenvisage.model.ChartOutputUtil;
import edu.uiuc.zenvisage.model.FormQuery;
import edu.uiuc.zenvisage.model.Result;
import edu.uiuc.zenvisage.model.ZvQuery;
import edu.uiuc.zenvisage.server.UploadHandleServlet;
import edu.uiuc.zenvisage.service.Analysis;
import edu.uiuc.zenvisage.service.Outlier;
import edu.uiuc.zenvisage.service.Representative;
import edu.uiuc.zenvisage.service.Similarity;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.KMeans;
import edu.uiuc.zenvisage.service.distance.DTWDistance;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.service.distance.MVIP;
import edu.uiuc.zenvisage.service.distance.SegmentationDistance;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.LinearNormalization;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.Original;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryGraph;
import edu.uiuc.zenvisage.zqlcomplete.querygraph.ZQLParser;

public class SysMain {

	private static Logger log = LoggerFactory.getLogger(SysMain.class);

	private Result cachedResult = new Result();
	private BaselineQuery cachedQuery = new BaselineQuery();

	private Database inMemoryDatabase;

	public Analysis analysis;
	public Distance distance;
	public Normalization normalization;
	public Normalization outputNormalization;
	public PiecewiseAggregation paa;
	public ArrayList<List<Double>> data;
	public String databaseName;
	public String buffer = null;

	static SysMain _sysMain = null;

	public static SysMain getSysMain() {
		if (_sysMain == null) {
			_sysMain = new SysMain();
		}
		return _sysMain;
	}

	public void fileUpload(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, InterruptedException, SQLException {
		UploadHandleServlet uploadHandler = new UploadHandleServlet();
		List<String> names = uploadHandler.upload(request, response);

		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		SysMain sysMain = SysMain.getSysMain();
		Metafilelocation metafilelocation = new Metafilelocation();

		metafilelocation.setDatabase(names.get(0));
		metafilelocation.setCsvfilelaction(names.get(1));
		hdfsUtil.upFile(names.get(1), SysConfig.Catalog_Data + names.get(0) + ".csv");
		metafilelocation.setMetafilelaction(names.get(2));
		hdfsUtil.upFile(names.get(2), SysConfig.Catalog_Data + names.get(0) + ".txt");

		sysMain.uploadDatasettoHDFS(metafilelocation, true);

		sysMain.uploadNewDatatoHDFS();
	}

	public String getInterfaceFomData(String query) throws IOException, InterruptedException, SQLException {
		FormQuery fq = new ObjectMapper().readValue(query, FormQuery.class);
		this.databaseName = fq.getDatabasename();
		String locations[] = _sysMain.getMetaFileLocation(databaseName);
		inMemoryDatabase = new Database(this.databaseName, locations[0], locations[1], false);
		buffer = new ObjectMapper().writeValueAsString(inMemoryDatabase.getFormMetdaData());
		System.out.println(buffer);
		return buffer;
	}

	public String[] getMetaFileLocation(String databaseName) throws IOException {
		Metafilelocation m = new Metafilelocation();
		boolean flag = false;
		for (Metafilelocation metafilelocation : SysConfig.MetafilelocationDataset) {
			if (metafilelocation.getDatabase().equals(databaseName)) {
				m = metafilelocation;
				flag = true;
				break;
			}
		}
		if (flag)
			return new String[] { m.getMetafilelaction(), m.getCsvfilelaction() };
		else
			return null;
	}

	// uploadDatasettoDB
	public void uploadDatasettoHDFS(Metafilelocation metafilelocation, boolean overwrite) {
		SchemeToHDFS schemeToHDFS = SchemeToHDFS.getSchemeToHDFS();
		try {
			String databaseName = metafilelocation.getDatabase();
			schemeToHDFS.schemeFileToHDFSStream(metafilelocation.getMetafilelaction(), metafilelocation.getDatabase(),
					overwrite);
			if (overwrite) {
				for (Metafilelocation me : SysConfig.MetafilelocationDataset) {
					if (me.getDatabase().equals(databaseName)) {
						SysConfig.MetafilelocationDataset.remove(me);
						break;
					}
				}
				SysConfig.MetafilelocationDataset.add(metafilelocation);
			}

			new Database(metafilelocation.getDatabase(), metafilelocation.getMetafilelaction(),
					metafilelocation.getCsvfilelaction(), true);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String runDragnDropInterfaceQuerySeparated(String query, String method)
			throws InterruptedException, IOException, SQLException {
		ZvQuery args = new ObjectMapper().readValue(query, ZvQuery.class);
		Query q = new Query("query").setGrouby(args.groupBy + "," + args.xAxis).setAggregationFunc(args.aggrFunc)
				.setAggregationVaribale(args.aggrVar);
		if (method.equals("SimilaritySearch"))
			setFilter(q, args);
		System.out.println("Before SQL");
		HDFSQueryExecutor hdfsQueryExecutor = new HDFSQueryExecutor();
		hdfsQueryExecutor.ZQLQueryEnhanced(q.getZQLRow(), this.databaseName);
		System.out.println("After SQL");
		LinkedHashMap<String, LinkedHashMap<Float, Float>> output = hdfsQueryExecutor.getVisualComponentList()
				.toInMemoryHashmap();
		System.out.println("After To HashMap");
		output = cleanUpDataWithAllZeros(output);

		// setup result format
		Result finalOutput = new Result();
		finalOutput.method = method;
		ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, HashBiMap.create());

		// generate the corresponding distance metric
		if (args.distance_metric.equals("Euclidean")) {
			distance = new Euclidean();
		} else if (args.distance_metric.equals("Segmentation")) {
			distance = new SegmentationDistance();
		} else if (args.distance_metric.equals("MVIP")) {
			distance = new MVIP();
		} else {
			distance = new DTWDistance();
		}
		// generate the corresponding data normalization metric
		if (args.distanceNormalized) {
			// normalization = new LinearNormalization();
			normalization = new LinearNormalization();
			// normalization = new Original();
		} else {
			// normalization = new Zscore();
			normalization = new LinearNormalization();
		}
		// generate the corresponding output normalization

		outputNormalization = new Original();
		// reformat database data
		DataReformation dataReformatter = new DataReformation(normalization);
		double[][] normalizedgroups;

		System.out.println("Before Methods");
		// generate the corresponding analysis method
		if (method.equals("Outlier")) {
			normalizedgroups = dataReformatter.reformatData(output);
			Clustering cluster = new KMeans(distance, normalization, args);
			analysis = new Outlier(chartOutput, new Euclidean(), normalization, cluster, args);
		} else if (method.equals("RepresentativeTrends")) {
			normalizedgroups = dataReformatter.reformatData(output);
			Clustering cluster = new KMeans(distance, normalization, args);
			analysis = new Representative(chartOutput, new Euclidean(), normalization, cluster, args);
		} else if (method.equals("SimilaritySearch")) {
			// paa = new PiecewiseAggregation(normalization, args,
			// inMemoryDatabase); // O(1)

			if (args.considerRange) {
				double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args); // O(V*P)
				normalizedgroups = overlappedDataAndQueries[0];
				double[][] overlappedQuery = overlappedDataAndQueries[1];
				analysis = new Similarity(chartOutput, distance, normalization, args, dataReformatter, overlappedQuery);
			} else {
				normalizedgroups = dataReformatter.reformatData(output);
				double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange,
						normalizedgroups[0].length); // O(P)
				analysis = new Similarity(chartOutput, distance, normalization, paa, args, dataReformatter,
						interpolatedQuery);
			}

			((Similarity) analysis).setDescending(false);
		} else { // (method.equals("DissimilaritySearch"))
					// paa = new PiecewiseAggregation(normalization, args,
					// inMemoryDatabase);

			if (args.considerRange) {
				double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args);
				normalizedgroups = overlappedDataAndQueries[0];
				double[][] overlappedQuery = overlappedDataAndQueries[1];
				analysis = new Similarity(chartOutput, distance, normalization, args, dataReformatter, overlappedQuery);
			} else {
				normalizedgroups = dataReformatter.reformatData(output);
				double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange,
						normalizedgroups[0].length);
				analysis = new Similarity(chartOutput, distance, normalization, paa, args, dataReformatter,
						interpolatedQuery);
			}
			((Similarity) analysis).setDescending(true);
		}
		System.out.println("After Interpolation and normalization");

		analysis.compute(output, normalizedgroups, args);
		System.out.println("After Distance calulations");

		ObjectMapper mapper = new ObjectMapper();
		System.out.println("After Interpolation and normalization");
		String res = mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
		System.out.println("After mapping to output string");
		return res;
	}

	LinkedHashMap<String, LinkedHashMap<Float, Float>> cleanUpDataWithAllZeros(
			LinkedHashMap<String, LinkedHashMap<Float, Float>> output) {
		List<String> toRemove = new ArrayList<String>();
		for (String s : output.keySet()) {
			LinkedHashMap<Float, Float> v = output.get(s);
			int flag = 1;
			for (Float f : v.keySet()) {
				if (v.get(f) != 0) {
					flag = 0;
					break;
				}
			}
			if (flag == 1) {
				toRemove.add(s);
			}
		}
		for (String s : toRemove) {
			output.remove(s);
		}
		return output;
	}

	/**
	 * @param q
	 * @param arg
	 */
	public void setFilter(Query q, ZvQuery arg) {
		if (arg.predicateValue.equals(""))
			return;
		Query.Filter filter = new Query.FilterPredicate(arg.predicateColumn,
				Query.FilterOperator.fromString(arg.predicateOperator), arg.predicateValue);
		q.setFilter(filter);
	}

	public void uploadDatatoHDFS() throws IOException {
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		log.debug("Metafilelocation num: {}", SysConfig.MetafilelocationDataset.size());
		log.debug("Metatable num: {}", SysConfig.MetatableDataset.size());

		String aJson = JSONArray.toJSONString(SysConfig.MetafilelocationDataset);
		log.debug("Metafilelocation to json: {}", aJson);
		hdfsUtil.appendContent(aJson, SysConfig.Catalog_Common + SysConfig.METAFILELOCATION);

		aJson = JSONArray.toJSONString(SysConfig.MetatableDataset);
		log.debug("Metatable to json: {}", aJson);
		hdfsUtil.appendContent(aJson, SysConfig.Catalog_Common + SysConfig.METATABLE);

		aJson = JSONArray.toJSONString(SysConfig.RowObjectList);
		log.debug("RowObject to json: {}", aJson);
		hdfsUtil.createFile(SysConfig.Catalog_Common + SysConfig.RowObject, aJson);
	}

	public void uploadNewDatatoHDFS() throws IOException {
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();

		hdfsUtil.delFile(SysConfig.Catalog_Common + SysConfig.METAFILELOCATION);
		hdfsUtil.delFile(SysConfig.Catalog_Common + SysConfig.METATABLE);
		hdfsUtil.delFile(SysConfig.Catalog_Common + SysConfig.RowObject);

		String aJson = JSONArray.toJSONString(SysConfig.MetafilelocationDataset);
		hdfsUtil.createFile(SysConfig.Catalog_Common + SysConfig.METAFILELOCATION, aJson);

		aJson = JSONArray.toJSONString(SysConfig.MetatableDataset);
		hdfsUtil.createFile(SysConfig.Catalog_Common + SysConfig.METATABLE, aJson);

		aJson = JSONArray.toJSONString(SysConfig.RowObjectList);
		hdfsUtil.createFile(SysConfig.Catalog_Common + SysConfig.RowObject, aJson);
	}

	public void uploadCashetoHDFS(String sourcePath, String filename) throws IOException {
		HdfsUtil hdfsUtil = HdfsUtil.getHdfsUtil();
		hdfsUtil.copyFile(sourcePath, SysConfig.Catalog_Sql + filename + ".txt");

		hdfsUtil.copyFile(SysConfig.Catalog_Project + "cashe/cashe.txt", SysConfig.Catalog_Cashe + "cashe.txt");
	}

	/**
	 * 
	 * @param zqlQuery
	 *            Receives as a string the JSON format of a ZQLTable
	 * @return String representing JSON format of Result (output of running
	 *         ZQLTable through our query graph)
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String runQueryGraph(String zqlQuery) throws IOException, InterruptedException {
		System.out.println(zqlQuery);
		edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable zqlTable = new ObjectMapper().readValue(zqlQuery,
				edu.uiuc.zenvisage.zqlcomplete.executor.ZQLTable.class);
		ZQLParser parser = new ZQLParser();
		QueryGraph graph;
		try {
			graph = parser.processZQLTable(zqlTable);
			VisualComponentList output = edu.uiuc.zenvisage.zqlcomplete.querygraph.QueryGraphExecutor.execute(graph);
			// convert it into front-end format.
			String result = new ObjectMapper().writeValueAsString(convertVCListtoVisualOutput(output));
			// System.out.println(" Query Graph Execution Results Are:");
			// System.out.println(result);
			System.out.println("Done");
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public Result convertVCListtoVisualOutput(VisualComponentList vcList) {
		Result finalOutput = new Result();
		// VisualComponentList -> Result. Only care about the outputcharts. this
		// is for submitZQL
		for (VisualComponent viz : vcList.getVisualComponentList()) {
			Chart outputChart = new Chart();

			outputChart.setzType(viz.getzAttribute());
			outputChart.setxType(viz.getxAttribute());
			outputChart.setyType(viz.getyAttribute());
			outputChart.title = viz.getZValue().getStrValue();
			outputChart.setNormalizedDistance(viz.getScore());
			// outputChart.setxType((++i) + " : " +
			// viz.getZValue().getStrValue());
			// outputChart.setyType("avg" + "(" + viz.getyAttribute() + ")");
			// outputChart.title = "From Query Graph";

			for (WrapperType xValue : viz.getPoints().getXList()) {
				outputChart.xData.add(xValue.toString());
			}
			for (WrapperType yValue : viz.getPoints().getYList()) {
				outputChart.yData.add(yValue.toString());
			}
			finalOutput.outputCharts.add(outputChart);
		}
		return finalOutput;
	}

}
