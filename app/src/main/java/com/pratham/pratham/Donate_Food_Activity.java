package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ColorSpace;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Donate_Food_Activity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView image_selected_text;

    EditText date_time_picker, chapati, dry_bhaji, wet_bhaji, rice;

    Button donate_food_btn,choose_image_btn;

    String url_Api;
    HashMap<String, String> ApiResponse = new HashMap<>();


    int day, month, year, hour, minute;
    int myday, myMonth, myYear, myHour, myMinute;

    SharedPreferences DonationDetailsSharedPreferences,UserDataSharedPreferences;
    SharedPreferences.Editor DonationDetailsSharedPreferencesEditor;


    FusedLocationProviderClient mFusedLocationClient;

    int PERMISSION_ID = 44;


    public int DonationNo;
    Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;

    String user_lat, user_long;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_donate_food );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location
        getLastLocation();


        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);
        DonationDetailsSharedPreferencesEditor =  DonationDetailsSharedPreferences.edit();


        date_time_picker = findViewById(R.id.date_time_picker);
        chapati = findViewById(R.id.chapati);
        dry_bhaji = findViewById(R.id.dry_bhaji);
        wet_bhaji = findViewById(R.id.wet_bhaji);
        rice = findViewById(R.id.rice);

        donate_food_btn = findViewById( R.id.donate_food_btn );
        choose_image_btn = findViewById( R.id.choose_image_btn );

        image_selected_text = findViewById( R.id.image_selected_text );

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();



        choose_image_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        } );


        donate_food_btn.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                uploadData();

            }
        } );


