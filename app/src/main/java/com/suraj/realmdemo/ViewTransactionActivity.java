package com.suraj.realmdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class ViewTransactionActivity extends Activity implements TransactionDisplayManager {
    private Realm realm;
    private TextView tvOwe;
    private TextView tvOwn;
    private TextView tvPersonName;
    private Spinner spinner;
    private String name;
    private ListView listView;
    private boolean person;
    private ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_transaction);

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        tvOwn = (TextView) findViewById(R.id.tvtotalown);
        tvOwe = (TextView) findViewById(R.id.tvtotalowe);
        tvPersonName = (TextView) findViewById(R.id.tvTransactionPersonName);
        spinner = (Spinner) findViewById(R.id.spinWhichView);
        listView = (ListView) findViewById(R.id.lstviewTransactions);


        if (getIntent().getExtras() !=null && getIntent().getExtras().getString("name") != null) {
            spinner.setSelection(1);
            String name = getIntent().getExtras().getString("name");
            this.name = name;
            tvPersonName.setText(name);
            person=true;
            displayInListView(getPersonTransactions(name));
        } else {
            person=false;
            spinner.setSelection(0);
            spinner.setEnabled(false);
            displayInListView(getAllRealmTransactions());
            tvPersonName.setText(getResources().getString(R.string.all_transactions));
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemPosition() == 0) {
                    person=false;
                    displayInListView(getAllRealmTransactions());
                    tvPersonName.setText(getResources().getString(R.string.all_transactions));
                } else {
                    person=true;
                    displayInListView(getPersonTransactions(name));
                    tvPersonName.setText(name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Transaction transaction = transactions.get(i);

                realm.beginTransaction();

                RealmResults<Transaction> realmResults = realm.where(Transaction.class).equalTo("timestamp", transaction.getTimestamp()).findAll();
                realmResults.deleteAllFromRealm();

                realm.commitTransaction();

                if(person)
                    ViewTransactionActivity.this.displayInListView(getPersonTransactions(name));
                else
                    ViewTransactionActivity.this.displayInListView(getAllRealmTransactions());
                return false;

            }
        });
    }

    public void displayInListView(final RealmResults<Transaction> results) {
        transactions = new ArrayList<>();

        int owe_to = 0;
        int owe_from = 0;

        for (Transaction transaction : results) {
            transactions.add(transaction);

            if (transaction.getAmount() < 0)
                owe_to += transaction.getAmount();
            else
                owe_from += transaction.getAmount();

        }

        owe_to *= -1;

        Collections.sort(transactions);

        if (person) {
            tvOwe.setText("You owe Rs. " + owe_to + " and they owe Rs. " + owe_from);
        } else {
            tvOwe.setText("You owe people Rs. " + owe_to + " and people owe you Rs. " + owe_from);
        }

        TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactions,this);
        listView.setAdapter(transactionAdapter);



        int diff = owe_from - owe_to;

        if (diff < 0) {
            tvOwn.setText("Give them Rs. " + Math.abs(diff) + ".");
        } else if (diff > 0) {
            tvOwn.setText("Take Rs. " + Math.abs(diff) + " from them.");
        } else {
            tvOwn.setText("You are all clear");
        }


    }

    RealmResults<Transaction> getAllRealmTransactions() {
        return realm.where(Transaction.class).findAll();
    }

    RealmResults<Transaction> getPersonTransactions(String name) {
        RealmQuery<Transaction> realmQuery = realm.where(Transaction.class).equalTo("name", name);
        return realmQuery.findAll();
    }

    @Override
    public void displayTransaction(View view, Transaction transaction) {
        if(person)
            ((TextView)view.findViewById(R.id.tvTransactionRowName)).setText(transaction.getReason());
        else
            ((TextView)view.findViewById(R.id.tvTransactionRowName)).setText(transaction.getName());

        TextView tvTransactionState = (TextView)view.findViewById(R.id.tvTransactionRowState);

        if(transaction.getAmount()>0){
            tvTransactionState.setText("They owe you");
        }else{
            tvTransactionState.setText("You owe them");
        }

        ((TextView)view.findViewById(R.id.tvTransactionRowAmount)).setText("Rs. " + Math.abs(transaction.getAmount()));

        Date date = new Date(transaction.getTimestamp());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        String dateString = formatter.format(date);

        ((TextView)view.findViewById(R.id.tvdate)).setText(dateString);
    }
}
