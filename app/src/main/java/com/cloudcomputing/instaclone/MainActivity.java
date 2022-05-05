package com.cloudcomputing.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.cloudcomputing.instaclone.util.InstaCloneApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//Start of app
public class MainActivity extends AppCompatActivity {
    private Button getStartedButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStartedButton = findViewById(R.id.startButton);

        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        //Sets authlistener to auto sign in user if valid
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser != null){
                    String currentUserId = currentUser.getUid();
                    //Gets user from collection where equal to id
                    collectionReference.whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(error != null)
                                        return;
                                    if(!value.isEmpty()){
                                        for(QueryDocumentSnapshot snapshot : value){
                                            //Sets singleton variables
                                            InstaCloneApi.getInstance().setUserId(snapshot.getString("userId"));
                                            InstaCloneApi.getInstance().setUsername(snapshot.getString("username"));
                                            startActivity(new Intent(MainActivity.this, InstaCloneActivity.class));
                                            finish();
                                        }
                                    }

                                }
                            });
                }
            }
        };

        getStartedButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        //Adds listener
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}