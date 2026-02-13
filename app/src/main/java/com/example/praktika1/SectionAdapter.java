package com.example.praktika1;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.*;
import java.util.*;

public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {

    public interface OnAdminClickListener {
        void onEdit(Section section);
    }

    List<Section> sections;
    boolean isAdmin;
    OnAdminClickListener adminListener;

    public SectionAdapter(List<Section> sections, boolean isAdmin, OnAdminClickListener adminListener) {
        this.sections = sections;
        this.isAdmin = isAdmin;
        this.adminListener = adminListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        Section s = sections.get(pos);

        h.name.setText(s.name);
        h.coach.setText("Тренер: " + s.coach);
        h.scheduleText.setText("Расписание: " + s.schedule);
        h.spotsText.setText(s.currentSpots + "/" + s.maxSpots);

        DatabaseReference sectionRef = FirebaseDatabase.getInstance().getReference("sections").child(s.id);
        DatabaseReference bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");

        if (!isAdmin) {
            h.btn.setText("Записаться");
            h.btn.setOnClickListener(v -> {
                bookingsRef.orderByChild("userLogin")
                        .equalTo(LoginActivity.CURRENT_LOGIN)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snap) {
                                for (DataSnapshot ds : snap.getChildren()) {
                                    if (ds.child("sectionId").getValue(String.class).equals(s.id)) {
                                        Toast.makeText(v.getContext(), "Вы уже записаны", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                                View dialog = LayoutInflater.from(v.getContext()).inflate(R.layout.dialog_user_info, null);
                                EditText etName = dialog.findViewById(R.id.etName);
                                EditText etAge = dialog.findViewById(R.id.etAge);

                                new androidx.appcompat.app.AlertDialog.Builder(v.getContext())
                                        .setTitle("Введите данные")
                                        .setView(dialog)
                                        .setPositiveButton("Записаться", (d, w) -> {
                                            String name = etName.getText().toString();
                                            String age = etAge.getText().toString();

                                            String id = bookingsRef.push().getKey();
                                            HashMap<String, Object> map = new HashMap<>();
                                            map.put("id", id);
                                            map.put("userLogin", LoginActivity.CURRENT_LOGIN);
                                            map.put("userName", name);
                                            map.put("userAge", age);
                                            map.put("sectionId", s.id);
                                            map.put("sectionName", s.name);
                                            map.put("status", "записан");

                                            bookingsRef.child(id).setValue(map);
                                            sectionRef.child("currentSpots").setValue(s.currentSpots + 1);

                                            Toast.makeText(v.getContext(), "Записаны!", Toast.LENGTH_SHORT).show();
                                        }).show();
                            }

                            @Override public void onCancelled(DatabaseError error) {}
                        });
            });
        } else {

            h.btn.setText("Редактировать");
            h.btn.setOnClickListener(v -> {
                if (adminListener != null) {
                    adminListener.onEdit(s);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, coach, scheduleText, spotsText;
        Button btn;

        ViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.tvName);
            coach = v.findViewById(R.id.tvCoach);
            scheduleText = v.findViewById(R.id.tvTime);
            spotsText = v.findViewById(R.id.tvSpotsCount);
            btn = v.findViewById(R.id.btnAction);
        }
    }
}
