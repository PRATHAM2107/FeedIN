package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_activity extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;

    ProgressDialog progressDialog;

    SharedPreferences UserDataSharedPreferences,DonationDetailsSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;

    ImageView logo;
    TextView register_user_link,forgot_password_link;
    Button login_btn;

    EditText login_email, login_password;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setContentView( R.layout.activity_login );

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        register_user_link = findViewById(R.id.register_user_link);
        forgot_password_link = findViewById(R.id.forgot_password_link);

        login_email = findViewById(R.id.login_email );
        login_password = findViewById(R.id.login_password );

        login_btn = findViewById( R.id.login_btn );







        //        forgot password Link Click
        forgot_password_link.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent frgtpassd =  new Intent(getApplication(), Forgot_Password_Activity.class);
                startActivity( frgtpassd );
                finish();
            }
        } );

//        Login Btn Click

        login_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(Login_activity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("Signing in"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                String email = login_email.getText().toString();
                String password = login_password.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    firebaseAuth.signInWithEmailAndPassword( email, password )
                            .addOnSuccessListener( Login_activity.this , new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    String userid= firebaseAuth.getUid().toString();

                                    DocumentReference docRef = db.collection("Users").document(userid);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document != null) {
                                                    String userType = document.getString("userType");
                                                    String name = document.getString("name");
                                                    String phone = document.getString("mobile");
//                                                    Log.e("Err", userType);
                                                    Log.e("Err", userid);
                                                    UserDataSharedPreferencesEditor.putString("email",email);
                                                    UserDataSharedPreferencesEditor.putString("name",name);
                                                    UserDataSharedPreferencesEditor.putString("password", password);
                                                    UserDataSharedPreferencesEditor.putString("uid", userid);
                                                    UserDataSharedPreferencesEditor.putString("user_type", userType);
                                                    UserDataSharedPreferencesEditor.putString("phone", phone);
                                                    UserDataSharedPreferencesEditor.commit();
                                                    UserDataSharedPreferencesEditor.apply();
                                                    progressDialog.dismiss();


                                                    if(userType.equals( "Ngo" )){
                                                        Intent loginAction =  new Intent(getApplication(), NGO_Dashboard_Activity.class);
                                                        startActivity( loginAction );
                                                        finish();
                                                    }else if(userType.equals( "Donor" )){
                                                        Intent loginAction =  new Intent(getApplication(), Donor_Dashboard.class);
                                                        startActivity( loginAction );
                                                        finish();
                                                    }else if(userType.equals( "Volunteer" )){
                                                        if(DonationDetailsSharedPreferences.getString("DonationStatus", "").equals("Requested")){
                                                            Intent loginAction =  new Intent(getApplication(), Loading_Activity.class);
                                                            startActivity( loginAction );
                                                            finish();
                                                        }else {
                                                            Intent loginAction =  new Intent(getApplication(), Volunteer_Dashboard_Activity.class);
                                                            startActivity( loginAction );
                                                            finish();
                                                        }
                                                    }
                                                    else {
                                                        Intent loginAction =  new Intent(getApplication(), Login_activity.class);
                                                        startActivity( loginAction );
                                                        finish();
                                                    }

                                                } else {
                                                    Toast.makeText( Login_activity.this, "Error occured... Please Try Again Later!", Toast.LENGTH_SHORT ).show();
                                                }
                                            } else {
                                                Toast.makeText( Login_activity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT ).show();
                                            }
                                        }
                                    });


                                }
                            } )
                            .addOnFailureListener( Login_activity.this , new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_activity.this);
                                    alertDialogBuilder.setMessage( e.getLocalizedMessage() + "\nPlease Try Again...!");
                                    alertDialogBuilder.setPositiveButton("Ok",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface arg0, int arg1) {

//                                                                    Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
//                                                                    finish();
//                                                                    startActivity(getIntent());
                                                }
                                            });
                                    AlertDialog alertDialog = alertDialogBuilder.create();
                                    alertDialog.show();
                                }
                            } );
                }else{
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login_activity.this);
                    alertDialogBuilder.setMessage("Email or Password is Empty");
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

//                                                                    Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
//                                                                    finish();
//                                                                    startActivity(getIntent());
                                }
                            });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }

            }
        } );


        //        New User Link Click
        register_user_link.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newUser =  new Intent(getApplication(), Register_As_Activity.class);
                startActivity( newUser );
                finish();
            }
        } );

    }



    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            return;
        }

        new Handler( Looper.getMainLooper()).postDelayed( new Runnable() {

            @Override
            public void run() {
                Toast.makeText( Login_activity.this, "press back again for exit", Toast.LENGTH_SHORT ).show();
                doubleBackToExitPressedOnce=true;
            }
        }, 100);
    }
}