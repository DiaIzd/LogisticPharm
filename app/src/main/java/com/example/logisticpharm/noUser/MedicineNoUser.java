package com.example.logisticpharm.noUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import com.example.logisticpharm.R;
import com.example.logisticpharm.model.MedicineInfo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MedicineNoUser extends AppCompatActivity {
    RecyclerView recviewUserNo;
    boolean isNightModeOn;
    AdapterNoUser adapterUserNo;
    SearchView searchViewUserNo;
    FloatingActionButton fb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_no_user);
        setTitle("");
        Button btnDarkMode = findViewById(R.id.darkModeLightUserNo);
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


        searchViewUserNo = findViewById(R.id.searchViewUserNo);
        searchViewUserNo.clearFocus();
        searchViewUserNo.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

                adapterUserNo = new AdapterNoUser(options, favoritesRef, medicineInfoRef);
                adapterUserNo.startListening();
                recviewUserNo.setAdapter(adapterUserNo);

            }
        });

        recviewUserNo = (RecyclerView) findViewById(R.id.recviewUserNo);
        recviewUserNo.setLayoutManager(new LinearLayoutManager(this));
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
        adapterUserNo = new AdapterNoUser(options, favoritesRef, medicineInfoRef) {


            // recviewUser.setAdapter(adapterUser);
            @Override
            protected void onBindViewHolder (@NonNull AdapterNoUser.myviewholderUserNo holder, int position,
                                             @NonNull MedicineInfo medicineInfo){
                super.onBindViewHolder(holder, position, medicineInfo);
                holder.availability.setText(medicineInfo.getAvailabilityStatus());
                holder.availability.setTextColor(ContextCompat.getColor(MedicineNoUser.this, medicineInfo.getAvailabilityStatusColor()));
            }
        };
        recviewUserNo.setAdapter(adapterUserNo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapterUserNo.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapterUserNo.stopListening();
    }


}