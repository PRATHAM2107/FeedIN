package com.pratham.feedin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register_As_Volunteer_Activity extends AppCompatActivity {


    EditText volunteer_name, volunteer_email, volunteer_mobile, volunteer_password, volunteer_confirm_password;

    Button register_btn;
    ImageButton back_btn;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    SharedPreferences UserDataSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_as_volunteer );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        volunteer_name = findViewById( R.id.volunteer_name );
        volunteer_email = findViewById( R.id.volunteer_email );
        volunteer_mobile = findViewById( R.id.volunteer_mobile );
        volunteer_password = findViewById( R.id.volunteer_password );
        volunteer_confirm_password = findViewById( R.id.volunteer_confirm_password );

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();


        back_btn = findViewById( R.id.back_btn );
        register_btn = findViewById( R.id.register_btn );





        register_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(Register_As_Volunteer_Activity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("Registering As Volunteer"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);
                String volunteerName, volunteerEmail, volunteerMobile, volunteerPassword, volunteerConfirmPassword, volunteerAddress;

                volunteerName = volunteer_name.getText().toString();
                volunteerEmail = volunteer_email.getText().toString();
                volunteerMobile = volunteer_mobile.getText().toString();
                volunteerPassword = volunteer_password.getText().toString();
                volunteerConfirmPassword = volunteer_confirm_password.getText().toString();
                if ( !volunteerName.isEmpty() && !volunteerEmail.isEmpty() && !volunteerMobile.isEmpty() && !volunteerPassword.isEmpty() && !volunteerConfirmPassword.isEmpty()){


                    if (volunteerPassword.equals( volunteerConfirmPassword )){



                        firebaseAuth.createUserWithEmailAndPassword( volunteerEmail, volunteerPassword )
                                .addOnCompleteListener(Register_As_Volunteer_Activity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            String userid = FirebaseAuth.getInstance().getUid().toString();
                                            Date getdate = Calendar.getInstance().getTime();

                                            String date = getdate.toString();

                                            RegisterClass registerClass = new RegisterClass();
                                            registerClass.setName( volunteerName );
                                            registerClass.setEmail( volunteerEmail );
                                            registerClass.setMobile( volunteerMobile );
                                            registerClass.setPassword( volunteerPassword );
                                            registerClass.setUserId( userid );
                                            registerClass.setAddress( "" );
                                            registerClass.setUserType( "Volunteer" );
                                            registerClass.getLastActive(date);
                                            registerClass.setTotalDonation( 0 );
                                            registerClass.setTotalFlames( 0 );
                                            registerClass.setGeoIp( "" );
                                            registerClass.setOrganizationName( "" );
                                            registerClass.setOrganizationCertificate( "" );
                                            registerClass.setProfilePic( "" );


                                            db.collection( "Users" )
                                                    .document(userid)
                                                    .set( registerClass )
                                                    .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            UserDataSharedPreferencesEditor.putString("name", volunteerName);
                                                            UserDataSharedPreferencesEditor.putString("email",volunteerEmail);
                                                            UserDataSharedPreferencesEditor.putString("mobile",volunteerMobile);
                                                            UserDataSharedPreferencesEditor.putString("password",volunteerPassword);
                                                            UserDataSharedPreferencesEditor.putString("uid",userid);
                                                            UserDataSharedPreferencesEditor.putString("user_type","Volunteer");
                                                            UserDataSharedPreferencesEditor.commit();
                                                            UserDataSharedPreferencesEditor.apply();
                                                            progressDialog.dismiss();
                                                            Map<String, Object> data = new HashMap<>();
                                                            data.put("isActice", true);
                                                            db.collection( "Donations" )
                                                                    .document(userid)
                                                                    .set(data, SetOptions.merge());
                                                            Toast.makeText( Register_As_Volunteer_Activity.this, "Successfully Registered you as Volunteer", Toast.LENGTH_SHORT ).show();
                                                            startActivity( new Intent(Register_As_Volunteer_Activity.this, Login_activity.class) );
                                                            finish();
                                                        }
                                                    } )

                                                    .addOnFailureListener( new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText( Register_As_Volunteer_Activity.this, "Failed To Create Account", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    } );
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText( Register_As_Volunteer_Activity.this, "Error in Registering as Volunteer", Toast.LENGTH_SHORT ).show();
                                        }

                                    }
                                } )
                                .addOnFailureListener( Register_As_Volunteer_Activity.this , new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register_As_Volunteer_Activity.this);
                                        alertDialogBuilder.setMessage( e.getLocalizedMessage() + "\nPlease Try Again...!");
                                        alertDialogBuilder.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {
//                                                                    Intent intent = new Intent(Register_As_Volunteer_Activity.this, Register_As_Activity.class);
//                                                                    finish();
//                                                                    startActivity(getIntent());
                                                    }
                                                });
                                        AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.show();
                                    }
                                } );

//
                    }else {
                        progressDialog.dismiss();

                        Toast.makeText( Register_As_Volunteer_Activity.this, "Password not matching", Toast.LENGTH_SHORT ).show();

                    }
                }
                else {
                    progressDialog.dismiss();

                    Toast.makeText( Register_As_Volunteer_Activity.this, "All Fields are Empty", Toast.LENGTH_SHORT ).show();
                }
            }
        } );


        // Custom Back Btn

        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterAsintent = new Intent(Register_As_Volunteer_Activity.this, Register_As_Activity.class);
                startActivity( RegisterAsintent );
                finish();
            }
        } );


    }


    @Override
    public void onBackPressed(){
        Intent RegisterAsintent = new Intent(Register_As_Volunteer_Activity.this, Register_As_Activity.class);
        startActivity( RegisterAsintent );
        finish();
    }

}