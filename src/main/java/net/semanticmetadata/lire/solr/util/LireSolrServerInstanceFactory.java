package net.semanticmetadata.lire.solr.util;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.XMLResponseParser;

public class LireSolrServerInstanceFactory {
	
	private static String baseSolrURL = "http://localhost:8984/solr/test";
	
	private static HttpSolrServer INSTANCE = null;
	
	public static HttpSolrServer getSolrServer(String solrUrl){
		if(INSTANCE != null){
			return INSTANCE;
		}
		if(solrUrl == null){
			INSTANCE = new HttpSolrServer(baseSolrURL);
			//INSTANCE.setParser(new QESXMLResponseParser());
			INSTANCE.setParser(new XMLResponseParser());
			//INSTANCE.set
		}else{
			INSTANCE = new HttpSolrServer(solrUrl);
		}
		return INSTANCE;
	}
	
	
}

class QESXMLResponseParser extends XMLResponseParser {
    public QESXMLResponseParser() {
        super();
    }

    @Override
    public String getContentType() {
        return "application/xml";
    }
}