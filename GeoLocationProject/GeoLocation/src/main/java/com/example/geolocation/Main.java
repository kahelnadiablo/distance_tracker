package com.example.geolocation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/*created by Kahel 08/16/2013
* to read more about the location manager go here: http://www.vogella.com/articles/AndroidLocationAPI/article.html
*
* */

public class Main extends Activity implements LocationListener {

    private TextView latitude, longitude, distance;
    private Button getLocation;
    private LocationManager locationManager;
    private String provider;

    double distance_travelled;
    double latitude_prev;
    double longitude_prev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        latitude = (TextView)findViewById(R.id.txt_latitude);
        longitude = (TextView)findViewById(R.id.txt_longitude);
        distance = (TextView) findViewById(R.id.txt_distance);
        getLocation = (Button)findViewById(R.id.btn_getLocation);

        checkGps();

        getCurrentLocation();

        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locatorRunner();
            }
        });

    }

    private void locatorRunner(){
        Thread thread = new Thread()
        {
            @Override
            public void run() {
                try {
                    while(true) {
                        sleep(3000);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                 /*needs UI Thread*/
                                getCurrentLocation();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();
    }

    private void getCurrentLocation(){
        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);

        Toast.makeText(Main.this,String.valueOf(location),Toast.LENGTH_SHORT).show();


        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L,500.0f, (LocationListener) location);


        // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        } else {
            latitude.setText("Location not available");
            longitude.setText("Location not available");
            distance.setText("0");
        }
    }

    private void checkGps(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        Toast.makeText(Main.this,String.valueOf(enabled),Toast.LENGTH_SHORT).show();

        // Check if enabled and if not send user to the GSP settings
        // Better solution would be to display a dialog and suggesting to
        // go to the settings
        if (!enabled) {
            showGpsSettingDialog();
        }
    }

    private void showGpsSettingDialog(){
        /*Create a dialog to tell user to enable his GPS settings to pinpoint his or her location*/
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Main.this);
        alertDialog.setTitle("Settings para sa lokasyon"); /*should be on a string values*/

        alertDialog
                .setMessage("Kasalukuyang hindi aktibo ang iyong GPS para makuha ang iyong lokasyon. Nais mo bang i-set ito ngayon?")
                .setCancelable(false)
                .setPositiveButton("Oo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Hindi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        AlertDialog gpsSettingsDialog = alertDialog.create();
        gpsSettingsDialog.show();

    }

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        getLocation(location);
    }

    private void getLocation(Location location){

        int R = 6371;

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        latitude_prev = lat;
        longitude_prev = lon;

        Double dlat = latitude_prev - lat;
        Double dlon = longitude_prev - lon;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) + Math.cos(latitude_prev) *
                Math.cos(lat) * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        distance_travelled =  R * c;

        latitude.setText(String.valueOf(lat));
        longitude.setText(String.valueOf(lon));
        distance.setText(String.valueOf(distance_travelled));
    }

    /*private double toRad(double d) {
        return d * Math.PI / 180;
    }*/

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + provider,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + provider,Toast.LENGTH_SHORT).show();
    }
}