//        Date Set Listner
        date_time_picker.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(Donate_Food_Activity.this, Donate_Food_Activity.this,year, month,day);
                datePickerDialog.show();
            }
        } );

    }




    private void SelectImage()
    {

        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult( Intent.createChooser( intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode , int resultCode , @Nullable Intent data) {
        super.onActivityResult( requestCode , resultCode , data );
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                image_selected_text.setText( "Image is selected" );

            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadData()
    {


        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.setCancelable( false );
        progressDialog.show();

        String userid = UserDataSharedPreferences.getString( "uid" , "" );
        String username = UserDataSharedPreferences.getString( "name" , "" );
        String phone = UserDataSharedPreferences.getString( "phone" , "" );


        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
        String timeStamp = myDateObj.format(myFormatObj).toString();

        if(!userid.isEmpty()) {


            String Chapati, wetBhaji, dryBhaji, Rice;

            String ImageName = userid.concat( timeStamp );

            Chapati = chapati.getText().toString();
            wetBhaji = wet_bhaji.getText().toString();
            dryBhaji = dry_bhaji.getText().toString();
            Rice = rice.getText().toString();

            if(!user_lat.isEmpty() && !user_long.isEmpty()) {

                String FoodLocation = user_lat + "," + user_long;

                url_Api = "http://api.positionstack.com/v1/reverse?access_key=5cae8472f29f9940fd155bff756322ce&query=" + FoodLocation;

                new GetLocation().execute();

                if(!(ApiResponse.toString().length()  >= 5)){


                if (!Chapati.isEmpty() || !wetBhaji.isEmpty() || !dryBhaji.isEmpty() || !Rice.isEmpty()) {


                    if (!date_time_picker.getText().toString().isEmpty()) {

                        String BestBefore = date_time_picker.getText().toString();


                        if (filePath != null) {

                            // Code for showing progressDialog while uploading

                            StorageReference ref = storageReference.child( ImageName );


                            double reviewStar_double = (double) (Math.random() * (5 - 3.2) + 3.2);
                            DecimalFormat df = new DecimalFormat( "#.#" );
                            String reviewStar = String.valueOf( df.format( reviewStar_double ) );

                            ref.putFile( filePath )
                                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                            ref.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Got the download URL for 'users/me/profile.png'
                                                    DonateFoodClass donateFoodClass = new DonateFoodClass();
                                                    donateFoodClass.setChapati( Chapati );
                                                    donateFoodClass.setDryBhaji( dryBhaji );
                                                    donateFoodClass.setWetBhaji( wetBhaji );
                                                    donateFoodClass.setRice( Rice );
                                                    donateFoodClass.setFoodImage( uri.toString() );
                                                    donateFoodClass.setBestBefore( BestBefore );
                                                    donateFoodClass.setFoodStatus( "Visible" );
                                                    donateFoodClass.setUserName( username );
                                                    donateFoodClass.setUploadedAt( timeStamp );
                                                    donateFoodClass.setFoodLocation( "Near "+ApiResponse.get( "address" ) );
                                                    donateFoodClass.setReview( reviewStar );
                                                    donateFoodClass.setDonatedTo( "" );
                                                    donateFoodClass.setRequestedOn( "" );
                                                    donateFoodClass.setAcceptedOn( "" );
                                                    donateFoodClass.setDbLoc( "Donations/" + userid + "/allDetails/" + timeStamp + "/" );
                                                    donateFoodClass.setRequesterId( "" );
                                                    donateFoodClass.setDonorMobile( phone );
                                                    donateFoodClass.setLatLong( FoodLocation );

                                                    db.collection( "Donations" )
                                                            .document( userid )
                                                            .collection( "allDetails" )
                                                            .document( timeStamp )
                                                            .set( donateFoodClass )
                                                            .addOnSuccessListener( Donate_Food_Activity.this , new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText( Donate_Food_Activity.this , "Success" , Toast.LENGTH_SHORT ).show();
                                                                }
                                                            } )
                                                            .addOnFailureListener( Donate_Food_Activity.this , new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText( Donate_Food_Activity.this , "Failed...to upload data" , Toast.LENGTH_SHORT ).show();
                                                                }
                                                            } );

//                                                    HashMap<String, String> recentDonMap = new HashMap<>();
//
//                                                    recentDonMap.put( "foodStatus", "Visible" );
//                                                    recentDonMap.put( "requestedOn", timeStamp );
//                                                    recentDonMap.put( "donatedFrom", username);
//                                                    recentDonMap.put( "foodImage", uri.toString());
//
//                                                    db.collection( "Donations" )
//                                                            .document(userid)
//                                                            .collection( "RecentDonations" )
//                                                            .document(timeStamp)
//                                                            .set(recentDonMap)
//                                                            .addOnSuccessListener( new OnSuccessListener<Void>() {
//                                                                @Override
//                                                                public void onSuccess(Void unused) {
//                                                                    Toast.makeText( Donate_Food_Activity.this , "Success" , Toast.LENGTH_SHORT ).show();
//                                                                }
//                                                            } );

                                                }
                                            } ).addOnFailureListener( new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle any errors
                                                }
                                            } );

                                            progressDialog.dismiss();
                                            Toast.makeText( Donate_Food_Activity.this , "Data Uploaded Successfully" , Toast.LENGTH_SHORT ).show();
                                            finish();
                                            startActivity( getIntent() );
                                        }
                                    } )
                                    .addOnFailureListener( new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Error, Image not uploaded
                                            progressDialog.dismiss();
                                            Toast.makeText( Donate_Food_Activity.this , "Data Failed to upload" + e.getMessage() , Toast.LENGTH_SHORT ).show();
                                        }
                                    } )
                                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                            progressDialog.setMessage( "Uploaded " + (int) progress + "%" );
                                        }
                                    } );

                        } else {
                            progressDialog.dismiss();
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( Donate_Food_Activity.this );
                            alertDialogBuilder.setMessage( "Please Choose Image of Food" );
                            alertDialogBuilder.setPositiveButton( "Ok" ,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface arg0 , int arg1) {
                                            //                          Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
                                            //                          finish();
                                            //                          startActivity(getIntent());
                                        }
                                    } );
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( Donate_Food_Activity.this );
                        alertDialogBuilder.setMessage( "Please Select Best Before Time" );
                        alertDialogBuilder.setPositiveButton( "Ok" ,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0 , int arg1) {

                                        //                                   Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
                                        //                                   finish();
                                        //                                   startActivity(getIntent());
                                    }
                                } );
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                } else {
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( Donate_Food_Activity.this );
                    alertDialogBuilder.setMessage( "All Fields are empty, Please Enter appropriate values..." );
                    alertDialogBuilder.setPositiveButton( "Ok" ,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0 , int arg1) {

                                    //                              Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
                                    //                              finish();
                                    //                              startActivity(getIntent());
                                }
                            } );
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
                else{
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donate_Food_Activity.this);
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
                    startActivity( new Intent(Donate_Food_Activity.this, Donate_Food_Activity.class ) );
                    finish();
                }
            }else {
                requestNewLocationData();
            }

        }else {
            progressDialog.dismiss();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donate_Food_Activity.this);
            alertDialogBuilder.setMessage( "Error Occured");
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
            startActivity( new Intent(Donate_Food_Activity.this, Login_activity.class ) );
            finish();

        }
    }


    //    Date and Time Set
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myday = day;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(Donate_Food_Activity.this, Donate_Food_Activity.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;


        String myTime = myMonth+"-"+myday+"-"+myYear+" "+myHour+":"+myMinute;


        date_time_picker.setText(myTime);

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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donate_Food_Activity.this);
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

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    private class GetLocation extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            // Making a request to url and getting response

            OkHttpClient httpClient = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url_Api)
                    .build();

            Response response = null;
            try {
                response = httpClient.newCall(request).execute();

                String jsonStr = response.body().string();
                HashMap<String, String> contact = new HashMap<>();

                Log.e("Err", "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray contacts = jsonObj.getJSONArray("data");

                        // looping through All Contacts
                        for (int i = 0; i < 1; i++) {
                            JSONObject c = contacts.getJSONObject(i);
                            String locality = c.getString("locality");
                            String name = c.getString("name");
                            String email = c.getString("latitude");
                            String mobile = c.getString("longitude");
                            String address = c.getString("label");

                            // Phone node is JSON Object


                            // tmp hash map for single contact

                            // adding each child node to HashMap key => value
                            contact.put("locality", locality);
                            contact.put("name", name);
                            contact.put("lat", email);
                            contact.put("long", mobile);
                            contact.put("address", address);

                            // adding contact to contact list

                        }

                        ApiResponse = contact;


                    } catch (final JSONException e) {
                        Log.e("Err", "Json parsing error: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                } else {
                    Log.e("Err", "Couldn't get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}



