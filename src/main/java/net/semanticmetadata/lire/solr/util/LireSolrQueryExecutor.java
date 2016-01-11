package net.semanticmetadata.lire.solr.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.ClientHandlerException;

import net.semanticmetadata.lire.solr.bean.LireDocument;
import net.semanticmetadata.lire.solr.bean.LireSolrResponseBean;
import net.semanticmetadata.lire.solr.client.LireJerseyClient;
import net.semanticmetadata.lire.solr.filters.LireFilters;
import net.semanticmetadata.lire.solr.filters.LireParams;

/**
 * This util class is used to search the closed list of documents that match to a given image.
 * 
 * @author jabong
 *
 */
public class LireSolrQueryExecutor {
	public static String LIREQ = "lireq";
	
	/**
	 * This util method creates the lire query
	 * @param queryType lireq
	 * @param imgUrl url of image against which image matching would be executed
	 * @param rows number of rows in output
	 * @param field name of the algorithms used for matching (ce_ha,sc_ha....)
	 * @param filterMap filterQuery details
	 * @return
	 */
	public String createLireQuery(String queryType, String imgUrl, int rows, String field, Map<String, String> filterMap){
		StringBuilder queryBuilder = new StringBuilder();
		queryType = queryType != null ? queryType : LIREQ;
		queryBuilder.append("/").append(queryType).append("?");
		queryBuilder.append(LireParams.Query.getParam()).append("=").append("*:*");
		try{
			if(filterMap != null && !filterMap.isEmpty()){
				if(!validateFilterMaps(filterMap)){
					throw new RuntimeException("params not correct");
				}
				boolean isFirst = true;
				queryBuilder.append("&");
				queryBuilder.append(LireParams.FQ.getParam()).append("=");
				for(LireFilters lireFilter : LireFilters.values()){
					if(filterMap.get(lireFilter.getFilter()) != null){
						if(isFirst){
							isFirst = false;
						}else{
							queryBuilder.append(",");
						}
						queryBuilder.append(lireFilter.getFilter()).append(":").append(
								URLEncoder.encode(filterMap.get(lireFilter.getFilter()),"utf-8"));
					}
				}
			}
			
			if(imgUrl != null){
				queryBuilder.append("&");
				queryBuilder.append(LireParams.URL.getParam()).append("=").append(
						URLEncoder.encode(imgUrl, "utf-8"));
			}
			
			if(field != null){
				queryBuilder.append("&");
				queryBuilder.append(LireParams.FIELD.getParam()).append("=").append(
						URLEncoder.encode(field,"utf-8"));
			}
			
			if(rows == 0){
				rows = 10;
			}
			queryBuilder.append("&");
			queryBuilder.append(LireParams.ROWS.getParam()).append("=").append(rows);
			queryBuilder.append("&");
			queryBuilder.append(LireParams.WT.getParam()).append("=").append("json");
			queryBuilder.append("&");
			queryBuilder.append(LireParams.FL.getParam()).append("=").append("*");
			queryBuilder.append("&");
			queryBuilder.append(LireParams.START.getParam()).append("=").append("0");
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return queryBuilder.toString();
	}
	
	/**
	 * validating the filter Map given by the user if the filters are supported by the 
	 * lireRequestHandler. if not it throws run time exception
	 * @param filterMap
	 * @return
	 */
	private boolean validateFilterMaps(Map<String, String> filterMap) {
		for(String filter : filterMap.keySet()){
			if(!isPresent(filter)){
				return false;
			}
		}
		return true;
	}
	
	private boolean isPresent(String filter){
		for(LireFilters filterPermissible : LireFilters.values()){
			if(filter.equals(filterPermissible.getFilter())){
				return true;
			}
		}
		return false;
	}

	
	/**
	 * This is used to execute the lireQuery and gets the list of lire documents
	 * @param serverURL url of the solr server
	 * @param query query to be executed in the solr
	 * @return list of documents
	 */
	public List<LireDocument> executeQuery(String serverURL, String query){
		LireJerseyClient clientJ = new LireJerseyClient();
		String url = serverURL+query;
		String response = null;
		try{
			response = clientJ.getDataFromServer(url, "application/json", null, null);
		}catch(ClientHandlerException che){
			throw new ClientHandlerException(che);
		}
		return parseSolrDocumentList(response);
	}
	
	/**
	 * This util method creates the lire query and gives the list of lire document
	 * @param serverURL server where solr is hosted
	 * @param queryType lireq
	 * @param imgUrl url of image against which image matching would be executed
	 * @param rows number of rows in output
	 * @param field name of the algorithms used for matching (ce_ha,sc_ha....)
	 * @param filterMap filterQuery details
	 * @return list of LireDocument
	 */
	public List<LireDocument> createAndExecuteLireQuery(String serverURL, String queryType, String imgUrl, int rows, String field, Map<String, String> filterMap){
		String query = createLireQuery(queryType, imgUrl, rows, field, filterMap);
		return executeQuery(serverURL, query);
	}

	private List<LireDocument> parseSolrDocumentList(String response) {
		if(response == null){
			return null;
		}
		LireSolrResponseBean responseBean = new GsonBuilder().create().fromJson(response,
				new TypeToken<LireSolrResponseBean>(){}.getType());
		return responseBean.getDocs();
	}
	
	private List<LireDocument> executeQuery(String serverURL, SolrQuery query){
		HttpSolrServer server = LireSolrServerInstanceFactory.getSolrServer(serverURL);
		QueryResponse response = null;
		
		try {
			response = server.query(query);
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
		SolrDocumentList docList = response.getResults();
		NamedList<Object> result1 = response.getResponse();
		Object result2 = result1.get("docs");
		System.out.println(result2.getClass());
		NamedList<?> result = response.getResponseHeader();
		result.get("");
		SolrDocumentList docs = (SolrDocumentList) result1.get("response");
		System.out.println(result.getClass());
		Map<String, SolrDocumentList> list = response.getExpandedResults();
		Map<String, String> map1 = response.getExplainMap();
		return null;
	}
	
	private SolrQuery createQuery(String queryType, String imgUrl, int rows, String field, Map<String, String> filterMap){
		SolrQuery query = new SolrQuery("*:*");
		queryType = queryType != null ? queryType : LIREQ; 
		String imageURL = null;
		try {
			imageURL = URLEncoder.encode(imgUrl, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		query.setRequestHandler("/lireq");
		query.setFilterQueries(/*LireFilters.COMPANY+":"+filterMap.get(LireFilters.COMPANY),*/
				LireFilters.CATEGORY.getFilter()+":"+filterMap.get(LireFilters.CATEGORY.getFilter())+","+
				LireFilters.BRAND.getFilter()+":"+filterMap.get(LireFilters.BRAND.getFilter()));
		query.setRows(rows);
		query.setParam(LireParams.URL.getParam(), imgUrl);
		query.setParam(LireParams.FIELD.getParam(), field);
		query.setParam("wt", "json");
		query.setFields("*");
		query.setStart(0);
		return query;
	}
}
