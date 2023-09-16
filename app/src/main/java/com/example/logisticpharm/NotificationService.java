package com.example.logisticpharm;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.example.logisticpharm.notification.AvailabilityChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.Provider;


public class NotificationService extends Service {
    private AvailabilityChangeListener availabilityChangeListener;
    private DatabaseReference favoritesRef;
    private DatabaseReference medicineInfoRef;
    private String userId;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inicjalizacja favoritesRef, medicineInfoRef, userId i innych potrzebnych zmiennych
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            favoritesRef = FirebaseDatabase.getInstance().getReference("Favorites").child(userId);
            medicineInfoRef = FirebaseDatabase.getInstance().getReference("MedicineInfo");
        }
        // Inicjalizacja AvailabilityChangeListener
        availabilityChangeListener = new AvailabilityChangeListener(
                this, favoritesRef, medicineInfoRef, userId
        );
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Uruchom nasłuchiwanie zmian w AvailabilityChangeListener
        availabilityChangeListener.startListeningForChanges();

        // Zwróć, że usługa nie powinna być ponownie uruchamiana po ewentualnym zatrzymaniu
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Zatrzymaj nasłuchiwanie zmian w AvailabilityChangeListener przed zakończeniem usługi
        availabilityChangeListener.stopListeningForChanges();
    }
}