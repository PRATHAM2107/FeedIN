package com.faijan.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class NGO_Dashboard_Activity extends AppCompatActivity {
    ImageButton logout_btn;

    BottomNavigationView bottom_navbar;


    SharedPreferences UserDataSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_ngo_dashboard );

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();



        logout_btn = findViewById( R.id.logout_btn );


        bottom_navbar =  findViewById( R.id.bottom_navbar_ngo );
        bottom_navbar.setOnItemSelectedListener( NavListnerNGO );
        getSupportFragmentManager().beginTransaction().replace( R.id.fragment_container,
                new dashboard_fragment()).commit();


        logout_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NGO_Dashboard_Activity.this);
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
    private NavigationBarView.OnItemSelectedListener NavListnerNGO =
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