package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRvAdapter extends RecyclerView.Adapter<WeatherRvAdapter.ViewHolder> {
    private Context context;
    ArrayList <WeatherRvModel> weatherRvModelArrayList;

    public WeatherRvAdapter(Context context, ArrayList<WeatherRvModel> weatherRvModelArrayList) {
        this.context = context;
        this.weatherRvModelArrayList = weatherRvModelArrayList;
    }

    @NonNull
    @Override
    public WeatherRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRvAdapter.ViewHolder holder, int position) {
        WeatherRvModel model = weatherRvModelArrayList.get(position);
        holder.tempToday.setText(model.getTemperature()+"°C");
        String icon = model.getIcon();
        Picasso.get().load("http:".concat(icon)).into(holder.conditionToday);
        holder.windSpeedToday.setText(model.getWindSpeed()+"km/h");
        SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat dateTimeOutput = new SimpleDateFormat("hh:mm aa");
        try{
            Date t = dateTime.parse(model.getTime());
            holder.time.setText(dateTimeOutput.format(t));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return weatherRvModelArrayList.size();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder{
        private TextView time,tempToday,windSpeedToday;
        private ImageView conditionToday;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //getting the ids from card

            time = itemView.findViewById(R.id.time);
            tempToday = itemView.findViewById(R.id.tempToday);
            windSpeedToday = itemView.findViewById(R.id.windSpeedToday);

            conditionToday = itemView.findViewById(R.id.conditionToday);
        }
    }
}
