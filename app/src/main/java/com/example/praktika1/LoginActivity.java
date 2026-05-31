package com.example.praktika1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.*;

public class LoginActivity extends AppCompatActivity {

    boolean isLoginMode = true;
    DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference("users"); // 🔴 4.1 Firebase Error — нет проверки инициализации Firebase

    public static String CURRENT_LOGIN = ""; // 🔴 7.3 Session Error — статическая переменная для сессии (проблемы с памятью)

    private static final int MIN_LENGTH = 4;
    private static final int MAX_LENGTH = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String role = getIntent().getStringExtra("ROLE"); // 🔴 1.1 NullPointerException — role может быть null

        EditText etLogin = findViewById(R.id.etLogin);
        EditText etPassword = findViewById(R.id.etPassword);
        Button btnSubmit = findViewById(R.id.btnSubmit);
        TextView tvSwitch = findViewById(R.id.tvSwitchMode);
        TextView tvTitle = findViewById(R.id.tvTitle);

        if ("admin".equals(role)) {
            tvTitle.setText("Вход для администратора");
            tvSwitch.setVisibility(View.GONE);
        }

        tvSwitch.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;

            if (isLoginMode) {
                tvTitle.setText("Авторизация");
                btnSubmit.setText("Войти");
                tvSwitch.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setText("Регистрация");
                btnSubmit.setText("Создать аккаунт");
                tvSwitch.setVisibility(View.GONE); // 🔴 5.2 Logic Error — после регистрации нет возможности вернуться ко входу
            }
        });

        btnSubmit.setOnClickListener(v -> {
            String login = etLogin.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (login.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Логин и пароль не могут быть пустыми", Toast.LENGTH_SHORT).show();
                return;
            }
            if (login.length() < MIN_LENGTH || login.length() > MAX_LENGTH) {
                Toast.makeText(this, "Логин должен быть от " + MIN_LENGTH + " до " + MAX_LENGTH + " символов", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pass.length() < MIN_LENGTH || pass.length() > MAX_LENGTH) {
                Toast.makeText(this, "Пароль должен быть от " + MIN_LENGTH + " до " + MAX_LENGTH + " символов", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("admin".equals(role)) {
                // 🔴 5.1 Logic Error — пароль admin123 захардкожен в коде
                if (login.equals("admin") && pass.equals("admin123")) {
                    startActivity(new Intent(this, AdminMainActivity.class));
                } else {
                    Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (isLoginMode) {
                    // 🔴 6.1 Async Error — нет обработки задержки при загрузке
                    usersDb.child(login).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot s) {
                            if (s.exists() && pass.equals(s.child("password").getValue(String.class))) {
                                CURRENT_LOGIN = login;
                                startActivity(new Intent(LoginActivity.this, ClientMainActivity.class));
                            } else {
                                Toast.makeText(LoginActivity.this, "Неверный логин или пароль", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // 🔴 4.2 Firebase Error — ошибка не обрабатывается, нет уведомления пользователя
                        }
                    });
                } else {
                    usersDb.child(login).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot s) {
                            if (s.exists()) {
                                Toast.makeText(LoginActivity.this, "Логин уже занят", Toast.LENGTH_SHORT).show();
                            } else {
                                usersDb.child(login).child("password").setValue(pass);
                                Toast.makeText(LoginActivity.this, "Аккаунт создан", Toast.LENGTH_SHORT).show();
                                // 🔴 5.2 Logic Error — после регистрации не происходит автоматический вход, остаётся на экране регистрации
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // 🔴 4.2 Firebase Error — ошибка не обрабатывается
                        }
                    });
                }
            }
        });
    }
}