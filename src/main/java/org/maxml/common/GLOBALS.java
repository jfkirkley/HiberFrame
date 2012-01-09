package org.maxml.common;

import java.io.File;

import org.maxml.util.Util;

public class GLOBALS {

	public static final String ROOTDIR =
		 (File.separator.equals("\\")) ? Util.i().ensureSlashes("C:\\eclipse\\setfer")
				: Util.i().ensureSlashes("/home/jkirkley/jrp/setfer");

		 

	public static final String ROOTXMLDIR = ROOTDIR + File.separator + "xml";	

 
}
