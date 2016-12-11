package com.suraj.realmdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by suraj on 9/12/16.
 */
public class TransactionAdapter extends ArrayAdapter {
    private ArrayList<Transaction> transactions;
    private Context context;
    private TransactionDisplayManager transactionDisplayManager;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions, TransactionDisplayManager transactionDisplayManager) {
        super(context, R.layout.transaction_row);
        this.context = context;
        this.transactions = transactions;
        this.transactionDisplayManager = transactionDisplayManager;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = layoutInflater.inflate(R.layout.transaction_row,parent,false);

        this.transactionDisplayManager.displayTransaction(row, transactions.get(position));

        return row;
    }
}

interface TransactionDisplayManager {
    void displayTransaction(View view, Transaction transaction);
}
