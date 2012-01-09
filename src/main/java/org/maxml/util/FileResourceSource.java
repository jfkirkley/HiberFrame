package org.maxml.util;

import java.io.File;


public interface FileResourceSource extends ResourceSource {

    public File getFile(String spec);

}
