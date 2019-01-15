package com.poman.atm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class ContactsActivity extends AppCompatActivity {

    private static final int REQUEST_CONTACT = 80;
    private static final String TAG = ContactsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        int permission = ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            readContacts();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CONTACT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CONTACT) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContacts();
            }
        }
    }

    private void readContacts() {
    // read contacts
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                null,null,null,null);
        List<Contact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Contact contact = new Contact(id, name);
            int hasPhone = cursor.getInt(
                    cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            Log.d(TAG, "readContacts: " + name);
            if (hasPhone == 1) {
                Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[]{String.valueOf(id)},
                        null);
                while (phoneCursor.moveToNext()) {
                    String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                    Log.d(TAG, "readContacts: \t" + phone);
                    contact.getPhones().add(phone);
                }
            }
            contacts.add(contact);
        }
        ContactAdapter adapter = new ContactAdapter(contacts);
        RecyclerView contactRV = findViewById(R.id.contact_recycler);
        contactRV.setHasFixedSize(true);
        contactRV.setLayoutManager(new LinearLayoutManager(this));
        contactRV.setAdapter(adapter);
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {
        List<Contact> contacts;
        public ContactAdapter(List<Contact> contacts) {
            this.contacts = contacts;
        }

        @NonNull
        @Override
        public ContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_2, parent, false);
            return new  ContactHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ContactHolder contactHolder, int position) {
            Contact contact = contacts.get(position);
            contactHolder.nameText.setText(contact.getName());
            StringBuilder sb = new StringBuilder();
            for (String phone : contact.getPhones()) {
                sb.append(phone);
                sb.append(" ");
            }
            contactHolder.phoneText.setText(sb.toString());
        }

        @Override
        public int getItemCount() {
            return contacts.size();
        }

        public class ContactHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView phoneText;
            public ContactHolder(@NonNull View itemView) {
                super(itemView);
                nameText = itemView.findViewById(android.R.id.text1);
                phoneText = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
