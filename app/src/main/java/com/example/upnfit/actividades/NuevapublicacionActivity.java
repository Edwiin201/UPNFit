package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NuevapublicacionActivity extends AppCompatActivity {

    private EditText editTextTitulo, editTextPublicacion;
    private Spinner spinnerCategorias;
    private Button btnPublicar;
    private ImageView btnRegresar;
    private TextView textViewNombre, avatarIniciales;

    private static final String URL_PUBLICAR = "http://upnfit.atwebpages.com/upnfit/insertar_publicacion.php";
    private static final String URL_DATOS_USUARIO = "http://upnfit.atwebpages.com/upnfit/obtener_datos_usuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevapublicacion);

        editTextTitulo = findViewById(R.id.editTextTitulo);
        editTextPublicacion = findViewById(R.id.editTextPublicacion);
        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        btnPublicar = findViewById(R.id.btnPublicar);
        btnRegresar = findViewById(R.id.regresomenu);
        textViewNombre = findViewById(R.id.textViewNombrePublicacion);
        avatarIniciales = findViewById(R.id.avatarIniciales);

        // -------------------------------
        // SPINNER CON TEXTO NEGRO
        // -------------------------------
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categorias_publicacion,
                R.layout.spinner_item_black
        );
        adapter.setDropDownViewResource(R.layout.spinner_item_black);
        spinnerCategorias.setAdapter(adapter);

        // -------------------------------
        // OBTENER ID USUARIO
        // -------------------------------
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = sharedPreferences.getInt("usuarioID", 0);

        obtenerNombreDesdeBD(usuarioID);

        btnPublicar.setOnClickListener(v -> publicar(usuarioID));
        btnRegresar.setOnClickListener(v -> irAComunidad());
    }

    // -------------------------------
    // VOLVER AL FRAGMENTO COMUNIDAD
    // -------------------------------
    private void irAComunidad() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("openFragment", "comunidad");
        startActivity(intent);
        finish();
    }

    // -------------------------------
    // OBTENER NOMBRE USUARIO
    // -------------------------------
    private void obtenerNombreDesdeBD(int usuarioID) {

        StringRequest request = new StringRequest(Request.Method.POST, URL_DATOS_USUARIO, response -> {

            try {
                JSONObject json = new JSONObject(response);
                int codigo = json.optInt("Codigo", 0);

                if (codigo == 1) {
                    String nombreCompleto = json.optString("NombreCompleto", "Usuario");
                    String primerNombre = nombreCompleto.split("\\s+")[0];

                    textViewNombre.setText(primerNombre);
                    avatarIniciales.setText(obtenerIniciales(nombreCompleto));
                }

            } catch (JSONException e) {
                Toast.makeText(this, "Error al procesar datos del usuario", Toast.LENGTH_SHORT).show();
            }

        }, error -> Toast.makeText(this, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // -------------------------------
    // PUBLICAR
    // -------------------------------
    private void publicar(int usuarioID) {

        String titulo = editTextTitulo.getText().toString().trim();
        String contenido = editTextPublicacion.getText().toString().trim();
        String categoria = spinnerCategorias.getSelectedItem().toString();

        if (titulo.isEmpty() || contenido.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoriaID = obtenerCategoriaID(categoria);

        StringRequest request = new StringRequest(
                Request.Method.POST,
                URL_PUBLICAR,
                response -> {

                    try {
                        JSONObject json = new JSONObject(response);

                        boolean success = json.optBoolean("success", false);
                        String message = json.optString("message", "Respuesta desconocida");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (success) irAComunidad();

                    } catch (JSONException e) {
                        Toast.makeText(this, "Error al interpretar respuesta", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                params.put("categoriaID", String.valueOf(categoriaID));
                params.put("titulo", titulo);
                params.put("contenido", contenido);
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    // -------------------------------
    // MAPEO CATEGORÍAS
    // -------------------------------
    private int obtenerCategoriaID(String categoria) {
        switch (categoria) {
            case "#RECETASALUDABLE": return 1;
            case "#MEDITACIÓN": return 2;
            case "#UPNFIT": return 3;
            case "#BIENESTAR": return 4;
            default: return 0;
        }
    }

    // -------------------------------
    // INICIALES
    // -------------------------------
    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "US";

        String[] partes = nombreCompleto.trim().split("\\s+");
        String iniNombre = partes[0].substring(0, 1).toUpperCase();
        String iniApellido = (partes.length > 1) ? partes[1].substring(0, 1).toUpperCase() : "";

        return iniNombre + iniApellido;
    }
}
