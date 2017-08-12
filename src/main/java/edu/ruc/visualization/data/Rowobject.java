package edu.ruc.visualization.data;

public class Rowobject {

	private String tablename;
	private String attribute;
	private String type;

	public Rowobject() {
		super();
	}

	public Rowobject(String tablename, String attribute, String type) {
		super();
		this.tablename = tablename;
		this.attribute = attribute;
		this.type = type;
	}

	public String getTablename() {

		return tablename;
	}

	public void setTablename(String tablename) {

		this.tablename = tablename;
	}

	public String getAttribute() {

		return attribute;
	}

	public void setAttribute(String attribute) {

		this.attribute = attribute;
	}

	public String getType() {

		return type;
	}

	public void setType(String type) {

		this.type = type;
	}

	@Override
	public String toString() {
		return "Rowobject [tablename=" + tablename + ", attribute=" + attribute
				+ ", type=" + type + "]";
	}

}