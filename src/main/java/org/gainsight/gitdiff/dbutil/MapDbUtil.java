package org.gainsight.gitdiff.dbutil;

import org.gainsight.gitdiff.data.BasePropertyInfo;
import org.gainsight.gitdiff.data.FoundFile;
import org.gainsight.gitdiff.data.LastRun;
import org.gainsight.gitdiff.data.RunInfo;
import org.mapdb.*;

import java.io.File;
import java.util.*;

/**
 * Created by vmenon on 12/18/2015.
 */
public class MapDbUtil implements DbUtil {
    public static final String dbFilePath = "git-diff-db1";
    private static final String lastRunStoreName = "lastRunStore";
    private static final String foundFileStoreName = "foundFileStore";
    private static final String runInfoStoreName = "runInfoStore";
    private static final String baseInfoStoreName = "baseInfoStore";

    private static final String lastRunAtomicIdStoreName = "lastRunAtomicIdStore";
    private static final String foundFileAtomicIdStoreName = "foundFileAtomicIdStore";
    private static final String runInfoAtomicIdStoreName = "runInfoAtomicIdStore";
    private static final String baseInfoAtomicIdStoreName = "baseInfoAtomicIdStore";

    TxMaker txMaker;

    DB db;

    public MapDbUtil(){
        init();
    }

    public void init() {
        //txMaker = DBMaker.newFileDB(new File((dbFilePath))).closeOnJvmShutdown().makeTxMaker();
        db = DBMaker.newFileDB(new File((dbFilePath))).closeOnJvmShutdown().make();
        //txMaker = DBMaker.newTempFileDB().closeOnJvmShutdown().makeTxMaker();
    }

    public LastRun getLastRun(String branchName) {
        //DB db = txMaker.makeTx();
        LastRun lastRun = null;
        BTreeMap<Long, LastRun> lastRunStore = db.createTreeMap(lastRunStoreName).makeOrGet();

        NavigableSet<Fun.Tuple2<String, Long>> branchIndex = new TreeSet<Fun.Tuple2<String, Long>>();

        Bind.secondaryKey(lastRunStore, branchIndex, new Fun.Function2<String, Long, LastRun>() {
            public String run(Long key, LastRun value) {
                return value.getBranchName();
            }
        });

        Iterable<Long> ids = Fun.filter(branchIndex, branchName);
        List<LastRun> lastRuns = new ArrayList<LastRun>();
        for(Long id : ids) {
            lastRuns.add(lastRunStore.get(id));
        }
        if(lastRuns.size()>1){
            throw new RuntimeException("More than one Last run found for the same branch name.");
        }
        if (lastRuns.size() == 1) {
            lastRun = lastRuns.get(0);
        }

        //db.close();
        return lastRun;
    }

    public BasePropertyInfo getBaseProperty(long runId) {
        //DB db = txMaker.makeTx();
        BasePropertyInfo basePropertyInfo = null;
        BTreeMap<Long, BasePropertyInfo> baseInfoStore = db.createTreeMap(baseInfoStoreName).makeOrGet();

        NavigableSet<Fun.Tuple2<Long, Long>> runIndex = new TreeSet<Fun.Tuple2<Long, Long>>();

        Bind.secondaryKey(baseInfoStore, runIndex, new Fun.Function2<Long, Long, BasePropertyInfo>() {
            public Long run(Long key, BasePropertyInfo value) {
                return value.getRunId();
            }
        });

        Iterable<Long> ids = Fun.filter(runIndex, runId);
        List<BasePropertyInfo> basePropertyInfos = new ArrayList<BasePropertyInfo>();
        for(Long id : ids) {
            basePropertyInfos.add(baseInfoStore.get(id));
        }
        if(basePropertyInfos.size()>1){
            throw new RuntimeException("More than one Base Info found for the same run id.");
        }
        if (basePropertyInfos.size() == 1) {
            basePropertyInfo = basePropertyInfos.get(0);
        }

        //db.close();
        return basePropertyInfo;

    }

