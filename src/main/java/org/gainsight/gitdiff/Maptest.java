package org.gainsight.gitdiff;

import org.gainsight.gitdiff.data.RunInfo;

/**
 * Created by vmenon on 12/21/2015.
 */
public class Maptest {
    public static void main(String[] args) {
        GitDiff gitDiff = new GitDiff("","");
        RunInfo runInfo = new RunInfo();
        runInfo.setRunId(1);
        gitDiff.printDiffForRun(runInfo);
        gitDiff.printDiffForRun(runInfo);
        gitDiff.printDiffForRun(runInfo);
    }
}
