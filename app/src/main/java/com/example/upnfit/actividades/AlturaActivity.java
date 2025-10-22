package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class AlturaActivity extends AppCompatActivity {

    private SeekBar seekAltura;
    private TextView txtAlturaSeleccionada;
    private MaterialButton btnContinuarAltura;

    private String nombre, correo, contrasena, genero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_altura);

        // Obtener datos del intent anterior
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        contrasena = intent.getStringExtra("contrasena");
        genero = intent.getStringExtra("genero");

        // Referencias UI
        seekAltura = findViewById(R.id.seekAltura);
        txtAlturaSeleccionada = findViewById(R.id.txtAlturaSeleccionada);
        btnContinuarAltura = findViewById(R.id.btnContinuarAltura);

        // Mostrar la altura inicial
        actualizarTextoAltura(seekAltura.getProgress());

        // Cambios en el SeekBar
        seekAltura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                actualizarTextoAltura(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Botón Continuar
        btnContinuarAltura.setOnClickListener(v -> {
            float alturaMetros = seekAltura.getProgress() / 100f;
            String alturaTexto = String.format("%.2f", alturaMetros); // Ejemplo: "1.80"

            // GUARDAR altura en SharedPreferences
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("altura", alturaTexto);
            editor.apply();

            // Ir a la siguiente pantalla con los datos
            Intent siguiente = new Intent(AlturaActivity.this, PesoActivity.class);
            siguiente.putExtra("nombre", nombre);
            siguiente.putExtra("correo", correo);
            siguiente.putExtra("contrasena", contrasena);
            siguiente.putExtra("genero", genero);
            siguiente.putExtra("altura", alturaMetros);

            startActivity(siguiente);
        });
    }

    private void actualizarTextoAltura(int alturaCm) {
        float alturaMetros = alturaCm / 100f;
        txtAlturaSeleccionada.setText(String.format("%.2f m", alturaMetros));
    }
}
