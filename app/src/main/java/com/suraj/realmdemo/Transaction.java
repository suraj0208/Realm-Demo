package com.suraj.realmdemo;

import io.realm.RealmObject;

/**
 * Created by suraj on 9/12/16.
 */
public class Transaction extends RealmObject implements Comparable<Transaction> {
    protected String name;
    protected int amount;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    protected String reason;
    protected Long ID;

    public Transaction(){

    }

    public Transaction(String name, int amount, String reason, Long URI) {
        this.name = name;
        this.amount = amount;
        this.reason = reason;
        this.ID = URI;
    }

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
        return Long.valueOf(this.timestamp).compareTo(transaction.timestamp);
    }

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
