package com.sndwave.weather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private LocationManager locationManager;
    private LocationListener listener;
    private RequestQueue requestQueue;
    private String longt;
    private String latt;
    private String urts;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //locationManager.requestLocationUpdates("network", 5000, 0, listener);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //textView.append("n " + location.getLongitude() + " " + location.getLatitude());
                longt= Double.toString(location.getLongitude());
                latt= Double.toString(location.getLatitude());
                urts = "https://api.darksky.net/forecast/62881cce04dfebae4028d98a03561c39/"+ latt+ ", " +longt ;
                loadApiData();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
        requestQueue = Volley.newRequestQueue(this);
        locationManager.requestLocationUpdates("network", 5000, 0, listener);
        //loadApiData();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button() {
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET}
                        , 10);
            }
            return;
        }
        //this code won'textView execute IF permissions are not allowed, because in the line above there is return statement.
       button.setOnClickListener(new View.OnClickListener() {
           @SuppressLint("MissingPermission")
           @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("network", 5000, 0, listener);
                //loadApiData();
            }
        });
    }

    private void loadApiData() {
        String url = urts;
        JsonObjectRequest request = new JsonObjectRequest(
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONObject currently = jsonObject.getJSONObject("currently");
                            String WeatherCommon = currently.getString("summary");
                            double temperature = currently.getDouble("temperature");
                            double humidity = currently.getDouble("humidity");

                            TextView weathercommon = findViewById(R.id.WeatherCommon);
                            TextView temp = findViewById(R.id.Temp);
                            TextView humid = findViewById(R.id.Humid);
                            TextView hou = findViewById(R.id.weathehourtl);

                            weathercommon.setText(WeatherCommon);
                            Double C = (temperature-32)*5/9;
                            Double D = humidity*100;
                            temp.setText(String.format("%.2f", C)+"°C");
                            humid.setText(Double.toString(D)+"%");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // TODO: Handle error
                    }
                });

        requestQueue.add(request);
        //end
    }
}