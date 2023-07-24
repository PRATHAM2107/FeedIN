package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Volunteer_Dashboard_Activity extends AppCompatActivity {

    ImageButton logout_btn;
    BottomNavigationView bottom_navbar;

    SharedPreferences UserDataSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;


    String user_lat,user_long;


    int PERMISSION_ID = 44;

    FusedLocationProviderClient mFusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_volunteer_dashboard );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        logout_btn = findViewById( R.id.logout_btn );

        bottom_navbar =  findViewById( R.id.bottom_navbar );
        bottom_navbar.setOnItemSelectedListener( NavListner );
        getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                new dashboard_fragment()).commit();


        logout_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Volunteer_Dashboard_Activity.this);
                alertDialogBuilder.setMessage( "Confirm Logout");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        UserDataSharedPreferencesEditor.putString("email",null);
                        UserDataSharedPreferencesEditor.putString("password", null);
                        UserDataSharedPreferencesEditor.putString("uid", null);
                        UserDataSharedPreferencesEditor.putString("user_type", null);
                        UserDataSharedPreferencesEditor.putString( "userLat", user_lat );
                        UserDataSharedPreferencesEditor.putString( "userLong", user_long );

                        UserDataSharedPreferencesEditor.commit();
                        UserDataSharedPreferencesEditor.apply();
                        Intent loginAction =  new Intent(getApplication(), Login_activity.class);
                        startActivity( loginAction );
                        finish();

                    }
                })
                .setNegativeButton( "Cancel" , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog , int which) {

                    }
                } );

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        } );


    }


//    Nav bar Code
    private NavigationBarView.OnItemSelectedListener NavListner =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.dashboard_icon:
                            selectedFragment = new dashboard_fragment();
                            break;
                        case R.id.history_icon:
                            selectedFragment = new history_fragment();
                            break;
                        case R.id.wallet_icon:
                            selectedFragment = new wallet_fragment();
                            break;
                        case R.id.profile_icon:
                            selectedFragment = new profile_fragment();
                            break;

                    }
                    getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };





    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            user_lat = location.getLatitude() + "";
                            user_long = location.getLongitude() + "";
                        }
                    }
                });
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Volunteer_Dashboard_Activity.this);
                alertDialogBuilder.setMessage( "Please turn on your location...");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {


        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();

            user_lat = mLastLocation.getLatitude() + "";
            user_long = mLastLocation.getLongitude() + "";
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService( Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    public static double getKmFromLatLong(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        double distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters/1000;
    }
}