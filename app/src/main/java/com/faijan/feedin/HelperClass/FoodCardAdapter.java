package com.faijan.feedin.HelperClass;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.faijan.feedin.Donation_Details_Activity;
import com.faijan.feedin.R;
import com.faijan.feedin.dashboard_fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodCardAdapter extends RecyclerView.Adapter<FoodCardAdapter.FoodCardViewHolder>{
    ArrayList<FoodCardHelperClass>  foodCardHelperClassArrayList;

    private Context context;

    public FoodCardAdapter(Context context, ArrayList<FoodCardHelperClass> foodCardHelperClassArrayList) {
        this.foodCardHelperClassArrayList = foodCardHelperClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public FoodCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.ngo_food_card,parent,false );
        FoodCardViewHolder foodCardViewHolder = new FoodCardViewHolder( view );
        return foodCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FoodCardViewHolder holder , int position) {
        FoodCardHelperClass foodCardHelperClass = foodCardHelperClassArrayList.get( position );


        List<String> list=new ArrayList<String>();

//        list.add( "Chapati -"+foodCardHelperClass.getChapati().toString()+" pcs");
//        list.add( "Dry Bhaji -"+foodCardHelperClass.getChapati().toString()+" kg");
//        list.add( "Wet Bhaji -"+foodCardHelperClass.getChapati().toString()+" kg");
//        list.add( "Rice -"+foodCardHelperClass.getChapati().toString() +" kg");
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
                .apply(new RequestOptions().override(250, 250))
                .centerCrop()
                .diskCacheStrategy( DiskCacheStrategy.ALL)
                .into( holder.food_card_img );
        holder.donor_name.setText( foodCardHelperClass.getUserName() );
        holder.donor_review.setText( foodCardHelperClass.getReview() );
        holder.food_name.setText( FoodTypes );
        holder.food_distance.setText( foodCardHelperClass.getFoodLocation() );
        holder.best_before_time.setText( foodCardHelperClass.getBestBefore() );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( holder.itemView.getContext(), Donation_Details_Activity.class);
                intent.putExtra("card_path", foodCardHelperClass.getDbLoc().toString());
                context.startActivity(intent);
            }
        } );

    }

    @Override
    public int getItemCount() {
        return foodCardHelperClassArrayList.size();
    }

    public static class FoodCardViewHolder extends RecyclerView.ViewHolder{

        private String chapati, dryBhaji, wetBhaji, rice, foodImage, bestBefore,uploadedAt, foodStatus, foodLocation,userName, donatedTo, review;

        TextView donor_name, donor_review,food_name,food_distance,best_before_time;

        ImageView food_card_img;

        public FoodCardViewHolder(@NonNull View itemView) {
            super( itemView );

//            Hooks



            food_card_img = itemView.findViewById( R.id.food_card_img );
            donor_name = itemView.findViewById( R.id.donor_name );
            donor_review = itemView.findViewById( R.id.donor_review );
            food_name = itemView.findViewById( R.id.food_name );
            food_distance = itemView.findViewById( R.id.food_distance );
            best_before_time = itemView.findViewById( R.id.best_before_time );


        }
    }
}
