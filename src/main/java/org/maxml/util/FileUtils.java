package org.maxml.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;

public class FileUtils {

	public static String		rootPath;

	public static final String	NUM_DLS_BEFORE_ARCHIVE		= "downloads.num.before.archive";

	public static final String	NUM_ARCHIVES_BEFORE_BACKUP	= "archives.num.before.backup";

	public static final String	FIREFOX_BROWSER_PATH		= "browser.app.firefox";

	public static final String	IE_BROWSER_PATH				= "browser.app.ie";

	public static final String	IE_COOKIE_PATH				= "IE_BROWSER_PATH";

	public static final String	FIREFOX_COOKIE_PATH			= "FIREFOX_BROWSER_PATH";

	public static final String	COOKIE_PATH					= "cookie.file.path";

	private static String		rootConfigFileName			= "config.properties";

	public static String		COMP_NAME_KEY				= "FogDucker Soft";
	public static String		PROG_NAME_KEY				= "Thumb Grabber 3.0";
	public static String		ROOT_DIR_KEY				= "ROOT";

	private static Properties	rootConfigProperties;

	public static FileUtils		instance					= null;

	public FileUtils() {
		//this(rootPath);
	}

	public FileUtils(String rpath) {
		rootPath = rpath;

		rootConfigProperties = loadProperties(rootPath + rootConfigFileName);
	}

	public static FileUtils i() {
		if (instance == null) {
			instance = new FileUtils();
		}
		return instance;
	}

	public static FileUtils i(String rpath) {
		if (instance == null) {
			instance = new FileUtils(rpath);
		}
		return instance;
	}

	public String get(String propertyName) {
		return (String) rootConfigProperties.get(propertyName);
	}

	public String getProperty(String propertyName) {
		return i().get(propertyName);
	}

	public void setProperty(String propertyName, String val) {
		rootConfigProperties.put(propertyName, val);
	}

	public String getPath(String propertyName) {
		return rootPath + i().get(propertyName);
	}

	public Properties loadProperties(String propertyFileName) {
		Properties properties = new Properties();
		try {
			FileInputStream fis = new FileInputStream(propertyFileName);
			properties.load(fis);
			// } catch (FileNotFoundException fnfe) {
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return properties;
	}

	public void saveProperties(Properties properties, String propertyFileName) {
		try {
			FileOutputStream fos = new FileOutputStream(propertyFileName);
			properties.store(fos, null);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public void saveProperties() {
		saveProperties(rootConfigProperties, rootPath + rootConfigFileName);
	}

    public List getAllJarEntries(String jarFileName) throws Exception {
        return getAllJarEntries(jarFileName, null);
    }
    public List getAllJarEntries(String jarFileName, String prefixPath) throws Exception {
        FileInputStream fis = new FileInputStream(new File(jarFileName));
        JarInputStream jis = new JarInputStream(fis);
        JarEntry entry;
        ArrayList entries = new ArrayList();
        while ((entry = jis.getNextJarEntry()) != null) {
            String name = entry.getName();
//            if(!name.endsWith("class")) 
//                System.out.println(prefixPath + " >> " + name);
            if( !entry.isDirectory() ) continue;
            
            if(prefixPath!=null ) { 
                if(name.startsWith(prefixPath)) {
                    entries.add(name);
                }
            } else {
                entries.add(name);
            }
        }
        return entries;
    }

    public List getJarPkgClassEntries(String jarFileName, String pkgPath) throws Exception {
        FileInputStream fis = new FileInputStream(new File(jarFileName));
        JarInputStream jis = new JarInputStream(fis);
        
        int cutPoint = pkgPath.length();
        JarEntry entry;
        ArrayList entries = new ArrayList();
        
        while ((entry = jis.getNextJarEntry()) != null) {
            String name = entry.getName();
            if( name.startsWith(pkgPath)) {
                String rest = name.substring(cutPoint);
                if( rest.indexOf('/') == -1 && rest.endsWith(".class")) {
                    entries.add(name);
                }
            }
        }
        
        return entries;
    }

	
	public void fixFFiles() {
		String archiveDir = getProperty("archive.folder");
		File archiveDirFile = new File(archiveDir);
		File[] flo = archiveDirFile.listFiles();
		for (int j = 0; j < flo.length; ++j) {
			if (flo[j].isDirectory()) {
				File[] fl = flo[j].listFiles();
				for (int i = 0; i < fl.length; ++i) {
					File f = getFileInDir(fl[i], "thumbsControl.html");
					if (f == null)
						continue;
					try {
						StringBuffer sb = getFileContents(f.getPath());
						StringBuffer sb2 = new StringBuffer();
						StringTokenizer st = new StringTokenizer(sb.toString(), "\"");
						while (st.hasMoreTokens()) {
							String tk = st.nextToken();
							if (tk.startsWith("file:")) {
								int p = tk.lastIndexOf("thumbs/");
								if (p != -1) {
									tk = tk.substring(p);
								} else {
									p = tk.lastIndexOf('/');
									if (p != -1) {
										tk = tk.substring(p + 1);
									}
								}
							}
							sb2.append(tk);
							if (st.hasMoreTokens()) {
								sb2.append("\"");
							}
						}
						// System.out.println(sb2.toString());
						writeContents(f.getPath(), sb2.toString());
						// if(i==1)break;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}

	public boolean usingFireFox() {
		String type = getProperty("browser.app");
		return type.equals(FIREFOX_BROWSER_PATH);
	}

	public String getBrowserProcStr() {
		String type = getProperty("browser.app");
		return getProperty(type) + " ";
	}

	public void ensureDir(String dir) {
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
	}

	public void moveAllContents(String fromDir, String toDir) {
		toDir = ensureEndSlash(toDir);
		ensureDir(toDir);
		File fromDirFile = new File(fromDir);
		File[] fl = fromDirFile.listFiles();
		for (int i = 0; i < fl.length; ++i) {
			String destPath = toDir + fl[i].getName();
			fl[i].renameTo(new File(destPath));
		}
	}

	public int getNumSubDirsWithPrefix(String dir, String prefix) {
		File dirFile = new File(dir);
		File[] fl = dirFile.listFiles();
		int numDirs = 0;
		for (int i = 0; i < fl.length; ++i) {
			if (fl[i].isDirectory() && fl[i].getName().startsWith(prefix)) {
				++numDirs;
			}
		}
		return numDirs;
	}

	public String getFirstFileNameInSubDir(File dirFile, String subDir) {
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].isDirectory() && fl[i].getName().equals(subDir)) {
					File[] fl2 = fl[i].listFiles();
					if (fl2.length > 0)
						return fl2[0].getAbsolutePath();
				}
			}
		}
		return "";
	}

	public List getFilesInDirModifiedAfterDate(File dirFile, long date) {
		ArrayList al = new ArrayList();
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].lastModified() > date) {
					al.add(fl[i]);
				}
			}
		}
		return al;
	}

