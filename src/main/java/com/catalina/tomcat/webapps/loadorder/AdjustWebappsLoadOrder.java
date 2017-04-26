package com.catalina.tomcat.webapps.loadorder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.catalina.Host;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *	HostConfig's deployApps method call example:
 *	-------------------------
 protected void deployApps() {
		File appBase = host.getAppBaseFile();
		File configBase = host.getConfigBaseFile();
		String[] filteredAppPaths = filterAppPaths(appBase.list());
		
		AdjustWebappsLoadOrder.iniLoadOrders(host);
		String[] adjustedWebapps=AdjustWebappsLoadOrder.adjustWebappsOrder(configBase.list(), filteredAppPaths, filteredAppPaths);
		for(String webapp : adjustedWebapps){
			if(webapp.endsWith(".xml")){
		        // Deploy XML descriptors from configBase
				deployDescriptors(configBase, new String[]{webapp});
			}else if(webapp.endsWith(".war")){
				// Deploy WARs
				 deployWARs(appBase, new String[]{webapp});
			}else{
		        // Deploy expanded folders
				deployDirectories(appBase, new String[]{webapp});
			}	      
		}
	}
 *	-------------------------	
 * @author demo
 *
 */
public class AdjustWebappsLoadOrder {
	
	private static Log log=LogFactory.getLog(AdjustWebappsLoadOrder.class);
	private static List<String> configWebapps=new ArrayList<String>();
	
	/**
	 * @param host
	 */
	public static void iniLoadOrders(Host host){
		iniLoadOrders(host.getCatalinaBase().getPath()+"/conf");
	}
	/**
	 * @param basePath
	 * @return
	 */
	public static void iniLoadOrders(String basePath){
		try {
			File orderFile=new File(basePath+"/webapps-load-order.xml");
			if(!orderFile.exists()){return;}
			
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = db.parse(orderFile);
			Element root = document.getDocumentElement();
			NodeList nodes = root.getChildNodes();			
			for (int i = 0; i < nodes.getLength(); i++) {
				if (!(nodes.item(i) instanceof Element)) {
					continue;
				}
				if (nodes.item(i).getFirstChild() == null) {
					continue;
				}
				configWebapps.add(nodes.item(i).getFirstChild().getNodeValue());
			}
			log.info("will expect the load order of the webapps: "+configWebapps);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param xmlWebapps
	 * @param warWebapps
	 * @param dirWebapps
	 * @return
	 */
	public static String[] adjustWebappsOrder(String[] xmlWebapps, String[] warWebapps, String[] dirWebapps){
		Set<String> allWebapps=new TreeSet<String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		allWebapps.addAll(Arrays.asList(xmlWebapps));
		allWebapps.addAll(Arrays.asList(warWebapps));
		allWebapps.addAll(Arrays.asList(dirWebapps));
		
		List<String> tempWebapps=new ArrayList<String>(configWebapps.size()*3+allWebapps.size());
		for(String webapp : configWebapps){
			tempWebapps.add(webapp+".xml");
			tempWebapps.add(webapp+".war");
			tempWebapps.add(webapp);
		}
		
		 for (Iterator<String> iter = tempWebapps.iterator(); iter.hasNext();){
			 String webapp=iter.next();
			if(!allWebapps.contains(webapp)){
				iter.remove();
			}
		}
		
		for(String webapp : allWebapps){
			if(!tempWebapps.contains(webapp)){
				tempWebapps.add(webapp);
			}
		}
		
		if(!tempWebapps.isEmpty()){
			log.info("before adjusting the load order of the webapps: "+allWebapps);
			log.info("after adjusted the load order of the webapps: "+tempWebapps);
		}
		return tempWebapps.toArray(new String[tempWebapps.size()]);
	}

}
