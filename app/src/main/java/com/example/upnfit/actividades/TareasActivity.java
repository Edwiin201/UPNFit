package com.example.upnfit.actividades;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.TareaAdapter;
import com.example.upnfit.modelos.Tarea;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class TareasActivity extends AppCompatActivity {

    private RecyclerView rvTareas;
    private TareaAdapter adapter;
    private List<Tarea> tareaList;
    private String userRole;
    private FloatingActionButton fabNuevaTarea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tareas);

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userRole = sp.getString("rol", "Apoderado");
        int usuarioID = sp.getInt("usuarioID", 0);

        rvTareas = findViewById(R.id.rvTareas);
        fabNuevaTarea = findViewById(R.id.fabNuevaTarea);
        ImageButton btnBack = findViewById(R.id.btnBackTareas);

        if (userRole.equals("Profesor") || usuarioID == 888) {
            fabNuevaTarea.setVisibility(View.VISIBLE);
            fabNuevaTarea.setOnClickListener(v -> mostrarDialogoNuevaTarea());
        }

        tareaList = new ArrayList<>();
        cargarTareasDemo();

        adapter = new TareaAdapter(tareaList);
        rvTareas.setLayoutManager(new LinearLayoutManager(this));
        rvTareas.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarTareasDemo() {
        tareaList.add(new Tarea(1, "Matemática", "Resolver ejercicios de ecuaciones cuadráticas (Pág. 80-82).", "15 Mayo", "Prof. Ricardo García", false));
        tareaList.add(new Tarea(2, "Comunicación", "Redactar un ensayo sobre la literatura del siglo XIX.", "18 Mayo", "Prof. Ana Belén", true));
        tareaList.add(new Tarea(3, "Ciencias", "Investigación sobre el impacto ambiental en la costa peruana.", "20 Mayo", "Prof. Luis Torres", false));
    }

    private void mostrarDialogoNuevaTarea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_nueva_tarea, null);
        builder.setView(dialogView);

        EditText etCurso = dialogView.findViewById(R.id.etCursoTarea);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionTarea);
        EditText etFecha = dialogView.findViewById(R.id.etFechaTarea);
        Button btnGuardar = dialogView.findViewById(R.id.btnGuardarTarea);
        Button btnCancelar = dialogView.findViewById(R.id.btnCancelarTarea);

        AlertDialog dialog = builder.create();

        btnGuardar.setOnClickListener(v -> {
            String curso = etCurso.getText().toString();
            String desc = etDescripcion.getText().toString();
            String fecha = etFecha.getText().toString();

            if (!curso.isEmpty() && !desc.isEmpty() && !fecha.isEmpty()) {
                // Agregar a la lista local (Simulación)
                Tarea nueva = new Tarea(tareaList.size() + 1, curso, desc, fecha, "Mí (Profesor)", false);
                tareaList.add(0, nueva);
                adapter.notifyItemInserted(0);
                rvTareas.scrollToPosition(0);
                
                Toast.makeText(this, "Tarea publicada con éxito", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancelar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
