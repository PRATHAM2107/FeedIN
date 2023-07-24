package com.pratham.feedin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Forgot_Password_Activity extends AppCompatActivity {

    TextView register_user_link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_forgot_password );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        register_user_link = findViewById( R.id.register_user_link );


        //        New User Link Click
        register_user_link.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent oldUser =  new Intent(getApplication(), Login_activity.class);
                startActivity( oldUser );
            }
        } );

    }
}