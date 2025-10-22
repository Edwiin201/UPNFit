package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.upnfit.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        // Recuperar el nombre guardado en SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        String nombreGuardado = sharedPreferences.getString("nombre", "Usuario");

        // Obtener iniciales y mostrar en el botón circular
        String iniciales = obtenerIniciales(nombreGuardado);
        AppCompatButton btnPerfil = findViewById(R.id.btnmenuPerfil);
        btnPerfil.setText(iniciales);

        // Mostrar mensaje de bienvenida con solo el primer nombre
        String primerNombre = obtenerPrimerNombre(nombreGuardado);
        TextView textoBienvenida = findViewById(R.id.textoBienvenida);
        textoBienvenida.setText("¡Buen día, " + primerNombre + "!");

        // ✅ MOSTRAR OBJETIVOS GUARDADOS (objetivo1 y objetivo2)
        TextView txtObjetivos = findViewById(R.id.txtObjetivosMenu);
        String objetivo1 = sharedPreferences.getString("objetivo1", "");
        String objetivo2 = sharedPreferences.getString("objetivo2", "");

        if (!objetivo1.isEmpty() && !objetivo2.isEmpty()) {
            txtObjetivos.setText("Tus objetivos: " + objetivo1 + ", " + objetivo2);
        } else if (!objetivo1.isEmpty()) {
            txtObjetivos.setText("Tu objetivo: " + objetivo1);
        } else {
            txtObjetivos.setText("Tus objetivos: (no seleccionados)");
        }

        TextView tvConsejoDia = findViewById(R.id.tvConsejoDia);

        cargarConsejoDelDia(tvConsejoDia);

        // Botón Nutrición
        Button btnNutricion = findViewById(R.id.nutricionButton);
        btnNutricion.setOnClickListener(v -> startActivity(new Intent(this, NutricionActivity.class)));

        // Botón Actividad Física
        Button btnActividad = findViewById(R.id.ejercicioButton);
        btnActividad.setOnClickListener(v -> startActivity(new Intent(this, ActividadfisicaActivity.class)));

        // Botón Salud Mental
        Button btnMental = findViewById(R.id.mentalButton);
        btnMental.setOnClickListener(v -> startActivity(new Intent(this, SaludmentalActivity.class)));

        // Botón Comunidad
        Button btnComunidad = findViewById(R.id.comunidadButton);
        btnComunidad.setOnClickListener(v -> startActivity(new Intent(this, ComunidadActivity.class)));

        // Botón Configuración
        ImageButton btnConfiguracion = findViewById(R.id.configuracion);
        btnConfiguracion.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));
    }

    // ✅ Obtener las iniciales del primer nombre y primer apellido
    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "US";

        String[] partes = nombreCompleto.trim().split("\\s+");
        String inicialNombre = partes.length >= 1 ? partes[0].substring(0, 1).toUpperCase() : "";
        String inicialApellido = partes.length >= 3 ? partes[2].substring(0, 1).toUpperCase() :
                (partes.length >= 2 ? partes[1].substring(0, 1).toUpperCase() : "");

        return inicialNombre + inicialApellido;
    }

    // ✅ Obtener solo el primer nombre
    private String obtenerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "Usuario";
        String[] partes = nombreCompleto.trim().split("\\s+");
        return partes.length >= 1 ? partes[0] : nombreCompleto;
    }

    private void cargarConsejoDelDia(TextView tvConsejoDia) {
        String url = "http://renovaapp.atwebpages.com/Services/Consejo_diario.php"; // Cambia por tu URL real

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject consejo = response.getJSONObject("data");
                            String titulo = consejo.getString("titulo");
                            String contenido = consejo.getString("contenido");
                            tvConsejoDia.setText("💡 " + titulo + ":\n" + contenido);
                        } else {
                            tvConsejoDia.setText("💡 Hoy es un buen día para empezar algo nuevo.");
                        }
                    } catch (JSONException e) {
                        tvConsejoDia.setText("💡 Consejo no disponible por el momento.");
                    }
                },
                error -> {
                    Log.e("API_ERROR", "Código de error HTTP: " + error.networkResponse.statusCode);
                    tvConsejoDia.setText("💡 Error al obtener el consejo.");
                }
        );

        queue.add(request);
    }

}
