package com.example.praktika1;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;
import java.util.*;

public class HistoryActivity extends AppCompatActivity {

    DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
    ArrayList<String> list = new ArrayList<>();
    ArrayList<DataSnapshot> snaps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_history);

        ListView lv = findViewById(R.id.lvHistory);

        bookingsRef.orderByChild("userLogin")
                .equalTo(LoginActivity.CURRENT_LOGIN)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        list.clear();
                        snaps.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            list.add(ds.child("sectionName").getValue(String.class)
                                    + " — " + ds.child("status").getValue(String.class));
                            snaps.add(ds);
                        }
                        lv.setAdapter(new ArrayAdapter<>(HistoryActivity.this,
                                android.R.layout.simple_list_item_1, list));
                    }
                    @Override public void onCancelled(DatabaseError error) {}
                });

        lv.setOnItemClickListener((p, v, pos, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Отмена записи")
                    .setMessage("Отменить запись?")
                    .setPositiveButton("Да", (d, w) -> snaps.get(pos).getRef().removeValue())
                    .setNegativeButton("Нет", null)
                    .show();
        });
    }
}
