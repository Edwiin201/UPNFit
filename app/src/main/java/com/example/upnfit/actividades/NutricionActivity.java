package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ComidasFragmet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NutricionActivity extends AppCompatActivity {

    private TextView txtIMCValor, txtGrasaValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutricion);

        // 🔹 Referencias UI
        Button btnDesayuno = findViewById(R.id.btnDesayuno);
        Button btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        Button btnCena = findViewById(R.id.btnCena);
        Button btnSnacks = findViewById(R.id.btnSnacks);

        txtIMCValor = findViewById(R.id.txtIMCValor);
        txtGrasaValor = findViewById(R.id.txtGrasaValor);

        // 🔹 Obtener el ID del usuario logueado desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = prefs.getInt("usuarioID", 0);

        if (usuarioID == 0) {
            Toast.makeText(this, "⚠️ No se encontró el ID del usuario logueado", Toast.LENGTH_SHORT).show();
        } else {
            // ✅ Cargar IMC y grasa corporal desde PHP usando el ID del usuario actual
            obtenerIndicadores(usuarioID);
        }

        // 🔹 Eventos de botones de comidas
        btnDesayuno.setOnClickListener(v -> obtenerComidaYMostrar("Desayuno"));
        btnAlmuerzo.setOnClickListener(v -> obtenerComidaYMostrar("Almuerzo"));
        btnCena.setOnClickListener(v -> obtenerComidaYMostrar("Cena"));
        btnSnacks.setOnClickListener(v -> obtenerComidaYMostrar("Snacks"));

        // 🔹 Navegación inferior
        findViewById(R.id.inicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));

        findViewById(R.id.ejercicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadfisicaActivity.class)));

        findViewById(R.id.mentalButton).setOnClickListener(v ->
                startActivity(new Intent(this, SaludmentalActivity.class)));

        findViewById(R.id.comunidadButton).setOnClickListener(v ->
                startActivity(new Intent(this, ComunidadActivity.class)));
    }

    // 🔹 Mostrar comida según tipo (no tocamos)
    private void obtenerComidaYMostrar(String tipo) {
        String url = "http://renovaapp.atwebpages.com/Services/Obtener_comida.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");
                        if (success) {
                            String comida = jsonObject.getString("comida");
                            String preparacion = jsonObject.getString("preparacion");

                            ComidasFragmet dialog = new ComidasFragmet(comida, preparacion);
                            dialog.show(getSupportFragmentManager(), "ComidaDialog");
                        } else {
                            Toast.makeText(this, "No se encontró comida para hoy", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos de comida", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor de comidas", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tipo", tipo);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // 🔹 Obtener IMC y Grasa corporal desde el servidor
    private void obtenerIndicadores(int usuarioID) {
        String url = "http://10.0.2.2/upnfit/obtener_todas_medidas.php"; // ✅ Archivo PHP local

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);

                        int codigo = json.optInt("Codigo", 0);
                        String mensaje = json.optString("Mensaje", "");

                        if (codigo == 1) {
                            double imc = json.optDouble("IMC", 0);
                            double grasa = json.optDouble("GrasaPct", 0);

                            txtIMCValor.setText(String.format("%.1f", imc));
                            txtGrasaValor.setText(String.format("%.1f%%", grasa));
                        } else {
                            txtIMCValor.setText("--");
                            txtGrasaValor.setText("--");
                            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al leer los datos del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error de conexión con el servidor de indicadores", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID)); // ✅ Envía el ID de la sesión actual
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
