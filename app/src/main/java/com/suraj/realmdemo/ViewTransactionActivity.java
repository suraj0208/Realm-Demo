package com.suraj.realmdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewTransactionActivity extends Activity {

    private Realm realm;
    private int owe_from = 0;
    private int owe_to = 0;
    private TextView tvOwe;
    private TextView tvOwn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<Transaction> results = realm.where(Transaction.class).findAll();

        ArrayList<Transaction> transactions = new ArrayList<>();

        for (Transaction transaction : results) {
            transactions.add(transaction);

            if (transaction.getAmount() < 0)
                owe_to += transaction.getAmount();
            else
                owe_from += transaction.getAmount();

        }

        owe_to *=-1;

        Collections.sort(transactions);g

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactions);

        ((ListView) findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

        tvOwn = (TextView) findViewById(R.id.tvtotalown);
        tvOwe = (TextView) findViewById(R.id.tvtotalowe);

        tvOwe.setText("You owe Rs. " + owe_to + " to following people.");
        tvOwn.setText("Following people owe Rs. " + owe_from + " to you.");


    }
}
