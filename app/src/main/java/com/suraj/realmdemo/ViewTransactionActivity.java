package com.suraj.realmdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmQuery;
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

        if (getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            RealmQuery<Transaction> realmQuery = realm.where(Transaction.class).equalTo("name", name);

            System.out.println(name);

            RealmResults<Transaction> realmResults = realmQuery.findAll();
            displayInListView(realmResults, true);

        } else {
            RealmResults<Transaction> results = realm.where(Transaction.class).findAll();
            displayInListView(results, false);
        }
    }

    public void displayInListView(RealmResults<Transaction> results, boolean single) {
        ArrayList<Transaction> transactions = new ArrayList<>();

        for (Transaction transaction : results) {
            transactions.add(transaction);

            if (transaction.getAmount() < 0)
                owe_to += transaction.getAmount();
            else
                owe_from += transaction.getAmount();

        }

        owe_to *= -1;

        Collections.sort(transactions);

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactions);

        ((ListView) findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

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

            }


            return;

        }

        tvOwe.setText("You owe these people Rs. " + owe_to);
        tvOwn.setText("Following people owe you Rs. " + owe_from);
    }
}
