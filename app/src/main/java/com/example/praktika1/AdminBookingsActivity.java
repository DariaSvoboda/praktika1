package com.example.praktika1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class AdminBookingsActivity extends AppCompatActivity {

    RecyclerView rv;
    List<DataSnapshot> list = new ArrayList<>();
    AdminBookingsAdapter adapter;
    DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        rv = findViewById(R.id.recyclerViewBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminBookingsAdapter(list);
        rv.setAdapter(adapter);

        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    list.add(ds);
                }
                adapter.notifyDataSetChanged();
            }

            @Override public void onCancelled(DatabaseError error) {}
        });
    }
}
