package com.faijan.feedin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Donation_Details_Activity extends AppCompatActivity {

    LinearLayout request_btn;

    ImageView food_image;

    TextView donor_name, donor_rating, food_name, donated_date, best_before_time, address;

    FirebaseFirestore db;
    SharedPreferences DonationDetailsSharedPreferences,UserDataSharedPreferences;
    SharedPreferences.Editor DonationDetailsSharedPreferencesEditor;

    String foodImg;

    String FoodNameUp;
    JSONObject food_loc = null;
    RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_donation_details );
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        UserDataSharedPreferences = getSharedPreferences("UserPreferences",MODE_PRIVATE);
        String username = UserDataSharedPreferences.getString( "name" , "" );
        String uid = UserDataSharedPreferences.getString( "uid" , "" );

        request_btn = findViewById( R.id.request_btn );

        food_image = findViewById( R.id.food_image );
        donor_name = findViewById( R.id.donor_name );
        donor_rating = findViewById( R.id.donor_rating );
        food_name = findViewById( R.id.food_name );
        donated_date = findViewById( R.id.donated_date );
        best_before_time = findViewById( R.id.best_before_time );
        address = findViewById( R.id.address );
        queue = Volley.newRequestQueue(this);


        DonationDetailsSharedPreferences = getSharedPreferences("DonationDetails",MODE_PRIVATE);
        DonationDetailsSharedPreferencesEditor =  DonationDetailsSharedPreferences.edit();

        String id = getIntent().getStringExtra("card_path");


        db=  FirebaseFirestore.getInstance();

        List<String> list=new ArrayList<String>();
        List<String> list1=new ArrayList<String>();


        String[] FoodStatus = new String[1];


        db.document(id)
            .get()
            .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    String bestBefore = snapshot.get( "bestBefore" ).toString();
                    String chapati = snapshot.get( "chapati" ).toString();
                    String donatedTo = snapshot.get( "donatedTo" ).toString();
                    String dryBhaji = snapshot.get( "dryBhaji" ).toString();
                    String foodImage = snapshot.get( "foodImage" ).toString();
                    String foodLocation = snapshot.get( "foodLocation" ).toString();
                    String foodStatus = snapshot.get( "foodStatus" ).toString();
                    String review = snapshot.get( "review" ).toString();
                    String rice = snapshot.get( "rice" ).toString();
                    String uploadedAt = snapshot.get( "uploadedAt" ).toString();
                    String userName = snapshot.get( "userName" ).toString();
                    String wetBhaji = snapshot.get( "wetBhaji" ).toString();

                    FoodStatus[0] = foodStatus;


                    list.add( "Chapati -"+chapati+" pcs");
                    list.add( "Dry Bhaji -"+dryBhaji+" kg");
                    list.add( "Wet Bhaji -"+wetBhaji+" kg");
                    list.add( "Rice -"+rice +" kg");

//                    for db upload of recentDOnation
                    list1.add( "Chapati");
                    list1.add( "Dry Bhaji");
                    list1.add( "Wet Bhaji");
                    list1.add( "Rice");



                    if(chapati.isEmpty()){
                        list.remove(  "Chapati -"+chapati+" pcs");
                        list1.remove(  "Chapati");
                    }
                    if(dryBhaji.isEmpty()){
                        list.remove( "Dry Bhaji -"+dryBhaji+" kg");
                        list1.remove( "Dry Bhaji");
                    }
                    if(wetBhaji.isEmpty()){
                        list.remove( "Wet Bhaji -"+wetBhaji+" kg");
                        list1.remove( "Wet Bhaji");

                    }
                    if(rice.isEmpty()){
                        list.remove( "Rice -"+rice +" kg");
                        list1.remove( "Rice");

                    }

                    foodImg = foodImage;

                    String FoodTypes = list.toString();

                    FoodNameUp = list1.toString();

                    FoodTypes = FoodTypes.replaceAll("[\\[\\](){}]","");
                    FoodNameUp = FoodNameUp.replaceAll("[\\[\\](){}]","");

                    Glide.with(getApplicationContext())
                            .load(foodImage )
                            .placeholder(R.drawable.loading_image)
                            .error(R.drawable.not_found_image)
                            .centerCrop()
                            .apply(new RequestOptions().override(1200, 1200))
                            .diskCacheStrategy( DiskCacheStrategy.ALL)
                            .into( food_image );


                    donor_name.setText( userName );
                    donor_rating.setText(review );
                    food_name.setText(FoodTypes);
                    donated_date.setText( uploadedAt);
                    best_before_time.setText( bestBefore );
                    address.setText( foodLocation );

