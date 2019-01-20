package com.poman.atm;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CAMERA = 5;
    private  EditText edUserid;
    private  EditText edPasswd;
    private CheckBox cbRemUserid;
    private Intent testService;
    private AlertDialog.Builder alert;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "onReceive: Test " + intent.getAction());
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(TestService.ACTION_TEST_DONE);
        registerReceiver(receiver,filter);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        stopService(testService);
        unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Fragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.add(R.id.container_news, NewsFragment.getInstance());
        fragmentTransaction.commit();
        // Service
//        serviceTest();
        // Check the permission of camera
//        camera();
        // Access the SharedPreference
//        settingsTest();
        findViews();
        // Test Async Task
//        new TestTask().execute("http://tw.yahoo.com");
        alert = new AlertDialog.Builder(this);
    }

    private void serviceTest() {
        testService = new Intent(this,TestService.class);
        testService.putExtra("NAME","T1");
        startService(testService);
        testService.putExtra("NAME","T2");
        startService(testService);
        testService.putExtra("NAME","T3");
        startService(testService);
    }

    public void map(View view) {
        startActivity(new Intent(this,MapsActivity.class));
    }

    public class TestTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute: ");
            Toast.makeText(LoginActivity.this, "onPreExecute",Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Log.d(TAG, "onPostExecute: ");
            Toast.makeText(LoginActivity.this,"onPostExecute" + integer,Toast.LENGTH_LONG).show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            int data = 0;
            try {
                URL url = new URL(strings[0]);
                data = url.openStream().read();
                Log.d(TAG, "TestTask: " + data);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }
    }

    private void findViews() {
        edUserid = findViewById(R.id.userid);
        edPasswd = findViewById(R.id.passwd);
        cbRemUserid = findViewById(R.id.cb_rem_userid);
        cbRemUserid.setChecked(
                getSharedPreferences("atm",MODE_PRIVATE)
                .getBoolean("REMEMBER_USERID",false));
        cbRemUserid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getSharedPreferences("atm",MODE_PRIVATE)
                        .edit()
                        .putBoolean("REMEMBER_USERID", isChecked)
                        .apply();
            }
        });
        String userid = getSharedPreferences("atm",MODE_PRIVATE)
                .getString("USERID", "");
        edUserid.setText(userid);
    }

    private void settingsTest() {
        getSharedPreferences("atm",MODE_PRIVATE)
                .edit()
                .putInt("LEVEL", 3)
                .putString("NAME", "Poman")
                .apply();
        int level = getSharedPreferences("atm",MODE_PRIVATE)
                .getInt("LEVEL",0);
        Log.d(TAG, "onCreate: " + level);
    }

    private void camera() {
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_GRANTED) {
//            takePhoto();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        }
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            }
        }
    }

    public void login(View view) {
        final String userid = edUserid.getText().toString();
        final String passwd = edPasswd.getText().toString();

        if (!checkNetwork()) {
            showNetworkAlert();
            return;
        }

        if (userid.equals("") || passwd.equals("")) {
            showAlert();
            return;
        }

        FirebaseDatabase.getInstance().getReference("users").child(userid).child("password")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String pw = (String) dataSnapshot.getValue();
                        Log.d(TAG, "onDataChange: "+pw);
                        if (pw.equals(passwd)) {
                            boolean remember = getSharedPreferences("atm",MODE_PRIVATE)
                                    .getBoolean("REMEMBER_USERID", false);
                            if (remember) {
                                // save userid
                                getSharedPreferences("atm", MODE_PRIVATE)
                                        .edit()
                                        .putString("USERID", userid)
                                        .apply();
                            }
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            showAlert();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, "onCancelled: ");
                    }
                });
    }

    private boolean checkNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null) && networkInfo.isConnected();
    }

    private void showNetworkAlert() {
        alert.setTitle("網路異常")
                .setMessage("請檢查網路連線是否正常")
                .setPositiveButton("OK", null)
                .show();
    }

    private void showAlert() {
        alert.setTitle("登入失敗")
            .setMessage("請檢查帳號密碼")
            .setPositiveButton("OK",null)
            .show();
    }

    public void quit(View vIew) {
        finish();
    }

}
