package com.catalina.tomcat.webapps.loadorder;

import static com.catalina.tomcat.webapps.loadorder.AdjustWebappsLoadOrder.adjustWebappsOrder;

public class Test {

	public static void main(String[] args) {
		String basePath="E:/space_open/tomcat/tomcat-webapps-loadorder/src/main/java/com/catalina/tomcat/webapps/loadorder/";
		
		adjustWebappsOrder(basePath,new String[]{"a.xml", "b.xml", "c.xml","d.xml"},new String[]{"a", "a.war", "b", "b.war","c", "c.war","d", "d.war",});
	}

}
