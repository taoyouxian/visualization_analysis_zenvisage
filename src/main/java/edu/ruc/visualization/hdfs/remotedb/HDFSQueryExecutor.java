package edu.ruc.visualization.hdfs.remotedb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.spark.sql.Row;

import com.alibaba.fastjson.JSON;

import edu.ruc.visualization.data.Metatable;
import edu.ruc.visualization.hdfs.common.SysConfig;
import edu.ruc.visualization.hdfs.server.SysServer;
import edu.ruc.visualization.hdfs.utils.HdfsUtil;
import edu.uiuc.zenvisage.data.remotedb.Points;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.zqlcomplete.executor.Constraints;
import edu.uiuc.zenvisage.zqlcomplete.executor.ZQLRow;

/**
 * PostgreSQL database connection portal for my local machine need to change to
 * in general
 *
 */
public class HDFSQueryExecutor {

	private VisualComponentList visualComponentList;

	/* This is the main ZQL->SQLExcecution query */
	public void ZQLQueryEnhanced(ZQLRow zqlRow, String databaseName)
			throws SQLException, IOException {
		String sql = null;

		databaseName = databaseName.toLowerCase();
		String z = zqlRow.getZ().getAttribute().toLowerCase()
				.replaceAll("'", "").replaceAll("\"", "");
		String agg = zqlRow.getViz().getVariable().toLowerCase()
				.replaceAll("'", "").replaceAll("\"", "");

		// support list of x, y values, general all possible x,y combinations,
		// generate sql
		int xLen = zqlRow.getX().getAttributes().size();
		int yLen = zqlRow.getY().getAttributes().size();

		this.visualComponentList = new VisualComponentList();
		this.visualComponentList
				.setVisualComponentList(new ArrayList<VisualComponent>());

		// clean Y attributes to use for query
		// if we have y1<-{'soldprice','listingprice'
		// this would build agg(soldprice),agg(listingprice),
		StringBuilder build = new StringBuilder();
		for (int j = 0; j < yLen; j++) {
			String cleanY = zqlRow.getY().getAttributes().get(j).toLowerCase()
					.replaceAll("'", "").replaceAll("\"", "");
			build.append(agg);
			build.append("(");
			build.append(cleanY);
			build.append(")");
			build.append(",");
		}
		// remove extra ,
		build.setLength(build.length() - 1);

		for (int i = 0; i < xLen; i++) {
			String x = zqlRow.getX().getAttributes().get(i).toLowerCase()
					.replaceAll("'", "").replaceAll("\"", "");

			// zqlRow.getConstraint() has replaced the whereCondiditon
			if (zqlRow.getConstraint() == null
					|| zqlRow.getConstraint().size() == 0) {
				sql = "SELECT " + z + "," + x + " ," + build.toString()
						// zqlRow.getViz() should replace the avg() function
						+ " FROM " + databaseName + " GROUP BY " + z + ", " + x
						+ " ORDER BY " + x;
			} else {

				sql = "SELECT " + z + "," + x + " ," + build.toString()
						+ " FROM " + databaseName + " WHERE "
						+ appendConstraints(zqlRow.getConstraint())
						// zqlRow.getConstraint() has replaced the
						// whereCondiditon
						+ " GROUP BY " + z + ", " + x + " ORDER BY " + x;
			}

			System.out.println("Running ZQL Query :" + sql);
			// excecute sql and put into VisualComponentList
			executeSQL(sql, zqlRow, databaseName, x, zqlRow.getY()
					.getAttributes());
		}

		/* Testing below */
		// System.out.println("Printing Visual Groups:\n" +
		// this.visualComponentList.toString());
	}

