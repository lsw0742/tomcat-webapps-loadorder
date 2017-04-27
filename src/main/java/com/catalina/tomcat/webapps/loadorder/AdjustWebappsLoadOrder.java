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
		
		List<String> adjustedWebapps=AdjustWebappsLoadOrder.adjustWebappsOrder(host, configBase.list(), filteredAppPaths);
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
	
	/**
	 * @param basePath
	 * @return
	 */
	private static List<String> iniLoadOrders(String basePath){
		try {
			List<String> configWebapps=new ArrayList<String>();
			File orderFile=new File(basePath+"/webapps-load-order.xml");
			if(!orderFile.exists()){return configWebapps;}
			
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
			return configWebapps;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param host
	 * @param configXmls
	 * @param appPaths
	 * @return
	 */
	public static List<String> adjustWebappsOrder(Host host, String[] configXmls, String[] appPaths){
		return adjustWebappsOrder(host.getCatalinaBase().getPath()+"/conf", configXmls, appPaths);
	}
	
	/**
	 * @param basePath
	 * @param configXmls
	 * @param appPaths
	 * @return
	 */
	public static List<String> adjustWebappsOrder(String basePath, String[] configXmls, String[] appPaths){
		List<String> configWebapps=iniLoadOrders(basePath);
		
		Set<String> allWebapps=new TreeSet<String>(new Comparator<String>(){
			public int compare(String o1, String o2) {
				return o2.compareTo(o1);
			}
		});
		allWebapps.addAll(Arrays.asList(configXmls));
		allWebapps.addAll(Arrays.asList(appPaths));
		
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
		return tempWebapps;
	}

}
