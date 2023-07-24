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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class Register_As_Donor_Activity extends AppCompatActivity {


    EditText donor_name, donor_email, donor_mobile, donor_password, donor_confirm_password, donor_address;

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
        setContentView( R.layout.activity_register_as_donor );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        donor_name = findViewById( R.id.donor_name );
        donor_email = findViewById( R.id.donor_email );
        donor_mobile = findViewById( R.id.donor_mobile );
        donor_password = findViewById( R.id.donor_password );
        donor_confirm_password = findViewById( R.id.donor_confirm_password );
        donor_address = findViewById( R.id.donor_address );

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();


        back_btn = findViewById( R.id.back_btn );
        register_btn = findViewById( R.id.register_btn );







        register_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(Register_As_Donor_Activity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("Registering As Donor"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                String donorName, donorEmail, donorMobile, donorPassword, donorConfirmPassword, donorAddress;

                donorName = donor_name.getText().toString();
                donorEmail = donor_email.getText().toString();
                donorMobile = donor_mobile.getText().toString();
                donorPassword = donor_password.getText().toString();
                donorConfirmPassword = donor_confirm_password.getText().toString();
                donorAddress = donor_address.getText().toString();
                if ( !donorName.isEmpty() && !donorEmail.isEmpty() && !donorMobile.isEmpty() && !donorPassword.isEmpty() && !donorConfirmPassword.isEmpty() && !donorAddress.isEmpty()){


                    if (donorPassword.equals( donorConfirmPassword )){

                        Map<String, Object> data = new HashMap<>();
                        data.put("isActice", true);


                        firebaseAuth.createUserWithEmailAndPassword( donorEmail, donorPassword )
                                .addOnCompleteListener(Register_As_Donor_Activity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            String userid = FirebaseAuth.getInstance().getUid().toString();
                                            Date getdate = Calendar.getInstance().getTime();

                                            String date = getdate.toString();

                                            RegisterClass registerClass = new RegisterClass();
                                            registerClass.setName( donorName );
                                            registerClass.setEmail( donorEmail );
                                            registerClass.setMobile( donorMobile );
                                            registerClass.setPassword( donorPassword );
                                            registerClass.setAddress( donorAddress );
                                            registerClass.setUserId( userid );
                                            registerClass.setUserType( "Donor" );
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
                                                            UserDataSharedPreferencesEditor.putString("name", donorName);
                                                            UserDataSharedPreferencesEditor.putString("email",donorEmail);
                                                            UserDataSharedPreferencesEditor.putString("mobile",donorMobile);
                                                            UserDataSharedPreferencesEditor.putString("password",donorPassword);
                                                            UserDataSharedPreferencesEditor.putString("address",donorAddress);
                                                            UserDataSharedPreferencesEditor.putString("uid",userid);
                                                            UserDataSharedPreferencesEditor.putString("user_type","Donor");
                                                            UserDataSharedPreferencesEditor.commit();
                                                            UserDataSharedPreferencesEditor.apply();

                                                            progressDialog.dismiss();

                                                            db.collection( "Donations" )
                                                                .document(userid)
                                                                .set(data, SetOptions.merge());

                                                            Toast.makeText( Register_As_Donor_Activity.this, "Successfully Registered you as Donor", Toast.LENGTH_SHORT ).show();
                                                            startActivity( new Intent(Register_As_Donor_Activity.this, Login_activity.class) );
                                                            finish();
                                                        }
                                                    } )
                                                    .addOnFailureListener( new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            progressDialog.dismiss();
                                                            Toast.makeText( Register_As_Donor_Activity.this, "Failed To Create Account", Toast.LENGTH_SHORT ).show();
                                                        }
                                                    } );
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText( Register_As_Donor_Activity.this, "Error in Registering as Donor", Toast.LENGTH_SHORT ).show();
                                        }

                                    }
                                } )
                                .addOnFailureListener( Register_As_Donor_Activity.this , new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register_As_Donor_Activity.this);
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

//
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText( Register_As_Donor_Activity.this, "Password not matching", Toast.LENGTH_SHORT ).show();

                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText( Register_As_Donor_Activity.this, "All Fields are Empty", Toast.LENGTH_SHORT ).show();
                }
            }
        } );


        // Custom Back Btn

        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterAsintent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
                startActivity( RegisterAsintent );
                finish();
            }
        } );

    }






    @Override
    public void onBackPressed(){
        Intent RegisterAsintent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
        startActivity( RegisterAsintent );
        finish();
    }

}