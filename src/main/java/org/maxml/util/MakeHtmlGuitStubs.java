package org.maxml.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import org.maxml.xpath.ApplyXPath;

public class MakeHtmlGuitStubs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// if (args.length > 1) {

		for (int i = 1; i < args.length; i++) {
			String filename = args[i];// "/home/jkirkley/3p/web/lps-4.0.16/Server/lps-4.0.16/tabs/a/A1___AVEN/A1/CAUG_WALK/Make-It-Good.tab.xzy.xml";
			if (!filename.endsWith("/list.xml")) {
				String htmlFileName = filename.substring(0, filename.length() - 12) + ".html";
				System.out.println(htmlFileName);
				try {

					InputSource in = new /* noIB */InputSource(new FileInputStream(filename));

					DOMParser parser = new DOMParser();
					try {

						parser.parse(in);
						Document d = parser.getDocument();

						// System.out.println(ApplyXPath.i().getXPathString("/tune/@sid",
						// d));
						// System.out.println(ApplyXPath.i().getXPathString("/tune/ArtistName/text()",
						// d));
						// System.out.println(ApplyXPath.i().getXPathString("/tune/Title/text()",
						// d));
						// System.out.println(ApplyXPath.i().getXPathString("/tune/SongName/text()",
						// d));

						String sid = ApplyXPath.i().getXPathString("/tune/@sid", d);
						String artistName = ApplyXPath.i().getXPathString("/tune/ArtistName/text()", d);
						String title = ApplyXPath.i().getXPathString("/tune/Title/text()", d);
						String songName = ApplyXPath.i().getXPathString("/tune/SongName/text()", d);

						String html = "<html>" + "<head>"
								+ "<meta name=\"generator\" content=\"HTML Tidy, see www.w3.org\" />" + "<title>"
								+ artistName
								+ " Guitar Bass Tabs for - "
								+ songName
								+ " Tabs, Chords, Lyrics</title>"
								+ "<meta name=\"robots\" content=\"ALL\" />"
								+ "<meta name=\"name\" content=\"Guitar Tablatures, Chords, Tabs, Song, Lyrics, Tune, Tabs for Bass\" />"
								+ "<meta name=\"description\" content=\"Guitar "
								+ artistName
								+ " Tabs, "
								+ songName
								+ " Tablatures, Chords, Tabs, Song, Lyrics, Tune\" />"
								+ "<meta name=\"keywords\" content=\"Guitar Tabs for "
								+ artistName
								+ " - "
								+ songName
								+ " Tablatures, Chords, Tabs, Song, Lyrics, Tune\" />"
								+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" />"
								+ "<META http-equiv=\"refresh\" content=\"0;URL=http://www.guizzard.com/wizard.swf?tune="
								+ sid + "\">" + "</head>" + "</html>";

						// System.out.println(html);

						FileUtils.i().writeContents(htmlFileName, html);

					} catch (Exception e1) {
						e1.printStackTrace();
					}

				} catch (FileNotFoundException fnf) {
					System.err.println("FileInputStream of " + filename + " threw: " + fnf.toString());
					fnf.printStackTrace();
				}

			}
		}
	}

}
