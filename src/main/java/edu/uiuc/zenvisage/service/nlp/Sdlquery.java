package edu.uiuc.zenvisage.service.nlp;
/* @tarique
 * 
 */
// This is the query that we get from front-end.
public class Sdlquery {
	public String x;
	public String y;
	public String z;
	public String dataset;
	public String sdlsegments;
	public String sdltext;
    public String approach;
	
	
	
	public Sdlquery(){};
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}
	public String getZ() {
		return z;
	}
	public void setZ(String z) {
		this.z = z;
	}
	public String getSdlsegments() {
		return sdlsegments;
	}
	public void setSdlsegments(String sdlsegments) {
		this.sdlsegments = sdlsegments;
	}
	public String getSdltext() {
		return sdltext;
	}
	public void setSdltext(String sdltext) {
		this.sdltext = sdltext;
	}
	public String getDataset() {
		return dataset;
	}
	public void setDataset(String dataset) {
		this.dataset = dataset;
	}
	
	public String getApproach() {
		return approach;
	}
	public void setApproach(String approach) {
		this.approach = approach;
	}

}
