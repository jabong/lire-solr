package net.semanticmetadata.lire.solr.bean;

import java.util.List;

import lombok.Data;

@Data
public class LireSolrResponseExtendedBean {
	private String RawDocsCount;
	
	private List<LireDocumentExtended> docs;
}
