package com.xhliyuxiao.weather;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import java.util.List;
import java.util.Objects;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int UPDATE_TODAY_WEATHER = 1;
    private static final int UPDATE_FUTURE_WEATHER = 2;

    private ImageView updateViewBtn;
    private ImageView selectCityBtn;
    private String currentCityName = "";

    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv, temperatureTv, weatherTv, windTv, cityNameTv;


    // 第1天
    private TextView week1T, temp1T, weather1T, wind1T;

    // 第2天
    private TextView week2T, temp2T, weather2T, wind2T;

    // 第3天
    private TextView week3T, temp3T, weather3T, wind3T;

    // 第4天
    private TextView week4T, temp4T, weather4T, wind4T;

    // 第5天
    private TextView week5T, temp5T, weather5T, wind5T;

    private ImageView weatherImg, pmImg;

    private List<WeatherInfo> weatherFuture;
    private WeatherInfo weatherToday;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeatherView();
                    break;
                case UPDATE_FUTURE_WEATHER:
                    updateFutureWeatherView();
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

        currentCityName = getIntent().getStringExtra("cityName");
        if (currentCityName == null || currentCityName.length() == 0) {
            currentCityName = "杭州";
        }

        updateWeather();
    }

    private void initView() {
        // today
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
//        pmQualityTv.setText("N/A");
        pmDataTv.setText("N/A");
        weekTv.setText("N/A");
        temperatureTv.setText("N/A");
        weatherTv.setText("N/A");
        windTv.setText("N/A");

        // 第1天
        week1T = findViewById(R.id.week1T);
        temp1T = findViewById(R.id.temp1T);
        weather1T = findViewById(R.id.weather1T);
        wind1T = findViewById(R.id.wind1T);
        week1T.setText("N/A");
        temp1T.setText("N/A");
        weather1T.setText("N/A");
        wind1T.setText("N/A");

        // 第2天
        week2T = findViewById(R.id.week2T);
        temp2T = findViewById(R.id.temp2T);
        weather2T = findViewById(R.id.weather2T);
        wind2T = findViewById(R.id.wind2T);
        week2T.setText("N/A");
        temp2T.setText("N/A");
        weather2T.setText("N/A");
        wind2T.setText("N/A");

        // 第3天
        week3T = findViewById(R.id.week3T);
        temp3T = findViewById(R.id.temp3T);
        weather3T = findViewById(R.id.weather3T);
        wind3T = findViewById(R.id.wind3T);
        week3T.setText("N/A");
        temp3T.setText("N/A");
        weather3T.setText("N/A");
        wind3T.setText("N/A");

        // 第4天
        week4T = findViewById(R.id.week4T);
        temp4T = findViewById(R.id.temp4T);
        weather4T = findViewById(R.id.weather4T);
        wind4T = findViewById(R.id.wind4T);
        week4T.setText("N/A");
        temp4T.setText("N/A");
        weather4T.setText("N/A");
        wind4T.setText("N/A");

        // 第5天
        week5T = findViewById(R.id.week5T);
        temp5T = findViewById(R.id.temp5T);
        weather5T = findViewById(R.id.weather5T);
        wind5T = findViewById(R.id.wind5T);
        week5T.setText("N/A");
        temp5T.setText("N/A");
        weather5T.setText("N/A");
        wind5T.setText("N/A");

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
        intent.putExtra("cityName", this.weatherFuture.get(0).getCitynm());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void updateFutureWeatherView() {
        updateT1();
        updateT2();
        updateT3();
        updateT4();
        updateT5();
    }

    @SuppressLint("SetTextI18n")
    private void updateTodayWeatherView() {
        cityNameTv.setText(this.weatherToday.getCitynm() + "天气");
        cityTv.setText(this.weatherToday.getCitynm());
        timeTv.setText(this.weatherToday.getDays());
        humidityTv.setText("湿度 " + this.weatherToday.getHumidity());
        pmDataTv.setText(this.weatherToday.getAqi());
        weekTv.setText(this.weatherToday.getWeek());
        temperatureTv.setText(this.weatherToday.getTemperature().replace("/", "~"));
        weatherTv.setText(this.weatherToday.getWeather());
        windTv.setText(this.weatherToday.getWind());
    }

    private void updateT1() {
        week1T.setText(this.weatherFuture.get(1).getWeek());
        temp1T.setText(this.weatherFuture.get(1).getTemperature());
        weather1T.setText(this.weatherFuture.get(1).getWeather());
        wind1T.setText(this.weatherFuture.get(1).getWind());
    }

    private void updateT2() {
        week2T.setText(this.weatherFuture.get(2).getWeek());
        temp2T.setText(this.weatherFuture.get(2).getTemperature());
        weather2T.setText(this.weatherFuture.get(2).getWeather());
        wind2T.setText(this.weatherFuture.get(2).getWind());
    }

    private void updateT3() {
        week3T.setText(this.weatherFuture.get(3).getWeek());
        temp3T.setText(this.weatherFuture.get(3).getTemperature());
        weather3T.setText(this.weatherFuture.get(3).getWeather());
        wind3T.setText(this.weatherFuture.get(3).getWind());
    }

    private void updateT4() {
        week4T.setText(this.weatherFuture.get(4).getWeek());
        temp4T.setText(this.weatherFuture.get(4).getTemperature());
        weather4T.setText(this.weatherFuture.get(4).getWeather());
        wind4T.setText(this.weatherFuture.get(4).getWind());
    }

    private void updateT5() {
        week5T.setText(this.weatherFuture.get(5).getWeek());
        temp5T.setText(this.weatherFuture.get(5).getTemperature());
        weather5T.setText(this.weatherFuture.get(5).getWeather());
        wind5T.setText(this.weatherFuture.get(5).getWind());
    }


    private void updateWeather() {

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORK_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
            Log.d("Weather", currentCityName);
            getTodayWeather(currentCityName);
            getFutureWeather(currentCityName);
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
    }

    private void getFutureWeather(String city) {
        final String url = Constant.WEATHER_FUTURE + city;
        Log.d(TAG, "getWeather: " + url);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = HttpClient.get(url);
                    Log.d(TAG, "run: " + response);
                    parseWeatherFuture(response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    private void parseWeatherFuture(String jsonStr) {
        Type jsonType = new TypeToken<WeatherResponse<List<WeatherInfo>>>() {
        }.getType();
        WeatherResponse<List<WeatherInfo>> weatherResponse = JsonUtil.parseJson(jsonStr, jsonType);
        this.weatherFuture = Objects.requireNonNull(weatherResponse).getResult();
        Message msg = new Message();
        msg.what = UPDATE_FUTURE_WEATHER;
        handler.sendMessage(msg);
    }
}
