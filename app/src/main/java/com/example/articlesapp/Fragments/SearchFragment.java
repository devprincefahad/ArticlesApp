package com.example.articlesapp.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.example.articlesapp.Adapters.SearchAdapter;
import com.example.articlesapp.Models.ListData;
import com.example.articlesapp.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {

    FirebaseDatabase database;
    LottieAnimationView lt;
    RecyclerView recyclerViewSearch;
    EditText edtSearch;
    TextView emptyText, searchKeywordTv, tvSearchArticle;
    ImageView imgBtn;
    ArrayList<ListData> listData;
    DatabaseReference ref;
    Query firebaseSearchQuery;
    SearchAdapter adapter;
    Context context;
    SearchView searchView;
    //    FirebaseRecyclerAdapter<ListData, SearchViewHolder> firebaseRecyclerAdapter;
    FirebaseRecyclerOptions<ListData> options;
    Client client;
    Index index;
    private static final String TAG = "SearchFragment";

    public SearchFragment() {
        // Required empty public constructor
    }

    private void setLocale(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getActivity().getBaseContext()
                .getResources()
                .updateConfiguration(config, getActivity().getBaseContext()
                        .getResources().getDisplayMetrics());

        //save data to shared preferences

        SharedPreferences.Editor editor = getActivity()
                .getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", language);
        editor.apply();
    }

    //load language saved in shared prefs

    public void loadLang() {
        SharedPreferences prefs = getActivity()
                .getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String lang = prefs.getString("My_Lang", "");
        setLocale(lang);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        loadLang();

        View mRoot = inflater.inflate(R.layout.fragment_search, container, false);

        setFadeAnimation(mRoot);

        ref = FirebaseDatabase.getInstance().getReference().child("articles");

        searchKeywordTv = mRoot.findViewById(R.id.searchKeywordTv);

        edtSearch = mRoot.findViewById(R.id.edtSearch);
        emptyText = mRoot.findViewById(R.id.tvEmptyText);
        tvSearchArticle = mRoot.findViewById(R.id.tvSearchArticle);
        recyclerViewSearch = mRoot.findViewById(R.id.recyclerViewSearch);
        lt = mRoot.findViewById(R.id.lt);
        lt.setVisibility(View.VISIBLE);

        loadDataFromFirebase();
        initAlgoliaClient();


        recyclerViewSearch.setVisibility(View.GONE);

      /*  edtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    recyclerViewSearch.setVisibility(View.VISIBLE);
                }else {
                    recyclerViewSearch.setVisibility(View.GONE);
                }
            }
        });*/

        String check = edtSearch.getText().toString();
        if (check.length() <= 0) {
            recyclerViewSearch.setVisibility(View.GONE);
            lt.setVisibility(View.VISIBLE);
        } else {
            recyclerViewSearch.setVisibility(View.VISIBLE);
            lt.setVisibility(View.GONE);
        }


        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                lt.setVisibility(View.GONE);
                recyclerViewSearch.setVisibility(View.VISIBLE);
                com.algolia.search.saas.Query query =
                        new com.algolia.search.saas.Query(s.toString())
                                .setAttributesToRetrieve("articles");

                index.searchAsync(query, new CompletionHandler() {
                    @Override
                    public void requestCompleted(@Nullable JSONObject content, @Nullable AlgoliaException e) {
                        try {
                            JSONArray hits = content.getJSONArray("hits");
                            Log.d(TAG, "requestCompleted: " + hits);
                            ArrayList<ListData> filteredList = new ArrayList<>();
                            for (int i = 0; i < hits.length(); i++) {
                                JSONObject hitObject = hits.getJSONObject(i);
                                String hitObjectId = hitObject.getString("objectID");
                                JSONObject highlightResultObject = hitObject.getJSONObject("_highlightResult");
                                JSONObject articleDataObj = highlightResultObject.getJSONObject("articleData");
                                JSONObject articleHeadlineObj = highlightResultObject.getJSONObject("articleHeadline");
                                JSONObject articleImgObj = highlightResultObject.getJSONObject("articleImage");
//
                                // get the values of these objects
                                String __data = articleDataObj.getString("value");
                                String _headline = articleHeadlineObj.getString("value");
                                String _image = articleImgObj.getString("value");
                                filteredList.add(new ListData(hitObjectId, _headline, __data, _image));
                                Log.d("e", "requestComplete " + hitObjectId + " " + _image);
                            }
                            //  update the filtered list in the adapter

                            if (filteredList.isEmpty()) {
                                searchKeywordTv.setVisibility(View.VISIBLE);
                                recyclerViewSearch.setVisibility(View.GONE);
                            } else {
                                adapter.filterList(filteredList);
                                searchKeywordTv.setVisibility(View.GONE);
                            }


                        } catch (JSONException e1) {
                            e.printStackTrace();
                            Toast t = Toast.makeText(getContext(),
                                    "Nothing Found",
                                    Toast.LENGTH_SHORT);
                            t.setGravity(Gravity.CENTER, 0, 0);
                            t.show();
                        }
                    }
                });
            }
        });

//        FirebaseRecyclerOptions<ListData> options =
//                new FirebaseRecyclerOptions.Builder<ListData>()
//                        .setQuery(FirebaseDatabase.getInstance()
//                                .getReference().child("articles"), ListData.class)
//                        .build();

