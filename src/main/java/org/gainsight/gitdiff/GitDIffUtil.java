package org.gainsight.gitdiff;

import org.gainsight.gitdiff.data.PropertyFileDetail;
import org.kohsuke.github.GHContent;

import java.io.IOException;
import java.util.*;

/**
 * Created by vmenon on 12/16/2015.
 */
public class GitDIffUtil {

    /*public static Map<String, PropertyFileDetail> getPropertyFiles (List<GHContent> confContents){
        long startTime = System.currentTimeMillis();
        Map<String, PropertyFileDetail> fileDetails = new HashMap<String, PropertyFileDetail>();
        for(GHContent ghContent : confContents) {
            if(ghContent.isFile() && (ghContent.getName().endsWith(".conf") || ghContent.getName().endsWith(".properties"))) {
                PropertyFileDetail propertyFileDetail = new PropertyFileDetail();
                propertyFileDetail.setFileName(ghContent.getName());
                propertyFileDetail.setFileUrl(ghContent.getUrl());

                Properties prop = new Properties();
                try {
                    prop.load(ghContent.read());

                }catch (IOException ioe) {
                    System.out.println("IOEXception thrown whiel reading file " + ghContent.getUrl());

                }
                propertyFileDetail.setProperties(prop);

                fileDetails.put(ghContent.getName(), propertyFileDetail);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("TIme taken for parsing property files: " + (endTime - startTime));
        return fileDetails;
    }*/

    /*public static void createTables(DbUtil dbUtil) {
        String runsTable = "create table if not exists diff_runs(id bigint primary key auto_increment, " +
                "run_id int, " +
                "run_date DATE default CURRENT_DATE, " +
                "run_time TIMESTAMP default CURRENT_TIMESTAMP);";

        String diffTable = "create table if not exists diff_found_files(id bigint primary key auto_increment, run_id int, " +
                "branch_name VARCHAR(50), " +
                "file_name VARCHAR(50), " +
                "prop_key VARCHAR(250), " +
                "new_value VARCHAR(255), " +
                "old_value VARCHAR(255) default '', " +
                "change_type VARCHAR(50), " +
                "changed_by VARCHAR(255) default '', " +
                "change_date_time DATETIME," +
                "FOREIGN KEY(run_id) " +
                "REFERENCES diff_runs(run_id));";

        String lastRunTable = "create table if not exists diff_last_run(id bigint primary key auto_increment, " +
                "branch_name  VARCHAR(50)," +
                "branch_sha  VARCHAR(50))";

        dbUtil.execute(runsTable);
        dbUtil.execute(diffTable);
        dbUtil.execute(lastRunTable);
    }*/


}
