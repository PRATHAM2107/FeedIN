package com.pratham.feedin;

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

import com.pratham.feedin.HelperClass.HistoryAdapter;
import com.pratham.feedin.HelperClass.RecentDonationHelperClass;
import com.pratham.feedin.HelperClass.TransactionAdapter;
import com.pratham.feedin.HelperClass.TransactionHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class wallet_fragment extends Fragment {


    SwipeRefreshLayout swipeRefreshLayout;

    RecyclerView card_recycler_view;
    RecyclerView.Adapter adapter;

    TextView total_flames;

    SharedPreferences UserDataSharedPreferences;
    private FirebaseFirestore db;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public wallet_fragment() {
    }


    public static wallet_fragment newInstance(String param1 , String param2) {
        wallet_fragment fragment = new wallet_fragment();
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
        View rootView = inflater.inflate( R.layout.fragment_wallet_fragment , container , false );

        total_flames = rootView.findViewById( R.id.total_flames );
        swipeRefreshLayout = rootView.findViewById( R.id.swipeRefreshLayout );

        db = FirebaseFirestore.getInstance();


        UserDataSharedPreferences = getActivity().getSharedPreferences("UserPreferences",MODE_PRIVATE);
        String username = UserDataSharedPreferences.getString( "name" , "" );


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

        ArrayList<TransactionHelperClass> recentDonationHelperClasses = new ArrayList<>();

        List<String> listUsers = new ArrayList<>();
        List<String> listDonates = new ArrayList<>();


        String uid = UserDataSharedPreferences.getString( "uid" , "" );

        Log.e( "Err", "--"+uid );

        db.collection( "Users" )
                .document(uid)
                .get()
                .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        total_flames.setText( snapshot.get( "totalFlames" ).toString() );
                    }
                } );


        db.collection( "Donations" )
                .document(uid)
                .collection( "Transaction" )
                .get()
                .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot Snapshot : task.getResult()){
                            db.collection( "Donations" )
                                    .document(uid)
                                    .collection( "Transaction" )
                                    .document(Snapshot.getId())
                                    .get()
                                    .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            String amt = documentSnapshot.getString( "amount" );
                                            String td = documentSnapshot.getString( "transactionDate" );
                                            String trnasDt = td.substring( 0, td.length() - 8 );
                                            recentDonationHelperClasses.add( new TransactionHelperClass(trnasDt,amt));
                                            adapter.notifyDataSetChanged();


                                        }
                                    } );
                        }
                    }
                } );
        adapter = new TransactionAdapter(getContext(), recentDonationHelperClasses );
        card_recycler_view.setAdapter( adapter );

    }
}