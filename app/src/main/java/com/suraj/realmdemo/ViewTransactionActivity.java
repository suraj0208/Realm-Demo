package com.suraj.realmdemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
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
    private static final String OWNER = "Suraj";
    private Realm realm;
    private TextView tvOwe;
    private TextView tvOwn;
    private TextView tvPersonName;
    private Spinner spinner;
    private String name;
    private ListView listView;
    private boolean person;
    private ArrayList<Transaction> transactions;
    private View btnShare;
    private StringBuilder email;

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
        btnShare = findViewById(R.id.btnShare);


        if (getIntent().getExtras() != null && getIntent().getExtras().getString("name") != null) {
            spinner.setSelection(1);
            String name = getIntent().getExtras().getString("name");
            this.name = name;
            tvPersonName.setText(name);
            person = true;
            btnShare.setVisibility(View.VISIBLE);
            displayInListView(getPersonTransactions(name));
        } else {
            person = false;
            spinner.setSelection(0);
            spinner.setEnabled(false);
            displayInListView(getAllRealmTransactions());
            tvPersonName.setText(getResources().getString(R.string.all_transactions));
            btnShare.setVisibility(View.VISIBLE);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (spinner.getSelectedItemPosition() == 0) {
                    person = false;
                    displayInListView(getAllRealmTransactions());
                    btnShare.setVisibility(View.INVISIBLE);
                    tvPersonName.setText(getResources().getString(R.string.all_transactions));
                } else {
                    person = true;
                    displayInListView(getPersonTransactions(name));
                    btnShare.setVisibility(View.VISIBLE);
                    tvPersonName.setText(name);
                    email  = new StringBuilder();
                    getEmailFromNameAsync(name,email);
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

                if (person)
                    ViewTransactionActivity.this.displayInListView(getPersonTransactions(name));
                else
                    ViewTransactionActivity.this.displayInListView(getAllRealmTransactions());
                return false;

            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder content = new StringBuilder();

                Intent share = new Intent(android.content.Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_EMAIL, new String[]{email.toString()});

                String prevDate = null;
                String currentDate;

                for (Transaction transaction : transactions) {
                    currentDate=getDateFromTimeStamp(transaction.getTimestamp());

                    if(!currentDate.equals(prevDate)){
                        content.append("\n\non ").append(getDateFromTimeStamp(transaction.getTimestamp())).append("\n");
                    }

                    prevDate=currentDate;

                    if (transaction.getAmount() < 0)
                        content.append(OWNER + " owes ").append(transaction.getName()).append(" Rs ").append(transaction.getAmount() * -1).append(" for ").append(transaction.getReason()).append("\n");
                    else
                        content.append(transaction.getName()).append(" owes ").append(OWNER).append(" Rs ").append(transaction.getAmount()).append(" for ").append(transaction.getReason()).append("\n");

                }

                share.putExtra(Intent.EXTRA_SUBJECT, "Transactions with " + transactions.get(0).getName());
                share.putExtra(Intent.EXTRA_TEXT, content.toString());

                startActivity(Intent.createChooser(share, "Share Transactions"));


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

        TransactionAdapter transactionAdapter = new TransactionAdapter(getApplicationContext(), transactions, this);
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
        if (person)
            ((TextView) view.findViewById(R.id.tvTransactionRowName)).setText(transaction.getReason());
        else
            ((TextView) view.findViewById(R.id.tvTransactionRowName)).setText(transaction.getName());

        TextView tvTransactionState = (TextView) view.findViewById(R.id.tvTransactionRowState);

        if (transaction.getAmount() > 0) {
            tvTransactionState.setText("They owe you");
        } else {
            tvTransactionState.setText("You owe them");
        }

        ((TextView) view.findViewById(R.id.tvTransactionRowAmount)).setText("Rs. " + Math.abs(transaction.getAmount()));

        ((TextView) view.findViewById(R.id.tvdate)).setText(getDateFromTimeStamp(transaction.getTimestamp()));
    }

    public String getDateFromTimeStamp(long timestamp) {
        return new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(new Date(timestamp));
    }

    public void getEmailFromNameAsync(final String contactName,final StringBuilder result) {

        (new AsyncTask<Void,Void,StringBuilder>(){
            @Override
            protected StringBuilder doInBackground(Void... voids) {

                ArrayList<String> emails = new ArrayList<String>();

                Cursor cursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                        new String[]{contactName}, null);

                while (cursor != null && cursor.moveToNext()) {
                    emails.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)));
                }


                if (cursor != null)
                    cursor.close();

                return emails.size()>0?new StringBuilder(emails.get(0)) :null;
            }

            @Override
            protected void onPostExecute(StringBuilder stringBuilder) {
                result.append(stringBuilder);
            }
        }).execute();



    }

}