	public File getFileInDir(File dirFile, String fileName) {
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].getName().equals(fileName)) {
					return fl[i];
				}
			}
		}
		return null;
	}

	public File getLastSubDirInDir(String dirFileName, String prefix) {
		File dirFile = new File(dirFileName);
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = fl.length - 1; i >= 0; --i) {
				if (fl[i].isDirectory() && fl[i].getName().startsWith(prefix)) {
					System.out.println("dir last: " + fl[i]);
					return fl[i];
				}
			}
		}
		return null;
	}

	public File getFirstSubDirInDir(String dirFileName) {
		File dirFile = new File(dirFileName);
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].isDirectory()) {
					return fl[i];
				}
			}
		}
		return null;
	}

	public int getMaxSuffixCountForFilesInDir(File dirFile, String prefix) {
		int len = prefix.length();
		if (dirFile.isDirectory()) {
			int max = 0;
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].isDirectory()) {
					try {
						String fname = fl[i].getName();
						if (fname.startsWith(prefix)) {
							max = Math.max(Integer.parseInt(fname.substring(len)), max);
						}
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return max;
		}
		return 0;
	}

	public String getNextMaxSuffixCountForFilesInDir(File dirFile, String prefix) {
		return "" + (getMaxSuffixCountForFilesInDir(dirFile, prefix) + 1);

	}

	public String padStr(String s, String pad, int num) {
		int len = s.length();
		for (int i = 0; i < num - len; ++i)
			s = pad + s;
		return s;
	}

	public String getNextMaxSuffixCountForFilesInDir(File dirFile, String prefix, int numDigits) {
		String s = getNextMaxSuffixCountForFilesInDir(dirFile, prefix);
		return padStr(s, "0", numDigits);
	}

	public String ensureEndSlash(String dirName) {
		if (!dirName.endsWith(File.separator))
			dirName += File.separator;
		return dirName;
	}

	public File getFileInSubDir(File dirFile, String subDir, String fileName) {
		if (dirFile.isDirectory()) {
			File[] fl = dirFile.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].isDirectory() && fl[i].getName().equals(subDir)) {
					File[] fl2 = fl[i].listFiles();
					for (int j = 0; j < fl2.length; ++j) {
						if (fl2[j].getName().equals(fileName))
							return fl2[j];
					}

				}
			}
		}
		return null;
	}

	public void addToFavs(String str) {
		String favFile = getProperty("favorites.filename");
		try {
			File ff = new File(favFile);
			if (ff.exists())
				appendToFile(favFile, str);
			else
				writeContents(favFile, str);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void appendToFile(String fileName, String str) throws IOException {
		StringBuffer sb = getFileContents(fileName);
		sb.append(str);
		writeContents(fileName, sb.toString());
	}

    public StringBuffer getFileContents(String fileName) throws IOException {
        return getStreamContents(new FileInputStream(fileName));
    }

    public StringBuffer getFileContents(File file) throws IOException {
        return getStreamContents(new FileInputStream(file));
    }

	public StringBuffer getStreamContents(InputStream is) throws IOException {
		return getStreamContents(new InputStreamReader(is));
	}

	public StringBuffer getStreamContents(InputStreamReader isr) throws IOException {
		StringBuffer sb = new StringBuffer();
		char[] buf = new char[10 * 1024];
		int cnt = 0;
		while ((cnt = isr.read(buf)) != -1) {
			sb.append(buf, 0, cnt);
		}
		isr.close();

		return sb;
	}

	public byte[] getStreamBytes(String fileName, int size) throws IOException {
		return getStreamBytes(new FileInputStream(new File(fileName)), size);
	}

	public byte[] getStreamBytes(InputStream is, int size) throws IOException {

		if (is == null)
			return null;
		byte[] buf = new byte[size];
		int cnt = is.read(buf);
		if (cnt < size) {
			byte[] buf2 = new byte[cnt];
			for (int i = 0; i < cnt; ++i) {
				buf2[i] = buf[i];
			}
			return buf2;
		}
		is.close();
		return buf;
	}


	public void transferContents(InputStream is, OutputStream os) throws IOException {
		byte[] buf = new byte[10 * 1024];
		int cnt = 0;
		while ((cnt = is.read(buf)) != -1) {
			os.write(buf, 0, cnt);
		}
		os.close();
		is.close();

	}

	public void writeContents(String fileName, String str) throws IOException {
		writeContents(fileName, str, false);
	}
	public void writeContents(String fileName, String str, boolean ensureDirs) throws IOException {
		if( ensureDirs ) {
			File f = new File(fileName);
			ensureDir(f.getParent());
			writeContents(f, str);
		} else {
			writeContents(new FileOutputStream(new File(fileName)), new StringReader(str));
		}
	}

	public void writeContents(String fileName, byte[] buf) throws IOException {
		FileOutputStream fos = new FileOutputStream(new File(fileName));
		fos.write(buf);
		fos.close();
	}

	public void writeContents(File file, String str) throws IOException {
		writeContents(new FileOutputStream(file), new StringReader(str));
	}

	public void writeContents(OutputStream os, StringReader sr) throws IOException {
		writeContents(new OutputStreamWriter(os), sr);
	}
	
	public void writeContents(OutputStream os, String str) throws IOException {
		writeContents(new OutputStreamWriter(os), new StringReader(str));
	}

	public void writeContents(OutputStreamWriter os, StringReader sr) throws IOException {
		char[] buf = new char[10 * 1024];
		int cnt = 0;
		while ((cnt = sr.read(buf)) != -1) {
			os.write(buf, 0, cnt);
		}
		os.close();
		sr.close();
	}

	public String getFName(String fn) {
		return new File(fn).getName();
	}

	public void delete(String fname) {
		
		delete(new File(fname));
	}

	public void delete(File f) {
		System.err.println("Deleting: " + f.getAbsolutePath());
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				delete(fl[i]);
			}
		}
		f.delete();
	}

	public void deleteSubDirs(String fname) {
		deleteSubDirs(new File(fname));
	}

	public File getOldestContainedFile(File d, String ignoreFile) {
		File[] fl = d.listFiles();
		File oldest = null;
		long modDate = Long.MAX_VALUE;
		for (int i = 0; i < fl.length; ++i) {
			if (!fl[i].getName().equals(ignoreFile) && fl[i].lastModified() <= modDate) {
				modDate = fl[i].lastModified();
				oldest = fl[i];
			}
		}
		return oldest;
	}

	public void deleteSubDirs(File f) {
		if (f.isDirectory()) {
			File[] fl = f.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				delete(fl[i]);
			}
		}
	}

	public void moveAllFiles(String targetPath, String destinationPath) {

		if (!targetPath.endsWith(File.separator))
			targetPath += File.separator;
		if (!destinationPath.endsWith(File.separator))
			destinationPath += File.separator;

		File target = new File(targetPath);
		File dest = new File(destinationPath);
		if (target.exists() && !dest.exists()) {
			dest.mkdirs();
		}

		if (target.isDirectory()) {
			File[] fl = target.listFiles();
			for (int i = 0; i < fl.length; ++i) {
				if (fl[i].isFile()) {
					String fname = fl[i].getName();
					move(targetPath + fname, destinationPath + fname);
				}
			}
		}
	}

	public void move(String targetPath, String destinationPath) {
		File target = new File(targetPath);
		if (target.exists())
			target.renameTo(new File(destinationPath));
		else
			System.err.println("RootConfig.i().move: '" + targetPath + "' non existant.");
	}

	public boolean exists(String path) {
		File f = new File(path);
		return f.exists();
	}

	public String escForwardSlash(String s) {
		return esc(s.replace('/', '\\'));
	}

	public String esc(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); ++i) {
			if (s.charAt(i) == '\\')
				sb.append(s.charAt(i));
			sb.append(s.charAt(i));
		}
		return sb.toString();
	}

	public boolean compareStreamToBuff(InputStream is, StringBuffer compareBuf) {
		try {
			int c;
			int cnt = 0;
			while ((c = is.read()) != -1) {
				char ch = (char) c;
				char bc = compareBuf.charAt(cnt++);
				// System.out.print(ch);
				if (ch != bc) {
					// System.out.println(
					// "*************************************************************************"
					// );
					// System.out.println( " - " + bc);
					// System.out.println(
					// "*************************************************************************"
					// );
					return false;
				}
			}
			is.close();
		} catch (IOException io) {
			io.printStackTrace();
		}

		return true;
	}

	public boolean compareStreamToBuff(InputStream is, byte[] compareBuf) {
		if (is == null)
			return false;
		try {
			int c;
			int cnt = 0;
			while ((c = is.read()) != -1 && cnt < compareBuf.length) {
				byte ch = (byte) c;
				byte bc = compareBuf[cnt++];
				// System.out.print(ch);
				if (ch != bc) {
					// System.out.println(
					// "*************************************************************************"
					// );
					// System.out.println( " - " + bc);
					// System.out.println(
					// "*************************************************************************"
					// );
					return false;
				}

			}
			is.close();
			if (cnt < compareBuf.length)
				return false;

		} catch (IOException io) {

			io.printStackTrace();
			return false;
		}

		return true;
	}

	public long getStreamLen(InputStream is) {
		long len = 0;
		System.out.println(is);
		try {
			while ((is.read()) != -1) {
				++len;
			}
		} catch (IOException io) {
			io.printStackTrace();
		}
		System.out.println(" got len: " + len);

		return len;
	}

	public Properties getRootConfigProperties() {
		return rootConfigProperties;
	}

	public void setRootConfigProperties(Properties properties) {
		rootConfigProperties = properties;
	}

    public String stripExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if( i != -1) {
            return fileName.substring(0,i);
        }
        return fileName;
    }
    
    public String getExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if( i != -1) {
            return fileName.substring(i);
        }
        return fileName;
    }

    public boolean isDirectory(String fileName) {
        File f = new File(fileName);
        return f.isDirectory();
    }
    
    public File getEnclosingDirectory(String fileName) {
        File f = new File(fileName);
        if( f.isDirectory() ) {
            return f;
        }
        return f.getParentFile();
    }

    public String getParentName(String fileName) {
        File f = new File(fileName);
        return f.getParent();
    }
    
    public String removeSuffixPathElements(String path, int numElements) {
    	if( path != null) {
    		String [] elements = (File.separator.equals("\\"))? path.split("\\\\"): path.split("/");
    		String sep = "";//(path.startsWith(File.separator))? File.separator: "";
    		boolean endsWithSep = path.endsWith(File.separator);
    		path = "";
    		for (int i = 0; i < elements.length-numElements; i++) {
				path += sep + elements[i];  
	    		sep = File.separator;
			}
    		if(endsWithSep) {
				path += sep;
    		}
    	}
    	return path;
    }
    public String removePrefixPathElements(String path, int numElements) {
    	if( path != null) {
    		String [] elements = (File.separator.equals("\\"))? path.split("\\\\"): path.split("/");
    		String sep = "";
    		boolean endsWithSep = path.endsWith(File.separator);
    		path = "";
    		for (int i = numElements; i < elements.length; i++) {
				path += sep + elements[i];  
	    		sep = File.separator;
			}
    		if(endsWithSep) {
				path += sep;
    		}
    	}
    	return path;
    }

    public String normalize(String path) {
        String normalizedPath = "";
        char lc = ' ';
        for (int i = 0; i < path.length(); i++) {
            char c = path.charAt(i);
            if(i>0 && lc == File.separatorChar && c == File.separatorChar) {
                continue;
            }
            normalizedPath += c;
            lc = c;
        }
        return normalizedPath;
    }

}
