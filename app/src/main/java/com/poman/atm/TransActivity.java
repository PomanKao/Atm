package com.poman.atm;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TransActivity extends AppCompatActivity {

    private static final String TAG = TransActivity.class.getSimpleName();
    private List<Transaction> transations;
    private RecyclerView recyclerView;
    private int tryServerTimes;
    private String fakeJson = "[{\"account\":\"jack\",\"date\":\"20160501\",\"amount\":1500,\"type\":0}" +
            ",{\"account\":\"jack\",\"date\":\"20160501\",\"amount\":6000,\"type\":0}" +
            ",{\"account\":\"jack\",\"date\":\"20160502\",\"amount\":3000,\"type\":1}" +
            ",{\"account\":\"jack\",\"date\":\"20160501\",\"amount\":20000,\"type\":0}" +
            ",{\"account\":\"jack\",\"date\":\"20160501\",\"amount\":1000,\"type\":1}]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trans);

        recyclerView = findViewById(R.id.trans_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        new TransTask().execute("http://atm201605.appspot.com/h");
        getServerDate();
    }

    private void getServerDate() {
        tryServerTimes = 0;
        final OkHttpClient client = new OkHttpClient();
        client.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(7, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url("http://atm201605.appspot.com/h")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e.getCause().equals(SocketTimeoutException.class)) {
                    e.printStackTrace();
                }
                parseGSON(fakeJson);
                /*if (e.getCause().equals(SocketTimeoutException.class) && tryServerTimes < 3) {
                    tryServerTimes++;
                    client.newCall(call.request()).enqueue(this);
                } else {
                    e.printStackTrace();
                }*/
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 非UI thread，不能存取UI物件
                final String json = response.body().string();
                Log.d(TAG, "onResponse: " + json);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        parseJSON(json);
                        parseGSON(json);
                    }
                });
            }
        });
    }

    private void parseGSON(String json) {
        Gson gson = new Gson();
        transations = gson.fromJson(json,
                new TypeToken<ArrayList<Transaction>>(){}.getType());
        TransAdapter adpater = new TransAdapter();
        recyclerView.setAdapter(adpater);
    }

    private void parseJSON(String jsonStr) {
        transations = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(jsonStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                transations.add(new Transaction(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        TransAdapter adpater = new TransAdapter();
        recyclerView.setAdapter(adpater);
    }

    public class TransAdapter extends RecyclerView.Adapter<TransAdapter.TransHolder> {
        @NonNull
        @Override
        public TransHolder onCreateViewHolder(@NonNull ViewGroup partent, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_transaction,partent,false);
            return new TransHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull TransHolder transHolder, int i) {
            Transaction trans = transations.get(i);
            transHolder.bindTo(trans);
            transHolder.dateText.setText(trans.date);
        }

        @Override
        public int getItemCount() {
            return transations.size();
        }

        public class TransHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView amountText;
            TextView typeText;
            public TransHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.item_date);
                amountText = itemView.findViewById(R.id.item_amount);
                typeText = itemView.findViewById(R.id.item_type);
            }

            public void bindTo(Transaction trans) {
                Log.d(TAG, "bindTo: " + trans.getAmount());
                dateText.setText(trans.getDate());
                amountText.setText(String.valueOf(trans.getAmount()));
                typeText.setText(String.valueOf(trans.getType()));
            }
        }
    }

    public class TransTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            StringBuilder sb = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                InputStream is = url.openStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    line = br.readLine();
                }
                Log.d(TAG, "doInBackground: " + sb.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d(TAG, "onPostExecute: " + s);
        }
    }
}
