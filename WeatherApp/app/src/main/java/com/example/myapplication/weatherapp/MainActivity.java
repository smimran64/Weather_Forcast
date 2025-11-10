package com.example.myapplication.weatherapp;


import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class MainActivity extends AppCompatActivity {

    private TextView cityNameTex, temperatureText, humidityText, descriptionText, windText;

    private ImageView weatherIcon;
    private Button refreshButton;

    private EditText cityNameInput;

    private static final String API_KEY = "52be28b7efbd977939d0e2a22ebcc2bd";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


                                                                                                                                                                                                                                                                             cityNameTex = findViewById(R.id.cityName);
        temperatureText = findViewById(R.id.temperature);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        descriptionText = findViewById(R.id.descriptionText);
        weatherIcon = findViewById(R.id.weatherIcon);
        refreshButton = findViewById(R.id.fetchWeatherButton);
        cityNameInput = findViewById(R.id.cityNameInput);


        refreshButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String cityName = cityNameInput.getText().toString();

                if (!cityName.isEmpty()) {

                    FetchWeatherData(cityName);

                }
                else {

                    cityNameInput.setError("Please Enter the city Name");

                }
            }
        });

        FetchWeatherData("Dhaka");


    }
    private void FetchWeatherData(String cityName) {

        String url = "https:api.openweathermap.org/data/2.5/weather?q="+ cityName + "&appid=" + API_KEY + "&units=metric";

        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() ->

                {

                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder().url(url).build();

                    try {

                        Response response = client.newCall(request).execute();
                        String result = response.body().string();
                        runOnUiThread(() -> updateUI(result));
                    }

                    catch (IOException e){

                        e.printStackTrace();
                    }
                }


                );

    }

    private void updateUI(String result){

    if (result != null)
    {

        try {

            JSONObject jsonObject = new JSONObject(result);

            JSONObject main = jsonObject.getJSONObject("main");
            double temperature = main.getDouble("temp");
            double humidity = main.getDouble("humidity");
            double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");


            String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
            String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");
            String resourceName = "ic_"+ iconCode;

            int resId = getResources().getIdentifier(resourceName,"drawable",getPackageName());

            weatherIcon.setImageResource(resId);

            cityNameTex.setText(jsonObject.getString("name"));
            temperatureText.setText(String.format("%.0f°", temperature));
            humidityText.setText(String.format("%.0f%%", humidity));
            windText.setText(String.format("%.0f km/h", windSpeed));
            descriptionText.setText(description);




        }catch (JSONException e){

            e.printStackTrace();

        }
    }

    }


}