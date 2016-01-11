package net.semanticmetadata.lire.solr;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.solr.bean.LireDocument;
import net.semanticmetadata.lire.solr.filters.LireFilters;
import net.semanticmetadata.lire.solr.util.LireSolrQueryExecutor;
import net.semanticmetadata.lire.utils.ImageUtils;

class TestImage{
	public static void main(String args[]){
		
		LireSolrQueryExecutor executor = new LireSolrQueryExecutor();
		Map<String, String> filterMap = new HashMap<String, String>();
		filterMap.put(LireFilters.BRAND.getFilter(), "COLORS");
		filterMap.put(LireFilters.CATEGORY.getFilter(), "WOMEN APPAREL");
		//filterMap.put(LireFilters.COMPANY.getFilter(), "JABONG");
		List<LireDocument> lireDocs = executor.executeQuery("http://localhost:8984/solr/test", 
				executor.createLireQuery("lireq", "http://static.jabong.com/p/-0658341-1-zoom.jpg", 3, "ce_ha", filterMap));
		
		/*BufferedImage img3 = getBufferedImageByFile();
		BufferedImage img2 = ImageUtils.createWorkingCopy(img3);
		DespeckleFilter df = new DespeckleFilter();
        img2 = df.filter(img2, null);
        img2 = ImageUtils.trimWhiteSpace(img2); // trims white space
		BufferedImage img1 = getBufferedImageByURL();
		System.out.println("done");
		if(img1 != null && img2 != null){
			System.out.println(img1.getTransparency()+":"+img2.getTransparency());
			System.out.println(img1.toString());
			System.out.println(img2.toString());
			System.out.println(img1.getRaster().getDataBuffer().getSize());
			System.out.println(img2.getRaster().getDataBuffer().getSize());
			System.out.println(img1.getColorModel().getColorSpace().hashCode());
			System.out.println(img2.getColorModel().getColorSpace().hashCode());
		}*/
	}
	
	static BufferedImage getBufferedImageByURL(){
		String paramURL = "http://static.jabong.com/p/-0658341-1-zoom.jpg";
		BufferedImage img = null;
		try {
			img = ImageIO.read(new URL(paramURL).openStream());
			img = ImageUtils.trimWhiteSpace(img);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return img;
	}
	
	static BufferedImage getBufferedImageByFile(){
		String path = "/home/jabong/Downloads/img/visualsearchdemo/images/index_images/women_apparel/-0658341-1-zoom.jpg";
		File next = new File(path);
		BufferedImage img = null;
        try {
            int fileSize = (int) next.length();
            byte[] buffer = new byte[fileSize];
            FileInputStream fis = new FileInputStream(next);
            fis.read(buffer);
            ByteArrayInputStream b = new ByteArrayInputStream(buffer);
            BufferedImage read = ImageIO.read(b);
            img = ImageUtils.createWorkingCopy(read);
        } catch (Exception e) {
            System.err.println("Could not read image " + path + ": " + e.getMessage());
        }
		return img;
	}
}