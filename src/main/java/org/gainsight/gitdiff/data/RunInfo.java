package org.gainsight.gitdiff.data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by vmenon on 12/17/2015.
 */
public class RunInfo implements Serializable{
    private long id = -1;
    private long runId = -1;
    private Date runDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public Date getRunDate() {
        return runDate;
    }

    public void setRunDate(Date runDate) {
        this.runDate = runDate;
    }

}
