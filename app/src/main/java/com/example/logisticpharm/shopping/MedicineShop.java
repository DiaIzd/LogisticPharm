package com.example.logisticpharm.shopping;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logisticpharm.R;
import com.example.logisticpharm.article.ArticleAdd;
import com.example.logisticpharm.article.ArticleShow;
import com.example.logisticpharm.authentication.LoginActivity;
import com.example.logisticpharm.employeeGUI.EmployeeAddMEdicine;
import com.example.logisticpharm.model.MedicineInfo;
import com.example.logisticpharm.userGUI.AdapterShowUser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
public class MedicineShop extends AppCompatActivity {
    private RecyclerView recviewBuy;
    private BottomNavigationView bottomNavigationView;
    private AdapterBuy adapterBuy;
    private DatabaseReference ordersRef;
    private String userId;
    private TextView summaryCost;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_shop);
        setTitle("");
        summaryCost = findViewById(R.id.summaryCost);
        TextView summaryCost = findViewById(R.id.summaryCost);
        recviewBuy = findViewById(R.id.recviewBuyShow);
        Button btnOrder = findViewById(R.id.btnOrderBuy);
        recviewBuy.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.action_dodaj) {
                    startActivity(new Intent(MedicineShop.this, EmployeeAddMEdicine.class));
                    return true;
                } else if (itemId == R.id.action_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MedicineShop.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.action_article) {
                    startActivity(new Intent(MedicineShop.this, ArticleAdd.class));
                    return true;
                } else if (itemId == R.id.action_buy) {
                    startActivity(new Intent(MedicineShop.this, MedicineShop.class));
                    return true;
                } else if (itemId == R.id.action_articleShow) {
                    startActivity(new Intent(MedicineShop.this, ArticleShow.class));
                    return true;
                } else {
                    return false;
                }
            }
        });

        ordersRef = FirebaseDatabase.getInstance().getReference().child("MedicineBuy");

        FirebaseRecyclerOptions<MedicineInfo> options =
                new FirebaseRecyclerOptions.Builder<MedicineInfo>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("MedicineInfo"), MedicineInfo.class)
                        .build();

        adapterBuy = new AdapterBuy(options, summaryCost, recviewBuy) {
            @Override
            protected void onBindViewHolder(@NonNull AdapterBuy.myviewholderBuy holder, int position, @NonNull MedicineInfo medicineInfo) {
                super.onBindViewHolder(holder, position, medicineInfo);
            }
        };

        recviewBuy.setAdapter(adapterBuy);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<MedicineInfo> selectedMedicines = adapterBuy.getSelectedMedicines();

                // Check if any medicines are selected
                if (selectedMedicines.isEmpty()) {
                    Toast.makeText(MedicineShop.this, "Select at least one medicine", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the order to the database
                saveOrderToDatabase(selectedMedicines);

                // Show a success message
                Toast.makeText(MedicineShop.this, "Order saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveOrderToDatabase(List<MedicineInfo> selectedMedicines) {
        String orderId = ordersRef.push().getKey();

        if (orderId == null) {
            // Handle the case when orderId is null
            Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show();
            return;
        }

        for (MedicineInfo medicine : selectedMedicines) {
            DatabaseReference medicineRef = FirebaseDatabase.getInstance().getReference().child("Order").push();

            medicineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String medicineKey = snapshot.getKey();
                        double price = Double.parseDouble(snapshot.child("medicineCost").getValue().toString());
                        double totalCost = price;

                        DatabaseReference orderItemRef = ordersRef.child(orderId).child(medicineKey);
                        orderItemRef.child("medicineName").setValue(snapshot.child("medicineName").getValue().toString());
                        orderItemRef.child("price").setValue(price);
                        orderItemRef.child("totalCost").setValue(totalCost);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });

            // Set the values for the medicine in the MedicineInfo node
            medicineRef.child("medicineName").setValue(medicine.getMedicineName());
            medicineRef.child("medicineCost").setValue(medicine.getMedicineCost());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            if (ordersRef != null) {
                ordersRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<String> favoriteKeys = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            favoriteKeys.add(ds.getKey());
                        }

                        Query query = FirebaseDatabase.getInstance().getReference()
                                .child("MedicineInfo");

                        FirebaseRecyclerOptions<MedicineInfo> options =
                                new FirebaseRecyclerOptions.Builder<MedicineInfo>()
                                        .setQuery(query, MedicineInfo.class)
                                        .build();

                        adapterBuy = new AdapterBuy(options, summaryCost, recviewBuy) {
                            @Override
                            protected void onBindViewHolder(@NonNull myviewholderBuy holder, int position, @NonNull MedicineInfo medicineInfo) {
                                if (favoriteKeys.contains(getRef(position).getKey())) {
                                    super.onBindViewHolder(holder, position, medicineInfo);
                                } else {
                                    holder.itemView.setVisibility(View.GONE);
                                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                }
                            }
                        };
                        recviewBuy.setAdapter(adapterBuy);
                        adapterBuy.startListening();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapterBuy != null) {
            adapterBuy.stopListening();
        }
    }

}