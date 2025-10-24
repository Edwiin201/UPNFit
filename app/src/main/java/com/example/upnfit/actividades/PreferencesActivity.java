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
    private Button buttonSavePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ⚡ Aplica el modo guardado antes de crear la UI
        aplicarModoGuardado();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_preferences_layout);

        // Ajuste visual para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Referencias UI ---
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        buttonSavePreferences = findViewById(R.id.button_save_preferences);

        // --- Cargar preferencia actual ---
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(darkModeEnabled);

        // --- Listener: aplicar el modo en tiempo real ---
        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            // Aplicar el tema inmediatamente sin reiniciar la app
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );

            Toast.makeText(this,
                    isChecked ? "🌙 Modo oscuro activado" : "☀️ Modo claro activado",
                    Toast.LENGTH_SHORT).show();
        });

        // --- Botón Guardar: redirigir al menú principal ---
        buttonSavePreferences.setOnClickListener(v -> {
            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show();

            // ⚡ Redirige al MenuActivity con el modo ya aplicado
            Intent intent = new Intent(PreferencesActivity.this, MenuActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish(); // Cierra esta pantalla
        });
    }

    /**
     * Aplica el modo oscuro guardado antes de cargar la interfaz
     */
    private void aplicarModoGuardado() {
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }
}
