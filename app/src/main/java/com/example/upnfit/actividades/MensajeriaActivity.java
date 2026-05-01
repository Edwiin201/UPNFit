package com.example.upnfit.actividades;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.adaptadores.MensajeAdapter;
import com.example.upnfit.modelos.Mensaje;
import java.util.ArrayList;
import java.util.List;

public class MensajeriaActivity extends AppCompatActivity {

    private RecyclerView rvMensajes;
    private MensajeAdapter adapter;
    private List<Mensaje> mensajeList;
    private EditText etMensaje;
    private ImageButton btnEnviar, btnBack;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajeria);

        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        userRole = sp.getString("rol", "Apoderado");

        rvMensajes = findViewById(R.id.rvMensajes);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviarMensaje);
        btnBack = findViewById(R.id.btnBackMensajeria);
        TextView tvTitulo = findViewById(R.id.tvTituloMensajeria);

        if (userRole.equals("Profesor")) {
            tvTitulo.setText("COMUNICACIÓN CON PADRES");
        }

        mensajeList = new ArrayList<>();
        cargarMensajesDemo();

        adapter = new MensajeAdapter(mensajeList);
        rvMensajes.setLayoutManager(new LinearLayoutManager(this));
        rvMensajes.setAdapter(adapter);
        rvMensajes.scrollToPosition(mensajeList.size() - 1);

        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (!texto.isEmpty()) {
                String remitente = userRole.equals("Profesor") ? "Prof. García" : "Familia Pérez";
                mensajeList.add(new Mensaje(remitente, texto, "Ahora", true));
                adapter.notifyItemInserted(mensajeList.size() - 1);
                rvMensajes.scrollToPosition(mensajeList.size() - 1);
                etMensaje.setText("");
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void cargarMensajesDemo() {
        if (userRole.equals("Profesor")) {
            mensajeList.add(new Mensaje("Sra. Pérez", "Buenas tardes profesor, ¿cuándo es el examen de matemática?", "09:00 AM", false));
            mensajeList.add(new Mensaje("Yo", "Hola, es el próximo martes 15.", "09:05 AM", true));
        } else {
            mensajeList.add(new Mensaje("Prof. García", "Estimados padres, se les recuerda la reunión de mañana.", "Ayer", false));
            mensajeList.add(new Mensaje("Yo", "Muchas gracias por el aviso, ahí estaremos.", "Ayer", true));
            mensajeList.add(new Mensaje("Prof. García", "No olviden traer el cuaderno de trabajo.", "08:30 AM", false));
        }
    }
}
