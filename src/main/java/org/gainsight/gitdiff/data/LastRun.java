package org.gainsight.gitdiff.data;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by vmenon on 12/17/2015.
 */
public class LastRun implements Serializable {
    private long id = -1;

    private String branchName;
    private String lastSha;

    private Map<String, PropertyFileDetail> propertyFileDetailMap;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getLastSha() {
        return lastSha;
    }

    public void setLastSha(String lastSha) {
        this.lastSha = lastSha;
    }

    public Map<String, PropertyFileDetail> getPropertyFileDetailMap() {
        return propertyFileDetailMap;
    }

    public void setPropertyFileDetailMap(Map<String, PropertyFileDetail> propertyFileDetailMap) {
        this.propertyFileDetailMap = propertyFileDetailMap;
    }
}
