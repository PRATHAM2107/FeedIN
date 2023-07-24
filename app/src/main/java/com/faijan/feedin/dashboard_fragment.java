package com.faijan.feedin;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ethanhua.skeleton.RecyclerViewSkeletonScreen;
import com.ethanhua.skeleton.Skeleton;
import com.faijan.feedin.HelperClass.FoodCardAdapter;
import com.faijan.feedin.HelperClass.FoodCardHelperClass;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class dashboard_fragment extends Fragment {

    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView card_recycler_view;
    RecyclerView.Adapter adapter;

    TextView user_name;

    String user_lat,user_long;

    Context cntx;


    SharedPreferences UserDataSharedPreferences;





    private FirebaseFirestore db;

//    FirebaseDatabase firebaseDatabase;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public dashboard_fragment() {
    }


    public static dashboard_fragment newInstance(String param1 , String param2) {
        dashboard_fragment fragment = new dashboard_fragment();
        Bundle args = new Bundle();
        args.putString( ARG_PARAM1 , param1 );
        args.putString( ARG_PARAM2 , param2 );
        fragment.setArguments( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        if (getArguments() != null) {
            mParam1 = getArguments().getString( ARG_PARAM1 );
            mParam2 = getArguments().getString( ARG_PARAM2 );

        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup container ,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_dashboard_fragment , container , false );
        swipeRefreshLayout = rootView.findViewById( R.id.swipe_layout );


        db = FirebaseFirestore.getInstance();

        user_name = rootView.findViewById( R.id.user_name );

        UserDataSharedPreferences = getActivity().getSharedPreferences("UserPreferences",MODE_PRIVATE);
        String username = UserDataSharedPreferences.getString( "name" , "" );

        user_name.setText( "Hey, "+username );

        final Handler refreshHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // do updates for imageview
                refreshHandler.postDelayed(this, 30 * 100);
            }
        };
        refreshHandler.postDelayed(runnable, 30 * 100);


        card_recycler_view = rootView.findViewById( R.id.card_recycler_view );

        card_recycler_view();
        card_recycler_view.setHasFixedSize( true );
        card_recycler_view.setLayoutManager( new LinearLayoutManager( rootView.getContext(), LinearLayoutManager.VERTICAL, false ) );

        GetData();

        swipeRefreshLayout.setOnRefreshListener( new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                GetData();
            }
        } );


//        RecyclerViewSkeletonScreen skeletonScreen = Skeleton.bind(card_recycler_view)
//                .adapter(adapter)
//                .load(R.layout.fragment_dashboard_fragment)
//                .show();

        return rootView;
    }

    private void GetData() {

        ArrayList<FoodCardHelperClass> foodCardHelperClasses = new ArrayList<>();

        List<String> listUsers = new ArrayList<>();
        List<String> listDonates = new ArrayList<>();

        DonateFoodClass donateFoodClass = new DonateFoodClass();


        db.collection("Donations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot document : task.getResult()) {
//                        listUsers.add(document.getId());

                        db.collection( "Donations" )
                                .document(document.getId().toString())
                                .collection( "allDetails" )
                                .orderBy( "uploadedAt", Query.Direction.DESCENDING )
                                .get()
                                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                            db.collection( "Donations" )
                                                    .document(document.getId().toString())
                                                    .collection( "allDetails" )
                                                    .document(documentSnapshot.getId())
                                                    .get()
                                                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            String foodStatus = documentSnapshot.getString( "foodStatus" );
                                                            if (!foodStatus.equals( "Requested" ) && !foodStatus.equals( "Accepted" ) && foodStatus.equals( "Visible" )) {

                                                                donateFoodClass.setFoodImage( documentSnapshot.getString( "foodImage" ).toString() );
                                                                donateFoodClass.setUserName( documentSnapshot.getString( "userName" ).toString() );
                                                                donateFoodClass.setReview( documentSnapshot.getString( "review" ).toString() );
                                                                donateFoodClass.setChapati( documentSnapshot.getString( "chapati" ).toString() );
                                                                donateFoodClass.setWetBhaji( documentSnapshot.getString( "wetBhaji" ).toString() );
                                                                donateFoodClass.setDryBhaji( documentSnapshot.getString( "dryBhaji" ).toString() );
                                                                donateFoodClass.setDonatedTo( documentSnapshot.getString( "donatedTo" ).toString() );
                                                                donateFoodClass.setRice( documentSnapshot.getString( "rice" ).toString() );
                                                                donateFoodClass.setFoodLocation( documentSnapshot.getString( "foodLocation" ).toString() );
                                                                donateFoodClass.setFoodStatus( documentSnapshot.getString( "foodStatus" ).toString() );
                                                                donateFoodClass.setUploadedAt( documentSnapshot.getString( "uploadedAt" ).toString() );
                                                                donateFoodClass.setBestBefore( documentSnapshot.getString( "bestBefore" ).toString() );
                                                                donateFoodClass.setDbLoc( documentSnapshot.getReference().getPath().toString() );
                                                                donateFoodClass.setRequestedOn( documentSnapshot.getString( "requestedOn" ) );
                                                                donateFoodClass.setAcceptedOn( documentSnapshot.getString( "acceptedOn" ) );
                                                                donateFoodClass.setLatLong( documentSnapshot.getString( "latLong" ) );

                                                                foodCardHelperClasses.add( new FoodCardHelperClass( donateFoodClass.getChapati().toString() , donateFoodClass.getDryBhaji().toString() , donateFoodClass.getWetBhaji().toString() , donateFoodClass.getRice().toString() , donateFoodClass.getFoodImage().toString() , donateFoodClass.getBestBefore().toString() , donateFoodClass.getUploadedAt().toString() , donateFoodClass.getFoodStatus().toString() , donateFoodClass.getFoodLocation().toString() , donateFoodClass.getUserName().toString() , donateFoodClass.getDonatedTo().toString() , donateFoodClass.getReview().toString() , donateFoodClass.getDbLoc().toString() , donateFoodClass.getRequestedOn().toString() , donateFoodClass.getAcceptedOn().toString(), donateFoodClass.getLatLong().toString() ) );

                                                                adapter.notifyDataSetChanged();

                                                            }
                                                        }
                                                    } );
                                        }
                                    }
                                } );


                    }

                    Log.d("Err", listUsers.toString());
                } else {
                    Log.e("Err", "Error getting documents: ", task.getException());
                }
            }
        });
        adapter = new FoodCardAdapter(getContext(), foodCardHelperClasses );
        card_recycler_view.setAdapter( adapter );

    }

    private void card_recycler_view() {
    }

    public static double getKmFromLatLong(double lat1, double lng1, double lat2, double lng2){
        Location loc1 = new Location("");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lng1);
        Location loc2 = new Location("");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lng2);
        double distanceInMeters = loc1.distanceTo(loc2);
        return distanceInMeters/1000;
    }


}