package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.upnfit.R;

public class PreferencesActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Switch switchMotivacionales;
    private Button buttonSavePreferences;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        aplicarModoGuardado();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences_layout);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        switchMotivacionales = findViewById(R.id.switch_notifications_enabled);
        buttonSavePreferences = findViewById(R.id.button_save_preferences);
        btnBack = findViewById(R.id.btnBackPreferences);

        // SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);
        boolean notiMotivacionales = prefs.getBoolean("noti_motivacionales", false);

        switchDarkMode.setChecked(darkModeEnabled);
        switchMotivacionales.setChecked(notiMotivacionales);

        // Modo oscuro
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // 🔥 Guardar notificaciones motivacionales
        switchMotivacionales.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("noti_motivacionales", isChecked).apply();
            Toast.makeText(this, isChecked ?
                            "🔥 Notificaciones motivacionales activadas" :
                            "❌ Notificaciones desactivadas",
                    Toast.LENGTH_SHORT).show();
        });

        // Botón volver
        btnBack.setOnClickListener(v -> finish());

        // Guardar
        buttonSavePreferences.setOnClickListener(v -> {
            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MenuActivity.class));
            finish();
        });
    }

    private void aplicarModoGuardado() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
