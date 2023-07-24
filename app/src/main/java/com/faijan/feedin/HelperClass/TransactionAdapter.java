package com.faijan.feedin.HelperClass;

import android.content.Context;
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
import com.faijan.feedin.R;

import java.util.ArrayList;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.FoodCardViewHolder> {

    ArrayList<TransactionHelperClass> foodCardHelperClassArrayList;

    private Context context;

    public TransactionAdapter(Context context, ArrayList<TransactionHelperClass> foodCardHelperClassArrayList) {
        this.foodCardHelperClassArrayList = foodCardHelperClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionAdapter.FoodCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.transaction_card_layout,parent,false );
        TransactionAdapter.FoodCardViewHolder foodCardViewHolder = new TransactionAdapter.FoodCardViewHolder( view );
        return foodCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionAdapter.FoodCardViewHolder holder , int position) {
        TransactionHelperClass foodCardHelperClass = foodCardHelperClassArrayList.get( position );




//        Uri foodImageUri = Uri.parse(foodCardHelperClass.getFoodImage());

        holder.transactionDate.setText( foodCardHelperClass.getTransactionDate() );
        holder.transationAmount.setText( foodCardHelperClass.getAmount() );


    }

    @Override
    public int getItemCount() {
        return foodCardHelperClassArrayList.size();
    }

    public static class FoodCardViewHolder extends RecyclerView.ViewHolder{


        TextView transactionDate, transationAmount;



        public FoodCardViewHolder(@NonNull View itemView) {
            super( itemView );

//            Hooks

            transactionDate = itemView.findViewById( R.id.transition_date );
            transationAmount = itemView.findViewById( R.id.transaction_amt );


        }
    }
}
