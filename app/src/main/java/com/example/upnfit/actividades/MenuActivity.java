package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private TextView textoBienvenida;
    private AppCompatButton btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 🌓 APLICAR modo oscuro o claro guardado antes de mostrar la UI
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);

        // --- Referencias UI ---
        textoBienvenida = findViewById(R.id.textoBienvenida);
        btnPerfil = findViewById(R.id.btnmenuPerfil);

        // Recuperar el ID del usuario logueado
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = sharedPreferences.getInt("usuarioID", 0);

        if (usuarioID == 0) {
            textoBienvenida.setText("¡Buen día, Usuario!");
        } else {
            obtenerPrimerNombreDesdeBD(usuarioID);
        }

        // ✅ Mostrar objetivos guardados
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

        // ✅ Consejo del día
        TextView tvConsejoDia = findViewById(R.id.tvConsejoDia);
        cargarConsejoDelDia(tvConsejoDia);

        // 🔹 Botones de navegación
        Button btnNutricion = findViewById(R.id.nutricionButton);
        btnNutricion.setOnClickListener(v -> startActivity(new Intent(this, NutricionActivity.class)));

        Button btnActividad = findViewById(R.id.ejercicioButton);
        btnActividad.setOnClickListener(v -> startActivity(new Intent(this, ActividadfisicaActivity.class)));

        Button btnMental = findViewById(R.id.mentalButton);
        btnMental.setOnClickListener(v -> startActivity(new Intent(this, SaludmentalActivity.class)));

        Button btnComunidad = findViewById(R.id.comunidadButton);
        btnComunidad.setOnClickListener(v -> startActivity(new Intent(this, ComunidadActivity.class)));

        ImageButton btnConfiguracion = findViewById(R.id.configuracion);
        btnConfiguracion.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));
    }

    // ✅ Obtener nombre desde la BD
    private void obtenerPrimerNombreDesdeBD(int usuarioID) {
        String url = "http://10.0.2.2/upnfit/obtener_datos_usuario.php"; // tu PHP local

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                int codigo = json.optInt("Codigo", 0);

                if (codigo == 1) {
                    String nombreCompleto = json.optString("NombreCompleto", "Usuario");
                    String primerNombre = nombreCompleto.split("\\s+")[0];

                    textoBienvenida.setText("¡Buen día, " + primerNombre + "!");
                    String iniciales = obtenerIniciales(nombreCompleto);
                    btnPerfil.setText(iniciales);
                } else {
                    textoBienvenida.setText("¡Buen día, Usuario!");
                }

            } catch (JSONException e) {
                textoBienvenida.setText("¡Buen día, Usuario!");
                e.printStackTrace();
            }

        }, error -> {
            Log.e("MenuActivity", "Error de conexión: " + error.toString());
            textoBienvenida.setText("¡Buen día, Usuario!");
            Toast.makeText(this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // ✅ Obtener iniciales
    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "US";

        String[] partes = nombreCompleto.trim().split("\\s+");
        String inicialNombre = partes.length >= 1 ? partes[0].substring(0, 1).toUpperCase() : "";
        String inicialApellido = partes.length >= 2 ? partes[1].substring(0, 1).toUpperCase() : "";

        return inicialNombre + inicialApellido;
    }

    // ✅ Consejo del día
    private void cargarConsejoDelDia(TextView tvConsejoDia) {
        String url = "http://renovaapp.atwebpages.com/Services/Consejo_diario.php";

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
                    Log.e("API_ERROR", "Error al obtener el consejo");
                    tvConsejoDia.setText("💡 Error al obtener el consejo.");
                });

        queue.add(request);
    }
}
