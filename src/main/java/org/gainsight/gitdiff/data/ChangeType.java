package org.gainsight.gitdiff.data;

/**
 * Created by vmenon on 12/17/2015.
 */
public enum ChangeType {
    NEW("NEW"),
    CHANGED("CHANGED"),
    REMOVED("REMOVED");

    private String name;

    ChangeType(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
