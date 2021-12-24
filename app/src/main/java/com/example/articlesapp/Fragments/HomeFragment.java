package com.example.articlesapp.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.articlesapp.Activities.ShowDataRVActivity;
import com.example.articlesapp.Adapters.MyAdapter;
import com.example.articlesapp.Models.ListData;
import com.example.articlesapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static maes.tech.intentanim.CustomIntent.customType;

public class HomeFragment extends Fragment {

//    private ArrayList<ListData> listData = new ArrayList<>();

//    FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    DatabaseReference likereference;
    Boolean testclick = false;
    FirebaseDatabase database;
    private MyAdapter adapter;
    FirebaseRecyclerAdapter<ListData, MyAdapter.myviewholder>
            firebaseRecyclerAdapter;

    //    private ShimmerRecyclerView recyclerViewHome;
    private ShimmerRecyclerView recyclerViewHome;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mRoot = inflater.inflate(R.layout.fragment_home, container, false);
        setFadeAnimation(mRoot);

        database = FirebaseDatabase.getInstance();

        likereference = FirebaseDatabase.getInstance().getReference("likes");

        recyclerViewHome = mRoot.findViewById(R.id.recyclerViewHome);

        recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseRecyclerOptions<ListData> options = new FirebaseRecyclerOptions.Builder
                <ListData>().setQuery(database.getReference().child("articles"), ListData.class)
                .build();
        database.getReference().child("articles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recyclerViewHome.hideShimmerAdapter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        /*
            start
        */

        firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<ListData, MyAdapter.myviewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MyAdapter.myviewholder holder,
                                                    int position, @NonNull ListData model) {

                        holder.tvArticleHeadline.setText(model.getArticleHeadline());

                        holder.tvArticleData.setText(model.getArticleData());

                        holder.tvArticleData.setVisibility(View.GONE);
                        holder.imgLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        Glide.with(holder.imgArticleImage.getContext())
                                .load(model.getArticleImage())
                                .placeholder(R.color.gray)
                                .into(holder.imgArticleImage);

                        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(getContext(), ShowDataRVActivity.class);
                                intent.putExtra("articleHeadline", model.getArticleHeadline());
                                intent.putExtra("articleImage", model.getArticleImage());
                                intent.putExtra("articleData", model.getArticleData());
                                getActivity().startActivity(intent);
                                customType(getContext(), "fadein-to-fadeout");

                            }
                        });

                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        final String userid = firebaseUser.getUid();
                        final String postkey = getRef(position).getKey();

                        holder.getlikebuttonstatus(postkey, userid);

                        holder.imgLikes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                testclick = true;

                                likereference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (testclick == true) {
                                            if (snapshot.child(postkey).hasChild(userid)) {
                                                likereference.child(postkey).child(userid).removeValue();
                                                testclick = false;
                                            } else {
                                                likereference.child(postkey).child(userid).setValue(true);
                                                testclick = false;
                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                            }
                        });

                    }

                    @NonNull
                    @Override
                    public MyAdapter.myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
                        return new MyAdapter.myviewholder(view);
                    }
                };


//        firebaseRecyclerAdapter.startListening();
//        recyclerViewHome.setAdapter(firebaseRecyclerAdapter);

        /*end*/

//        adapter = new MyAdapter(options, getContext());
//        recyclerViewHome.setAdapter(adapter);
        adapter = new MyAdapter(options, getContext());

        firebaseRecyclerAdapter.startListening();
        recyclerViewHome.setAdapter(firebaseRecyclerAdapter);
        recyclerViewHome.showShimmerAdapter();

//        recyclerViewHome.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                recyclerViewHome.hideShimmerAdapter();
//            }
//        },2500);

//        recyclerViewHome.setAdapter(adapter);

//        database.getReference().child("articles")
//                .addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                listData.clear();
//                for (DataSnapshot snapshot1: snapshot.getChildren()){
//                    ListData ld = snapshot1.getValue(ListData.class);
//                    Collections.shuffle(listData);
//                    listData.add(ld);
//                }
//                recyclerViewHome.hideShimmerAdapter();
//                adapter.notifyDataSetChanged();
//            }

