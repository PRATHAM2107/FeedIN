package com.pratham.feedin;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class Loading_Activity extends AppCompatActivity {

    ImageView imageView3;
    SharedPreferences DonationDetailsSharedPreferences;
    SharedPreferences.Editor DonationDetailsSharedPreferencesEditor;

    Bundle extras;

    Timer timer;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        setContentView( R.layout.activity_loading );

        timer = new Timer();
        db = FirebaseFirestore.getInstance();

        imageView3 = findViewById( R.id.imageView3 );
        extras = getIntent().getExtras();
        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);
        DonationDetailsSharedPreferencesEditor =  DonationDetailsSharedPreferences.edit();

        FetchData();

        timer.scheduleAtFixedRate( new TimerTask() {
            @Override
            public void run() {
                FetchData();
            }
        }, 0, 2000);


        imageView3.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginAction =  new Intent(getApplication(), Accepted_Donation_Details.class);
                startActivity( loginAction );
            }
        } );
    }


    void FetchData(){

        String FoodCardPath = DonationDetailsSharedPreferences.getString("DonationCardPath", "");

        if(!FoodCardPath.isEmpty()){
            db.document( FoodCardPath )
                    .get()
                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            String acceptStatus = snapshot.getString( "foodStatus" );

                            if (acceptStatus.equals( "Accepted" )){
                                timer.cancel();
                                timer.purge();

                                DonationDetailsSharedPreferencesEditor.putString("DonationStatus", "Accepted");
                                DonationDetailsSharedPreferencesEditor.commit();
                                DonationDetailsSharedPreferencesEditor.apply();

                                LocalDateTime myDateObj = LocalDateTime.now();
                                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
                                String timeStamp = myDateObj.format(myFormatObj).toString();

                                HashMap<String, String> recentDonMap = new HashMap<>();

                                recentDonMap.put( "foodStatus", "Accepted" );
                                recentDonMap.put( "requestedOn", timeStamp );
                                recentDonMap.put( "donatedFrom", extras.getString( "donorName" ));
                                recentDonMap.put( "foodImage", extras.getString( "foodImg" ));
                                recentDonMap.put( "foodName", extras.getString( "foodName" ));

                                String uid = extras.getString( "userid" );
                                String data_path = "Donations/"+uid+"/"+"RecentDonations/"+timeStamp;
                                db.document(data_path)
                                        .set(recentDonMap)
                                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Intent loginAction =  new Intent(getApplication(), Accepted_Donation_Details.class);
                                                loginAction.putExtra("card_path", FoodCardPath);
                                                startActivity( loginAction );
                                                finish();
                                                Toast.makeText( Loading_Activity.this, "Requested is Accepted", Toast.LENGTH_SHORT ).show();
                                            }
                                        } );



                            }
                        }
                    } );
        }
    }
}