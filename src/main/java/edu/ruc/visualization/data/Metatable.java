package edu.ruc.visualization.data;

public class Metatable {

	private String tablename;
	private String attribute;
	private String type;

	// private String min;
	// private String max;
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
		return "Metatable [tablename=" + tablename + ", attribute=" + attribute
				+ ", type=" + type + "]";
	}

	public Metatable(String tablename, String attribute, String type) {
		super();
		this.tablename = tablename;
		this.attribute = attribute;
		this.type = type;
	}

	public Metatable() {
		super();
	}

}