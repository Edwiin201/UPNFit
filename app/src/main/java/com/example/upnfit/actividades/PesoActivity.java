package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.google.android.material.button.MaterialButton;

public class PesoActivity extends AppCompatActivity {

    private SeekBar seekBarPeso;
    private TextView tvPeso;
    private MaterialButton btnRegistrarPeso;

    private String nombre, correo, contrasena, genero;
    private float altura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peso);

        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        contrasena = intent.getStringExtra("contrasena");
        genero = intent.getStringExtra("genero");
        altura = intent.getFloatExtra("altura", 0f);

        seekBarPeso = findViewById(R.id.seekBarPeso);
        tvPeso = findViewById(R.id.tvPeso);
        btnRegistrarPeso = findViewById(R.id.btnRegistrarPeso);

        tvPeso.setText(String.valueOf(seekBarPeso.getProgress()));

        seekBarPeso.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPeso.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        btnRegistrarPeso.setOnClickListener(v -> {
            int pesoSeleccionado = seekBarPeso.getProgress();

            // 🔐 GUARDAR peso en SharedPreferences (para que aparezca en editar perfil)
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("peso", String.valueOf(pesoSeleccionado));
            editor.apply();

            // Pasar todos los datos al siguiente intent
            Intent siguiente = new Intent(PesoActivity.this, EdadActivity.class);
            siguiente.putExtra("nombre", nombre);
            siguiente.putExtra("correo", correo);
            siguiente.putExtra("contrasena", contrasena);
            siguiente.putExtra("genero", genero);
            siguiente.putExtra("altura", altura);
            siguiente.putExtra("peso", pesoSeleccionado);

            startActivity(siguiente);
        });
    }
}
