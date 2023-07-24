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

import com.faijan.feedin.HelperClass.FoodCardAdapter;
import com.faijan.feedin.HelperClass.FoodCardHelperClass;
import com.faijan.feedin.HelperClass.HistoryAdapter;
import com.faijan.feedin.HelperClass.RecentDonationHelperClass;
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


public class history_fragment extends Fragment {


    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView card_recycler_view;
    RecyclerView.Adapter adapter;

    TextView user_name;

    SharedPreferences UserDataSharedPreferences;


    private FirebaseFirestore db;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public history_fragment() {
    }

    public static history_fragment newInstance(String param1 , String param2) {
        history_fragment fragment = new history_fragment();
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
        View rootView = inflater.inflate( R.layout.fragment_history_fragment , container , false );

        swipeRefreshLayout = rootView.findViewById( R.id.swipeRefreshLayout );

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

        return rootView;
    }


    private void GetData() {

        ArrayList<RecentDonationHelperClass> recentDonationHelperClasses = new ArrayList<>();

        List<String> listUsers = new ArrayList<>();
        List<String> listDonates = new ArrayList<>();

        RecentDonationCLass recentDonationCLass = new RecentDonationCLass();
        String uid = UserDataSharedPreferences.getString( "uid" , "" );

        Log.e( "Err", uid );

        db.collection( "Donations" )
                .document(uid)
                .collection( "RecentDonations" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot Snapshot : task.getResult()){
                            db.collection( "Donations" )
                                    .document(uid)
                                    .collection( "RecentDonations" )
                                    .document(Snapshot.getId())
                                    .get()
                                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String foodStatus = documentSnapshot.getString( "foodStatus" );
                                            if (foodStatus.equals( "Accepted" )) {

                                                recentDonationCLass.setFoodImage( documentSnapshot.getString( "foodImage" ).toString() );
                                                recentDonationCLass.setDonatedFrom( documentSnapshot.getString( "donatedFrom" ).toString() );
                                                recentDonationCLass.setRequestedOn( documentSnapshot.getString( "requestedOn" ).toString() );
                                                recentDonationCLass.setFoodName( documentSnapshot.getString( "foodName" ).toString() );
                                                recentDonationCLass.setFoodStatus( documentSnapshot.getString( "foodStatus" ).toString() );
                                                recentDonationHelperClasses.add( new RecentDonationHelperClass(recentDonationCLass.getDonatedFrom(), recentDonationCLass.getFoodImage() , recentDonationCLass.getFoodName() , recentDonationCLass.getFoodStatus() , recentDonationCLass.getRequestedOn() )
                                                );

                                                adapter.notifyDataSetChanged();

                                            }
                                        }
                                    } );
                        }
                    }
                } );
        adapter = new HistoryAdapter(getContext(), recentDonationHelperClasses );
        card_recycler_view.setAdapter( adapter );

    }
}