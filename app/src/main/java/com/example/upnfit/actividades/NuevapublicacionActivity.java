package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Usar ImageView
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NuevapublicacionActivity extends AppCompatActivity {

    private EditText editTextPublicacion;
    private Button btnPublicar;
    private TextView textViewNombre;

    // CORRECCIÓN 1: Cambiar de ImageButton a ImageView
    private ImageView btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevapublicacion);

        // Inicializar los elementos
        editTextPublicacion = findViewById(R.id.editTextPublicacion);
        btnPublicar = findViewById(R.id.btnPublicar);
        textViewNombre = findViewById(R.id.textViewNombrePublicacion);

        // CORRECCIÓN 2: Usar el ID correcto del XML (R.id.regresomenu)
        btnRegresar = findViewById(R.id.regresomenu);

        // Recuperar el nombre del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String nombre = sharedPreferences.getString("nombre", "Usuario");

        // Establecer el nombre en el TextView
        TextView tvNombreUsuario = findViewById(R.id.textViewNombrePublicacion);
        tvNombreUsuario.setText(nombre);

        // Acción al presionar el botón Publicar
        btnPublicar.setOnClickListener(v -> {
            String mensaje = editTextPublicacion.getText().toString();
            String currentTime = new SimpleDateFormat("HH:mm").format(new Date());

            Intent intent = new Intent(NuevapublicacionActivity.this, ComunidadActivity.class);
            intent.putExtra("mensaje", mensaje);
            intent.putExtra("nombre", nombre);
            intent.putExtra("hora", currentTime);

            guardarPublicacionEnSharedPreferences(mensaje, nombre, currentTime);

            startActivity(intent);
        });

        // Acción al presionar el botón de regreso ("X")
        btnRegresar.setOnClickListener(v -> {
            // Regresar a la actividad ComunidadActivity
            Intent intent = new Intent(NuevapublicacionActivity.this, ComunidadActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Método para guardar las publicaciones en SharedPreferences
    private void guardarPublicacionEnSharedPreferences(String mensaje, String nombre, String hora) {
        SharedPreferences sharedPreferences = getSharedPreferences("Publicaciones", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String publicacionesGuardadas = sharedPreferences.getString("publicaciones", "");
        String nuevaPublicacion = "{\"mensaje\":\"" + mensaje + "\",\"nombre\":\"" + nombre + "\",\"hora\":\"" + hora + "\"}";

        if (!publicacionesGuardadas.isEmpty()) {
            publicacionesGuardadas = publicacionesGuardadas + "," + nuevaPublicacion;
        } else {
            publicacionesGuardadas = nuevaPublicacion;
        }

        editor.putString("publicaciones", publicacionesGuardadas);
        editor.apply();
    }
}