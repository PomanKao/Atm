package com.poman.atm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FinanceActivity extends AppCompatActivity {

    private static final String TAG = FinanceActivity.class.getSimpleName();
    private ExpenseHelper helper;
    private ExpenseAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete) {
            if (helper.deleteAll()) {
                Cursor cursor = helper.getReadableDatabase().
                        query("expense",
                        null,null,null,
                        null,null,"cdate DESC");
                adapter.setCursor(cursor);
                Log.d(TAG, "onOptionsItemSelected: "+adapter.getItemCount());
                recyclerView.removeAllViews();
                adapter.notifyDataSetChanged();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_finance, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addIntent = new Intent(FinanceActivity.this, AddActivity.class);
                startActivity(addIntent);
            }
        });

        recyclerView = findViewById(R.id.finance_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        helper = new ExpenseHelper(this);
        Cursor cursor = helper.getReadableDatabase().
                query("expense",
                        null,null,null,
                        null,null,"cdate DESC");
        adapter = new ExpenseAdapter(cursor);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (helper != null)
            helper.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (helper == null)
            helper = new ExpenseHelper(this);
        Cursor cursor = helper.getReadableDatabase().
                query("expense",
                        null,null,null,
                        null,null,"cdate DESC");
        adapter = new ExpenseAdapter(cursor);
        recyclerView.setAdapter(adapter);
    }

    public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseHolder> {
        private Cursor cursor;

        public ExpenseAdapter(Cursor cursor) {
            this.cursor = cursor;
        }

        public void setCursor(Cursor cursor) {
            this.cursor = cursor;
        }

        @NonNull
        @Override
        public ExpenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
            View view = getLayoutInflater().inflate(R.layout.expense_item, parent, false);
            return new ExpenseHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ExpenseHolder expenseHolder, int position) {
            cursor.moveToPosition(position);
            String date = cursor.getString(cursor.getColumnIndex("cdate"));
            String info = cursor.getString(cursor.getColumnIndex("info"));
            int amount = cursor.getInt(cursor.getColumnIndex("amount"));
            expenseHolder.dateText.setText(date);
            expenseHolder.infoText.setText(info);
            expenseHolder.amountText.setText(String.valueOf(amount));
        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ExpenseHolder extends RecyclerView.ViewHolder {
            TextView dateText;
            TextView infoText;
            TextView amountText;
            public ExpenseHolder(@NonNull View itemView) {
                super(itemView);
                dateText = itemView.findViewById(R.id.item_date);
                infoText = itemView.findViewById(R.id.item_info);
                amountText = itemView.findViewById(R.id.item_amount);
            }
        }
    }
}
