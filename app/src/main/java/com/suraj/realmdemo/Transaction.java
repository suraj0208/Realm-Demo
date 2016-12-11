package com.suraj.realmdemo;

import android.support.annotation.NonNull;

import io.realm.RealmObject;

/**
 * Created by suraj on 9/12/16.
 */
public class Transaction extends RealmObject implements Comparable<Transaction> {
    private String name;
    private int amount;
    private String reason;
    private Long ID;
    private long timestamp;

    public Transaction() {
    }

    public Transaction(Transaction transaction) {
        this.name = transaction.name;
        this.amount = transaction.amount;
        this.reason = transaction.reason;
        this.ID = transaction.ID;
        this.timestamp = transaction.timestamp;
    }

    @Override
    public int compareTo(@NonNull Transaction transaction) {
        return Long.valueOf(this.timestamp).compareTo(transaction.timestamp);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

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

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }
}
