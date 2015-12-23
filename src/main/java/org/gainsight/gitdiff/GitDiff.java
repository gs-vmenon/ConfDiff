package org.gainsight.gitdiff;

import org.gainsight.gitdiff.data.*;
import org.gainsight.gitdiff.dbutil.DbUtil;
import org.gainsight.gitdiff.dbutil.MapDbUtil;
import org.kohsuke.github.*;

import java.io.*;
import java.util.*;

/**
 * Created by vmenon on 12/20/2015.
 */
public class GitDiff {
    String baseTag;
    GHRepository ghRepository;
    DbUtil dbQueryUtil;
    RunInfo runInfo;

    public GitDiff(String baseTag, String gitToken) {
        this.baseTag = baseTag;
        try {
            ghRepository = GitHub.connectUsingOAuth(gitToken).getRepository("Gainsight/mda");
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        dbQueryUtil = new MapDbUtil();
    }

    public void runConfCompare() {
        try {
            createRunInfo();
            Map<String, PropertyFileDetail> masterPropDetail = processBaseTag();
            Map<String, Map<String, PropertyFileDetail>> branchValues = processBranches();
            compareBranches(masterPropDetail, branchValues);
            printDiffForCurrentRun();
        }catch (IOException ioe){
            throw new RuntimeException(ioe);
        }
    }

    public void createRunInfo() {
        runInfo = new RunInfo();
        Date currentDate = new Date();
        runInfo.setRunDate(currentDate);
        dbQueryUtil.addRunInfo(runInfo);
    }

    public Map<String, PropertyFileDetail> getPropertyFiles(List<GHContent> confContents) {
        long startTime = System.currentTimeMillis();
        Map<String, PropertyFileDetail> fileDetails = new HashMap<String, PropertyFileDetail>();
        for (GHContent ghContent : confContents) {
            if (ghContent.isFile() && (ghContent.getName().endsWith(".conf") || ghContent.getName().endsWith(".properties"))) {
                PropertyFileDetail propertyFileDetail = new PropertyFileDetail();
                propertyFileDetail.setFileName(ghContent.getName());
                propertyFileDetail.setFileUrl(ghContent.getUrl());

                Properties prop = new Properties();
                try {
                    prop.load(ghContent.read());

                } catch (IOException ioe) {
                    System.out.println("IOEXception thrown whiel reading file " + ghContent.getUrl());

                }
                propertyFileDetail.setProperties(prop);

                fileDetails.put(ghContent.getName(), propertyFileDetail);
            }
        }
        long endTime = System.currentTimeMillis();
        System.out.println("TIme taken for parsing property files: " + (endTime - startTime));
        return fileDetails;
    }

    public Map<String, PropertyFileDetail> processBaseTag() throws IOException {
        Map<String, PropertyFileDetail> masterPropDetail;
        String baseTagSha = "";
        for (GHTag tag : ghRepository.listTags()) {
            if (tag.getName().contentEquals(baseTag)) {
                baseTagSha = tag.getCommit().getSHA1();
            }
        }
        if(baseTagSha.contentEquals("")){
            throw new RuntimeException("Unable to find any tag with name: " + baseTag);
        }
        if (evaluateBranchOrTag(baseTag, baseTagSha)) {
            List<GHContent> confContents1 = ghRepository.getDirectoryContent("gs-conf", baseTag);

            masterPropDetail = getPropertyFiles(confContents1);
            addLastRun(baseTag,baseTagSha,masterPropDetail);
            BasePropertyInfo basePropertyInfo = new BasePropertyInfo();
            basePropertyInfo.setRunId(runInfo.getRunId());
            basePropertyInfo.setBranchOrTagName(baseTag);
            basePropertyInfo.setPropertyFileDetailMap(masterPropDetail);

            dbQueryUtil.addBaseProperty(basePropertyInfo);
        } else {
            masterPropDetail = dbQueryUtil.getLastRun(baseTag).getPropertyFileDetailMap();
        }
        return masterPropDetail;
    }

    private void addLastRun(String branchTagName, String baseTagSha, Map<String, PropertyFileDetail> propertyFileDetailMap) {
        LastRun baseLastRun = dbQueryUtil.getLastRun(branchTagName);
        if(baseLastRun == null) {
            baseLastRun = new LastRun();
        }
        baseLastRun.setBranchName(branchTagName);
        baseLastRun.setLastSha(baseTagSha);
        baseLastRun.setPropertyFileDetailMap(propertyFileDetailMap);
        dbQueryUtil.addLastRun(baseLastRun);

    }

    private boolean evaluateBranchOrTag(String branchTagName, String sha) {
        LastRun lastRun = dbQueryUtil.getLastRun(branchTagName);
        boolean evaluate = false;
        if (lastRun == null) {
            evaluate = true;
        } else {
            if (!lastRun.getLastSha().contentEquals(sha)) {
                evaluate = true;
            }
        }
        return evaluate;
    }

    public Map<String, Map<String, PropertyFileDetail>> processBranches() throws IOException {
        Map<String, GHBranch> branchMap = ghRepository.getBranches();
        Map<String, Map<String, PropertyFileDetail>> branchValues = new HashMap<String, Map<String, PropertyFileDetail>>();
        List<String> branchesToSearch = getBranchesToSearch();
        for (Map.Entry<String, GHBranch> entry : branchMap.entrySet()) {
            String branchName = entry.getKey();
            GHBranch ghBranch = entry.getValue();
            if (branchesToSearch.size() > 0 && branchesToSearch.contains(branchName)) {
                List<GHContent> branchContents;
                Map<String, PropertyFileDetail> branchPropDetail;
                if (evaluateBranchOrTag(branchName, ghBranch.getSHA1())) {
                    try {
                        branchContents = ghRepository.getDirectoryContent("gs-conf", branchName);
                    } catch (Exception e) {
                        branchContents = new ArrayList<GHContent>();
                        System.out.println(e);
                    }
                    branchPropDetail = getPropertyFiles(branchContents);

                    addLastRun(branchName, ghBranch.getSHA1(), branchPropDetail);

                } else {
                    branchPropDetail = dbQueryUtil.getLastRun(branchName).getPropertyFileDetailMap();
                }
                branchValues.put(branchName, branchPropDetail);

            }
        }
        return branchValues;
    }

    private List<String> getBranchesToSearch() {
        String branchesTOSearchProperty = System.getProperty("branches.to.search", "");
        List<String> branchesToSearch = new ArrayList<String>();
        if (!branchesTOSearchProperty.contentEquals("")) {
            String[] temp = branchesTOSearchProperty.split(",");
            branchesToSearch.addAll(Arrays.asList(temp));
        }
        return branchesToSearch;
    }

    public void compareBranches(Map<String, PropertyFileDetail> masterPropDetail, Map<String, Map<String, PropertyFileDetail>> branchValues){
        for(Map.Entry<String, Map<String, PropertyFileDetail>> entry : branchValues.entrySet()){
            String branchName = entry.getKey();
            Map<String, PropertyFileDetail> branchPropertyDetails = entry.getValue();
            for(Map.Entry<String, PropertyFileDetail> masterEntry: masterPropDetail.entrySet()) {
                String fileName = masterEntry.getKey();
                if(branchPropertyDetails.containsKey(fileName)) {
                    PropertyFileDetail masterPropertyFileDetail = masterEntry.getValue();
                    PropertyFileDetail fileDetail = branchPropertyDetails.get(fileName);
                    List<FoundFile> foundFiles = compareProperty(masterPropertyFileDetail, fileDetail, branchName, runInfo);
                    dbQueryUtil.addFoundFiles(foundFiles);
                }
            }
        }
    }

    private List<FoundFile> compareProperty(PropertyFileDetail base,
                                                   PropertyFileDetail branch, String branchName, RunInfo runInfo) {
        Properties baseProp = base.getProperties();
        Properties branchProp = branch.getProperties();
        List<FoundFile> foundFiles = new ArrayList<FoundFile>();
        for (Map.Entry<Object, Object> entry : baseProp.entrySet()) {
            String baseKey = (String) entry.getKey();
            String baseValue = (String) entry.getValue();
            FoundFile foundFile = null;
            if (baseProp.containsKey(baseKey)) {
                if(!(baseValue.contentEquals(baseProp.getProperty(baseKey)))) {
                    foundFile = new FoundFile();
                    foundFile.setFileName(base.getFileName());
                    foundFile.setPropKey(baseKey);
                    foundFile.setOldValue(baseValue);
                    foundFile.setNewValue(baseProp.getProperty(baseKey));
                    foundFile.setChangeType(ChangeType.CHANGED);
                }

            } else {
                foundFile = new FoundFile();
                foundFile.setFileName(base.getFileName());
                foundFile.setPropKey(baseKey);
                foundFile.setOldValue(baseValue);
                foundFile.setNewValue("");
                foundFile.setChangeType(ChangeType.REMOVED);
            }
            if (foundFile != null) {
                foundFile.setRunId(runInfo.getRunId());
                foundFile.setBranchName(branchName);
                foundFiles.add(foundFile);
            }
        }

        Set<Object> tempBaseSet = ((Properties)baseProp.clone()).keySet();
        Set<Object> tempBranchSet = ((Properties)branchProp.clone()).keySet();
        tempBranchSet.removeAll(tempBaseSet);

        for(Object key: tempBranchSet){
            FoundFile foundFile;
            foundFile = new FoundFile();
            foundFile.setFileName(branch.getFileName());
            foundFile.setPropKey((String) key);
            foundFile.setOldValue("");
            foundFile.setNewValue(branchProp.getProperty((String) key));
            foundFile.setChangeType(ChangeType.NEW);
            foundFile.setBranchName(branchName);
            foundFile.setRunId(runInfo.getRunId());
            foundFiles.add(foundFile);
        }

        return foundFiles;
    }

    public void printDiffForCurrentRun(){
        printDiffForRun(runInfo);
    }

    public void printDiffForRun(RunInfo cRunInfo) {
        List<FoundFile> foundFiles = dbQueryUtil.getFoundFiles(cRunInfo);
        if(foundFiles.size() == 0){
            System.out.println("No difference found.");
        } else {
            Map<String, Map<String, List<FoundFile>>> branchGroup = new HashMap<String, Map<String, List<FoundFile>>>();

            for(FoundFile foundFile : foundFiles){
                String fileName = foundFile.getFileName();
                String branchName = foundFile.getBranchName();
                Map<String, List<FoundFile>> fileGroup;
                if(branchGroup.containsKey(branchName)){
                    fileGroup = branchGroup.get(branchName);
                } else {
                    fileGroup = new HashMap<String, List<FoundFile>>();
                }
                List<FoundFile> sameFileGroup;
                if( fileGroup.containsKey(fileName)){
                    sameFileGroup = fileGroup.get(fileName);
                } else {
                    sameFileGroup = new ArrayList<FoundFile>();
                }
                sameFileGroup.add(foundFile);
                fileGroup.put(fileName,sameFileGroup);
                branchGroup.put(branchName,fileGroup);
            }
            String tableDivision = String.format("%250s"," \n").replaceAll(" ","-");
            StringBuilder stringBuilder = new StringBuilder();
            for(Map.Entry<String, Map<String, List<FoundFile>>> entry : branchGroup.entrySet()){
                String branchName = entry.getKey();
                Map<String, List<FoundFile>> branchFiles = entry.getValue();
                stringBuilder.append(tableDivision);
                stringBuilder.append("Following is the diff for the branch: " + branchName+"\n");
                String table = "| %-80s | %-50s | %-50s | %-10s\n";
                for(Map.Entry<String, List<FoundFile>> fileEntry:branchFiles.entrySet()){
                    stringBuilder.append(tableDivision);
                    stringBuilder.append("\t\tDiff for file: " + fileEntry.getKey()+" and branch: " +branchName + "\n");
                    stringBuilder.append(tableDivision);
                    stringBuilder.append(String.format(table, "Key", "New Value", "Old Value", "Change Type"));
                    stringBuilder.append(tableDivision);

                    for(FoundFile foundFile : fileEntry.getValue()) {
                        stringBuilder.append(String.format(table, foundFile.getPropKey(), foundFile.getNewValue(), foundFile.getOldValue(), foundFile.getChangeType().getName()));
                    }
                    stringBuilder.append(tableDivision + "\n\n");
                }
            }
            OutputStreamWriter osw = null;
            try {
                osw = new OutputStreamWriter(new FileOutputStream("./target/git-diff.txt"));
                osw.write(stringBuilder.toString());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                if (osw != null) {
                    try {
                        osw.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
