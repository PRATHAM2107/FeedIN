package com.faijan.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class MainActivity extends AppCompatActivity {
    ImageView splash_logo;
    public boolean doubleBackToExitPressedOnce = false;

    SharedPreferences PermissionSharedPreferences,DonationDetailsSharedPreferences;
    SharedPreferences.Editor PermissionSharedPreferencesEditor;

    SharedPreferences UserDataSharedPreferences;
    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;






    String pref_email, pref_password, pref_userType;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private String[] Permissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        PermissionSharedPreferences = getSharedPreferences("PermissionPreferences",MODE_PRIVATE);
        PermissionSharedPreferencesEditor = PermissionSharedPreferences.edit();

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);

        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);




        setContentView( R.layout.activity_main );
        splash_logo = findViewById(R.id.splash_logo);


        Permissions = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION

        };

        if (!OnLoadFunction( MainActivity.this, Permissions )){
            ActivityCompat.requestPermissions( MainActivity.this,Permissions, PERMISSION_REQUEST_CODE );
        }
        else {
            if (UserDataSharedPreferences.getAll().containsKey( "uid" )){

                pref_email = UserDataSharedPreferences.getString( "email", "" );
                pref_password = UserDataSharedPreferences.getString( "password", "" );
                pref_userType = UserDataSharedPreferences.getString( "user_type", "" );

                if(!pref_email.isEmpty() && !pref_password.isEmpty()){
                    LoginwithEmail( pref_email, pref_password );
                }
            }else {
                LoginScreen();
            }

        }



    }


    private boolean OnLoadFunction(Context context, String... Permissions) {

        int flag = 0;
        if(context != null && Permissions != null){
            for (String perms : Permissions){
                if(ContextCompat.checkSelfPermission( context, perms ) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }

            }

        }
        else {
            if (UserDataSharedPreferences.getAll().containsKey( "uid" )){

                pref_email = UserDataSharedPreferences.getString( "email", "" );
                pref_password = UserDataSharedPreferences.getString( "password", "" );
                pref_userType = UserDataSharedPreferences.getString( "user_type", "" );

                if(!pref_email.isEmpty() && !pref_password.isEmpty()){
                    LoginwithEmail( pref_email, pref_password );
                }
                else {
                    LoginScreen();
                }
            }else {
                LoginScreen();
            }
        }

        return true;

    }

    private void LoginScreen(){

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {

                        @SuppressLint("ResourceType") Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.transition.slide);
                        splash_logo.startAnimation(animation);


                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        Intent loginPage = new Intent( MainActivity.this , Login_activity.class );
                                        startActivity( loginPage );
                                        finish();
                                    }
                                },
                                1000
                        );

                    }
                },
                2000
        );

    }




    @Override
    public void onRequestPermissionsResult(int requestCode , @NonNull String[] permissions , @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("INTERNET_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("INTERNET_Permission","false");

            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("ACCESS_NETWORK_STATE_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("ACCESS_NETWORK_STATE_Permission","false");

            }
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("ACCESS_WIFI_STATE_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("ACCESS_WIFI_STATE_Permission","false");

            }
            if (grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("WRITE_EXTERNAL_STORAGE_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("WRITE_EXTERNAL_STORAGE_Permission","false");

            }
            if (grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("READ_EXTERNAL_STORAGE_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("READ_EXTERNAL_STORAGE_Permission","false");

            }
            if (grantResults[5] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("CAMERA_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("CAMERA_Permission","false");

            }
            if (grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("READ_CONTACTS_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("READ_CONTACTS_Permission","false");

            }
            if (grantResults[7] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("ACCESS_COARSE_LOCATION_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("ACCESS_COARSE_LOCATION_Permission","false");

            }
            if (grantResults[8] == PackageManager.PERMISSION_GRANTED) {
                PermissionSharedPreferencesEditor.putString("ACCESS_FINE_LOCATION_Permission","true");

            } else {
                PermissionSharedPreferencesEditor.putString("ACCESS_FINE_LOCATION_Permission","false");

            }

            PermissionSharedPreferencesEditor.commit();

            PermissionSharedPreferencesEditor.apply();

        }

        LoginScreen();
        super.onRequestPermissionsResult( requestCode , permissions , grantResults );

    }



    //    Back to Click
    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        new Handler( Looper.getMainLooper()).postDelayed( new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
    public void LoginwithEmail(String email, String password){
        firebaseAuth.signInWithEmailAndPassword( email, password )
                .addOnSuccessListener( MainActivity.this , new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pref_email = UserDataSharedPreferences.getString( "email", "" );
                        pref_password = UserDataSharedPreferences.getString( "password", "" );
                        pref_userType = UserDataSharedPreferences.getString( "user_type", "" );



                        if(pref_userType.equals( "Ngo" )){
                            String DonationStatus = DonationDetailsSharedPreferences.getString("DonationStatus", "");
                            if (!DonationStatus.isEmpty() && DonationStatus.equals( "Requested" )){
                                String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");
                                db.document(FoodCardPath)
                                        .get()
                                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                String foodStatus =  snapshot.getString( "foodStatus" );
                                                if (foodStatus.equals( "Requested" )){
                                                    Intent loginAction =  new Intent(getApplication(), Loading_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }else {
                                                    Intent loginAction =  new Intent(getApplication(), Volunteer_Dashboard_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }
                                            }
                                        } );

                            }
                            else if (!DonationStatus.isEmpty() && DonationStatus.equals( "Accepted" )){
                                String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");
                                db.document(FoodCardPath)
                                        .get()
                                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                String foodStatus =  snapshot.getString( "foodStatus" );
                                                if (foodStatus.equals( "Accepted" )){
                                                    Intent loginAction =  new Intent(getApplication(), Accepted_Donation_Details.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }else {
                                                    Intent loginAction =  new Intent(getApplication(), NGO_Dashboard_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }
                                            }
                                        } );

                            }else {
                                Intent loginAction =  new Intent(getApplication(), NGO_Dashboard_Activity.class);
                                startActivity( loginAction );
                                finish();
                            }

                        }
                        else if(pref_userType.equals( "Volunteer" )){

                            String DonationStatus = DonationDetailsSharedPreferences.getString("DonationStatus", "");
                            if (!DonationStatus.isEmpty() && DonationStatus.equals( "Requested" )){
                                String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");
                                db.document(FoodCardPath)
                                        .get()
                                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                String foodStatus =  snapshot.getString( "foodStatus" );
                                                if (foodStatus.equals( "Requested" )){
                                                    Intent loginAction =  new Intent(getApplication(), Loading_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }else {
                                                    Intent loginAction =  new Intent(getApplication(), Volunteer_Dashboard_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }
                                            }
                                        } );

                            }
                            else if (!DonationStatus.isEmpty() && DonationStatus.equals( "Accepted" )){
                                String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");
                                db.document(FoodCardPath)
                                        .get()
                                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot snapshot) {
                                                String foodStatus =  snapshot.getString( "foodStatus" );
                                                if (foodStatus.equals( "Accepted" )){
                                                    Intent loginAction =  new Intent(getApplication(), Accepted_Donation_Details.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }else {
                                                    Intent loginAction =  new Intent(getApplication(), Volunteer_Dashboard_Activity.class);
                                                    startActivity( loginAction );
                                                    finish();
                                                }
                                            }
                                        } );

                            }
                            else {
                                Intent loginAction =  new Intent(getApplication(), Volunteer_Dashboard_Activity.class);
                                startActivity( loginAction );
                                finish();
                            }
                        }
                        else if(pref_userType.equals( "Donor" )){
                            Intent loginAction =  new Intent(getApplication(), Donor_Dashboard.class);
                            startActivity( loginAction );
                            finish();
                        }

                    }
                } )
                .addOnFailureListener( MainActivity.this , new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        alertDialogBuilder.setMessage("There is an issue on server-side\nTry login in manually...!");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                                                    Intent intent = new Intent(MainActivity.this, Login_activity.class);
                                                                    finish();
                                                                    startActivity(intent);
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                } );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity( new Intent(this, MainActivity.class) );
    }
}


