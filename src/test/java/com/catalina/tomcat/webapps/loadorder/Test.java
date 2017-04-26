package com.catalina.tomcat.webapps.loadorder;

import static com.catalina.tomcat.webapps.loadorder.AdjustWebappsLoadOrder.iniLoadOrders;
import static com.catalina.tomcat.webapps.loadorder.AdjustWebappsLoadOrder.adjustWebappsOrder;

public class Test {

	public static void main(String[] args) {
		iniLoadOrders("E:/space_open/tomcat/tomcat-webapps-loadorder/src/main/java/com/catalina/tomcat/webapps/loadorder/");
		adjustWebappsOrder(new String[]{"a.war", "e.xml", "c.xml"},new String[]{"a.war", "e.war", "c.war"}, new String[]{"a", "e", "c"});
	}

}
