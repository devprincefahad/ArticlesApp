package com.example.articlesapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.articlesapp.Activities.ShowDataRVActivity;
import com.example.articlesapp.Models.ListData;
import com.example.articlesapp.R;

import java.util.ArrayList;

import static maes.tech.intentanim.CustomIntent.customType;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyViewHolder> {

    ArrayList<ListData> list;
    Context context;

    public SearchAdapter(Context context, ArrayList<ListData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.articleHeadlineSearch.setText(Html.fromHtml(list.get(position)
                    .getArticleHeadline(),Html.FROM_HTML_MODE_COMPACT));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.articleDataSearch.setText(Html.fromHtml(list.get(position)
                    .getArticleData(),Html.FROM_HTML_MODE_COMPACT));
        }
        Glide.with(context)
                .load(list.get(position).getArticleImage())
                .placeholder(R.color.gray)
                .into(holder.articleImageSearch);

        holder.cardViewSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String headline = holder.articleHeadlineSearch.getText().toString().trim();
                String data = holder.articleDataSearch.getText().toString().trim();

                Intent intent = new Intent(context, ShowDataRVActivity.class);
                intent.putExtra("articleHeadline", headline);
                intent.putExtra("articleImage", list.get(position).getArticleImage());
                intent.putExtra("articleData", data);
                context.startActivity(intent);
                customType(context, "fadein-to-fadeout");
            }
        });
    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    // assign the filtered list to the existing list
    public void filterList(ArrayList<ListData> filteredList){
        list = filteredList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView articleHeadlineSearch;
        TextView articleDataSearch;
        ImageView articleImageSearch;
        CardView cardViewSearch;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            articleHeadlineSearch = itemView.findViewById(R.id.articleHeadlineSearch);
            articleDataSearch = itemView.findViewById(R.id.articleDataSearch);
            articleImageSearch = itemView.findViewById(R.id.articleImageSearch);
            cardViewSearch = itemView.findViewById(R.id.cardViewSearch);

        }
    }
}
    /*95 new*/
//import android.content.Context;
//import android.content.Intent;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.example.articlesapp.Activities.ShowDataRVActivity;
//import com.example.articlesapp.Models.ListData;
//import com.example.articlesapp.R;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
//import com.firebase.ui.database.FirebaseRecyclerOptions;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static maes.tech.intentanim.CustomIntent.customType;
//
//public class SearchAdapter extends FirebaseRecyclerAdapter<ListData,SearchViewHolder> {
//
//    Context context;
//    private ArrayList<ListData> ld;
//
//    public SearchAdapter(@NonNull FirebaseRecyclerOptions<ListData> options) {
//        super(options);
//    }
//
//    @Override
//    protected void onBindViewHolder(@NonNull SearchViewHolder holder,
//                                    int position, @NonNull ListData model) {
//        holder.setDetails(context,
//                model.getArticleHeadline(),
//                model.getArticleData(),
//                model.getArticleImage());
//    }
//
//    @NonNull
//    @Override
//    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
//                                               int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.row_search, parent, false);
//        return new SearchViewHolder(view);
//    }
//
//
//}
//class SearchViewHolder extends RecyclerView.ViewHolder {
//
//    View mView;
//
//    public SearchViewHolder(View itemView) {
//        super(itemView);
//        mView = itemView;
//
//    }
//
//    public void setDetails(Context ctx, String articleHeadline,
//                           String articleData, String articleImage) {
//
//        TextView articleHeadlineSearch = mView.findViewById(R.id.articleHeadlineSearch);
//        TextView articleDataSearch = mView.findViewById(R.id.articleDataSearch);
//        ImageView articleImageSearch = mView.findViewById(R.id.articleImageSearch);
//        CardView cardViewSearch = mView.findViewById(R.id.cardViewSearch);
//
//        articleHeadlineSearch.setText(articleHeadline);
//        articleDataSearch.setText(articleData);
//
//        Glide.with(mView.getContext())
//                .load(articleImage)
//                .placeholder(R.color.gray)
//                .into(articleImageSearch);
//
//        cardViewSearch.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String headline = articleHeadlineSearch.getText().toString().trim();
//                String data = articleDataSearch.getText().toString().trim();
//
//                Intent intent = new Intent(mView.getContext(), ShowDataRVActivity.class);
//                intent.putExtra("articleHeadline", headline);
//                intent.putExtra("articleImage", articleImage);
//                intent.putExtra("articleData", data);
//                mView.getContext().startActivity(intent);
//                customType(mView.getContext(), "fadein-to-fadeout");
//
//            }
//        });
//
//    }
//
//}
//
