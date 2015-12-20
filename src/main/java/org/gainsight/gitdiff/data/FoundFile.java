package org.gainsight.gitdiff.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vmenon on 12/17/2015.
 */
public class FoundFile implements Serializable {

    private int id;
    private long runId;

    private String branchName = "";
    private String fileName = "";
    private String propKey = "";
    private String oldValue = "" ;
    private String newValue = "";
    private ChangeType changeType ;
    private String changedBy = "";
    private Date changedDateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public Date getChangedDateTime() {
        return changedDateTime;
    }

    public void setChangedDateTime(Date changedDateTime) {

        this.changedDateTime = changedDateTime;
    }
}
