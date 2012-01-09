package org.maxml.util;

import java.io.File;

public class FilePropertyFileResourceSource extends PropertyFileResourceSource implements FileResourceSource{

    protected String baseDir=null;
    
    public FilePropertyFileResourceSource(String propertyFile) {
        super(propertyFile);
    }

    
    public String getFileName(String spec) {
        String fileName = super.getString(spec);
        if( baseDir != null ) {
            return baseDir + fileName; 
        }
        return fileName;
    }
    
    public File getFile(String spec) {
        String fileName = getFileName(spec);
        
        return new File(fileName);
    }


    public String getBaseDir() {
        return baseDir;
    }


    public void setBaseDir(String baseDir) {
        this.baseDir = FileUtils.i().ensureEndSlash(baseDir);
    }

    
    
}
