package com.example.upnfit.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.GestionarNotasAdapter;
import com.example.upnfit.modelos.Alumno;
import java.util.ArrayList;
import java.util.List;

public class GestionarNotasActivity extends AppCompatActivity {

    private RecyclerView rvAlumnos;
    private GestionarNotasAdapter adapter;
    private List<Alumno> alumnoList;
    private Spinner spinnerCurso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_notas);

        rvAlumnos = findViewById(R.id.rvGestionarNotas);
        spinnerCurso = findViewById(R.id.spinnerCursoNotas);
        ImageButton btnBack = findViewById(R.id.btnBackGestionarNotas);
        AppCompatButton btnGuardar = findViewById(R.id.btnGuardarNotas);

        // Configurar Spinner
        String[] cursos = {"Matemática - 5to A", "Comunicación - 5to A", "Ciencias - 5to A"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cursos);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurso.setAdapter(spinnerAdapter);

        alumnoList = new ArrayList<>();
        cargarAlumnosDemo();

        adapter = new GestionarNotasAdapter(alumnoList);
        rvAlumnos.setLayoutManager(new LinearLayoutManager(this));
        rvAlumnos.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(this, "Calificaciones guardadas y enviadas al sistema", Toast.LENGTH_LONG).show();
            finish();
        });

        spinnerCurso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Simular recarga de alumnos según curso
                Toast.makeText(GestionarNotasActivity.this, "Cargando lista de: " + cursos[position], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cargarAlumnosDemo() {
        alumnoList.add(new Alumno("Pérez Malpartida, Sofia"));
        alumnoList.add(new Alumno("García Torres, Juan"));
        alumnoList.add(new Alumno("López Ramos, María"));
        alumnoList.add(new Alumno("Mendoza Quispe, Pedro"));
        alumnoList.add(new Alumno("Sánchez Díaz, Ana"));
        
        // Inicializar con algunas notas demo
        alumnoList.get(0).setNota(18.5);
        alumnoList.get(1).setNota(15.0);
        alumnoList.get(2).setNota(19.0);
        alumnoList.get(3).setNota(14.5);
        alumnoList.get(4).setNota(17.0);
    }
}
