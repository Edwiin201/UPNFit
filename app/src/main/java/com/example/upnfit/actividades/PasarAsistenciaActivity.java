package com.example.upnfit.actividades;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.AsistenciaAlumnoAdapter;
import com.example.upnfit.modelos.Alumno;
import java.util.ArrayList;
import java.util.List;

public class PasarAsistenciaActivity extends AppCompatActivity {

    private RecyclerView rvAlumnos;
    private AsistenciaAlumnoAdapter adapter;
    private List<Alumno> alumnoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasar_asistencia);

        rvAlumnos = findViewById(R.id.rvAsistenciaAlumnos);
        ImageButton btnBack = findViewById(R.id.btnBackAsistenciaProf);
        AppCompatButton btnGuardar = findViewById(R.id.btnGuardarAsistencia);

        alumnoList = new ArrayList<>();
        cargarAlumnosDemo();

        adapter = new AsistenciaAlumnoAdapter(alumnoList);
        rvAlumnos.setLayoutManager(new LinearLayoutManager(this));
        rvAlumnos.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(this, "Asistencia guardada exitosamente", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void cargarAlumnosDemo() {
        alumnoList.add(new Alumno("Pérez Malpartida, Sofia"));
        alumnoList.add(new Alumno("García Torres, Juan"));
        alumnoList.add(new Alumno("López Ramos, María"));
        alumnoList.add(new Alumno("Mendoza Quispe, Pedro"));
        alumnoList.add(new Alumno("Sánchez Díaz, Ana"));
    }
}
