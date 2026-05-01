package com.example.upnfit.actividades;

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.CursoAdapter;
import com.example.upnfit.modelos.Curso;
import java.util.ArrayList;
import java.util.List;

public class CursosActivity extends AppCompatActivity {

    private RecyclerView rvCursos;
    private CursoAdapter adapter;
    private List<Curso> cursoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cursos);

        rvCursos = findViewById(R.id.rvCursos);
        ImageButton btnBack = findViewById(R.id.btnBackCursos);

        cursoList = new ArrayList<>();
        cargarCursosDemo();

        adapter = new CursoAdapter(cursoList);
        rvCursos.setLayoutManager(new LinearLayoutManager(this));
        rvCursos.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarCursosDemo() {
        cursoList.add(new Curso("Matemática", "Prof. Ricardo García", "Lun - Mié 08:00 - 10:00", android.R.drawable.ic_menu_edit));
        cursoList.add(new Curso("Comunicación", "Prof. Ana Belén", "Mar - Jue 10:00 - 12:00", android.R.drawable.ic_menu_edit));
        cursoList.add(new Curso("Ciencias", "Prof. Luis Torres", "Vie 08:00 - 11:00", android.R.drawable.ic_menu_edit));
        cursoList.add(new Curso("Historia", "Prof. Carmen Rosa", "Lun 11:00 - 13:00", android.R.drawable.ic_menu_edit));
        cursoList.add(new Curso("Inglés", "Prof. John Doe", "Mié 11:00 - 13:00", android.R.drawable.ic_menu_edit));
        cursoList.add(new Curso("Arte", "Prof. Sofía Luna", "Jue 08:00 - 10:00", android.R.drawable.ic_menu_edit));
    }
}
