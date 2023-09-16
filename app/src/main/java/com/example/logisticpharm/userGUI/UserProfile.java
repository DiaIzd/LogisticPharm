package com.example.logisticpharm.userGUI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.logisticpharm.R;
import com.example.logisticpharm.authentication.LoginActivity;
import com.example.logisticpharm.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserProfile extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private BottomNavigationView bottomNavigationView;
    private EditText userNameEditText;
    private EditText userLastNameEditText;
    private EditText userEmailEditText;

    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("User");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        userNameEditText = findViewById(R.id.userNameEditText);
        userLastNameEditText = findViewById(R.id.userLastNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        saveButton = findViewById(R.id.saveButton);
        bottomNavigationView = findViewById(R.id.bottomNavigationViewUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_favorite) {
                    startActivity(new Intent(UserProfile.this, UserFavorite.class));
                    return true;
                } else if (itemId == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(UserProfile.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_profile) {
                    startActivity(new Intent(UserProfile.this, UserProfile.class));
                    return true;
                } else if (itemId == R.id.action_medicine) {
                    startActivity(new Intent(UserProfile.this, RecyclerViewUser.class));
                    return true;
                } else if (itemId == R.id.action_articleUser) {
                    startActivity(new Intent(UserProfile.this, ArticleUser.class));
                    return true;
                } else
                    return false;

                //case R.id.action_article:
                //    startActivity(new Intent(RecylcerShow.this, SearchActivity.class));
                //   return true;


            }
        });
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            userEmailEditText.setText(userEmail);
        }
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            String userName = user.getUserName();
                            String userLastName = user.getUserLastName();
                            userNameEditText.setText(userName);
                            userLastNameEditText.setText(userLastName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Obsługa błędu
                }
            });
        }
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserData();
            }
        });
    }

    public void saveUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            String userName = userNameEditText.getText().toString();
            String userLastName = userLastNameEditText.getText().toString();

            User user = new User();
            user.setUserName(userName);
            user.setUserLastName(userLastName);

            userRef.child(currentUserId).setValue(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(UserProfile.this, "Dane zapisane.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfile.this, "Błąd podczas zapisywania danych.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}