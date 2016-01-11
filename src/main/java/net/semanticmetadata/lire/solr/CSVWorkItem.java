package net.semanticmetadata.lire.solr;

import java.awt.image.BufferedImage;

import lombok.Data;
import net.semanticmetadata.lire.indexing.parallel.WorkItem;

/**
 * This file is part of LIRE Solr, a Java library for content based image retrieval.
 */

@Data
public class CSVWorkItem extends WorkItem {
	
	private String id;
	
	private String productId;
	
	private String category;
	
	private String brand;
	
	private String company;
	
	public CSVWorkItem(String id, String fileName, BufferedImage image) {
		super(fileName, image);
		this.id = id;
	}

	public CSVWorkItem(String id, String fileName, byte[] buffer) {
		super(fileName, buffer);
		this.id = id;
	}
	
	public CSVWorkItem(String id, String fileName, BufferedImage image, String productId, String category, String brand, String company) {
		super(fileName, image);
		this.id = id;
		this.productId = productId;
		this.category = category;
		this.brand = brand;
		this.company = company;
	}
	
	public CSVWorkItem(String id, String fileName, byte[] buffer, String productId, String category, String brand, String company) {
		super(fileName, buffer);
		this.id = id;
		this.productId = productId;
		this.category = category;
		this.brand = brand;
		this.company = company;
	}
	
}