//        adapter=new SearchAdapter(options);
//        recyclerViewSearch.setAdapter(adapter);

//        ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.isSuccessful()) {
//                    ArrayList<ListData> list = new ArrayList<>();
//                    for (DocumentSnapshot document : task.getResult()) {
//                        list.add(document.getString("productName"));
//                    }
//                    SearchAdapter arrayAdapter = new SearchAdapter(getContext(),list);
//                    recyclerViewSearch.setAdapter(arrayAdapter);
//                } else {
//                    Log.d("e", "Error getting documents: ", task.getException());
//                }
//            }
//        });

//        imgBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String searchTerm = edtSearch.getText().toString().trim();
//
//                if (searchTerm.isEmpty()) {
//                    edtSearch.setError("Please type something");
//                    edtSearch.requestFocus();
//                    return;
//                }
//                firebaseSearch(searchTerm);
//            }
//        });

        return mRoot;
    }

    private void initAlgoliaClient() {
        String yourApplicationID = "3IIR3R7ICU";
        String yourAdminAPIKey = "3492fb62a703e5938ec07386c08b38af";
        client = new Client(yourApplicationID, yourAdminAPIKey);
        index = client.getIndex("articles");
    }

    private void loadDataFromFirebase() {
        listData = new ArrayList<>();
        ArrayList<JSONObject> objectList = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ListData ld = dataSnapshot.getValue(ListData.class);
                    listData.add(ld);

                    try {
                        objectList.add(new JSONObject()
                                .put("objectID", ld.getObjectId())
                                .put("articleData", ld.getArticleData())
                                .put("articleHeadline", ld.getArticleHeadline())
                                .put("articleImage", ld.getArticleImage())
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                adapter = new SearchAdapter(getContext(), listData);
                recyclerViewSearch.setAdapter(adapter);
                recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));
                // push the array of article json objects to algolia
                index.addObjectsAsync(new JSONArray(objectList), null);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(Objects.requireNonNull(getActivity())
//                                .getApplicationContext(),
//                        error.getMessage(),
//                        Toast.LENGTH_SHORT).show();
            }
        });

        listData = new ArrayList<>();
        recyclerViewSearch.setHasFixedSize(true);
        recyclerViewSearch.setLayoutManager(new LinearLayoutManager(getContext()));


    }

//    private void firebaseSearch(String str) {
//
////        Toast.makeText(getContext(), "searching",
////                Toast.LENGTH_SHORT).show();
//
//        firebaseSearchQuery = ref.orderByChild("articleHeadline")
//                .startAt(str.toUpperCase())
//                .endAt(str.toLowerCase() + "\uf8ff");
//
//        options = new FirebaseRecyclerOptions.Builder<ListData>()
//                .setQuery(firebaseSearchQuery, ListData.class)
//                .setLifecycleOwner(this)
//                .build();
//
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ListData, SearchViewHolder>
//                (options) {
//            @Override
//            protected void onBindViewHolder(@NonNull SearchViewHolder holder,
//                                            int position, @NonNull ListData model) {
//                holder.setDetails(getContext(), model.getArticleHeadline(),
//                        model.getArticleData(), model.getArticleImage());
//            }
//
//            @NonNull
//            @Override
//            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
//                                                       int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.row_search, parent, false);
//                setFadeAnimation(view);
//                return new SearchViewHolder(view);
//            }
//        };
//        recyclerViewSearch.setAdapter(firebaseRecyclerAdapter);
//    }

//    class SearchViewHolder extends RecyclerView.ViewHolder {
//
//        View mView;
//
//        public SearchViewHolder(@NonNull View itemView) {
//            super(itemView);
//            mView = itemView;
//        }
//
//        public void setDetails(Context ctx, String articleHeadline,
//                               String articleData, String articleImage) {
//
//            TextView articleHeadlineSearch = mView.findViewById(R.id.articleHeadlineSearch);
//            TextView articleDataSearch = mView.findViewById(R.id.articleDataSearch);
//            ImageView articleImageSearch = mView.findViewById(R.id.articleImageSearch);
//            CardView cardViewSearch = mView.findViewById(R.id.cardViewSearch);
//
//            articleHeadlineSearch.setText(articleHeadline);
//            articleDataSearch.setText(articleData);
//
//            Glide.with(ctx)
//                    .load(articleImage)
//                    .placeholder(R.color.gray)
//                    .into(articleImageSearch);
//
//            cardViewSearch.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    String headline = articleHeadlineSearch.getText().toString().trim();
//                    String data = articleDataSearch.getText().toString().trim();
//
//                    Intent intent = new Intent(ctx, ShowDataRVActivity.class);
//                    intent.putExtra("articleHeadline", headline);
//                    intent.putExtra("articleImage", articleImage);
//                    intent.putExtra("articleData", data);
//                    mView.getContext().startActivity(intent);
//                    customType(ctx, "fadein-to-fadeout");
//
//                }
//            });
//
//        }
//    }


    private void setFadeAnimation(View itemView) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f); //(0.0f, 1.0f);
        anim.setDuration(400);
        itemView.startAnimation(anim);

    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        adapter.stopListening();
//    }

}

