package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class BienvenidaActivity extends AppCompatActivity {

    private Button btnContinuarCorreo;
    private EditText inputCorreoBienvenida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenida);

        // Referenciar vistas
        btnContinuarCorreo = findViewById(R.id.btnContinuarCorreo);
        inputCorreoBienvenida = findViewById(R.id.inputCorreoBienvenida); // Asegúrate que este ID sea el mismo en tu XML

        btnContinuarCorreo.setOnClickListener(v -> {
            String correoIngresado = inputCorreoBienvenida.getText().toString().trim();

            if (correoIngresado.isEmpty()) {
                Toast.makeText(this, "Por favor, ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener el correo registrado desde SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String correoRegistrado = prefs.getString("correo", "");

            if (correoRegistrado.isEmpty()) {
                Toast.makeText(this, "No hay un correo registrado previamente", Toast.LENGTH_SHORT).show();
                return;
            }

            if (correoIngresado.equalsIgnoreCase(correoRegistrado)) {
                // Correo válido, ir al menú
                Intent intent = new Intent(BienvenidaActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "El correo no coincide con el registrado", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
