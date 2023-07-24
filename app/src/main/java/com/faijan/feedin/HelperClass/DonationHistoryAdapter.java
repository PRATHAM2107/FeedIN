package com.faijan.feedin.HelperClass;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.faijan.feedin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.FoodCardViewHolder>{

    ArrayList<FoodCardHelperClass> foodCardHelperClassArrayList;

    private Context context;

    private FirebaseFirestore db;

    public DonationHistoryAdapter(Context context, ArrayList<FoodCardHelperClass> foodCardHelperClassArrayList) {
        this.foodCardHelperClassArrayList = foodCardHelperClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DonationHistoryAdapter.FoodCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.donor_donation_card,parent,false );
        DonationHistoryAdapter.FoodCardViewHolder foodCardViewHolder = new DonationHistoryAdapter.FoodCardViewHolder( view );
        return foodCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DonationHistoryAdapter.FoodCardViewHolder holder , int position) {
        FoodCardHelperClass foodCardHelperClass = foodCardHelperClassArrayList.get( position );


        List<String> list=new ArrayList<String>();

        list.add( "Chapati");
        list.add( "Dry Bhaji");
        list.add( "Wet Bhaji");
        list.add( "Rice");
        if(foodCardHelperClass.getChapati().isEmpty()){
            list.remove( "Chapati");
        }
        if(foodCardHelperClass.getDryBhaji().isEmpty()){
            list.remove( "Dry Bhaji");
        }
        if(foodCardHelperClass.getWetBhaji().isEmpty()){
            list.remove( "Wet Bhaji");
        }
        if(foodCardHelperClass.getRice().isEmpty()){
            list.remove( "Rice");
        }
        String FoodTypes = list.toString();

        FoodTypes = FoodTypes.replaceAll("[\\[\\](){}]","");

//        Uri foodImageUri = Uri.parse(foodCardHelperClass.getFoodImage());
        Glide.with(context)
                .load( foodCardHelperClassArrayList.get( position ).getFoodImage() )
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.not_found_image)
                .centerCrop()
                .apply(new RequestOptions().override(250, 250))
                .diskCacheStrategy( DiskCacheStrategy.ALL)
                .into( holder.food_card_img );
        holder.requester_name.setText( foodCardHelperClass.getDonatedTo() );
        holder.food_name.setText( FoodTypes );
        holder.request_on.setText( foodCardHelperClass.getAcceptedOn() );



    }

    @Override
    public int getItemCount() {
        return foodCardHelperClassArrayList.size();
    }

    public static class FoodCardViewHolder extends RecyclerView.ViewHolder{

        private String chapati, dryBhaji, wetBhaji, rice, foodImage, bestBefore,uploadedAt, foodStatus, foodLocation,userName, donatedTo, review;

        TextView requester_name,food_name,request_on;


        ImageView food_card_img;

        FirebaseFirestore db;


        public FoodCardViewHolder(@NonNull View itemView) {
            super( itemView );

//            Hooks



            food_card_img = itemView.findViewById( R.id.food_card_img );
            requester_name = itemView.findViewById( R.id.requester_name );
            food_name = itemView.findViewById( R.id.food_name );
            request_on = itemView.findViewById( R.id.donated_date );


        }
    }
}
