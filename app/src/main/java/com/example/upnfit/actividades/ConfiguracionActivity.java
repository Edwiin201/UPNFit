package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.upnfit.R;

public class ConfiguracionActivity extends AppCompatActivity {

    private ImageButton btnRegresar;
    private SwitchCompat switchDarkMode;
    private View btnCerrarSesion;
    private TextView tvNombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);

        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // Referencias
        btnRegresar = findViewById(R.id.regresomenu);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        tvNombreUsuario = findViewById(R.id.tvNombreUsuarioConf);

        // Cargar nombre del SharedPreferences si existe
        SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String nombre = userPrefs.getString("nombreCompleto", "Usuario del Portal");
        if (tvNombreUsuario != null) {
            tvNombreUsuario.setText(nombre);
        }

        // Configurar Modo Oscuro
        SharedPreferences settingsPrefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean isDarkMode = settingsPrefs.getBoolean("dark_mode", false);
        if (switchDarkMode != null) {
            switchDarkMode.setChecked(isDarkMode);
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = settingsPrefs.edit();
                editor.putBoolean("dark_mode", isChecked);
                editor.apply();

                AppCompatDelegate.setDefaultNightMode(
                        isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
            });
        }

        // Acciones
        if (btnRegresar != null) {
            btnRegresar.setOnClickListener(v -> finish());
        }

        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> {
                // Limpiar sesión
                SharedPreferences.Editor editor = userPrefs.edit();
                editor.clear();
                editor.apply();

                // Ir al login
                Intent intent = new Intent(ConfiguracionActivity.this, SesionActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }
}
