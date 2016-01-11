package net.semanticmetadata.lire.solr.filters;

public enum LireParams {
	URL("url"),
	FIELD("field"),
	FQ("fq"),
	FL("fl"),
	ROWS("rows"),
	START("start"),
	Query("q"),
	WT("wt");
	
	String param; 
	
	private LireParams(String param) {
		this.param = param;
	}
	
	public String getParam(){
		return this.param;
	}
}
