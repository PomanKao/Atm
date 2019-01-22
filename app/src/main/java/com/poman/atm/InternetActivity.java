package com.poman.atm;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class InternetActivity {

    private static InternetActivity instance;

    public static InternetActivity getInstance() {
        if (instance == null) {
            instance = new InternetActivity();
        }
        return instance;
    }

    // ICMP
    public boolean isOnlineByPing() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public boolean isOnlineBySocket() {
        int timeoutmMs = 1500;
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress("74.125.203.103", 53);

        try {
            socket.connect(address, timeoutmMs);
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public class InternetCheck extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            return null;
        }
    }
}
