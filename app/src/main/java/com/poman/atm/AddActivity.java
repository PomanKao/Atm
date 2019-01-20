package com.poman.atm;

import android.content.ContentValues;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class AddActivity extends AppCompatActivity implements DatePickerFragment.OnDateSetListener {

    private static final String TAG = AddActivity.class.getSimpleName();
    private EditText edDate;
    private EditText edInfo;
    private EditText edAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViews();
    }

    private void findViews() {
        edDate = findViewById(R.id.ed_date);
        edInfo = findViewById(R.id.ed_info);
        edAmount = findViewById(R.id.ed_amount);
        edDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    pickerDate();
                }
            }
        });
    }

    public void add(View view) {
        String date = edDate.getText().toString();
        String info = edInfo.getText().toString();
        int amount = Integer.parseInt(edAmount.getText().toString());
        ExpenseHelper helper = new ExpenseHelper(this);
        ContentValues values = new ContentValues();
        values.put("cdate", date);
        values.put("info", info);
        values.put("amount", amount);
        long id = helper.getWritableDatabase().
                insert("expense", null,values);
        if (id > -1) {
            Toast.makeText(this, "新增完成", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "新增失敗", Toast.LENGTH_LONG).show();
        }
        finish();
    }

    public void pickerDate() {
        DialogFragment pickerFragment = DatePickerFragment.getInstance();
        pickerFragment.show(getSupportFragmentManager(),"datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String day = String.valueOf(dayOfMonth);
        if (day.length() < 2) {
            day = "0" + day;
        }
        String date = year+"-"+month+1+"-"+day;
        edDate.setText(date);
    }
}
