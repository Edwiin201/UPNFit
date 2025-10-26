package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.google.android.material.button.MaterialButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EdadActivity extends AppCompatActivity {

    private SeekBar seekEdad;
    private TextView txtEdadSeleccionada;
    private MaterialButton btnContinuarEdad;
    private ImageView imgEdad;

    private String nombre, correo, contrasena, genero;
    private float altura;
    private float peso;

    private static final String URL_REGISTRAR_MEDIDAS = "http://upnfit.atwebpages.com/upnfit/registrar_medidas.php";

    // 🔹 Definir edad mínima y máxima
    private final int EDAD_MINIMA = 14;
    private final int EDAD_MAXIMA = 50;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edad);

        // Referencias UI
        seekEdad = findViewById(R.id.seekEdad);
        txtEdadSeleccionada = findViewById(R.id.txtEdadSeleccionada);
        btnContinuarEdad = findViewById(R.id.btnContinuarEdad);
        imgEdad = findViewById(R.id.imgEdad);

        // Recibir datos de Activity anterior
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        contrasena = intent.getStringExtra("contrasena");
        genero = intent.getStringExtra("genero");
        altura = intent.getFloatExtra("altura", 0f);
        peso = intent.getFloatExtra("peso", 0f);

        // Ajustar SeekBar
        seekEdad.setMax(EDAD_MAXIMA - EDAD_MINIMA); // rango = 50 - 14 = 36
        seekEdad.setProgress(6); // valor inicial = 14 + 6 = 20 años

        // Mostrar valor inicial
        txtEdadSeleccionada.setText((EDAD_MINIMA + seekEdad.getProgress()) + " años");

        // Listener del SeekBar
        seekEdad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int edad = EDAD_MINIMA + progress;
                txtEdadSeleccionada.setText(edad + " años");
            }

            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Botón continuar
        btnContinuarEdad.setOnClickListener(v -> {
            int edadSeleccionada = EDAD_MINIMA + seekEdad.getProgress(); // Ajuste al mínimo

            if (edadSeleccionada < EDAD_MINIMA || edadSeleccionada > EDAD_MAXIMA) {
                Toast.makeText(this, "Edad fuera de rango (" + EDAD_MINIMA + "–" + EDAD_MAXIMA + " años)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar edad en SharedPreferences
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("edad", edadSeleccionada);
            editor.apply();

            // Obtener usuarioID
            int usuarioID = preferences.getInt("usuarioID", 0);
            if (usuarioID == 0) {
                Toast.makeText(this, "⚠️ No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            registrarMedida(usuarioID, peso, altura, genero, edadSeleccionada);
        });
    }

    private void registrarMedida(int usuarioID, float peso, float altura, String genero, int edad) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("usuarioID", usuarioID);
        params.put("peso", String.format("%.2f", peso));
        params.put("altura", String.format("%.2f", altura * 100)); // metros → cm
        params.put("genero", genero);
        params.put("edad", edad);

        client.post(URL_REGISTRAR_MEDIDAS, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(EdadActivity.this, "✅ Medidas registradas correctamente", Toast.LENGTH_SHORT).show();

                Intent siguiente = new Intent(EdadActivity.this, ObjetivoActivity.class);
                siguiente.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                siguiente.putExtra("nombre", nombre);
                siguiente.putExtra("correo", correo);
                siguiente.putExtra("contrasena", contrasena);
                siguiente.putExtra("genero", genero);
                siguiente.putExtra("altura", altura);
                siguiente.putExtra("peso", peso);
                startActivity(siguiente);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(EdadActivity.this,
                        " Error al registrar: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
