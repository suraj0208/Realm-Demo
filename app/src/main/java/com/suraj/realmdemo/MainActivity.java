package com.suraj.realmdemo;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, FrequentsAdapter.ClickListener {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private EditText etName;
    private EditText etAmount;
    private EditText etReason;
    private Spinner spinTransaction;
    private Realm realm;
    private String name;
    private long current_id;
    private ImageView imgviewPhoto;
    private FrequentsAdapter frequentsAdapter;
    //private ImageView[] imageViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etAmount = (EditText) findViewById(R.id.etTransactionAmount);
        etReason = (EditText) findViewById(R.id.etReason);

        spinTransaction = (Spinner) findViewById(R.id.spinTransaction);

        (findViewById(R.id.btnPick)).setOnClickListener(this);
        (findViewById(R.id.btnCommit)).setOnClickListener(this);
        (findViewById(R.id.btnViewTransactions)).setOnClickListener(this);

        (findViewById(R.id.btn10)).setOnClickListener(this);
        (findViewById(R.id.btn20)).setOnClickListener(this);
        (findViewById(R.id.btn30)).setOnClickListener(this);
        (findViewById(R.id.btn50)).setOnClickListener(this);
        (findViewById(R.id.btn100)).setOnClickListener(this);


        imgviewPhoto = ((ImageView) findViewById(R.id.imgviewContact));

        imgviewPhoto.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_xxl)));


        Realm.init(this);

        realm = Realm.getDefaultInstance();

        showFrequents();

    }

    @Override
    protected void onResume() {
        super.onResume();
        showFrequents();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            getContactData(data);
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnPick:
                pickContact();
                break;

            case R.id.btnCommit:
                commitTransaction();
                break;

            case R.id.btnViewTransactions:
                Intent intent = new Intent(MainActivity.this, ViewTransactionActivity.class);
                intent.putExtra("name", name);
                startActivity(intent);
                break;

            case R.id.btn10:
            case R.id.btn20:
            case R.id.btn30:
            case R.id.btn50:
            case R.id.btn100:
                setAmount(view);
                break;

        }
    }

    private void showFrequents() {
        class FavTransaction extends Transaction {

            public FavTransaction(Transaction transaction) {
                super(transaction);
            }

            @Override
            public int hashCode() {
                return super.getName().hashCode();
            }

            @Override
            public boolean equals(Object obj) {

                if (obj instanceof FavTransaction)
                    return super.getName().equals(((FavTransaction) obj).getName());

                return super.equals(obj);
            }
        }

        RealmResults<Transaction> results = realm.where(Transaction.class).findAll();

        HashMap<FavTransaction, Integer> hashMap = new HashMap<>();

        for (Transaction transaction : results) {
            FavTransaction favTransaction = new FavTransaction(transaction);
            if (hashMap.containsKey(favTransaction)) {
                hashMap.put(favTransaction, hashMap.get(favTransaction) + 1);
            } else {
                hashMap.put(favTransaction, 1);
            }
        }

        List<Map.Entry<FavTransaction, Integer>> entryList = new ArrayList<>(hashMap.entrySet());

        Collections.sort(entryList, new Comparator<Map.Entry<FavTransaction, Integer>>() {
            @Override
            public int compare(Map.Entry<FavTransaction, Integer> favTransactionIntegerEntry, Map.Entry<FavTransaction, Integer> t1) {
                return t1.getValue().compareTo(favTransactionIntegerEntry.getValue());
            }
        });

        List<Contact> contacts = new ArrayList<>();

        for (int i = 0; i < entryList.size(); i++) {
            FavTransaction favTransaction = entryList.get(i).getKey();

            Bitmap bitmap = getPhotoFromId(getIDFromName(favTransaction.getName()));

            if(bitmap ==null)
                bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.contacts_xxl);

            contacts.add(new Contact(favTransaction.getName(), new RoundImageDrawable(bitmap)));

        }

        frequentsAdapter = new FrequentsAdapter(this, contacts, this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerFrequents);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(frequentsAdapter);

    }

    public boolean displayContactPictureFromID(final ImageView imageView, final Long id, final String contactName) {
        Bitmap photo = getPhotoFromId(id);

        if (photo == null) {
            return false;
        }

        imageView.setImageDrawable(new RoundImageDrawable(photo));
        imageView.setVisibility(View.VISIBLE);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RealmQuery realmQuery = realm.where(Transaction.class).equalTo("name", contactName);
                Transaction transaction = (Transaction) realmQuery.findFirst();
                etName.setText(transaction.getName());
                name = transaction.getName();
                imgviewPhoto.setImageDrawable(imageView.getDrawable());
            }
        });

        return true;
    }


    private void commitTransaction() {
        if (etName.getText().length() == 0 || etAmount.getText().length() == 0 || etReason.getText().length() == 0) {
            Toast.makeText(getApplicationContext(), "Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();

        Transaction transaction = realm.createObject(Transaction.class);
        transaction.setName(etName.getText().toString());
        transaction.setID(current_id);
        transaction.setReason(etReason.getText().toString());

        int amount = Integer.parseInt(etAmount.getText().toString());

        if (spinTransaction.getSelectedItemPosition() == 1)
            amount *= -1;

        transaction.setAmount(amount);
        transaction.setTimestamp(System.currentTimeMillis());

        realm.commitTransaction();

        etAmount.setText("");
        etReason.setText("");

        Toast.makeText(getApplicationContext(), "Data Entered", Toast.LENGTH_SHORT).show();

        showFrequents();

    }

    private void getContactData(Intent data) {
        getPermissionToReadUserContacts();

        if (data == null)
            return;

        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            current_id = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));

            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            etName.setText(name);
            this.name = name;

            Bitmap photo = getPhotoFromId(current_id);

            if (photo == null)
                imgviewPhoto.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_xxl)));
            else
                imgviewPhoto.setImageDrawable(new RoundImageDrawable(photo));

            c.close();
        }
    }

    private Bitmap getPhotoFromId(long id) {
        getPermissionToReadUserContacts();
        Bitmap photo = null;
        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            if (inputStream != null) inputStream.close();

            if (photo == null) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return photo;

    }

    private void pickContact() {
        getPermissionToReadUserContacts();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void setAmount(View amount) {
        etAmount.setText(((Button) amount).getText());
    }

    public void getPermissionToReadUserContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }

    }

    public long getIDFromName(String contactName) {
        ArrayList<String> phones = new ArrayList<String>();

        Cursor cursor = getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                new String[]{contactName}, null);

        while (cursor != null && cursor.moveToNext()) {
            phones.add(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID)));
        }

        if (cursor != null)
            cursor.close();

        return Long.parseLong(phones.get(0));
    }

    @Override
    public void itemClicked(View view, int position) {
        RealmQuery realmQuery = realm.where(Transaction.class).equalTo("name", frequentsAdapter.getContacts().get(position).getName());
        Transaction transaction = (Transaction) realmQuery.findFirst();
        etName.setText(transaction.getName());
        name = transaction.getName();
        imgviewPhoto.setImageDrawable(frequentsAdapter.getContacts().get(position).getPicture());
    }
}
