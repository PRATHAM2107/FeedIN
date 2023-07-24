package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Donor_Dashboard extends AppCompatActivity {

    SharedPreferences UserDataSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;

    private FirebaseFirestore db;

    ImageButton logout_btn;

    BottomNavigationView bottom_navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView( R.layout.activity_donor_dashboard );

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        logout_btn = findViewById( R.id.logout_btn );





        bottom_navbar =  findViewById( R.id.bottom_navbar_donor );
        bottom_navbar.setOnItemSelectedListener( NavListnerDonor );
        getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                new donor_dashboard_fragment()).commit();

        logout_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donor_Dashboard.this);
                alertDialogBuilder.setMessage( "Confirm Logout");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                UserDataSharedPreferencesEditor.putString("email",null);
                                UserDataSharedPreferencesEditor.putString("password", null);
                                UserDataSharedPreferencesEditor.putString("uid", null);
                                UserDataSharedPreferencesEditor.putString("user_type", null);
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
    private NavigationBarView.OnItemSelectedListener NavListnerDonor =
            new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.dashboard_icon:
                            selectedFragment = new donor_dashboard_fragment();
                            break;
                        case R.id.request_icon:
                            selectedFragment = new donor_request_fragment();
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

}