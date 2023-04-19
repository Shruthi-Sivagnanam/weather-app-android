package com.example.weatherapp;

public class WeatherRvModel {
    private String time,icon,temperature,windSpeed;

    public WeatherRvModel(String time, String icon, String temperature, String windSpeed) {
        this.time = time;
        this.icon = icon;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }
}
