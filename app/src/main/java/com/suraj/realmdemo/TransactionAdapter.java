package com.suraj.realmdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by suraj on 9/12/16.
 */
public class TransactionAdapter extends ArrayAdapter {
    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, R.layout.transaction_row);

        this.context = context;
        this.transactions = transactions;

    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = layoutInflater.inflate(R.layout.transaction_row,parent,false);

        ((TextView)row.findViewById(R.id.tvTransactionRowName)).setText(transactions.get(position).getName());

        TextView tvTransactionState = (TextView)row.findViewById(R.id.tvTransactionRowState);

        if(transactions.get(position).getAmount()>0){
            tvTransactionState.setText("They owe you Rs.");
        }else{
            tvTransactionState.setText("You owe them Rs.");
        }

        ((TextView)row.findViewById(R.id.tvTransactionRowAmount)).setText(Integer.toString(Math.abs(transactions.get(position).getAmount())));

        return row;
    }
}
