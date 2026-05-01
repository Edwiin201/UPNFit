package com.example.upnfit.actividades;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.ExamenAdapter;
import com.example.upnfit.modelos.Examen;
import java.util.ArrayList;
import java.util.List;

public class ExamenesActivity extends AppCompatActivity {

    private RecyclerView rvExamenes;
    private ExamenAdapter adapter;
    private List<Examen> examenList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_examenes);

        rvExamenes = findViewById(R.id.rvExamenes);
        ImageButton btnBack = findViewById(R.id.btnBackExamenes);

        examenList = new ArrayList<>();
        cargarExamenesDemo();

        adapter = new ExamenAdapter(examenList);
        rvExamenes.setLayoutManager(new LinearLayoutManager(this));
        rvExamenes.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarExamenesDemo() {
        examenList.add(new Examen("Matemática", "EXAMEN PARCIAL", "25 de Octubre", "08:30 AM", "Ecuaciones cuadráticas, funciones y límites."));
        examenList.add(new Examen("Comunicación", "PRÁCTICA CALIFICADA 2", "27 de Octubre", "10:00 AM", "Literatura del siglo XIX, análisis de textos."));
        examenList.add(new Examen("Ciencias", "EXAMEN PARCIAL", "30 de Octubre", "09:00 AM", "Ecosistemas, cadena alimenticia y clima."));
    }
}
