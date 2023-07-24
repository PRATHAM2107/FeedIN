package com.pratham.feedin.HelperClass;

import android.content.Context;
import android.content.Intent;
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
import com.pratham.feedin.Donation_Details_Activity;
import com.pratham.feedin.Loading_Activity;
import com.pratham.feedin.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DonorRequestFoodCardAdapter extends RecyclerView.Adapter<DonorRequestFoodCardAdapter.FoodCardViewHolder>{

    ArrayList<FoodCardHelperClass> foodCardHelperClassArrayList;

    private Context context;

    String foodName;

    private FirebaseAuth auth;

    private FirebaseFirestore db;

    public DonorRequestFoodCardAdapter(Context context, ArrayList<FoodCardHelperClass> foodCardHelperClassArrayList) {
        this.foodCardHelperClassArrayList = foodCardHelperClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public DonorRequestFoodCardAdapter.FoodCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int viewType) {
        View view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.donor_request_food_card,parent,false );
        DonorRequestFoodCardAdapter.FoodCardViewHolder foodCardViewHolder = new DonorRequestFoodCardAdapter.FoodCardViewHolder( view );
        return foodCardViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DonorRequestFoodCardAdapter.FoodCardViewHolder holder , int position) {
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
        foodName = FoodTypes;
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
        holder.request_on.setText( foodCardHelperClass.getRequestedOn() );

        holder.accept_request_btn.setOnClickListener( new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                db=  FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                String userid = auth.getCurrentUser().getUid().toString();
                LocalDateTime myDateObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
                String timeStamp = myDateObj.format(myFormatObj).toString();

                String path = foodCardHelperClass.getDbLoc().toString();
                Log.d("Err", path);
                db.document(path)
                        .update( "foodStatus", "Accepted", "acceptedOn",timeStamp )
                        .addOnSuccessListener( new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText( context.getApplicationContext() , "Request Accepted", Toast.LENGTH_SHORT ).show();
                                 db.collection( "Users" )
                                         .get()
                                         .addOnCompleteListener( new OnCompleteListener<QuerySnapshot>() {
                                             @Override
                                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                 if (task.isSuccessful()){
                                                     for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                                         db.collection( "Users" )
                                                                 .document(documentSnapshot.getId().toString())
                                                                 .get()
                                                                 .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                                                     @Override
                                                                     public void onSuccess(DocumentSnapshot snapshot) {
                                                                         String userName = snapshot.getString( "name" );
                                                                         String uids = snapshot.getString( "userId" );
                                                                         Log.d( "Err", "usrname - "+userName );
                                                                         Log.d( "Err", "DonatedTo - "+foodCardHelperClass.getDonatedTo().toString() );
                                                                         if (foodCardHelperClass.getDonatedTo().toString().equals( userName )){

                                                                             Log.e( "Err", "1 - "+uids );
                                                                             String Path = "Users/"+uids;
                                                                             db.document( Path )
                                                                                     .get()
                                                                                     .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                                                                         @Override
                                                                                         public void onSuccess(DocumentSnapshot snapshot) {


                                                                                             String totalDonation = snapshot.get( "totalDonation").toString();
                                                                                             String totalFlames = snapshot.get( "totalFlames" ).toString();
                                                                                             int td= Integer.valueOf( totalDonation )+1;
                                                                                             int tf = Integer.valueOf( totalFlames )+10;

//                                                                                             totalDonation = String.valueOf( td );
//                                                                                             totalFlames = String.valueOf( tf );
                                                                                             db.document( Path  )
                                                                                                     .update(  "totalDonation", td, "totalFlames", tf )
                                                                                                     .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                                         @Override
                                                                                                         public void onSuccess(Void unused) {


                                                                                                             HashMap<String, String> trans = new HashMap<>();

                                                                                                             trans.put( "amount", "10" );
                                                                                                             trans.put( "transactionDate", timeStamp );


                                                                                                             String data_path = "Donations/"+uids+"/"+"Transaction/"+timeStamp+"/";
                                                                                                             db.document(data_path)
                                                                                                                     .set(trans)
                                                                                                                     .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                                                         @Override
                                                                                                                         public void onSuccess(Void unused) {
                                                                                                                         }
                                                                                                                     } );


                                                                                                         }
                                                                                                     } );
                                                                                         }
                                                                                     } );
                                                                         }
                                                                         if (userid.equals( uids )){
                                                                             Log.e( "Err", "2 - "+uids );
                                                                             String Path = "Users/"+uids;
                                                                             db.document( Path )
                                                                                     .get()
                                                                                     .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                                                                                         @Override
                                                                                         public void onSuccess(DocumentSnapshot snapshot) {

                                                                                             HashMap<String, String> map1 = new HashMap<>();

                                                                                             // Adding elements to the Map
                                                                                             // using standard put() method
                                                                                             map1.put("requesterName", foodCardHelperClass.getDonatedTo().toString());
                                                                                             map1.put("foodName", foodName);
                                                                                             map1.put("requestStatus", "Accepted");
                                                                                             map1.put("time", timeStamp);

                                                                                             String totalDonation = snapshot.get( "totalDonation").toString();
                                                                                             String totalFlames = snapshot.get( "totalFlames" ).toString();
                                                                                             int td= Integer.valueOf( totalDonation )+1;
                                                                                             int tf = Integer.valueOf( totalFlames )+10;

//                                                                                             totalDonation = String.valueOf( td );
//                                                                                             totalFlames = String.valueOf( tf );
                                                                                             db.document( Path  )
                                                                                                     .update(  "totalDonation", td, "totalFlames", tf )
                                                                                                     .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                                         @Override
                                                                                                         public void onSuccess(Void unused) {


                                                                                                             HashMap<String, String> trans1 = new HashMap<>();

                                                                                                             trans1.put( "amount", "10" );
                                                                                                             trans1.put( "transactionDate", timeStamp );

                                                                                                             String data_path = "Donations/"+uids+"/"+"Transaction/"+timeStamp+"/";
                                                                                                             db.document(data_path)
                                                                                                                     .set(trans1)
                                                                                                                     .addOnSuccessListener( new OnSuccessListener<Void>() {
                                                                                                                         @Override
                                                                                                                         public void onSuccess(Void unused) {
                                                                                                                         }
                                                                                                                     } );


                                                                                                         }
                                                                                                     } );
                                                                                         }
                                                                                     } );
                                                                         }



                                                                     }
                                                                 } );
                                                     }
                                                 }
                                             }
                                         } );
                            }
                        } );

            }
        } );

    }

    @Override
    public int getItemCount() {
        return foodCardHelperClassArrayList.size();
    }

    public static class FoodCardViewHolder extends RecyclerView.ViewHolder{

        private String chapati, dryBhaji, wetBhaji, rice, foodImage, bestBefore,uploadedAt, foodStatus, foodLocation,userName, donatedTo, review;

        TextView requester_name,food_name,request_on;

        LinearLayout accept_request_btn;

        ImageView food_card_img;

        FirebaseFirestore db;


        public FoodCardViewHolder(@NonNull View itemView) {
            super( itemView );

//            Hooks

            FirebaseAuth auth;
            auth = FirebaseAuth.getInstance();

            accept_request_btn = itemView.findViewById( R.id.accept_request_btn );
            food_card_img = itemView.findViewById( R.id.food_card_img );
            requester_name = itemView.findViewById( R.id.requester_name );
            food_name = itemView.findViewById( R.id.food_name );
            request_on = itemView.findViewById( R.id.request_on );


        }
    }
}
