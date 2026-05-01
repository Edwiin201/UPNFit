package com.example.upnfit.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.upnfit.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NotasActivity extends AppCompatActivity {

    private String userRole;
    private FloatingActionButton fabSubirNota;
    private TextView tvToolbarTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userRole = sp.getString("rol", "Apoderado");

        tvToolbarTitulo = findViewById(R.id.tvToolbarTituloNotas);
        fabSubirNota = findViewById(R.id.fabSubirNota);

        if (userRole.equals("Profesor")) {
            if (tvToolbarTitulo != null) tvToolbarTitulo.setText("GESTIÓN DE NOTAS");
            if (fabSubirNota != null) {
                fabSubirNota.setVisibility(View.VISIBLE);
                fabSubirNota.setOnClickListener(v -> Toast.makeText(this, "Abrir formulario para subir notas", Toast.LENGTH_SHORT).show());
            }
        }

        ImageButton btnBack = findViewById(R.id.btnBackNotas);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
