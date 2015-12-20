package org.gainsight.gitdiff;

import org.gainsight.gitdiff.data.RunInfo;

/**
 * Created by vmenon on 12/20/2015.
 */
public class GitDiffMain {

    public static void main(String[] args) {
        //System.setProperty("branches.to.search","mda_successplans,master");
        String baseTag = System.getProperty("base.tag","");
        String oauthToken = System.getProperty("oauth.token", "");
        GitDiff gitDiff = new GitDiff(baseTag, oauthToken);
        gitDiff.runConfCompare();
    }
}
