package com.cloudcomputing.instaclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudcomputing.instaclone.util.InstaCloneApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

//Login page for app
public class LoginActivity extends AppCompatActivity {
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private Button signInButton;
    private Button createAccountButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;
    private CollectionReference collectionReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Sets firebase variables
        database = FirebaseFirestore.getInstance();
        collectionReference = database.collection("Users");
        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.emailTextView);
        passwordEditText = findViewById(R.id.passwordTextView);
        signInButton = findViewById(R.id.signInButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(emailEditText.getText().toString().trim(), passwordEditText.getText().toString());
            }
        });

        createAccountButton.setOnClickListener(view -> startActivity(
                new Intent(LoginActivity.this, CreateAccountActivity.class))
        );

    }

    //Logs in user
    private void login(String email, String password){
        //Check all fields are filled
        if(!email.isEmpty() && !password.isEmpty()){
            //Signs in
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        String currentUserId = user.getUid();
                        //Gets collection by userid
                        collectionReference.whereEqualTo("userId", currentUserId)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                        if (!value.isEmpty()) {
                                            for (QueryDocumentSnapshot snapshot : value) {
                                                //Adds to singleton
                                                InstaCloneApi instaCloneApi = InstaCloneApi.getInstance();
                                                instaCloneApi.setUsername(snapshot.getString("username"));
                                                instaCloneApi.setUserId(currentUserId);
                                                startActivity(new Intent(LoginActivity.this, InstaCloneActivity.class));
                                                finish();
                                            }
                                        }
                                    }
                                });
                    }else
                        Toast.makeText(LoginActivity.this, "Invalid Username or Password", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(LoginActivity.this, "All fields must be filled", Toast.LENGTH_LONG).show();
        }
    }
}