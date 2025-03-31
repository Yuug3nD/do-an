package com.example.do_an;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    private DatabaseReference databaseReference;

    public FirebaseDatabaseHelper() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReferenceFromUrl("https://doanmobile-t13-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .child("foods");
    }

    public void fetchFoodData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                        Log.d("FirebaseData", foodSnapshot.getValue().toString());
                    }
                } else {
                    Log.d("FirebaseData", "Không có dữ liệu!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseData", "Lỗi: " + error.getMessage());
            }
        });
    }
}
