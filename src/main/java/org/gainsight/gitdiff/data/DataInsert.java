package org.gainsight.gitdiff.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vmenon on 12/17/2015.
 */
public class DataInsert {
    /*DbUtil dbUtil;

    public DataInsert(DbUtil dbUtil) {
        this.dbUtil = dbUtil;
    }

    public void insertData(LastRun lastRun) {
        String query = "insert into diff_last_run(branch_name, branch_sha) values (%s,%s);";
        query = String.format(query,lastRun.getBranchName(), lastRun.getLastSha());
        dbUtil.execute(query);
    }

    public void insertData(RunInfo run) {
        String query = "insert into diff_runs(run_id, run_date, run_time) values (%s,%s,%s);";
        query = String.format(query, run.getRunId(), run.getRunDate(), run.getTimeRun());
        dbUtil.execute(query);
    }

    public void insertData(FoundFile foundFiles) {
        String query = "insert into diff_found_files" +
                "(run_id, branch_name, file_name, prop_key, " +
                "new_value, old_value, change_type, changed_by, change_date_time) " +
                "values (%s,'%s','%s','%s','%s','%s','%s','%s',%s);";
        query = String.format(query, foundFiles.getRunId(), foundFiles.getBranchName(),
                foundFiles.getFileName(), foundFiles.getPropKey(),
                foundFiles.getNewValue(), foundFiles.getOldValue(), foundFiles.getChangeType().getName(), foundFiles.getChangedBy(),
                foundFiles.getChangedDateTime());
        dbUtil.execute(query);
    }

    public List<FoundFile> getFoundFiles(String runId) {
        List<FoundFile> foundFiles = new ArrayList<FoundFile>();
        ResultSet resultSet = dbUtil.executeQuery("select * from diff_last_run;");
        try {
            while (resultSet.next()) {
                FoundFile foundFile = new FoundFile();
                foundFile.setId(resultSet.getInt("id"));
                foundFile.setRunId(resultSet.getInt("run_id"));
                foundFile.setBranchName(resultSet.getString("branch_name"));
                foundFile.setFileName(resultSet.getString("file_name"));
                foundFile.setPropKey(resultSet.getString("prop_key"));
                foundFile.setNewValue(resultSet.getString("new_value"));
                foundFile.setOldValue(resultSet.getString("old_value"));
                foundFile.setChangeType(ChangeType.valueOf(resultSet.getString("change_type")));
                foundFile.setChangedBy(resultSet.getString("changed_by"));
                foundFile.setChangedDateTime(resultSet.getDate("change_date_time"));

                foundFiles.add(foundFile);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return foundFiles;
    }

    public List<LastRun> getLastRunData(){
        List<LastRun> lastRuns = new ArrayList<LastRun>();
        ResultSet resultSet = dbUtil.executeQuery("select * from diff_last_run;");
        try {
            while (resultSet.next()) {
                LastRun lastRun = new LastRun();
                lastRun.setId(resultSet.getInt("id"));
                lastRun.setBranchName(resultSet.getString("branch_name"));
                lastRun.setLastSha(resultSet.getString("branch_sha"));
                lastRuns.add(lastRun);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lastRuns;
    }

    public LastRun getLastRunData(String branchName){
        LastRun lastRun = null;
        ResultSet resultSet = dbUtil.executeQuery(String.format("select * from diff_last_run where branch_name = '%s';",branchName));
        try {
            if (resultSet.next()) {
                lastRun = new LastRun();
                lastRun.setId(resultSet.getInt("id"));
                lastRun.setBranchName(resultSet.getString("branch_name"));
                lastRun.setLastSha(resultSet.getString("branch_sha"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lastRun;
    }

    public void updateLastRun(LastRun lastRun){
        String query = "update diff_last_run(branch_name, branch_sha)=(%s,%s) where id=%s;";
        query = String.format(query, lastRun.getBranchName(), lastRun.getLastSha(), lastRun.getId());
        dbUtil.execute(query);
    }

    public boolean lastRunExists(String branchName){
        boolean exists = dbUtil.execute(String.format("select * from diff_last_run where branch_name='%s'" , branchName));
        return exists;
    }*/

}
