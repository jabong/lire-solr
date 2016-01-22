package net.semanticmetadata.lire.solr.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.sun.jersey.api.client.ClientHandlerException;

import net.semanticmetadata.lire.solr.bean.LireDocument;
import net.semanticmetadata.lire.solr.bean.LireDocumentExtended;
import net.semanticmetadata.lire.solr.bean.LireSolrResponseBean;
import net.semanticmetadata.lire.solr.bean.LireSolrResponseExtendedBean;
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
	public static String SELECT = "select";
	
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
		//System.out.println("query creation started for "+field+":"+System.currentTimeMillis());
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
		//System.out.println("query creation ended:"+System.currentTimeMillis());
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
		//System.out.println("execution started:"+System.currentTimeMillis());
		LireJerseyClient clientJ = new LireJerseyClient();
		String url = serverURL+query;
		String response = null;
		try{
			response = clientJ.getDataFromServer(url, "application/json", null, null);
		}catch(ClientHandlerException che){
			throw new ClientHandlerException(che);
		}
		//System.out.println("execution ended:"+System.currentTimeMillis());
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
	
	/**
	 * This util method creates the lire query
	 * @param queryType lireq
	 * @param imgUrl url of image against which image matching would be executed
	 * @param rows number of rows in output
	 * @param field name of the algorithms used for matching (ce_ha,sc_ha....)
	 * @param filterMap filterQuery details
	 * @return
	 */
	public String createLireSearchQueryForProducts(String queryType, String productId, int rows, List<String> fields, Map<String, String> filterMap){
		//System.out.println("query creation started for "+field+":"+System.currentTimeMillis());
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
			
			if(productId != null){
				queryBuilder.append("&");
				queryBuilder.append(LireParams.PRODUCT_ID.getParam()).append("=").append(productId);
			}
			
			if(fields != null){
				StringBuilder fieldsBuilder = new StringBuilder();
				boolean first = true;
				for(String str : fields){
					if(first){
						first = false;
					}else{
						fieldsBuilder.append(",");
					}
					fieldsBuilder.append(str);
				}
				queryBuilder.append("&");
				queryBuilder.append(LireParams.FIELD.getParam()).append("=").append(fieldsBuilder.toString());
						//URLEncoder.encode(fieldsBuilder.toString(),"utf-8"));
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
		//System.out.println("query creation ended:"+System.currentTimeMillis());
		return queryBuilder.toString();
	}
	
	public List<LireDocumentExtended> executeProductBasedQuery(String serverURL, String query){
		//System.out.println("execution started:"+System.currentTimeMillis());
		LireJerseyClient clientJ = new LireJerseyClient();
		String url = serverURL+query;
		String response = null;
		try{
			response = clientJ.getDataFromServer(url, "application/json", null, null);
		}catch(ClientHandlerException che){
			throw new ClientHandlerException(che);
		}
		//System.out.println("execution ended:"+System.currentTimeMillis());
		return parseSolrDocumentExtendedList(response);
	}
	
	private List<LireDocumentExtended> parseSolrDocumentExtendedList(String response) {
		if(response == null){
			return null;
		}
		LireSolrResponseExtendedBean responseBean = new GsonBuilder().create().fromJson(response,
				new TypeToken<LireSolrResponseExtendedBean>(){}.getType());
		return responseBean.getDocs();
	}

	public List<LireDocumentExtended> createAndExecuteProductBasedLireQuery(
			String serverURL, String queryType, String productId, int rows, List<String> fields, Map<String, String> filterMap){
		String query = createLireSearchQueryForProducts(queryType, productId, rows, fields, filterMap);
		return executeProductBasedQuery(serverURL, query);
	}
	
	public List<LireDocument> createAndExecuteHashBasedLireQuery(String serverURL, String queryType, String hash, String feature, int rows, String field, Map<String, String> filterMap){
		String query = createLireQueryByHash(queryType, hash, feature, rows, field, filterMap);
		return executeQuery(serverURL, query);
	}
	
	/**
	 * This util method creates the lire query
	 * @param queryType lireq
	 * @param imgUrl url of image against which image matching would be executed
	 * @param rows number of rows in output
	 * @param field name of the algorithms used for matching (ce_ha,sc_ha....)
	 * @param filterMap filterQuery details
	 * @return
	 */
	public String createLireQueryByHash(String queryType, String hash, String feature, int rows, String field, Map<String, String> filterMap){
		//System.out.println("query creation started for "+field+":"+System.currentTimeMillis());
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
			
			if(hash != null){
				queryBuilder.append("&");
				queryBuilder.append(LireParams.HASH.getParam()).append("=").append(URLEncoder.encode(hash,"utf-8"));
			}
			
			if(feature != null){
				queryBuilder.append("&");
				queryBuilder.append(LireParams.FEATURE.getParam()).append("=").append(URLEncoder.encode(feature,"utf-8"));
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
		//System.out.println("query creation ended:"+System.currentTimeMillis());
		return queryBuilder.toString();
	}
	
}
