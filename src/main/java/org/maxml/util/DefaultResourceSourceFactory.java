package org.maxml.util;



public class DefaultResourceSourceFactory extends ResourceSourceFactory {

	private static final String RESOURCEPROPS=Util.RD + "/defaultRS.properties";
	public static final String DEFAULT="default";
	public static final String BASIC_XML="basic-xml";

	private static DefaultResourceSourceFactory instance = null;
	
	public static DefaultResourceSourceFactory i() {
		if( instance == null) {
			instance = new DefaultResourceSourceFactory();
		}
		return instance;
	}
	
	public DefaultResourceSourceFactory(){
		super(RESOURCEPROPS);
	}
	
	public ResourceSource getRS(String spec) {
		return super.getAndCache(DEFAULT, spec);
	}
	public ResourceSource getBasicXmlRS(String spec) {
		return super.getAndCache(BASIC_XML, spec);
	}

}
