package com.example.praktika1;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class AdminStatsActivity extends AppCompatActivity {

    DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stats);

        TextView tvTotal = findViewById(R.id.tvTotal);
        TextView tvVisited = findViewById(R.id.tvVisited);
        TextView tvMissed = findViewById(R.id.tvMissed);
        TextView tvBooked = findViewById(R.id.tvBooked);

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                int total = 0, visited = 0, missed = 0, booked = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    total++;
                    String status = ds.child("status").getValue(String.class);

                    if ("посетил".equals(status)) visited++;
                    else if ("пропустил".equals(status)) missed++;
                    else if ("записан".equals(status)) booked++;
                }

                tvTotal.setText("Всего записей: " + total);
                tvVisited.setText("Посетили: " + visited);
                tvMissed.setText("Пропустили: " + missed);
                tvBooked.setText("Записаны: " + booked);
            }

            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}
