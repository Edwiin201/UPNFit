package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class PerfilActivity extends AppCompatActivity {

    private String rol;
    private TextView tvNombrePerfil, tvRolPerfil;
    private LinearLayout layoutOpcionesProfesor;
    private Button btnCerrarSesion, imgPerfilGrande;
    private ImageButton btnBackPerfil;
    private View btnLlamarPadres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Obtener rol de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        rol = prefs.getString("rol", "Apoderado");
        String nombre = prefs.getString("nombre", "Usuario UPN");

        // Vincular vistas
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvRolPerfil = findViewById(R.id.tvRolPerfil);
        layoutOpcionesProfesor = findViewById(R.id.layoutOpcionesProfesor);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        imgPerfilGrande = findViewById(R.id.imgPerfilGrande);
        btnBackPerfil = findViewById(R.id.btnBackPerfil);
        btnLlamarPadres = findViewById(R.id.btnLlamarPadres);

        // Configurar datos según rol
        tvNombrePerfil.setText(nombre);
        if ("888".equals(rol)) {
            tvRolPerfil.setText("Docente / Personal Académico");
            layoutOpcionesProfesor.setVisibility(View.VISIBLE);
            imgPerfilGrande.setText(nombre.substring(0, 1).toUpperCase());
        } else {
            tvRolPerfil.setText("Padre de Familia / Apoderado");
            layoutOpcionesProfesor.setVisibility(View.GONE);
            imgPerfilGrande.setText(nombre.substring(0, 1).toUpperCase());
        }

        // Eventos
        btnBackPerfil.setOnClickListener(v -> finish());

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.apply();
            
            Intent intent = new Intent(PerfilActivity.this, SesionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnLlamarPadres.setOnClickListener(v -> {
            // Simular llamada a un número de contacto (en un app real vendría de la BD)
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:987654321"));
            startActivity(intent);
        });
    }
}
