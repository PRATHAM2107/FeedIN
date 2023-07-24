package com.faijan.feedin;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.faijan.feedin.HelperClass.DonorRequestFoodCardAdapter;
import com.faijan.feedin.HelperClass.FoodCardHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class donor_request_fragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView card_recycler_view;
    RecyclerView.Adapter adapter;

    TextView user_name,donation_request_tag;

    SharedPreferences UserDataSharedPreferences;

    private FirebaseFirestore db;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public donor_request_fragment() {
    }


    public static donor_request_fragment newInstance(String param1 , String param2) {
        donor_request_fragment fragment = new donor_request_fragment();
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate( R.layout.fragment_donor_request_fragment , container , false );
        swipeRefreshLayout = rootView.findViewById( R.id.swipe_layout );

        db = FirebaseFirestore.getInstance();

        user_name = rootView.findViewById( R.id.user_name );
        donation_request_tag = rootView.findViewById( R.id.donation_request_tag );
        UserDataSharedPreferences = getActivity().getSharedPreferences("UserPreferences",MODE_PRIVATE);
        String username = UserDataSharedPreferences.getString( "name" , "" );
        String uid = UserDataSharedPreferences.getString( "uid" , "" );

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
        UserDataSharedPreferences = getActivity().getSharedPreferences("UserPreferences",MODE_PRIVATE);
        String username = UserDataSharedPreferences.getString( "name" , "" );
        String uid = UserDataSharedPreferences.getString( "uid" , "" );

        ArrayList<FoodCardHelperClass> foodCardHelperClasses = new ArrayList<>();

        List<String> listUsers = new ArrayList<>();
        List<String> listDonates = new ArrayList<>();

        DonateFoodClass donateFoodClass = new DonateFoodClass();


        db.collection( "Donations" )
                .document(uid)
                .collection( "allDetails" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            db.collection( "Donations" )
                                    .document(uid)
                                    .collection( "allDetails" )
                                    .document(documentSnapshot.getId())
                                    .get()
                                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                            String foodstatus = documentSnapshot.getString( "foodStatus" );
                                            if (foodstatus.equals( "Requested" )){
                                                donateFoodClass.setFoodImage( documentSnapshot.getString( "foodImage" ).toString());
                                                donateFoodClass.setUserName(documentSnapshot.getString( "userName" ).toString());
                                                donateFoodClass.setReview( documentSnapshot.getString( "review" ).toString());
                                                donateFoodClass.setChapati( documentSnapshot.getString( "chapati" ).toString());
                                                donateFoodClass.setWetBhaji( documentSnapshot.getString( "wetBhaji" ).toString());
                                                donateFoodClass.setDryBhaji( documentSnapshot.getString( "dryBhaji" ).toString());
                                                donateFoodClass.setDonatedTo( documentSnapshot.getString( "donatedTo" ).toString());
                                                donateFoodClass.setRice( documentSnapshot.getString( "rice" ).toString());
                                                donateFoodClass.setFoodLocation( documentSnapshot.getString( "foodLocation" ).toString());
                                                donateFoodClass.setFoodStatus( documentSnapshot.getString( "foodStatus" ).toString());
                                                donateFoodClass.setUploadedAt( documentSnapshot.getString( "uploadedAt" ).toString());
                                                donateFoodClass.setBestBefore( documentSnapshot.getString( "bestBefore" ).toString());
                                                donateFoodClass.setDbLoc( documentSnapshot.getReference().getPath().toString() );
                                                donateFoodClass.setRequestedOn( documentSnapshot.getString( "requestedOn" ) );
                                                donateFoodClass.setAcceptedOn( documentSnapshot.getString( "acceptedOn" ) );
                                                donateFoodClass.setLatLong( documentSnapshot.getString( "latLong" ) );

                                                foodCardHelperClasses.add( new FoodCardHelperClass( donateFoodClass.getChapati().toString() , donateFoodClass.getDryBhaji().toString() , donateFoodClass.getWetBhaji() .toString(), donateFoodClass.getRice().toString() , donateFoodClass.getFoodImage().toString() , donateFoodClass.getBestBefore().toString(), donateFoodClass.getUploadedAt().toString() , donateFoodClass.getFoodStatus().toString() , donateFoodClass.getFoodLocation().toString() , donateFoodClass.getUserName().toString() , donateFoodClass.getDonatedTo().toString() , donateFoodClass.getReview().toString(), donateFoodClass.getDbLoc().toString(), donateFoodClass.getRequestedOn().toString(), donateFoodClass.getAcceptedOn().toString(), donateFoodClass.getLatLong().toString() ));

                                                adapter.notifyDataSetChanged();
                                            }

                                        }
                                    } );
                        }
                    }
                } );






//        foodCardHelperClasses.add( new FoodCardHelperClass("1","","0.5","",R.drawable.loading_image,"12|3|22", "17-June-2022", "Available", "12.21,143.2","Gorr","","4.3", "logo"));


        adapter = new DonorRequestFoodCardAdapter(getContext(), foodCardHelperClasses );
        card_recycler_view.setAdapter( adapter );
    }
}