//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
////                recyclerViewHome.setAdapter(firebaseRecyclerAdapter);
////                recyclerViewHome.showShimmerAdapter();
//                recyclerViewHome.setHasFixedSize(true);
//                recyclerViewHome.setLayoutManager(new LinearLayoutManager(getContext()));
////        FirebaseRecyclerOptions<ListData> options =
////                new FirebaseRecyclerOptions.Builder<ListData>()
////                        .setQuery(FirebaseDatabase.getInstance()
////                                        .getReference()
////                                        .child("articles")
////                                , ListData.class)
////                        .build();
//
//
//        /*FirebaseRecyclerAdapter<ListData, MyViewHolder> firebaseRecyclerAdapter =
//                new FirebaseRecyclerAdapter<ListData, MyViewHolder>(options) {
//                    @Override
//                    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ListData model) {
//
//                        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//                        final String userid=firebaseUser.getUid();
//                        final String postkey=getRef(position).getKey();
//
//                        holder.getlikebuttonstatus(postkey,userid);
//                        holder.imgLikes.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//
//                                Toast.makeText(getContext(), "Liked"
//                                        , Toast.LENGTH_SHORT).show();
//
//                                testclick=true;
//
//                                likereference.addValueEventListener(new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                        if(testclick==true)
//                                        {
//                                            if(snapshot.child(postkey).hasChild(userid))
//                                            {
//                                                likereference.child(postkey).child(userid).removeValue();
//                                                testclick=false;
//                                            }
//                                            else
//                                            {
//                                                likereference.child(postkey).child(userid).setValue(true);
//                                                testclick=false;
//                                            }
//
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                    }
//                                });
//
//
//                            }
//                        });
//
//
//                    }
//
//                    @NonNull
//                    @Override
//                    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                        return null;
//                    }
//                };*/
//
//
//
////        FirebaseRecyclerAdapter<ListData, MyAdapter.ViewHolder> firebaseRecyclerAdapter
////                = new FirebaseRecyclerAdapter<ListData, MyAdapter.ViewHolder>(options) {
////            @Override
////            protected void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder,
////                                            int position, @NonNull ListData model) {
//////                holder.prepareexoplayer(getApplication(),model.getTitle(),model.getVurl());
////
////                FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
////                final String userid=firebaseUser.getUid();
////                final String postkey=getRef(position).getKey();
////
////                holder.getlikebuttonstatus(postkey,userid);
////                holder.imgLikes.setOnClickListener(new View.OnClickListener() {
////                    @Override
////                    public void onClick(View view) {
////                        testclick=true;
////
////                        likereference.addValueEventListener(new ValueEventListener() {
////                            @Override
////                            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                                if(testclick==true)
////                                {
////                                    if(snapshot.child(postkey).hasChild(userid))
////                                    {
////                                        likereference.child(postkey).child(userid).removeValue();
////                                        testclick=false;
////                                    }
////                                    else
////                                    {
////                                        likereference.child(postkey).child(userid).setValue(true);
////                                        testclick=false;
////                                    }
////
////                                }
////                            }
////
////                            @Override
////                            public void onCancelled(@NonNull DatabaseError error) {
////
////                            }
////                        });
////
////
////                    }
////                });
////
////            }
////
////            @NonNull
////            @Override
////            public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
////                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.row,parent,false);
////                return new MyAdapter.ViewHolder(view);
////            }
////        };
//
////        firebaseRecyclerAdapter.startListening();
//
//       /* firebaseRecyclerAdapter = new FirebaseRecyclerAdapter
//                <ListData, MyViewHolder>
//                (options)
//        {
//
//            @NonNull
//            @Override
//            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
//                setFadeAnimation(view);
//
//                return new MyViewHolder(view);
//
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ListData model) {
//
////                holder.setDetails(getContext(), model.getArticleHeadline(), model.getArticleData(), model.getArticleImage());
//                holder.tvArticleHeadline.setText(model.getArticleHeadline());
//                holder.tvArticleData.setText(model.getArticleData());
//                Glide.with(holder.imgArticleImage.getContext())
//                        .load(ld.getArticleImage())
//                        .placeholder(R.color.gray)
//                        .into(holder.imgArticleImage);
//
//                holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent intent = new Intent(getContext(), ShowDataRVActivity.class);
//                        intent.putExtra("articleHeadline", listData.get(position).getArticleHeadline());
//                        intent.putExtra("articleImage", listData.get(position).getArticleImage());
//                        intent.putExtra("articleData", listData.get(position).getArticleData());
//                        getActivity().startActivity(intent);
//                        customType(getContext(), "fadein-to-fadeout");
//
//
//                    }
//                });
//
//                setFadeAnimation(holder.itemView);
//
//
//            }
//
//        };*/
//
//        adapter = new MyAdapter();
//        recyclerViewHome.setAdapter(adapter);
//
//        return mRoot;
//
//    }
        return mRoot;


    }

    private void setFadeAnimation(View itemView) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f); //(0.0f, 1.0f);
        anim.setDuration(400);
        itemView.startAnimation(anim);

    }

    @Override
    public void onStart() {
        super.onStart();

        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();

        adapter.stopListening();

    }
}


//
//class MyViewHolder extends RecyclerView.ViewHolder {
//
//    public TextView tvArticleHeadline, tvArticleData, tvLikes;
//    public ImageView imgArticleImage, imgLikes, imgShareArticle;
//    public RelativeLayout relativeLayout;
//
//    public MyViewHolder(@NonNull View itemView) {
//        super(itemView);
//
//
//        imgShareArticle = itemView.findViewById(R.id.imgShareArticle);
//        imgLikes = itemView.findViewById(R.id.imgLikeIcon);
//        tvLikes = itemView.findViewById(R.id.tvLikes);
//        tvArticleHeadline = itemView.findViewById(R.id.articleHeadline);
//        tvArticleData = itemView.findViewById(R.id.articleData);
//        imgArticleImage = itemView.findViewById(R.id.articleImage);
//        relativeLayout = itemView.findViewById(R.id.rlRv);

//        tvArticleHeadline.setText(articleHeadline);
//        tvArticleData.setText(articleData);
//        Glide.with(ctx)
//            .
//
//    load(articleImage)
//                .
//
//    placeholder(R.color.gray)
//                .
//
//    into(imgArticleImage);
//
//        relativeLayout.setOnClickListener(new View.OnClickListener()
//
//    {
//        @Override
//        public void onClick (View v){
//
//        String headline = tvArticleHeadline.getText().toString().trim();
//        String data = tvArticleData.getText().toString().trim();
//
//        Intent intent = new Intent(ctx, ShowDataRVActivity.class);
//        intent.putExtra("articleHeadline", headline);
//        intent.putExtra("articleImage", articleImage);
//        intent.putExtra("articleData", data);
//        ctx.startActivity(intent);
//        customType(ctx, "fadein-to-fadeout");
//
//    }
//    });


//    }



