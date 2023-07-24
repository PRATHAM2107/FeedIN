package com.faijan.feedin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register_As_Ngo_Activity extends AppCompatActivity {


    EditText ngo_name, ngo_email, ngo_mobile, ngo_password, ngo_confirm_password, ngo_address, ngo_org_name ,ngo_org_crt;

    Button register_btn,certificate_upload_btn;
    ImageButton back_btn;

    private FirebaseFirestore db;

    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    int PERMISSION_ID = 44;
    Uri filePath;

    private final int PICK_IMAGE_REQUEST = 22;

    FirebaseStorage storage;
    StorageReference storageReference;


    SharedPreferences UserDataSharedPreferences;
    SharedPreferences.Editor UserDataSharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_as_ngo );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        UserDataSharedPreferencesEditor = UserDataSharedPreferences.edit();

        ngo_name = findViewById( R.id.ngo_name );
        ngo_email = findViewById( R.id.ngo_email );
        ngo_mobile = findViewById( R.id.ngo_mobile );
        ngo_password = findViewById( R.id.ngo_password );
        ngo_confirm_password = findViewById( R.id.ngo_confirm_password );
        ngo_address = findViewById( R.id.ngo_address );
        ngo_org_name = findViewById( R.id.ngo_org_name );
        ngo_org_crt = findViewById( R.id.ngo_org_crt );
        certificate_upload_btn = findViewById( R.id.certificate_upload_btn );

        db = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();


        back_btn = findViewById( R.id.back_btn );
        register_btn = findViewById( R.id.register_btn );


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        certificate_upload_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        } );

        register_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog = new ProgressDialog(Register_As_Ngo_Activity.this);
                progressDialog.setMessage("Loading..."); // Setting Message
                progressDialog.setTitle("Registering As Ngo"); // Setting Title
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                progressDialog.show(); // Display Progress Dialog
                progressDialog.setCancelable(false);

                String ngoName, ngoEmail, ngoMobile, ngoPassword, ngoConfirmPassword, ngoAddress,ngoOrgName,ngoOrgCrt;

                ngoName = ngo_name.getText().toString();
                ngoEmail = ngo_email.getText().toString();
                ngoMobile = ngo_mobile.getText().toString();
                ngoPassword = ngo_password.getText().toString();
                ngoConfirmPassword = ngo_confirm_password.getText().toString();
                ngoAddress = ngo_address.getText().toString();
                ngoOrgName = ngo_address.getText().toString();

                if ( !ngoName.isEmpty() && !ngoEmail.isEmpty() && !ngoMobile.isEmpty() && !ngoPassword.isEmpty() && !ngoConfirmPassword.isEmpty() && !ngoAddress.isEmpty() && !ngoOrgName.isEmpty()){


                    if (ngoPassword.equals( ngoConfirmPassword )){



                        firebaseAuth.createUserWithEmailAndPassword( ngoEmail, ngoPassword )
                                .addOnCompleteListener(Register_As_Ngo_Activity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){

                                            if (filePath != null) {

                                                // Code for showing progressDialog while uploading
                                                String ImageName = ngoName + "-certificate";
                                                StorageReference ref = storageReference.child( ImageName );

                                                ref.putFile( filePath )
                                                        .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                ref.getDownloadUrl().addOnSuccessListener( new OnSuccessListener<Uri>() {
                                                                    @Override
                                                                    public void onSuccess(Uri uri) {


                                                                        String userid = FirebaseAuth.getInstance().getUid().toString();
                                                                        Date getdate = Calendar.getInstance().getTime();

                                                                        String date = getdate.toString();

                                                                        RegisterClass registerClass = new RegisterClass();
                                                                        registerClass.setName( ngoName );
                                                                        registerClass.setEmail( ngoEmail );
                                                                        registerClass.setMobile( ngoMobile );
                                                                        registerClass.setPassword( ngoPassword );
                                                                        registerClass.setAddress( ngoAddress );
                                                                        registerClass.setOrganizationName( ngoOrgName );
                                                                        registerClass.setOrganizationCertificate( uri.toString() );
                                                                        registerClass.setUserId( userid );
                                                                        registerClass.setUserType( "Ngo" );
                                                                        registerClass.setGeoIp( "" );
                                                                        registerClass.getLastActive(date);
                                                                        registerClass.setTotalDonation( 0 );
                                                                        registerClass.setTotalFlames( 0 );
                                                                        registerClass.setProfilePic( "" );

                                                                        db.collection( "Users" )
                                                                                .document(userid)
                                                                                .set( registerClass )
                                                                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
                                                                                        UserDataSharedPreferencesEditor.putString("name", ngoName);
                                                                                        UserDataSharedPreferencesEditor.putString("email",ngoEmail);
                                                                                        UserDataSharedPreferencesEditor.putString("mobile",ngoMobile);
                                                                                        UserDataSharedPreferencesEditor.putString("password",ngoPassword);
                                                                                        UserDataSharedPreferencesEditor.putString("address",ngoAddress);
                                                                                        UserDataSharedPreferencesEditor.putString("uid",userid);
                                                                                        UserDataSharedPreferencesEditor.putString("user_type","Ngo");
                                                                                        UserDataSharedPreferencesEditor.commit();
                                                                                        UserDataSharedPreferencesEditor.apply();
                                                                                        progressDialog.dismiss();

                                                                                        Map<String, Object> data = new HashMap<>();
                                                                                        data.put("isActice", true);
                                                                                        db.collection( "Donations" )
                                                                                                .document(userid)
                                                                                                .set(data, SetOptions.merge());

                                                                                        Toast.makeText( Register_As_Ngo_Activity.this , "Successfully Registered you as NGO" , Toast.LENGTH_SHORT ).show();
                                                                                        startActivity( new Intent( Register_As_Ngo_Activity.this , Login_activity.class ) );
                                                                                        finish();

                                                                                    }
                                                                                } )
                                                                                .addOnFailureListener( new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        progressDialog.dismiss();
                                                                                        Toast.makeText( Register_As_Ngo_Activity.this, "Failed To Create Account", Toast.LENGTH_SHORT ).show();
                                                                                    }
                                                                                } );
                                                                    }
                                                                } );

                                                            }
                                                        } );


                                            }
                                        }
                                        else {
                                            progressDialog.dismiss();
                                            Toast.makeText( Register_As_Ngo_Activity.this, "Error in Registering as NGO", Toast.LENGTH_SHORT ).show();
                                        }

                                    }
                                } )
                                .addOnFailureListener( Register_As_Ngo_Activity.this , new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register_As_Ngo_Activity.this);
                                        alertDialogBuilder.setMessage( e.getLocalizedMessage() + "\nPlease Try Again...!");
                                        alertDialogBuilder.setPositiveButton("Ok",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface arg0, int arg1) {

//                                                                    Intent intent = new Intent(Register_As_Ngo_Activity.this, Register_As_Activity.class);
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
                        Toast.makeText( Register_As_Ngo_Activity.this, "Password not matching", Toast.LENGTH_SHORT ).show();

                    }
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText( Register_As_Ngo_Activity.this, "All Fields are Empty", Toast.LENGTH_SHORT ).show();
                }
            }
        } );


        // Custom Back Btn

        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RegisterAsintent = new Intent(Register_As_Ngo_Activity.this, Register_As_Activity.class);
                startActivity( RegisterAsintent );
                finish();
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
                ngo_org_crt.setText( "Image is Selected" );

            }

            catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }






    @Override
    public void onBackPressed(){
        Intent RegisterAsintent = new Intent(Register_As_Ngo_Activity.this, Register_As_Activity.class);
        startActivity( RegisterAsintent );
        finish();
    }

}