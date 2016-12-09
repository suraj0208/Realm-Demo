package com.suraj.realmdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import io.realm.Realm;
import io.realm.RealmResults;

public class ViewTransactionActivity extends Activity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        RealmResults<Transaction> results = realm.where(Transaction.class).findAll();

        ArrayList<Transaction> transactions = new ArrayList<>();

        for(Transaction transaction : results){
            transactions.add(transaction);
        }

        Collections.sort(transactions);

        TransactionAdapter transactionAdapter = new TransactionAdapter(this,transactions);

        ((ListView)findViewById(R.id.lstviewTransactions)).setAdapter(transactionAdapter);

    }
}
