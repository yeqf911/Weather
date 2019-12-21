package com.xhliyuxiao.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xhliyuxiao.weather.bean.WeatherInfo;
import com.xhliyuxiao.weather.bean.WeatherResponse;
import com.xhliyuxiao.weather.utils.Constant;
import com.xhliyuxiao.weather.utils.HttpClient;
import com.xhliyuxiao.weather.utils.JsonUtil;
import com.xhliyuxiao.weather.utils.NetUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int UPDATE_TODAY_WEATHER = 1;

    private ImageView updateViewBtn;
    private ImageView selectCityBtn;

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, weatherTv, windTv, cityNameTv;

    private ImageView weatherImg, pmImg;

    private WeatherInfo weatherToday;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeatherView();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        updateWeather();
    }

    private void initView() {
        updateViewBtn = findViewById(R.id.title_update_btn);
        updateViewBtn.setOnClickListener(this);

        selectCityBtn = findViewById(R.id.title_city_manager);
        selectCityBtn.setOnClickListener(this);

        cityTv = findViewById(R.id.city);
        cityNameTv = findViewById(R.id.title_city_name);
        timeTv = findViewById(R.id.time);
        humidityTv = findViewById(R.id.humidity);
        weekTv = findViewById(R.id.week_today);
        pmQualityTv = findViewById(R.id.pm2_5_quality);
        pmImg = findViewById(R.id.pm2_5_img);
        pmDataTv = findViewById(R.id.pm_data);
        weatherImg = findViewById(R.id.weather_img);
        windTv = findViewById(R.id.wind);
        weatherTv = findViewById(R.id.weather);
        temperatureTv = findViewById(R.id.temperature);

        cityNameTv.setText("N/A");
        cityTv.setText("N/A");
        timeTv.setText("N/A");
        humidityTv.setText("N/A");
        pmQualityTv.setText("N/A");
        pmDataTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        weatherTv.setText("N/A");
        windTv.setText("N/A");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_update_btn:
                updateWeather();
                break;
            case R.id.title_city_manager:
                startSelectCityActivity();
                break;
        }
    }

    private void startSelectCityActivity() {
        Intent intent = new Intent(this, SelectCityActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @SuppressLint("SetTextI18n")
    private void updateTodayWeatherView() {
        cityNameTv.setText(this.weatherToday.getCitynm() + "天气");
        cityTv.setText(this.weatherToday.getCitynm());
        timeTv.setText(this.weatherToday.getDays());
        humidityTv.setText("湿度 " + this.weatherToday.getHumidity());
        pmQualityTv.setText(this.weatherToday.getAqi());
        pmDataTv.setText(this.weatherToday.getAqi());
        weekTv.setText(this.weatherToday.getWeek());
        temperatureTv.setText(this.weatherToday.getTemperature().replace("/", "~"));
        weatherTv.setText(this.weatherToday.getWeather());
        windTv.setText(this.weatherToday.getWind());
    }

    private void updateWeather() {

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "hangzhou");
            Log.d("Weather", cityCode);
            getTodayWeather(cityCode);
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
    }

    private void getTodayWeather(String city) {
        final String url = Constant.WEATHER_TODAY + city;
        Log.d(TAG, "getWeather: " + url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = HttpClient.get(url);
                    Log.d(TAG, "run: " + response);
                    parseWeatherToday(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void parseWeatherToday(String jsonStr) {
        Type jsonType = new TypeToken<WeatherResponse<WeatherInfo>>() {
        }.getType();
        WeatherResponse<WeatherInfo> weatherResponse = JsonUtil.parseJson(jsonStr, jsonType);
        this.weatherToday = Objects.requireNonNull(weatherResponse).getResult();

        Message msg = new Message();
        msg.what = UPDATE_TODAY_WEATHER;
        handler.sendMessage(msg);
    }
}
