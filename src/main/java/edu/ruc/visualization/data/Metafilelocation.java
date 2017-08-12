package edu.ruc.visualization.data;

public class Metafilelocation {

	private String database;
	private String metafilelaction;
	private String csvfilelaction;

	public Metafilelocation(String database, String metafilelaction,
			String csvfilelaction) {
		super();
		this.database = database;
		this.metafilelaction = metafilelaction;
		this.csvfilelaction = csvfilelaction;
	}

	public Metafilelocation() {
		super();
	}

	@Override
	public String toString() {
		return "Metafilelocation [database=" + database + ", metafilelaction="
				+ metafilelaction + ", csvfilelaction=" + csvfilelaction + "]";
	}

	public String getDatabase() {

		return database;
	}

	public void setDatabase(String database) {

		this.database = database;
	}

	public String getMetafilelaction() {

		return metafilelaction;
	}

	public void setMetafilelaction(String metafilelaction) {

		this.metafilelaction = metafilelaction;
	}

	public String getCsvfilelaction() {

		return csvfilelaction;
	}

	public void setCsvfilelaction(String csvfilelaction) {

		this.csvfilelaction = csvfilelaction;
	}

}