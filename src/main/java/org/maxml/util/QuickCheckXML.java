package org.maxml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.xerces.parsers.DOMParser;
import org.xml.sax.InputSource;


public class QuickCheckXML {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length > 1) {
			String phase = args[0];
			for (int i = 1; i < args.length; i++) {
				String filename = args[i];
				try {

					InputSource in = new /* noIB */InputSource(new FileInputStream(filename));

					DOMParser parser = new DOMParser();
					try {
						parser.parse(in);
					} catch (Exception e1) {

						if (filename.endsWith("/list.xml")) {

							if (phase.equals("1")) {

								System.out.println("e.pl " + filename);

							} else {

								System.out
										.println("java -cp /home/jkirkley/workspace/setfer/src/web/WEB-INF/classes/:/home/jkirkley/workspace/setfer/src/web/WEB-INF/classes/xercesImpl.jar com.AxEnts "
												+ filename);
							}

						} else {
							
							if (phase.equals("1")) {

								System.out.println("one.sh " + filename.substring(0, filename.length() - 8));

							} else if (phase.equals("2")) {
								
								System.out.println("e.pl " + filename.substring(0, filename.length() - 8));
								System.out.println("one.sh " + filename.substring(0, filename.length() - 8));

							} else {

								System.out
										.println("java -cp /home/jkirkley/workspace/setfer/src/web/WEB-INF/classes/:/home/jkirkley/workspace/setfer/src/web/WEB-INF/classes/xercesImpl.jar com.AxEnts "
												+ filename.substring(0, filename.length() - 8));
								System.out.println("one.sh " + filename.substring(0, filename.length() - 8));

							}
						}
					}

				} catch (FileNotFoundException fnf) {
					System.err.println("FileInputStream of " + filename + " threw: " + fnf.toString());
					fnf.printStackTrace();
				}

			}
		}
	}

}
