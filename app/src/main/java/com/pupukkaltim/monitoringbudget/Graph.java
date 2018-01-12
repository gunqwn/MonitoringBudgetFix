package com.pupukkaltim.monitoringbudget;

/**
 * Created by ROG-STRIX on 05/01/2018.
 */

public class Graph {
    private String CommitedItemName;
    private int persentase;

    public Graph(String commitedItemName, int persentase) {
        CommitedItemName = commitedItemName;
        this.persentase = persentase;
    }

    public String getCommitedItemName() {
        return CommitedItemName;
    }

    public void setCommitedItemName(String commitedItemName) {
        CommitedItemName = commitedItemName;
    }

    public int getPersentase() {
        return persentase;
    }

    public void setPersentase(int persentase) {
        this.persentase = persentase;
    }
}
