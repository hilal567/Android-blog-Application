package com.example.photoblog;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import static com.google.firebase.firestore.FirebaseFirestore.getInstance;


public class HomeFragment extends Fragment {

    private RecyclerView blog_list_view;
    //make a list in which the retrieved data will be stored before it is displayed

    private List<BlogPost> blog_list;


    private FirebaseFirestore firebaseFirestore;

    private BlogRecyclerAdapter blogRecyclerAdapter;

    private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater,final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        blog_list = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);

        blogRecyclerAdapter = new BlogRecyclerAdapter(blog_list);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);


        //initialize the firetore Variable
        firebaseFirestore=FirebaseFirestore.getInstance();

        Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

       firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                //for loop that detects the change in the firestore document
                for(DocumentChange doc:documentSnapshots.getDocumentChanges()){

                    //if statement to perfeom an action if the document has changed and if it hasn't
                    if (doc.getType() == DocumentChange.Type.ADDED){

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        blog_list.add(blogPost);

                        //notify us when the data has changed
                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });


        return view;
    }
    public void LoaadMorePost (){
        Query nextQuery = firebaseFirestore.collection("Posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(3);

        nextQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                //for loop that detects the change in the firestore document
                for(DocumentChange doc:documentSnapshots.getDocumentChanges()){

                    //if statement to perfeom an action if the document has changed and if it hasn't
                    if (doc.getType() == DocumentChange.Type.ADDED){

                        BlogPost blogPost = doc.getDocument().toObject(BlogPost.class);
                        blog_list.add(blogPost);

                        //notify us when the data has changed
                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });


    }

}