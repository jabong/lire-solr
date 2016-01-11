package net.semanticmetadata.lire.solr.bean;

import java.util.List;

import lombok.Data;

@Data
public class LireSolrResponseBean {
	private String RawDocsCount;
	
	private List<LireDocument> docs;
}
