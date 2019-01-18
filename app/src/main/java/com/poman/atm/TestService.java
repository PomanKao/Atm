package com.poman.atm;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TestService extends IntentService {
    public static final String ACTION_TEST_DONE = "action_test_done";
    private static final String TAG = TestService.class.getSimpleName();

    public TestService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String name = intent.getStringExtra("NAME");
        Log.d(TAG, "onHandleIntent: " + name);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent doneIntent = new Intent();
        doneIntent.setAction(ACTION_TEST_DONE);
        sendBroadcast(doneIntent);
    }
}
