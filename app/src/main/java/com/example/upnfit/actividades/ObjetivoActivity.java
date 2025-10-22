package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.upnfit.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjetivoActivity extends AppCompatActivity {

    private String nombre, correo, clave, genero,alturaT,pesoT;
    private float altura;
    private float peso;
    private final List<String> objetivosSeleccionados = new ArrayList<>();
    private final Map<AppCompatButton, String> mapaBotones = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objetivo);

        // Recuperar los datos correctamente (usa "contrasena" en lugar de "clave")
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        clave = intent.getStringExtra("contrasena");
        genero = intent.getStringExtra("genero");
        altura = intent.getFloatExtra("altura",0f);
        peso = intent.getIntExtra("peso",0);


        // Validación de datos faltantes
        if (nombre == null || correo == null || clave == null || genero == null) {
            Toast.makeText(this, "Faltan datos del registro", Toast.LENGTH_LONG).show();
            finish();  // Cierra la actividad si hay datos incompletos
            return;
        }

        // Referenciar botones de objetivo
        AppCompatButton btnBajarPeso = findViewById(R.id.btnBajarPeso);
        AppCompatButton btnMantenerPeso = findViewById(R.id.btnMantenerPeso);
        AppCompatButton btnSalud = findViewById(R.id.btnSalud);
        AppCompatButton btnClaridadMental = findViewById(R.id.btnClaridadMental);
        AppCompatButton btnEnergia = findViewById(R.id.btnEnergia);
        AppCompatButton btnVivirMas = findViewById(R.id.btnVivirMas);

        // Mapeo de botones y objetivos
        mapaBotones.put(btnBajarPeso, "Bajar de peso");
        mapaBotones.put(btnMantenerPeso, "Mantener el peso");
        mapaBotones.put(btnSalud, "Reforzar la salud");
        mapaBotones.put(btnClaridadMental, "Claridad mental");
        mapaBotones.put(btnEnergia, "Más energía");
        mapaBotones.put(btnVivirMas, "Vivir más tiempo");

        // Lógica de selección
        for (Map.Entry<AppCompatButton, String> entry : mapaBotones.entrySet()) {
            AppCompatButton btn = entry.getKey();
            String objetivo = entry.getValue();

            btn.setOnClickListener(v -> {
                if (objetivosSeleccionados.contains(objetivo)) {
                    objetivosSeleccionados.remove(objetivo);
                    btn.setBackgroundResource(R.drawable.boton_circular); // No seleccionado
                } else {
                    if (objetivosSeleccionados.size() >= 2) {
                        Toast.makeText(this, "Máximo 2 objetivos", Toast.LENGTH_SHORT).show();
                    } else {
                        objetivosSeleccionados.add(objetivo);
                        btn.setBackgroundResource(R.drawable.boton_circular_seleccionado); // Seleccionado
                    }
                }
            });
        }

        Button btnContinuarObjetivo = findViewById(R.id.btnContinuarObjetivo);

        btnContinuarObjetivo.setOnClickListener(v -> {
            if (objetivosSeleccionados.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos un objetivo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar en SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("objetivo1", objetivosSeleccionados.size() > 0 ? objetivosSeleccionados.get(0) : "");
            editor.putString("objetivo2", objetivosSeleccionados.size() > 1 ? objetivosSeleccionados.get(1) : "");
            editor.apply();

            // Ir a ContratoActivity
            Intent siguiente = new Intent(ObjetivoActivity.this, ContratoActivity.class);
            siguiente.putExtra("nombre", nombre);
            siguiente.putExtra("correo", correo);
            siguiente.putExtra("contrasena", clave);  // <- también corregido aquí
            siguiente.putExtra("genero", genero);
            siguiente.putExtra("altura", altura);
            siguiente.putExtra("peso", getIntent().getIntExtra("peso", 0));
            siguiente.putExtra("objetivo1", objetivosSeleccionados.size() > 0 ? objetivosSeleccionados.get(0) : "");
            siguiente.putExtra("objetivo2", objetivosSeleccionados.size() > 1 ? objetivosSeleccionados.get(1) : "");
            startActivity(siguiente);
        });
    }
}
