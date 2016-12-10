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
import android.view.View;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    private EditText etName;
    private EditText etAmount;
    private EditText etReason;
    private Spinner spinTransaction;
    private Realm realm;
    private String name;
    private long id;
    private ImageView imgviewPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


        imgviewPhoto =((ImageView) findViewById(R.id.imgviewContact));

        imgviewPhoto.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_xxl)));


        Realm.init(this);
        realm = Realm.getDefaultInstance();

        showFavorites();

    }

    private void showFavorites() {
        class FavTransaction extends Transaction {

            public FavTransaction(Transaction transaction) {
                super(transaction.name, transaction.amount, transaction.reason, transaction.ID);
            }

            @Override
            public int hashCode() {
                return this.name.hashCode();
            }

            @Override
            public boolean equals(Object obj) {

                if (obj instanceof FavTransaction)
                    return this.name.equals(((FavTransaction) obj).getName());

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


        List<FavTransaction> favTransactionList = new ArrayList<>();

        for (int i = 0; i < entryList.size(); i++) {
            favTransactionList.add(entryList.get(i).getKey());
        }


        ImageView[] imageViews = {(ImageView) findViewById(R.id.imgbtnFreq1),
                (ImageView) findViewById(R.id.imgbtnFreq2),
                (ImageView) findViewById(R.id.imgbtnFreq3),
                (ImageView) findViewById(R.id.imgbtnFreq4),
                (ImageView) findViewById(R.id.imgbtnFreq5)};

        int k = 0;

        for (int i = 0; k < 5 && i < favTransactionList.size(); i++) {

            if (displayContactPictureFromID(imageViews[k], favTransactionList.get(i).getID()))
                k++;

        }


    }

    public boolean displayContactPictureFromID(final ImageView imageView, final Long id) {
        Bitmap photo = null;

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            if (inputStream != null) inputStream.close();

            if (photo == null) {
                return false;
            }


            //imageView.setImageBitmap(photo);
            imageView.setImageDrawable(new RoundImageDrawable(photo));
            imageView.setVisibility(View.VISIBLE);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RealmQuery realmQuery = realm.where(Transaction.class).equalTo("ID", id);

                    final Transaction transaction = (Transaction) realmQuery.findFirst();

                    etName.setText(transaction.getName());
                    name = transaction.getName();
                    imgviewPhoto.setImageDrawable(imageView.getDrawable());


                }
            });

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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

    private void commitTransaction() {
        if (etName.getText().length() == 0 || etAmount.getText().length() == 0 || etReason.getText().length()==0) {
            Toast.makeText(getApplicationContext(), "Fill Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.beginTransaction();

        Transaction transaction = realm.createObject(Transaction.class);
        transaction.setName(etName.getText().toString());
        transaction.setID(id);
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

        showFavorites();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            getContactData(data);
        }

    }

    private void getContactData(Intent data) {
        Uri contactData = data.getData();
        Cursor c = getContentResolver().query(contactData, null, null, null, null);
        if (c != null && c.moveToFirst()) {
            id = c.getLong(c.getColumnIndex(ContactsContract.Contacts._ID));

            String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            etName.setText(name);
            this.name=name;

            Bitmap photo =null;

            try {
                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id));

                if (inputStream != null) {
                    photo = BitmapFactory.decodeStream(inputStream);
                }

                if (inputStream != null) inputStream.close();

                if (photo == null) {
                    imgviewPhoto.setImageDrawable(new RoundImageDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_xxl)));
                    return;
                }

                imgviewPhoto.setImageDrawable(new RoundImageDrawable(photo));

            } catch (IOException e) {
                e.printStackTrace();
            }


            c.close();
        }
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

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
            }

            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

}
