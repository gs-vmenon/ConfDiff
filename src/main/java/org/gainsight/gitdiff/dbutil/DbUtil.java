package org.gainsight.gitdiff.dbutil;

import org.gainsight.gitdiff.data.BasePropertyInfo;
import org.gainsight.gitdiff.data.FoundFile;
import org.gainsight.gitdiff.data.LastRun;
import org.gainsight.gitdiff.data.RunInfo;

import java.util.List;

/**
 * Created by vmenon on 12/18/2015.
 */
public interface DbUtil {
    public void init();
    public LastRun getLastRun(String branchName);
    public BasePropertyInfo getBaseProperty(long runId);
    public void addBaseProperty(BasePropertyInfo basePropertyInfo);
    public void addLastRun(LastRun lastRun);
    public void addFoundFiles(List<FoundFile> foundFiles);
    public List<FoundFile> getFoundFiles(RunInfo runInfo);
    public long addRunInfo(RunInfo runInfo);
    public List<RunInfo> getAllRunInfo();
}
