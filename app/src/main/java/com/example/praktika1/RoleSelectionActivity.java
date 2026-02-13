package com.example.praktika1;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        findViewById(R.id.btnClient).setOnClickListener(v -> startLogin("client"));
        findViewById(R.id.btnAdmin).setOnClickListener(v -> startLogin("admin"));
    }

    private void startLogin(String role) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("ROLE", role);
        startActivity(intent);
    }
}