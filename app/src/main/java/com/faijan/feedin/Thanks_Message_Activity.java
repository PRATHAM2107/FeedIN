package com.faijan.feedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Thanks_Message_Activity extends AppCompatActivity {

    Button back_btn;
    SharedPreferences UserDataSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView( R.layout.activity_thanks_message );

        back_btn = findViewById( R.id.back_btn );
        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);


        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String user= UserDataSharedPreferences.getString("user_type", "");
                if(user.equals( "Volunteer" )){
                    startActivity( new Intent(Thanks_Message_Activity.this, Volunteer_Dashboard_Activity.class) );
                    finish();
                }
                else if (user.equals( "Ngo" )){
                    startActivity( new Intent(Thanks_Message_Activity.this, NGO_Dashboard_Activity.class) );
                    finish();
                }
                else if (user.equals( "Donor" )){
                    startActivity( new Intent(Thanks_Message_Activity.this, Donor_Dashboard.class) );
                    finish();
                }else {
                    startActivity( new Intent(Thanks_Message_Activity.this, Login_activity.class) );
                    finish();
                }
            }
        } );


    }
}