package com.example.praktika1;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import java.util.List;

public class AdminBookingsAdapter extends RecyclerView.Adapter<AdminBookingsAdapter.ViewHolder> {

    List<DataSnapshot> list;

    public AdminBookingsAdapter(List<DataSnapshot> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        DataSnapshot ds = list.get(pos);

        String user = ds.child("userName").getValue(String.class);
        String age = ds.child("userAge").getValue(String.class);
        String section = ds.child("sectionName").getValue(String.class);
        String status = ds.child("status").getValue(String.class);

        h.tvInfo.setText(user + " (" + age + " лет) — " + section);
        h.tvStatus.setText("Статус: " + status);

        h.btnPresent.setOnClickListener(v -> ds.getRef().child("status").setValue("посетил"));
        h.btnMissed.setOnClickListener(v -> ds.getRef().child("status").setValue("пропустил"));
        h.btnBooked.setOnClickListener(v -> ds.getRef().child("status").setValue("записан"));
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInfo, tvStatus;
        Button btnPresent, btnMissed, btnBooked;

        ViewHolder(View v) {
            super(v);
            tvInfo = v.findViewById(R.id.tvInfo);
            tvStatus = v.findViewById(R.id.tvStatus);
            btnPresent = v.findViewById(R.id.btnPresent);
            btnMissed = v.findViewById(R.id.btnMissed);
            btnBooked = v.findViewById(R.id.btnBooked);
        }
    }
}
