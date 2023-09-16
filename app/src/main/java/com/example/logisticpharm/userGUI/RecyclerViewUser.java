package com.example.logisticpharm.userGUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logisticpharm.R;
import com.example.logisticpharm.authentication.LoginActivity;
import com.example.logisticpharm.model.MedicineInfo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RecyclerViewUser extends AppCompatActivity {
    RecyclerView recviewUser;
    boolean isNightModeOn;
    AdapterShowUser adapterUser;
    SearchView searchViewUser;
    FloatingActionButton fb;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view_user);
        setTitle("");
        Button btnDarkMode = findViewById(R.id.darkModeLightUser);
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO){
            isNightModeOn = false;
            btnDarkMode.setText("Tryb Ciemny");

        } else if (AppCompatDelegate.getDefaultNightMode()== AppCompatDelegate.MODE_NIGHT_YES) {
            isNightModeOn = true;
            btnDarkMode.setText("Tryb Jasny");
        }else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        btnDarkMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNightModeOn){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    isNightModeOn=false;
                    btnDarkMode.setText("Tryb Ciemny");
                }else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    isNightModeOn = true;
                    btnDarkMode.setText("Tryb Jasny");
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationViewUser);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_favorite) {
                    startActivity(new Intent(RecyclerViewUser.this, UserFavorite.class));
                    return true;
                } else if (itemId == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(RecyclerViewUser.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_profile) {
                    startActivity(new Intent(RecyclerViewUser.this, UserProfile.class));
                    return true;
                } else if (itemId == R.id.action_medicine) {
                    startActivity(new Intent(RecyclerViewUser.this, RecyclerViewUser.class));
                    return true;
                } else if (itemId == R.id.action_articleUser) {
                    startActivity(new Intent(RecyclerViewUser.this, ArticleUser.class));
                    return true;
                } else
                    return false;

                //case R.id.action_article:
                //    startActivity(new Intent(RecylcerShow.this, SearchActivity.class));
                //   return true;


            }
        });
        searchViewUser = findViewById(R.id.searchViewUser);
        searchViewUser.clearFocus();
        searchViewUser.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }

            private void filterList(String s) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference favoritesRef = database.getReference().child("Favorites");
                DatabaseReference medicineInfoRef = database.getReference().child("MedicineInfo");
                FirebaseRecyclerOptions<MedicineInfo> options =
                        new FirebaseRecyclerOptions.Builder<MedicineInfo>()
                                .setQuery(FirebaseDatabase.getInstance().getReference().child("MedicineInfo").orderByChild("medicineName").startAt(s).endAt(s + "\uf8ff"), MedicineInfo.class)
                                .build();

                adapterUser = new AdapterShowUser(options, favoritesRef, medicineInfoRef, "");
                adapterUser.startListening();
                recviewUser.setAdapter(adapterUser);

            }
        });

        recviewUser = (RecyclerView) findViewById(R.id.recviewUser);
        recviewUser.setLayoutManager(new LinearLayoutManager(this));
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference().child("Favorites");
        DatabaseReference medicineInfoRef = database.getReference().child("MedicineInfo");
        FirebaseRecyclerOptions<MedicineInfo> options =
                new FirebaseRecyclerOptions.Builder<MedicineInfo>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("MedicineInfo"), MedicineInfo.class)
                        .build();
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("medicineInfo");
        //FirebaseRecyclerOptions<MedicineInfo> options =
        //      new FirebaseRecyclerOptions.Builder<MedicineInfo>()
        //            .setQuery(reference, snapshot -> {
        //              MedicineInfo medicineInfo = snapshot.getValue(MedicineInfo.class);
        //            medicineInfo.setKey(snapshot.getKey());
        //          return medicineInfo;
        //    })
        //  .build();
        adapterUser = new AdapterShowUser(options, favoritesRef, medicineInfoRef, "") {


        // recviewUser.setAdapter(adapterUser);
        @Override
        protected void onBindViewHolder (@NonNull myviewholderUser holder,int position,
        @NonNull MedicineInfo medicineInfo){
            super.onBindViewHolder(holder, position, medicineInfo);
            holder.availability.setText(medicineInfo.getAvailabilityStatus());
            holder.availability.setTextColor(ContextCompat.getColor(RecyclerViewUser.this, medicineInfo.getAvailabilityStatusColor()));
        }
    };
        recviewUser.setAdapter(adapterUser);
}

    @Override
    protected void onStart() {
        super.onStart();
        adapterUser.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterUser.stopListening();
    }


}