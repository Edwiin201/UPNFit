package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ActividadfisicaFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
// Asegúrate de que esta ruta sea correcta según tu paquete

public class ActividadfisicaActivity extends AppCompatActivity {
    private static final String URL_ACTIVIDAD = "http://renovaapp.atwebpages.com/Services/Actividad_fisica.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividadfisica);

        // Botón para ir a MenuActivity
        Button btnMenu = findViewById(R.id.inicioButton);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadfisicaActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        // Botón para abrir NutricionActivity
        Button btnNutricion = findViewById(R.id.nutricionButton);
        btnNutricion.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadfisicaActivity.this, NutricionActivity.class);
            startActivity(intent);
        });

        // Botón para ir a SaludmentalActivity
        Button btnMental = findViewById(R.id.mentalButton);
        btnMental.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadfisicaActivity.this, SaludmentalActivity.class);
            startActivity(intent);
        });

        // Botón para ir a ComunidadActivity
        Button btnComunidad = findViewById(R.id.comunidadButton);
        btnComunidad.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadfisicaActivity.this, ComunidadActivity.class);
            startActivity(intent);
        });

        // 🔹 Botón para ver ubicación (abrir MapaActivity)
        Button btnVerUbicacion = findViewById(R.id.btnVerUbicacion);
        btnVerUbicacion.setOnClickListener(v -> {
            Intent intent = new Intent(ActividadfisicaActivity.this, MapaActivity.class);
            startActivity(intent);
        });

        Button btnVerActividad = findViewById(R.id.btnVerActividad);
        btnVerActividad.setOnClickListener(v -> obtenerActividadDelDia());
    }
    private void obtenerActividadDelDia() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL_ACTIVIDAD, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        String actividad = response.getString("actividad");
                        String tipo = response.getString("tipo");
                        String indicaciones = response.getString("indicaciones");

                        // Mostrar DialogFragment con los datos
                        ActividadfisicaFragment dialog = new ActividadfisicaFragment(actividad, tipo, indicaciones);
                        dialog.show(getSupportFragmentManager(), "actividadFisica");

                    } else {
                        Toast.makeText(ActividadfisicaActivity.this,
                                response.getString("message"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(ActividadfisicaActivity.this, "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ActividadfisicaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
