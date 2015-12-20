package org.gainsight.gitdiff.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by vmenon on 12/19/2015.
 */
public class BasePropertyInfo implements Serializable{
    long runId;
    private String branchOrTagName;
    private Map<String, PropertyFileDetail> propertyFileDetailMap;

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public Map<String, PropertyFileDetail> getPropertyFileDetailMap() {
        return propertyFileDetailMap;
    }

    public void setPropertyFileDetailMap(Map<String, PropertyFileDetail> propertyFileDetailMap) {
        this.propertyFileDetailMap = propertyFileDetailMap;
    }

    public String getBranchOrTagName() {
        return branchOrTagName;
    }

    public void setBranchOrTagName(String branchOrTagName) {
        this.branchOrTagName = branchOrTagName;
    }
}
