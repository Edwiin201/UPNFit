package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class GeneroActivity extends AppCompatActivity {

    private ImageView imgMasculino, imgFemenino;
    private String nombre, correo, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genero);

        // Referencias UI
        imgMasculino = findViewById(R.id.imgMasculino);
        imgFemenino = findViewById(R.id.imgFemenino);

        // Recuperar datos del intent anterior
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        contrasena = intent.getStringExtra("contrasena");

        // Eventos de clic
        imgMasculino.setOnClickListener(v -> seleccionarGenero("M"));
        imgFemenino.setOnClickListener(v -> seleccionarGenero("F"));
    }

    private void seleccionarGenero(String genero) {
        if (genero == null || genero.isEmpty()) {
            Toast.makeText(this, "Debe seleccionar un género", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Guardar el género como 'M' o 'F' en SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("genero", genero);
        editor.apply();

        // ✅ Pasar el género corto (M o F) a la siguiente pantalla
        Intent intent = new Intent(GeneroActivity.this, AlturaActivity.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("correo", correo);
        intent.putExtra("contrasena", contrasena);
        intent.putExtra("genero", genero);
        startActivity(intent);
        finish();
    }
}
