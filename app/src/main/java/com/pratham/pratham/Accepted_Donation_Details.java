package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class Accepted_Donation_Details extends AppCompatActivity {

    ImageView call_donor_btn;
    MapView maps_view;
    Bundle extras;

    Button collect_donation_btn;

    String user_lat,user_long;


    int PERMISSION_ID = 44;

    SharedPreferences.Editor DonationDetailsSharedPreferencesEditor;

    FusedLocationProviderClient mFusedLocationClient;

    String donorNumber;

    TextView estTime,donation_status,donation_address;
    GeoPoint geoPoint;
    SharedPreferences DonationDetailsSharedPreferences;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView( R.layout.activity_accepted_donation_details );

        Context ctx = this.getApplicationContext();
        Configuration.getInstance().load( ctx, PreferenceManager.getDefaultSharedPreferences( ctx ) );

        extras = getIntent().getExtras();

        db = FirebaseFirestore.getInstance();
        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);
        DonationDetailsSharedPreferencesEditor =  DonationDetailsSharedPreferences.edit();

        String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");
        Log.e("Err",FoodCardPath);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        getLastLocation();

        estTime = findViewById( R.id.estTime );
        donation_status = findViewById( R.id.donation_status );
        donation_address = findViewById( R.id.donation_address );
        collect_donation_btn = findViewById( R.id.collect_donation_btn );
        call_donor_btn = findViewById( R.id.call_donor_btn );



        maps_view = findViewById( R.id.maps_view );

        maps_view.setUseDataConnection( true );
        maps_view.setTileSource( TileSourceFactory.MAPNIK );
        maps_view.setMultiTouchControls( true );

        maps_view.getController().setZoom( 18.0 );
        maps_view.getZoomController().setVisibility( CustomZoomButtonsController.Visibility.NEVER );
        maps_view.setMultiTouchControls( true );

//        gpsMyLocationProvider = new GpsMyLocationProvider( this );
//        myLocationNewOverlay = new MyLocationNewOverlay( gpsMyLocationProvider, maps_view );
//        myLocationNewOverlay.enableMyLocation();
//        myLocationNewOverlay.enableFollowLocation();

        db.document( FoodCardPath )
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        String foodStatus = snapshot.getString( "foodStatus" ).toString();
                        String foodLocation = snapshot.getString( "foodLocation" ).toString();
                        donorNumber = snapshot.getString( "donorMobile" ).toString();
                        String latLong = snapshot.getString( "latLong" ).toString();
                        Log.e( "Err",  donorNumber+foodLocation + foodStatus +latLong);


                        String[] LatLongDiff = latLong.split(",", 2);


                        Double lat,longs;
                        lat = Double.valueOf( LatLongDiff[0] );
                        longs = Double.valueOf( LatLongDiff[1] );
                        Double lat1,longs1;
                        lat1 = Double.valueOf(user_lat);
                        longs1 = Double.valueOf( user_long);

                        String time = getKmFromLatLong(lat1,longs1,lat,longs);

                        estTime.setText(time + " Minutes" );
                        donation_status.setText( foodStatus );
                        donation_address.setText( foodLocation );


                        geoPoint = new GeoPoint( lat,longs );
                        Marker marker = new Marker( maps_view );
                        marker.setPosition( geoPoint );
                        marker.setAnchor( Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER );
                        maps_view.getOverlays().add( marker );
                        maps_view.getController().setCenter( geoPoint );


                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Accepted_Donation_Details.this);
                        alertDialogBuilder.setMessage( "Error Occured on Server-Side");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

//                           Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
//                           finish();
//                           startActivity(getIntent());
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } );



//        myLocationNewOverlay.runOnFirstFix( new Runnable() {
//            @Override
//            public void run() {
//                maps_view.getOverlays().clear();
//                maps_view.getOverlays().add( myLocationNewOverlay );
//                mapController.animateTo( myLocationNewOverlay.getMyLocation());
//            }
//        } );


        call_donor_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", donorNumber, null)));

            }
        } );

        collect_donation_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DonationDetailsSharedPreferencesEditor.putString("DonationStatus", "");
                DonationDetailsSharedPreferencesEditor.commit();
                startActivity( new Intent(Accepted_Donation_Details.this, Thanks_Message_Activity.class) );
            }
        } );
    }


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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Accepted_Donation_Details.this);
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


    public static String getKmFromLatLong(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        double distanceInMeters = loc1.distanceTo(loc2);
        double getKm = distanceInMeters / 1000;
        double speedinHr = getKm / 30;
        Double speed1 = speedinHr * 60;
        String speed =  String.valueOf( speed1 );
        speed = speed.substring( 0, speed.indexOf( "." ) );
        return speed;
    }

}