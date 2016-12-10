package com.suraj.realmdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
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
    private TextView tvPersonName;
    private Spinner spinner;
    private String name;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        tvOwn = (TextView) findViewById(R.id.tvtotalown);
        tvOwe = (TextView) findViewById(R.id.tvtotalowe);
        tvPersonName = (TextView) findViewById(R.id.tvTransactionPersonName);

        listView = (ListView) findViewById(R.id.lstviewTransactions);


        if (getIntent().getExtras() != null) {
            String name = getIntent().getExtras().getString("name");
            this.name = name;
            tvPersonName.setText(name);
            displayInListView(getPersonTransactions(name), true);
        } else {
            displayInListView(getAllReamTransactions(), false);
            tvPersonName.setText(getResources().getString(R.string.all_transactions));
        }

        spinner = (Spinner) findViewById(R.id.spinWhichView);

        spinner.setSelection(1);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemPosition() == 0) {
                    displayInListView(getAllReamTransactions(), false);
                    tvPersonName.setText(getResources().getString(R.string.all_transactions));
                } else {
                    displayInListView(getPersonTransactions(name), true);
                    tvPersonName.setText(name);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    public void displayInListView(final RealmResults<Transaction> results, final boolean single) {
        final ArrayList<Transaction> transactions = new ArrayList<>();

        owe_to = 0;
        owe_from = 0;

        for (Transaction transaction : results) {
            transactions.add(transaction);

            if (transaction.getAmount() < 0)
                owe_to += transaction.getAmount();
            else
                owe_from += transaction.getAmount();

        }

        owe_to *= -1;

        Collections.sort(transactions);


        if (single) {
            tvOwe.setText("You owe Rs. " + owe_to + " and they owe Rs. " + owe_from);

            TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactions);
            transactionAdapter.setPerson(true);
            listView.setAdapter(transactionAdapter);

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Transaction transaction = transactions.get(i);

                    realm.beginTransaction();

                    RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo("timestamp", transaction.getTimestamp()).findAll();
                    realmResults.deleteAllFromRealm();

                    realm.commitTransaction();

                    ViewTransactionActivity.this.displayInListView(getPersonTransactions(name), single);
                    return false;

                }
            });

        } else {
            TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactions);
            listView.setAdapter(transactionAdapter);

            tvOwe.setText("You owe people Rs. " + owe_to + " and people owe you Rs. " + owe_from);


            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Transaction transaction = transactions.get(i);

                    realm.beginTransaction();

                    RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo("timestamp", transaction.getTimestamp()).findAll();
                    realmResults.deleteFirstFromRealm();

                    realm.commitTransaction();

                    ViewTransactionActivity.this.displayInListView(getAllReamTransactions(), single);

                    return false;
                }
            });
        }

        int diff = owe_from - owe_to;

        if (diff < 0) {
            tvOwn.setText("Give them Rs. " + Math.abs(diff) + ".");
        } else if (diff > 0) {
            tvOwn.setText("Take Rs. " + Math.abs(diff) + " from them.");
        } else {
            tvOwn.setText("You are all clear");
        }


    }

    RealmResults<Transaction> getAllReamTransactions() {
        return realm.where(Transaction.class).findAll();
    }

    RealmResults<Transaction> getPersonTransactions(String name) {
        RealmQuery<Transaction> realmQuery = realm.where(Transaction.class).equalTo("name", name);
        return realmQuery.findAll();
    }
}
