package com.pratham.feedin.HelperClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pratham.feedin.Donation_Details_Activity;
import com.pratham.feedin.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.FoodCardViewHolder> {

    ArrayList<RecentDonationHelperClass> foodCardHelperClassArrayList;

    private Context context;

    public HistoryAdapter(Context context, ArrayList<RecentDonationHelperClass> foodCardHelperClassArrayList) {
        this.foodCardHelperClassArrayList = foodCardHelperClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public HistoryAdapter.FoodCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.recent_donation_food_card,parent,false );
        HistoryAdapter.FoodCardViewHolder foodCardViewHolder = new HistoryAdapter.FoodCardViewHolder( view );
        return foodCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.FoodCardViewHolder holder , int position) {
        RecentDonationHelperClass foodCardHelperClass = foodCardHelperClassArrayList.get( position );




//        Uri foodImageUri = Uri.parse(foodCardHelperClass.getFoodImage());
        Glide.with(context)
                .load( foodCardHelperClassArrayList.get( position ).getFoodImage() )
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.not_found_image)
                .apply(new RequestOptions().override(250, 250))
                .centerCrop()
                .diskCacheStrategy( DiskCacheStrategy.ALL)
                .into( holder.food_card_img );
        holder.donor_name.setText( foodCardHelperClass.getDonatedFrom() );
        holder.request_status_tag.setText( "Request "+foodCardHelperClass.getFoodStatus() );
        holder.food_name.setText( foodCardHelperClass.getFoodName() );
        holder.request_time.setText( foodCardHelperClass.getRequestedOn() );

//        holder.itemView.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent( holder.itemView.getContext(), Donation_Details_Activity.class);
//                intent.putExtra("card_path", foodCardHelperClass.getDbLoc().toString());
//                context.startActivity(intent);
//            }
//        } );

    }

    @Override
    public int getItemCount() {
        return foodCardHelperClassArrayList.size();
    }

    public static class FoodCardViewHolder extends RecyclerView.ViewHolder{

        private String chapati, dryBhaji, wetBhaji, rice, foodImage, bestBefore,uploadedAt, foodStatus, foodLocation,userName, donatedTo, review;

        TextView donor_name, request_status_tag,food_name,request_time;

        ImageView food_card_img;

        public FoodCardViewHolder(@NonNull View itemView) {
            super( itemView );

//            Hooks

            food_card_img = itemView.findViewById( R.id.food_card_img );
            donor_name = itemView.findViewById( R.id.donor_name );
            request_status_tag = itemView.findViewById( R.id.request_status_tag );
            food_name = itemView.findViewById( R.id.food_name );
            request_time = itemView.findViewById( R.id.request_time );

        }
    }
}
