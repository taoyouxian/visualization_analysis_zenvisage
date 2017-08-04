package edu.uiuc.zenvisage.service.nlpe;

public class Modifier {
	private String type;  // with or without quantifier
	private Quantifier q;
	private String nonquantifier;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Quantifier getQ() {
		return q;
	}
	public void setQ(Quantifier q) {
		this.q = q;
	}
	public String getNonquantifier() {
		return nonquantifier;
	}
	public void setNonquantifier(String nonquantifier) {
		this.nonquantifier = nonquantifier;
	}

}
