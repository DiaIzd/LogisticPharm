package com.example.logisticpharm.notification;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;
import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.logisticpharm.R;
import com.example.logisticpharm.model.MedicineInfo;
import com.example.logisticpharm.userGUI.AdapterShowUser;
import com.example.logisticpharm.userGUI.RecyclerViewUser;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AvailabilityChangeListener extends ContextWrapper {
    private static final String CHANNEL_ID = "availability_channel";
    private static final String CHANNEL_NAME = "Availability Channel";

    private NotificationManager mManager;
    private DatabaseReference favoritesRef;
    private DatabaseReference medicineInfoRef;
    private String userId;
    private ValueEventListener valueEventListener; // Add the missing valueEventListener variable

    public AvailabilityChangeListener(Context context, DatabaseReference favoritesRef, DatabaseReference medicineInfoRef, String userId) {
        super(context);
        this.favoritesRef = favoritesRef;
        this.medicineInfoRef = medicineInfoRef;
        this.userId = userId;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        startListeningForChanges();
    }

    private void createChannel() {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getManager().createNotificationChannel(channel);
        }
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(String title, String content) {
        Intent resultIntent = new Intent(this, RecyclerViewUser.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);
    }

    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Log.d("AvailabilityChangeListener", "onChildChanged() called");
        int previousMedicineBoxes = snapshot.child("previousMedicineBoxes").getValue(Integer.class);
        int currentMedicineBoxes = snapshot.child("medicineBoxes").getValue(Integer.class);

        if (previousMedicineBoxes == 0 && currentMedicineBoxes > 0) {
            sendMedicineAvailabilityChangedNotification("Dostępność leku zmieniona", "Twój ulubiony lek jest teraz dostępny.");
          //  snapshot.getRef().child("previousMedicineBoxes").setValue(currentMedicineBoxes);
            String medicineKey = snapshot.getKey();
            DatabaseReference medicineInfoRef = FirebaseDatabase.getInstance().getReference().child("MedicineInfo").child(medicineKey);
            medicineInfoRef.child("previousMedicineBoxes").setValue(currentMedicineBoxes);
        }
    }

    private void sendMedicineAvailabilityChangedNotification(String title, String content) {
        NotificationCompat.Builder builder = getChannelNotification(title, content);
        getManager().notify(1, builder.build());
    }

    public void stopListeningForChanges() {
        if (favoritesRef != null) {
            favoritesRef.removeEventListener(valueEventListener);
        }
    }

    public void startListeningForChanges() {
        if (favoritesRef != null) {
            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        onChildChanged(childSnapshot, null);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            };

            favoritesRef.addValueEventListener(valueEventListener);
        }
    }
}