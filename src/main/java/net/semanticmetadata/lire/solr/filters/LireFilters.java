package net.semanticmetadata.lire.solr.filters;

public enum LireFilters {
	CATEGORY("category"),
	BRAND("brand"),
	COMPANY("company"),
	PRODUCT_ID("product_id");
	
	private String filter;

	private LireFilters(String filter) {
		this.filter = filter;
	}
	
	public String getFilter(){
		return filter;
	}
}