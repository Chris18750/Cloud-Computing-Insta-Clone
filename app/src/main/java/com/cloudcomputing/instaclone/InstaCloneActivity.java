package com.cloudcomputing.instaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cloudcomputing.instaclone.model.Post;
import com.cloudcomputing.instaclone.ui.PostRecyclerAdapter;
import com.cloudcomputing.instaclone.util.InstaCloneApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

//Home page of user
public class InstaCloneActivity extends AppCompatActivity {
    private TextView name;
    private ImageView addImageView;
    private Button logOutButton;
    private TextView noPostsTextView;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;
    private StorageReference storageReference;
    private CollectionReference collectionReference;

    public List<Post> postList;
    public RecyclerView recyclerView;
    public PostRecyclerAdapter postRecyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insta_clone);

        //Sets firebase variables
        database = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        collectionReference = database.collection("Photos");

        name = findViewById(R.id.nameTextView);
        addImageView = findViewById(R.id.addImageView);
        logOutButton = findViewById(R.id.logOutButton);
        noPostsTextView = findViewById(R.id.noPostTextView);
        recyclerView = findViewById(R.id.recyclerView);

        name.setText(InstaCloneApi.getInstance().getUsername());

        addImageView.setOnClickListener(view ->
                startActivity(new Intent(InstaCloneActivity.this, PostImageActivity.class))
        );

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        postList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Gets collection reference to find User by userid
        collectionReference.whereEqualTo("userId", InstaCloneApi.getInstance().getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot posts: queryDocumentSnapshots){
                                Post post = posts.toObject(Post.class);
                                postList.add(post);
                            }
                            //Adds all images to recycler view
                            postRecyclerAdapter = new PostRecyclerAdapter(InstaCloneActivity.this, postList);
                            recyclerView.setAdapter(postRecyclerAdapter);
                            postRecyclerAdapter.notifyDataSetChanged();
                        }else{
                            noPostsTextView.setVisibility(View.VISIBLE);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //Logs out user
    private void logOut(){
        if(currentUser != null && firebaseAuth != null){
            firebaseAuth.signOut();
            startActivity(new Intent(InstaCloneActivity.this, MainActivity.class));
            finish();
        }
    }


}