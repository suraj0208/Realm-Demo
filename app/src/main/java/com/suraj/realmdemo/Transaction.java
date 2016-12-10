package com.suraj.realmdemo;

import io.realm.RealmObject;

/**
 * Created by suraj on 9/12/16.
 */
public class Transaction extends RealmObject implements Comparable<Transaction> {
    protected String name;
    protected int amount;
    protected Long ID;

    public Transaction(){

    }

    public Transaction(String name, int amount, Long URI) {
        this.name = name;
        this.amount = amount;
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
