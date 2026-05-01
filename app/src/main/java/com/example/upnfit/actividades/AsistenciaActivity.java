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

public class AsistenciaActivity extends AppCompatActivity {

    private String userRole;
    private FloatingActionButton fabPasarAsistencia;
    private TextView tvToolbarTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userRole = sp.getString("rol", "Apoderado");

        tvToolbarTitulo = findViewById(R.id.tvToolbarTituloAsistencia);
        fabPasarAsistencia = findViewById(R.id.fabPasarAsistencia);

        if (userRole.equals("Profesor")) {
            if (tvToolbarTitulo != null) tvToolbarTitulo.setText("REGISTRO DE ASISTENCIA");
            if (fabPasarAsistencia != null) {
                fabPasarAsistencia.setVisibility(View.VISIBLE);
                fabPasarAsistencia.setOnClickListener(v -> Toast.makeText(this, "Abrir lista para pasar asistencia", Toast.LENGTH_SHORT).show());
            }
        }

        ImageButton btnBack = findViewById(R.id.btnBackAsistencia);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}
