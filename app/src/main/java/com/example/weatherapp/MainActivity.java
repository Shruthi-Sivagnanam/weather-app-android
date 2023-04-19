package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private RelativeLayout homeRl;
    private ProgressBar loading;
    private TextView cityName,conditions,temperature,windSpeed,windDir;
    private TextInputEditText editCity;
    private ImageView backIv,iconIv,searchIv;
    private RecyclerView weather;
    private ArrayList<WeatherRvModel> weatherRvModelArrayList;
    private WeatherRvAdapter weatherRvAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityname;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.black));

        //getting all ids of the ui

        homeRl = findViewById(R.id.homePage);
        loading = findViewById(R.id.loading);

        editCity = findViewById(R.id.editCity);

        cityName = findViewById(R.id.city);
        temperature = findViewById(R.id.temperature);
        conditions = findViewById(R.id.condition);
        windSpeed = findViewById(R.id.windSpeed);
        windDir = findViewById(R.id.windDir);

        iconIv = findViewById(R.id.icon);
        searchIv = findViewById(R.id.search);
        backIv = findViewById(R.id.blackPage);

        weather = findViewById(R.id.weather);

        weatherRvModelArrayList = new ArrayList<>();
        weatherRvAdapter = new WeatherRvAdapter(this,weatherRvModelArrayList);
        weather.setAdapter(weatherRvAdapter);

        //Getting the location of the User
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_CODE);
        }

        //finding the city name
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        cityname = getCityName(location.getLongitude(),location.getLatitude());
        getWeatherForLocation(cityname);

        searchIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editCity.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Give the city Name", Toast.LENGTH_SHORT).show();
                }
                else{
                    getWeatherForLocation(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(this, "Please Provide the Permission ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double longitude, double latitude){
        String city_Name = "Not found";
        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude,longitude,10);
            for(Address adr: addressList){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        city_Name = city;
                    }
                    else{
                        Toast.makeText(this,"Your Location not found",Toast.LENGTH_SHORT).show();
                        //city_Name = "Chennai";
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return city_Name;
    }

    private void getWeatherForLocation(String city_Name){
        String url = "http://api.weatherapi.com/v1/forecast.json?key=266d27402bad446386a83226231904&q="+city_Name+"&days=1&aqi=no&alerts=no";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                weatherRvModelArrayList.clear();
                try {
                    String city = response.getJSONObject("location").getString("name");
                    cityName.setText(city);

                    String temp = response.getJSONObject("current").getString("temp_c");
                    temperature.setText(temp + "°C");

                    int isDay = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    conditions.setText(condition);
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIv);

                    String wind_speed = response.getJSONObject("current").getString("wind_kph");
                    String wind_dir = response.getJSONObject("current").getString("wind_dir");
                    windSpeed.setText(wind_speed+" km/h");
                    windDir.setText(wind_dir);

                    if(isDay==1){
                        Picasso.get().load("https://static.vecteezy.com/system/resources/thumbnails/003/279/108/small/panorama-sky-with-cloud-on-a-sunny-day-free-photo.jpg").into(backIv);
                    }
                    else{
                        Picasso.get().load("https://t4.ftcdn.net/jpg/03/58/75/17/360_F_358751701_4Gw4SXn8q4fzjgHWz5ZQhZaojoJM8oO2.jpg").into(backIv);
                    }

                    JSONObject forecastObject = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObject.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRvModelArrayList.add(new WeatherRvModel(time,img,temper,wind));
                    }
                    weatherRvAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid City name", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}