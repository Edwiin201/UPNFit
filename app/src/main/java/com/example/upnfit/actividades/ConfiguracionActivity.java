package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class ConfiguracionActivity extends AppCompatActivity {

    // Declaramos los botones que usaremos
    Button btnmenuPerfil, btnmenuCuenta, btnmenuPreferencias, btnmenuObjetivos;
    ImageButton regresomenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuracion);

        // === Vincular elementos XML con Java ===
        regresomenu = findViewById(R.id.regresomenu);
        btnmenuPerfil = findViewById(R.id.btnmenuPerfil);
        btnmenuCuenta = findViewById(R.id.btnmenuCuenta);
        btnmenuPreferencias = findViewById(R.id.btnmenuPreferencias);
        btnmenuObjetivos = findViewById(R.id.btnmenuObjetivos);

        // === Acción: regresar al menú anterior ===
        regresomenu.setOnClickListener(v -> finish());

        // === Acción: abrir pantalla de Preferencias ===
        btnmenuPreferencias.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracionActivity.this, PreferencesActivity.class);
            startActivity(intent);
        });

        // === Acción: abrir pantalla de Perfil ===
        btnmenuPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(ConfiguracionActivity.this, EditarperfilActivity.class);
            startActivity(intent);
        });

        // Puedes agregar más acciones según tus necesidades:
        // btnmenuPerfil.setOnClickListener(...);
        // btnmenuCuenta.setOnClickListener(...);
        // btnmenuObjetivos.setOnClickListener(...);
    }
}
