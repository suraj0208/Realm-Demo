package com.suraj.realmdemo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ViewTransactionActivity extends Activity {

    private Realm realm;
    private int owe_from = 0;
    private int owe_to = 0;
    private TextView tvOwe;
    private TextView tvOwn;
    private Spinner spinner;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        if (getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            RealmQuery<Transaction> realmQuery = realm.where(Transaction.class).equalTo("name", name);

            this.name=name;

            RealmResults<Transaction> realmResults = realmQuery.findAll();
            displayInListView(realmResults, true);

        } else {
            RealmResults<Transaction> results = realm.where(Transaction.class).findAll();
            displayInListView(results, false);
        }

        spinner = (Spinner)findViewById(R.id.spinWhichView);

        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner.getSelectedItemPosition()==0){
                    RealmResults<Transaction> results = realm.where(Transaction.class).findAll();
                    displayInListView(results, false);
                }else{
                    RealmQuery<Transaction> realmQuery = realm.where(Transaction.class).equalTo("name", name);
                    RealmResults<Transaction> realmResults = realmQuery.findAll();
                    displayInListView(realmResults, true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void displayInListView(RealmResults<Transaction> results, boolean single) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        owe_to=0;
        owe_from=0;

        for (Transaction transaction : results) {
            transactions.add(transaction);

            if (transaction.getAmount() < 0)
                owe_to += transaction.getAmount();
            else
                owe_from += transaction.getAmount();

        }

        owe_to *= -1;

        Collections.sort(transactions);



        tvOwn = (TextView) findViewById(R.id.tvtotalown);
        tvOwe = (TextView) findViewById(R.id.tvtotalowe);

        if (single) {
            tvOwe.setText("You owe Rs. " + owe_to + " and they owe Rs. " + owe_from);

            int diff = owe_from - owe_to;

            if (diff < 0) {
                tvOwn.setText("Give them Rs. " + Math.abs(diff) + ".");
            } else if (diff > 0) {
                tvOwn.setText("Take Rs. " + Math.abs(diff) + " from them.");
            } else {
                tvOwn.setText("You are all clear");
            }
            TransactionAdapter  transactionAdapter = new TransactionAdapter(getApplicationContext(),transactions);
            transactionAdapter.setPerson(true);


            ((ListView) findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

            return;

        }
        TransactionAdapter  transactionAdapter = new TransactionAdapter(getApplicationContext(),transactions);
        ((ListView) findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

        tvOwe.setText("You owe these people Rs. " + owe_to);
        tvOwn.setText("Following people owe you Rs. " + owe_from);
    }
}
