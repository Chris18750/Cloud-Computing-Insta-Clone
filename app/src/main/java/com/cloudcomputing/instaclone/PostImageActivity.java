package com.cloudcomputing.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudcomputing.instaclone.model.Post;
import com.cloudcomputing.instaclone.util.InstaCloneApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

//Post Image activity
public class PostImageActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 19870;
    private ImageView postImageView;
    private ImageView postCameraImageView;
    private Button postButton;
    private ProgressBar progressBar;

    private String currentUserId;
    private String currentUserName;

    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;

    private StorageReference storageReference;
    private CollectionReference collectionReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_image);

        //Set firebase variables
        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("Photos");
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        postImageView = findViewById(R.id.postImageView);
        postCameraImageView = findViewById(R.id.postCameraImageView);
        postButton = findViewById(R.id.postButton);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.INVISIBLE);

        //Set user variables
        if(InstaCloneApi.getInstance() != null){
            currentUserId = InstaCloneApi.getInstance().getUserId();
            currentUserName = InstaCloneApi.getInstance().getUsername();
        }

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){

            }else{

            }
        };

        postCameraImageView.setOnClickListener(view -> {
            //Get image from device
            Intent imagesIntent = new Intent(Intent.ACTION_GET_CONTENT);
            imagesIntent.setType("image/*");
            startActivityForResult(imagesIntent, REQUEST_CODE);
        });

        postButton.setOnClickListener(view -> {
            postImage();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth != null)
            firebaseAuth.removeAuthStateListener(authStateListener);
    }

    //Override onActivityResult to get data back from another activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            if(data != null){
                imageUri = data.getData();
                postImageView.setImageURI(imageUri);
            }
        }
    }

    //Method to post image
    private void postImage(){
        if(imageUri != null){
            progressBar.setVisibility(View.VISIBLE);

            //Create file path
            final StorageReference FILE_PATH = storageReference.child("images").child("new_image_" + Timestamp.now().getSeconds());

            //Puts file into storage
            FILE_PATH.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressBar.setVisibility(View.INVISIBLE);

                    FILE_PATH.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Post post = new Post();
                            post.setImageUrl(imageUrl);
                            post.setTimestamp(new Timestamp(new Date()));
                            post.setUserName(currentUserName);
                            post.setUserId(currentUserId);

                            //Add url to Photos collection
                            collectionReference.add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(PostImageActivity.this, "Image Post Failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(PostImageActivity.this, "Image Post Failed URL", Toast.LENGTH_LONG).show();
                }
            });
        }else{
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(PostImageActivity.this, "Image Post Failed", Toast.LENGTH_LONG).show();
        }
    }
}