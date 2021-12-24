package com.example.articlesapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.articlesapp.Models.ListData;
import com.example.articlesapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyAdapter extends FirebaseRecyclerAdapter<ListData, MyAdapter.myviewholder> {

    Context context;

    public MyAdapter(@NonNull FirebaseRecyclerOptions<ListData> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull myviewholder holder, int position, @NonNull ListData model) {


    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new myviewholder(view);
    }

    public static class myviewholder extends RecyclerView.ViewHolder {
        public TextView tvArticleHeadline, tvArticleData, tvLikes;
        public ImageView imgArticleImage, imgLikes, imgShareArticle;
        public RelativeLayout relativeLayout;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tvArticleHeadline = itemView.findViewById(R.id.articleHeadline);
            tvArticleData = itemView.findViewById(R.id.articleData);
            imgArticleImage = itemView.findViewById(R.id.articleImage);
            relativeLayout = itemView.findViewById(R.id.rlRv);
//            imgShareArticle = itemView.findViewById(R.id.imgShareArticle);
            imgLikes = itemView.findViewById(R.id.imgLikeIcon);
            tvLikes = itemView.findViewById(R.id.tvLikes);
        }

       public void getlikebuttonstatus(final String postkey, final String userid)
       {
           DatabaseReference likereference;
//           Boolean testclick = false;
           likereference= FirebaseDatabase.getInstance().getReference("likes");
           likereference.addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   if(snapshot.child(postkey).hasChild(userid))
                   {
                       int likecount=(int)snapshot.child(postkey).getChildrenCount();
                       tvLikes.setText(likecount+" likes");
                       imgLikes.setImageResource(R.drawable.favorite);
                   }
                   else
                   {
                       int likecount=(int)snapshot.child(postkey).getChildrenCount();
                       tvLikes.setText(likecount+" likes");
                       imgLikes.setImageResource(R.drawable.favorite_border);
                   }
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           });
       }


    }
}

//public class MyAdapter extends FirebaseRecyclerAdapter<ListData, MyAdapter.ViewHolder> {
//
//    Context context;
//
//    private ArrayList<ListData> listData;
//
//    public MyAdapter(@NonNull FirebaseRecyclerOptions<ListData> options,Context context) {
//        super(options);
//        this.context = context;
//    }
//
////    public MyAdapter(Context context, ArrayList<ListData> listData) {
////        this.context = context;
////        this.listData = listData;
////    }
//
//    @NonNull
//    @Override
//    public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
//        return new ViewHolder(view);
//    }
//
//    private void setFadeAnimation(View itemView) {
//        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f); //(0.0f, 1.0f);
//        anim.setDuration(500);
//        itemView.startAnimation(anim);
//
//    }
//
////    @Override
////    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
////
////
////    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ListData model) {
//
////        ListData ld = listData.get(position);
//
//        holder.tvArticleHeadline.setText(model.getArticleHeadline());
//
//        holder.tvArticleData.setText(model.getArticleData());
//
//        holder.tvArticleData.setVisibility(View.GONE);
//
//        Glide.with(holder.imgArticleImage.getContext())
//                .load(model.getArticleImage())
//                .placeholder(R.color.gray)
//                .into(holder.imgArticleImage);
//
//        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, ShowDataRVActivity.class);
//                intent.putExtra("articleHeadline", model.getArticleHeadline());
//                intent.putExtra("articleImage", model.getArticleImage());
//                intent.putExtra("articleData", model.getArticleData());
//                context.startActivity(intent);
//                customType(context, "fadein-to-fadeout");
//
//
//            }
//        });
//
//        setFadeAnimation(holder.itemView);
//    }
//
////    @Override
////    public int getItemCount() {
////        return listData.size();
////    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//
//        DatabaseReference likereference;
//        TextView tvArticleHeadline, tvArticleData, tvLikes;
//        ImageView imgArticleImage, imgLikes, imgShareArticle;
//        RelativeLayout relativeLayout;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//
//            imgShareArticle = itemView.findViewById(R.id.imgShareArticle);
//            tvArticleHeadline = itemView.findViewById(R.id.articleHeadline);
//            tvArticleData = itemView.findViewById(R.id.articleData);
//            imgArticleImage = itemView.findViewById(R.id.articleImage);
//            relativeLayout = itemView.findViewById(R.id.rlRv);
//            imgLikes = itemView.findViewById(R.id.imgLikeIcon);
//            tvLikes = itemView.findViewById(R.id.tvLikes);
//        }
//
//
//
//    }
//
//
//}
