package net.semanticmetadata.lire.solr.bean;

import lombok.Data;

@Data
public class LireDocument {
	private String company;
	private String product_id;
	private String category;
	private String brand;
	private String title;
	private int id;
	private double d;
}