    public void addBaseProperty(BasePropertyInfo basePropertyInfo) {
        //DB db = txMaker.makeTx();
        BTreeMap<Long, BasePropertyInfo> baseInfoStore = db.createTreeMap(baseInfoStoreName).makeOrGet();

        baseInfoStore.put(getAtomicId(baseInfoAtomicIdStoreName), basePropertyInfo);

        db.commit();
        //db.close();
    }

    public void addLastRun(LastRun lastRun) {
        if(lastRun == null) {
            throw new RuntimeException("LastRun object cannot be null");
        }
        //DB db = txMaker.makeTx();
        LastRun tLastRun = getLastRun(lastRun.getBranchName());

        BTreeMap<Long, LastRun> lastRunStore = db.createTreeMap(lastRunStoreName).makeOrGet();

        long lid;
        if(tLastRun == null){
            lid = getAtomicId(lastRunAtomicIdStoreName);
        } else {
            lid = tLastRun.getId();
        }
        lastRun.setId(lid);
        lastRunStore.put(lid, lastRun);
        db.commit();
        //db.close();
    }

    public void addFoundFiles(List<FoundFile> foundFiles) {
        //DB db = txMaker.makeTx();
        BTreeMap<Long, FoundFile> foundFileStore = db.createTreeMap(foundFileStoreName).makeOrGet();
        Atomic.Long id = getAtomicLong(foundFileAtomicIdStoreName, db);
        for (FoundFile foundFile : foundFiles) {
            foundFileStore.put(id.incrementAndGet(), foundFile);
        }
        db.commit();
        //db.close();
    }

    public void addFoundFile(FoundFile foundFile){
        //DB db = txMaker.makeTx();
        BTreeMap<Long, FoundFile> foundFileStore = db.createTreeMap(foundFileStoreName).makeOrGet();

        foundFileStore.put(getAtomicId(foundFileAtomicIdStoreName), foundFile);
        db.commit();
        //db.close();
    }

    public List<FoundFile> getFoundFiles(RunInfo runInfo) {
        //DB db = txMaker.makeTx();
        BTreeMap<Long, FoundFile> foundFileStore = db.createTreeMap(foundFileStoreName).makeOrGet();
        NavigableSet<Fun.Tuple2<Long, Long>> runIndex = new TreeSet<Fun.Tuple2<Long, Long>>();

        Bind.secondaryKey(foundFileStore, runIndex, new Fun.Function2<Long, Long, FoundFile>() {
            public Long run(Long key, FoundFile foundFile) {
                return foundFile.getRunId();
            }
        });

        List<FoundFile> foundFiles = new ArrayList<FoundFile>();
        Iterable<Long> ids = Fun.filter(runIndex, runInfo.getRunId());
        for(Long id : ids) {
            FoundFile foundFile = foundFileStore.get(id);
            foundFiles.add(foundFile);
        }
        db.commit();
        //db.close();
        return foundFiles;
    }

    public long addRunInfo(RunInfo runInfo) {
        if(runInfo == null) {
            runInfo = new RunInfo();
        }
        //DB db = txMaker.makeTx();
        BTreeMap<Long, RunInfo> runInfoStore = db.createTreeMap(runInfoStoreName).makeOrGet();
        long runId = getAtomicId(runInfoAtomicIdStoreName);
        if(runInfo.getRunId() == -1) {
            runInfo.setId(runId);
            runInfo.setRunId(runId);
        }
        runInfoStore.put(runId, runInfo);
        db.commit();
        //db.close();
        return runId;
    }

    public List<RunInfo> getAllRunInfo() {
        //DB db = txMaker.makeTx();
        BTreeMap<Long, RunInfo> runInfoStore = db.createTreeMap(runInfoStoreName).makeOrGet();
        List<RunInfo> runInfos = new ArrayList<RunInfo>();
        for(RunInfo runInfo: runInfoStore.values()){
            runInfos.add(runInfo);
        }
        //db.close();
        return runInfos;
    }

    public long getAtomicId(String storeName){
        //DB db = txMaker.makeTx();
        Atomic.Long atomic = getAtomicLong(storeName, db);
        long value = atomic.incrementAndGet();
        db.commit();
        //db.close();
        return value;
    }

    private Atomic.Long getAtomicLong(String storeName, DB db){
        Atomic.Long atomic = db.getAtomicLong(storeName);
        return atomic;
    }

}
