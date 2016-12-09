package com.suraj.realmdemo;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText etName;
    private EditText etAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etAmount = (EditText) findViewById(R.id.etTransactionAmount);

        (findViewById(R.id.btnPick)).setOnClickListener(this);

        (findViewById(R.id.btn10)).setOnClickListener(this);
        (findViewById(R.id.btn20)).setOnClickListener(this);
        (findViewById(R.id.btn30)).setOnClickListener(this);
        (findViewById(R.id.btn50)).setOnClickListener(this);
        (findViewById(R.id.btn100)).setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btnPick:
                pickContact();
                break;
            case R.id.btn10:
            case R.id.btn20:
            case R.id.btn30:
            case R.id.btn50:
            case R.id.btn100:
                setAmount(view);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c = getContentResolver().query(contactData, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                etName.setText(name);
                c.close();
            }
        }

    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void setAmount(View amount) {
        etAmount.setText(((Button) amount).getText());
    }
}