//                    String url = "http://api.positionstack.com/v1/reverse?access_key=5cae8472f29f9940fd155bff756322ce&query="+foodLocation;
//                    new JsonTask().execute(url);

                }
            } );


        request_btn.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {


                if(!FoodStatus[0].isEmpty()){
                    if( FoodStatus[0].equals( "Visible" )){

                        LocalDateTime myDateObj = LocalDateTime.now();
                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
                        String timeStamp = myDateObj.format(myFormatObj).toString();

                        db.document(id)
                                .update( "foodStatus", "Requested","requestedOn" , timeStamp, "donatedTo", username, "requesterId", uid )
                                .addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        DonationDetailsSharedPreferencesEditor.putString("DonationStatus", "Requested");
                                        DonationDetailsSharedPreferencesEditor.putString("DonationCardPath", id);

                                        DonationDetailsSharedPreferencesEditor.commit();
                                        DonationDetailsSharedPreferencesEditor.apply();

                                        String donorName = donor_name.getText().toString();

                                        Intent loginAction =  new Intent(getApplication(), Loading_Activity.class);
                                        loginAction.putExtra( "donorName", donorName );
                                        loginAction.putExtra( "foodImg", foodImg );
                                        loginAction.putExtra( "userid", uid );
                                        loginAction.putExtra( "DonationCardPath", id.toString() );
                                        loginAction.putExtra( "foodName", FoodNameUp );
                                        startActivity( loginAction );
                                        finish();
                                        Toast.makeText( Donation_Details_Activity.this, "Requested is being placed", Toast.LENGTH_SHORT ).show();
                                    }
                                } )
                                .addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText( Donation_Details_Activity.this, "Requested UnSuccesfull... Please try again....", Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                    }else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donation_Details_Activity.this);
                        alertDialogBuilder.setMessage( "Food Already Requested!");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Donation_Details_Activity.this);
                alertDialogBuilder.setMessage( "Error Occured on Server-Side");
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

//                           Intent intent = new Intent(Register_As_Donor_Activity.this, Register_As_Activity.class);
                                finish();
//                           startActivity(getIntent());
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
//
            }
        } );
    }

//    private class JsonTask extends AsyncTask<String, String, String> {
//
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        protected String doInBackground(String... params) {
//
//
//            HttpURLConnection connection = null;
//            BufferedReader reader = null;
//
//            try {
//                URL url = new URL(params[0]);
//                connection = (HttpURLConnection) url.openConnection();
//                connection.connect();
//
//
//                InputStream stream = connection.getInputStream();
//
//                reader = new BufferedReader(new InputStreamReader(stream));
//
//                StringBuffer buffer = new StringBuffer();
//                String line = "";
//
//                while ((line = reader.readLine()) != null) {
//                    buffer.append(line+"\n");
//                    Log.d("Err: ", "> " + line);   //here u ll get whole response...... :-)
//
//                }
//
//                return buffer.toString();
//
//
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                if (connection != null) {
//                    connection.disconnect();
//                }
//                try {
//                    if (reader != null) {
//                        reader.close();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//            if(result != null) {
//                try {
//                    // get JSONObject from JSON file
//                    JSONObject obj = new JSONObject(result);
//                    JSONArray arr = new JSONArray(result);
//
//                    // fetch JSONObject named employee
//                    JSONObject employee = obj.getJSONObject("data");
//                    // get employee name and salary
//                    String distance = employee.getString("distance");
//                    // set employee name and salary in TextView's
//
//                    for (int i = 0; i < arr.length(); i++) { // Walk through the Array.
//                        JSONObject objs = arr.getJSONObject(i);
//                        JSONArray arr2 = objs.getJSONArray("data");
//                        // Do whatever.
//                        Log.d( "Err", arr2.toString() );
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e( "Err", "-<"+e.getLocalizedMessage() );
//                }
//
//            }
//
//
//        }
//    }
}
