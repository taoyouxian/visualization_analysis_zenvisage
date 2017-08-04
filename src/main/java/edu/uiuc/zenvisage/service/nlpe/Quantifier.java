package edu.uiuc.zenvisage.service.nlpe;

public class Quantifier {

	private String type;		// change this enum
	private int minValue;			// inclusive
	private int maxValue;			// inclusive
	private int equalValue;			// to be used in when quantifier is exactly.
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	public int getEqualValue() {
		return equalValue;
	}
	public void setEqualValue(int equalValue) {
		this.equalValue = equalValue;
	}
	
}
