package com.cloudcomputing.instaclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cloudcomputing.instaclone.util.InstaCloneApi;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

//Class for creating a user account
public class CreateAccountActivity extends AppCompatActivity {
    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button createAccountButton;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore database;
    private CollectionReference userCollectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        //Initialize firebase references
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseFirestore.getInstance();
        userCollectionReference = database.collection("Users");

        usernameEditText = findViewById(R.id.createUsernameTextView);
        emailEditText = findViewById(R.id.createEmailTextView);
        passwordEditText = findViewById(R.id.createPasswordTextView);
        createAccountButton = findViewById(R.id.createNewAccountButton);
        progressBar = findViewById(R.id.createAccountProgressBar);


        authStateListener = firebaseAuth -> {
          currentUser = firebaseAuth.getCurrentUser();
          if(currentUser != null){

          }else{

          }
        };

        createAccountButton.setOnClickListener(view -> {
            //Check if all fields are filled
            if(!usernameEditText.getText().toString().isEmpty() && !emailEditText.getText().toString().isEmpty() &&
                    !passwordEditText.getText().toString().isEmpty()){
                createUserAccount(emailEditText.getText().toString().trim(), passwordEditText.getText().toString(),
                        usernameEditText.getText().toString().trim());
            }else{
                Toast.makeText(CreateAccountActivity.this, "All fields must be filled", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
    }

    //Creates a user account in firebase
    private void createUserAccount(String email, String password, String username){
        //Check if all strings are not empty
        if(!email.isEmpty() && !password.isEmpty() && !username.isEmpty()){
            progressBar.setVisibility(View.VISIBLE);

            //Create user into firebase authentication
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();

                    //Creates user object
                    Map<String, String> userObject = new HashMap<>();
                    userObject.put("userId", currentUserId);
                    userObject.put("username", username);

                    //Adds user to collection in firebase
                    userCollectionReference.add(userObject).addOnSuccessListener(documentReference -> {
                        documentReference.get().addOnCompleteListener(task1 -> {
                            if(task1.getResult().exists()){
                                progressBar.setVisibility(View.INVISIBLE);

                                InstaCloneApi instaCloneApi = InstaCloneApi.getInstance();
                                instaCloneApi.setUserId(currentUserId);
                                instaCloneApi.setUsername(username);

                                //Sends user to home page
                                startActivity(new Intent(CreateAccountActivity.this, InstaCloneActivity.class));
                                finish();

                            }else{
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });
                    }).addOnFailureListener(e -> {

                    });
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateAccountActivity.this, "Account Creation Failed", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
            });
        }else{
            Toast.makeText(CreateAccountActivity.this, "Account Creation Failed", Toast.LENGTH_LONG).show();
        }
    }


}