	public void executeSQL(String sql, ZQLRow zqlRow, String databaseName,
			String x, List<String> yAttributes) throws SQLException,
			IOException {
		SysServer sysServer = SysServer.getSysServer();
		System.out.println("before execute");
		List<Row> rsList = sysServer.executeQuery(sql, databaseName);
		System.out.println("after execute");

		WrapperType zValue = null;
		ArrayList<WrapperType> xList = null;
		ArrayList<WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;

		String zType = null, xType = null, yType = null;
		System.out.println("before loop");
		// Since we do not order by Z, we need a hashmap to keep track of all
		// the visualcomponents
		// Since X is sorted though, the XList and YList are sorted correctly
		HashMap<String, List<VisualComponent>> vcMap = new HashMap<String, List<VisualComponent>>();
		for (int j = 0; j < rsList.size(); j++) {
			Row rs = rsList.get(j);
			if (zType == null)
				zType = getMetaType(zqlRow.getZ().getAttribute().toLowerCase(),
						databaseName);
			if (xType == null)
				xType = getMetaType(x, databaseName);
			// uses the x and y that have extra stuff like '' removed
			String zStr = rs.getString(0);
			List<VisualComponent> vcList = vcMap.get(zStr);
			// adding new x,y points to existing visual components

			if (vcList != null) {
				int rs_col_index = 2;
				// for loop populates vcList for a specific Z
				// So say we have x1<-{'year'} y1<-{'soldprice','listingprice'}
				// Z='state'.'CA'
				// vcList for CA: (year,soldprice) , (year, listingprice) (in
				// that exact order)
				for (int i = 0; i < yAttributes.size(); i++) {
					VisualComponent vc = vcList.get(i);
					vc.getPoints()
							.getXList()
							.add(new WrapperType(String.valueOf(rs.get(1)),
									xType));
					vc.getPoints()
							.getYList()
							.add(new WrapperType(String.valueOf(rs
									.get(rs_col_index))));
					// don't get individual y meta types -- let WrapperType
					// interpret the int, float, or string
					rs_col_index++;
				}
			} else {
				vcList = new ArrayList<VisualComponent>();
				int rs_col_index = 2;
				for (int i = 0; i < yAttributes.size(); i++) {
					String yAtribute = yAttributes.get(i);
					// don't get individual y meta types -- let WrapperType
					// interpret the int, float, or string
					xList = new ArrayList<WrapperType>();
					yList = new ArrayList<WrapperType>();
					xList.add(new WrapperType(String.valueOf(rs.get(1)), xType));
					yList.add(new WrapperType(String.valueOf(rs
							.get(rs_col_index))));
					tempVisualComponent = new VisualComponent(new WrapperType(
							zStr, zType), new Points(xList, yList), x,
							yAtribute);
					vcList.add(tempVisualComponent);
					rs_col_index++;
				}
				vcMap.put(zStr, vcList);
			}
		}
		// will be in some unsorted order (b/c hashmap), which is fine
		// what is important is that have all VCs for one pair of X,Y first,
		// then another pair of X,Y, and so on
		for (int i = 0; i < yAttributes.size(); i++) {
			for (List<VisualComponent> vcList : vcMap.values()) {
				this.visualComponentList.addVisualComponent(vcList.get(i));
			}
		}
		System.out.println("after loop");
	}

	/**
	 * @param constraint
	 * @return
	 */
	private String appendConstraints(List<Constraints> constraints) {
		String appendedConstraints = "";
		boolean flag = false;
		for (Constraints constraint : constraints) {
			if (flag) {
				appendedConstraints += " AND ";
			}
			appendedConstraints += constraint.toString();
			flag = true;
		}
		appendedConstraints += " ";

		return appendedConstraints;
	}

	public String getMetaType(String variable, String table)
			throws SQLException, IOException {
		Metatable m = new Metatable();
		boolean flag = false;
		for (Metatable metatable : SysConfig.MetatableDataset) {
			if (metatable.getTablename().equals(table)
					&& metatable.getAttribute().equals(variable)) {
				m = metatable;
				flag = true;
				break;
			}
		}
		if (flag)
			return m.getType();
		else
			return null;
	}

	public VisualComponentList getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(VisualComponentList visualComponentList) {
		this.visualComponentList = visualComponentList;
	}

}
