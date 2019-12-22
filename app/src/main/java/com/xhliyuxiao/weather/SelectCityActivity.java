package com.xhliyuxiao.weather;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xhliyuxiao.weather.bean.City;

import java.util.ArrayList;
import java.util.List;

public class SelectCityActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SelectCityActivity";

    private ImageView backBtn;
    private ImageView addCityBtn;
    private ListView cityListView;
    private TextView cityTv;

    private WeatherApplication application;
    private ArrayList<String> arrayList;
    private List<City> cities;

    private String updateCityName = "杭州";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        backBtn = findViewById(R.id.title_select_city_back);
        backBtn.setOnClickListener(this);

        addCityBtn = findViewById(R.id.add_city_btn);
        addCityBtn.setOnClickListener(this);

        updateCityName = getIntent().getStringExtra("cityName");
        if (updateCityName == null || updateCityName.length() == 0) {
            updateCityName = "杭州";
        }

        cityTv = findViewById(R.id.title_select_city_name);
        cityTv.setText("当前城市: " + updateCityName);

        application = (WeatherApplication) getApplication();
        cities = application.getCityList();
        arrayList = new ArrayList<>();

        for (int i = 0; i < cities.size(); i++) {
            String name = cities.get(i).getName();
            arrayList.add(name);
        }

        cityListView = findViewById(R.id.select_city_lv);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(SelectCityActivity.this, android.R.layout.simple_list_item_1, arrayList);
        cityListView.setAdapter(adapter);

        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SelectCityActivity.this.updateCityName = cities.get(i).getName();
                ;
                Log.d(TAG, "onItemClick: " + SelectCityActivity.this.updateCityName);
                SelectCityActivity.this.cityTv.setText("当前城市: " + updateCityName);
            }
        };

        cityListView.setOnItemClickListener(itemClickListener);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_select_city_back) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("cityName", updateCityName);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else if (view.getId() == R.id.add_city_btn) {
            showDialog();
        }
    }

    private void showDialog() {
        View dialogView = LayoutInflater.from(SelectCityActivity.this).inflate(R.layout.dialog_add_city, null);
        final AlertDialog.Builder inputDialog = new AlertDialog.Builder(SelectCityActivity.this);

        final EditText cityNameEt = dialogView.findViewById(R.id.city_name);

        inputDialog.setView(dialogView).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = cityNameEt.getText().toString();
                City city = new City(name, name, "101040100", "中国");
                application.addCity(city);
            }
        });


        inputDialog.create().show();
    }
}
