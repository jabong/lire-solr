package net.semanticmetadata.lire.solr.client;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class LireJerseyClient {
	private static int exceptionThread=0;
	/*public static void main(String args[]){
		RankingRestClient client = new RankingRestClient();
		String str = client.getDataFromServer("http://pricing-india1.jabongfashion.com/catdata/get?fl=sku&type=catalog&start=0&rows=1000&fq=category_id:1", "application/json");
		JabongDataServiceClinetResponseBean map  = new GsonBuilder().create().fromJson(str,
				new TypeToken<JabongDataServiceClinetResponseBean>(){}.getType());
		
		
		System.out.println(map.getDocs().size());
	}*/
	
	public String getDataFromServer(String url, String mediaType, String headerKey, String headerValue){
		Client client = Client.create();
		client.setConnectTimeout(120000);
		ClientResponse response = null;
		try{
			WebResource webResource = client.resource(url);

			response = webResource.
					accept(mediaType).
					get(ClientResponse.class);
			if (response.getStatus() != 200) {
				   throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatus());
			}
			
			return response.getEntity(String.class);

	
		}catch(ClientHandlerException che){
			System.out.println("exception="+exceptionThread++);
			
			throw new ClientHandlerException(che);
		}finally{
			client.destroy();
		}
	}
	
}

