package org.gainsight.gitdiff.data;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by vmenon on 12/16/2015.
 */
public class PropertyFileDetail implements Serializable{
    private String fileName;
    private String fileUrl;
    private Properties properties;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }
}
