package com.suraj.realmdemo;

import io.realm.RealmObject;

/**
 * Created by suraj on 9/12/16.
 */
public class Transaction extends RealmObject implements Comparable<Transaction> {
    private String name;
    private int amount;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private long timestamp;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Transaction transaction) {
        return new Long(this.timestamp).compareTo(transaction.timestamp);
    }
}
