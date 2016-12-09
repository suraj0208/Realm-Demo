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
    private int owe = 0;
    private int own = 0;
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
                owe += transaction.getAmount();
            else
                own += transaction.getAmount();

        }
        owe *=-1;

        Collections.sort(transactions);

        TransactionAdapter transactionAdapter = new TransactionAdapter(this, transactions);

        ((ListView) findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

        tvOwn = (TextView) findViewById(R.id.tvtotalown);
        tvOwe = (TextView) findViewById(R.id.tvtotalowe);

        tvOwe.setText("You owe Rs. " + owe + " to following people.");
        tvOwn.setText("Following people owe Rs. " + own + " to you.");


    }
}
