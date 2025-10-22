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
    private int peso;

    // ⚠️ Cambia esta IP según tu entorno local (emulador usa 10.0.2.2)
    private static final String URL_REGISTRAR_MEDIDAS = "http://10.0.2.2/upnfit/registrar_medidas.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edad);

        // 🔹 Referencias UI
        seekEdad = findViewById(R.id.seekEdad);
        txtEdadSeleccionada = findViewById(R.id.txtEdadSeleccionada);
        btnContinuarEdad = findViewById(R.id.btnContinuarEdad);
        imgEdad = findViewById(R.id.imgEdad);

        // 🔹 Recibir datos anteriores
        Intent intent = getIntent();
        nombre = intent.getStringExtra("nombre");
        correo = intent.getStringExtra("correo");
        contrasena = intent.getStringExtra("contrasena");
        genero = intent.getStringExtra("genero");
        altura = intent.getFloatExtra("altura", 0f);
        peso = intent.getIntExtra("peso", 0);

        // 🔹 Mostrar valor inicial
        int edadInicial = seekEdad.getProgress();
        txtEdadSeleccionada.setText(edadInicial + " años");

        // 🔹 Cambios en el SeekBar
        seekEdad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtEdadSeleccionada.setText(progress + " años");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // 🔹 Acción del botón Continuar
        btnContinuarEdad.setOnClickListener(v -> {
            int edadSeleccionada = seekEdad.getProgress();

            if (edadSeleccionada < 10 || edadSeleccionada > 100) {
                Toast.makeText(this, "Edad fuera de rango (10–100 años)", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar localmente la edad
            SharedPreferences preferences = getSharedPreferences("UserData", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("edad", edadSeleccionada);
            editor.apply();

            // Obtener ID de usuario guardado en login/registro
            int usuarioID = preferences.getInt("usuarioID", 0);
            if (usuarioID == 0) {
                Toast.makeText(this, "⚠️ No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🌐 Enviar los datos al servidor
            registrarMedida(usuarioID, peso, altura, genero, edadSeleccionada);
        });
    }

    private void registrarMedida(int usuarioID, int peso, float altura, String genero, int edad) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("usuarioID", usuarioID);
        params.put("peso", peso);
        params.put("altura", altura * 100); // convertir metros a cm
        params.put("genero", genero);
        params.put("edad", edad);

        client.post(URL_REGISTRAR_MEDIDAS, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(EdadActivity.this, "✅ Medidas registradas correctamente", Toast.LENGTH_SHORT).show();

                // 🔹 Ir a ObjetivoActivity con todos los datos y limpiar la pila
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
                        "❌ Error al registrar: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}
