package com.example.praktika1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity {

    DatabaseReference db;
    List<Section> list = new ArrayList<>();
    SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Button btnBookings = findViewById(R.id.btnBookings);
        Button btnStats = findViewById(R.id.btnStats);

        btnBookings.setOnClickListener(v ->
                startActivity(new Intent(AdminMainActivity.this, AdminBookingsActivity.class))
        );

        btnStats.setOnClickListener(v ->
                startActivity(new Intent(AdminMainActivity.this, AdminStatsActivity.class))
        );

        db = FirebaseDatabase.getInstance().getReference("sections");
        RecyclerView rv = findViewById(R.id.recyclerView);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new SectionAdapter(list, true, new SectionAdapter.OnAdminClickListener() {
            @Override
            public void onEdit(Section section) {
                showEditDialog(section);
            }


            public void onDelete(Section section) {
                // Удаление через диалог (в нашем случае можно не использовать, так как удаление через редактирование)
            }
        });

        rv.setAdapter(adapter);

        fab.setOnClickListener(v -> showAddDialog());

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                if (!snapshot.exists()) {
                    addDefaultSections();
                } else {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Section section = ds.getValue(Section.class);
                        if (section != null) list.add(section);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void addDefaultSections() {
        addNewSection("Бокс", "Иванов А.С.", "Пн, Ср, Пт 18:00", 15);
        addNewSection("Йога", "Смирнова Е.В.", "Вт, Чт 10:00", 10);
        addNewSection("Плавание", "Петров Д.М.", "Ежедневно 09:00", 20);
    }

    private void addNewSection(String name, String coach, String schedule, int spots) {
        String id = db.push().getKey();
        Section section = new Section(id, name, coach, schedule, spots);
        if (id != null) db.child(id).setValue(section);
    }

    private void showAddDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_section, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etCoach = view.findViewById(R.id.etCoach);
        EditText etTime = view.findViewById(R.id.etTime);
        EditText etSpots = view.findViewById(R.id.etSpots);

        new AlertDialog.Builder(this)
                .setTitle("Новая секция")
                .setView(view)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String name = etName.getText().toString();
                    String coach = etCoach.getText().toString();
                    String schedule = etTime.getText().toString();
                    String spotsStr = etSpots.getText().toString();

                    if (!name.isEmpty() && !spotsStr.isEmpty()) {
                        addNewSection(name, coach, schedule, Integer.parseInt(spotsStr));
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showEditDialog(Section section) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_section, null);

        EditText etName = view.findViewById(R.id.etName);
        EditText etCoach = view.findViewById(R.id.etCoach);
        EditText etTime = view.findViewById(R.id.etTime);
        EditText etSpots = view.findViewById(R.id.etSpots);

        etName.setText(section.name);
        etCoach.setText(section.coach);
        etTime.setText(section.schedule);
        etSpots.setText(String.valueOf(section.maxSpots));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать секцию")
                .setView(view)
                .setPositiveButton("Сохранить", (dialog, which) -> {
                    section.name = etName.getText().toString();
                    section.coach = etCoach.getText().toString();
                    section.schedule = etTime.getText().toString();
                    section.maxSpots = Integer.parseInt(etSpots.getText().toString());

                    db.child(section.id).setValue(section);
                    Toast.makeText(this, "Секция обновлена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .setNeutralButton("Удалить", (dialog, which) -> {
                    new AlertDialog.Builder(this)
                            .setTitle("Удалить секцию?")
                            .setMessage("Вы уверены, что хотите удалить " + section.name + "?")
                            .setPositiveButton("Да", (d, w) -> {
                                db.child(section.id).removeValue();
                                Toast.makeText(this, "Секция удалена", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                })
                .show();
    }
}
