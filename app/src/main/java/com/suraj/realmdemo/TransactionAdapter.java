package com.suraj.realmdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by suraj on 9/12/16.
 */
public class TransactionAdapter extends ArrayAdapter {
    protected ArrayList<Transaction> transactions;
    protected Context context;

    public void setPerson(boolean person) {
        this.person = person;
    }

    protected boolean person;

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

        if(person)
            ((TextView)row.findViewById(R.id.tvTransactionRowName)).setText(transactions.get(position).getReason());
        else
            ((TextView)row.findViewById(R.id.tvTransactionRowName)).setText(transactions.get(position).getName());

        TextView tvTransactionState = (TextView)row.findViewById(R.id.tvTransactionRowState);

        if(transactions.get(position).getAmount()>0){
            tvTransactionState.setText("They owe you");
        }else{
            tvTransactionState.setText("You owe them");
        }

        ((TextView)row.findViewById(R.id.tvTransactionRowAmount)).setText("Rs. " + Math.abs(transactions.get(position).getAmount()));

        Date date = new Date(transactions.get(position).getTimestamp());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formatter.format(date);

        ((TextView)row.findViewById(R.id.tvdate)).setText(dateString);


        return row;
    }
}
