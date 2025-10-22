package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutricion);


        Button btnDesayuno = findViewById(R.id.btnDesayuno);
        Button btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        Button btnCena = findViewById(R.id.btnCena);
        Button btnSnacks = findViewById(R.id.btnSnacks);

        btnDesayuno.setOnClickListener(v -> obtenerComidaYMostrar("Desayuno"));
        btnAlmuerzo.setOnClickListener(v -> obtenerComidaYMostrar("Almuerzo"));
        btnCena.setOnClickListener(v -> obtenerComidaYMostrar("Cena"));
        btnSnacks.setOnClickListener(v -> obtenerComidaYMostrar("Snacks"));


        // Botón para ir a MenuActivity
        Button btnMenu = findViewById(R.id.inicioButton);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(NutricionActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        // Botón para ir a ActividadfisicaActivity
        Button btnActividad = findViewById(R.id.ejercicioButton);
        btnActividad.setOnClickListener(v -> {
            Intent intent = new Intent(NutricionActivity.this, ActividadfisicaActivity.class);
            startActivity(intent);
        });

        // Botón para ir a SaludmentalActivity
        Button btnMental = findViewById(R.id.mentalButton);
        btnMental.setOnClickListener(v -> {
            Intent intent = new Intent(NutricionActivity.this, SaludmentalActivity.class);
            startActivity(intent);
        });

        // Botón para ir a ComunidadActivity
        Button btnComunidad = findViewById(R.id.comunidadButton);
        btnComunidad.setOnClickListener(v -> {
            Intent intent = new Intent(NutricionActivity.this, ComunidadActivity.class);
            startActivity(intent);
        });
    }

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

                            // Mostrar diálogo con la comida y preparación
                            ComidasFragmet dialog = new ComidasFragmet(comida, preparacion);
                            dialog.show(getSupportFragmentManager(), "ComidaDialog");
                        } else {
                            Toast.makeText(NutricionActivity.this, "No se encontró comida para hoy", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(NutricionActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(NutricionActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tipo", tipo); // Desayuno, Almuerzo, etc.
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
