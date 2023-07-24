package com.faijan.feedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Register_As_Activity extends AppCompatActivity {

    TextView login_user_link;
    Button donor_btn,volunteer_btn,ngo_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_as );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        login_user_link = findViewById(R.id.login_user_link );

        donor_btn = findViewById(R.id.donor_btn);
        volunteer_btn = findViewById(R.id.volunteer_btn);
        ngo_btn = findViewById( R.id.ngo_btn );


//        Donor Button CLick

        donor_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent donorRegisteration =  new Intent(getApplication(), Register_As_Donor_Activity.class);
                startActivity( donorRegisteration );
                finish();
            }
        } );


//        Volunteer Button CLick

        volunteer_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent volunteerRegisteration =  new Intent(getApplication(), Register_As_Volunteer_Activity.class);
                startActivity( volunteerRegisteration );
                finish();
            }
        } );


        //        Volunteer Button CLick

        ngo_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ngoRegisteration =  new Intent(getApplication(), Register_As_Ngo_Activity.class);
                startActivity( ngoRegisteration );
                finish();
            }
        } );



//        Login User Link Click
        login_user_link.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registeredUser =  new Intent(getApplication(), Login_activity.class);
                startActivity( registeredUser );
                finish();
            }
        } );

    }

    @Override
    public void onBackPressed(){
        Intent RegisterAsintent = new Intent(Register_As_Activity.this, Login_activity.class);
        startActivity( RegisterAsintent );
        finish();
    }
}