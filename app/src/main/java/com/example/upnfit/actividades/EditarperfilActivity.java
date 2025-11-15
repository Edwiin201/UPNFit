package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EditarperfilActivity extends AppCompatActivity {

    private EditText editNombre, editSede, editGenero, editEdad, editAltura, editPeso;

    private static final String URL_DATOS_USUARIO =
            "http://upnfit.atwebpages.com/upnfit/obtener_datos_usuario.php";

    private static final String URL_MEDIDAS_USUARIO =
            "http://upnfit.atwebpages.com/upnfit/obtener_todas_medidas.php";

    private static final String URL_ACTUALIZAR_PERFIL =
            "http://upnfit.atwebpages.com/upnfit/actualizar_perfil_completo.php";

    private static final String TAG = "EditarPerfil";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarperfil);

        // 🔙 BOTÓN RETROCEDER
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Referencias UI
        editNombre = findViewById(R.id.editNombre);
        editSede   = findViewById(R.id.editSede);
        editGenero = findViewById(R.id.editGenero);
        editEdad   = findViewById(R.id.editEdad);
        editAltura = findViewById(R.id.editAltura);
        editPeso   = findViewById(R.id.editPeso);

        // Obtener el ID del usuario desde SharedPreferences
        int usuarioID = getSharedPreferences("UserData", MODE_PRIVATE)
                .getInt("usuarioID", 0);

        if (usuarioID == 0) {
            Toast.makeText(this,
                    "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔹 Cargar datos iniciales
        obtenerNombreDesdeServidor(usuarioID);
        obtenerMedidasDesdeServidor(usuarioID);

        // 🔹 Botón Guardar → Actualizar perfil
        findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> actualizarPerfil(usuarioID));
    }

    // =======================================================
    // 1️⃣ OBTENER DATOS DEL USUARIO (Nombre y Sede)
    // =======================================================
    private void obtenerNombreDesdeServidor(int usuarioID) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("usuarioID", usuarioID);

        client.post(URL_DATOS_USUARIO, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                int codigo = response.optInt("Codigo", 0);
                String mensaje = response.optString("Mensaje", "");

                if (codigo == 1) {
                    editNombre.setText(response.optString("NombreCompleto", ""));
                    editSede.setText(response.optString("SedeID", ""));
                } else {
                    Toast.makeText(EditarperfilActivity.this,
                            mensaje, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.e(TAG, "Error obtener datos usuario: " + throwable.getMessage());
                Toast.makeText(EditarperfilActivity.this,
                        "Error al conectar con el servidor (usuario)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =======================================================
    // 2️⃣ OBTENER MEDIDAS (Altura, Peso, Edad, Género)
    // =======================================================
    private void obtenerMedidasDesdeServidor(int usuarioID) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("usuarioID", usuarioID);

        client.post(URL_MEDIDAS_USUARIO, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                int codigo = response.optInt("Codigo", 0);
                String mensaje = response.optString("Mensaje", "");

                if (codigo == 1) {
                    editGenero.setText(response.optString("Genero", ""));
                    editEdad.setText(response.optString("Edad", ""));
                    editAltura.setText(response.optString("AlturaCm", ""));
                    editPeso.setText(response.optString("PesoKg", ""));
                } else {
                    Toast.makeText(EditarperfilActivity.this,
                            mensaje, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(
                    int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.e(TAG, "Error obtener medidas: " + throwable.getMessage());
                Toast.makeText(EditarperfilActivity.this,
                        "Error al conectar con el servidor (medidas)", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // =======================================================
    // 3️⃣ ACTUALIZAR TODO EL PERFIL EN LA BASE DE DATOS
    // =======================================================
    private void actualizarPerfil(int usuarioID) {

        String nombre = editNombre.getText().toString().trim();
        String sedeID = editSede.getText().toString().trim();
        String genero = editGenero.getText().toString().trim();
        String edad   = editEdad.getText().toString().trim();
        String altura = editAltura.getText().toString().trim();
        String peso   = editPeso.getText().toString().trim();

        if (nombre.isEmpty() || sedeID.isEmpty() || genero.isEmpty()
                || edad.isEmpty() || altura.isEmpty() || peso.isEmpty()) {

            Toast.makeText(this,
                    "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("usuarioID", usuarioID);
        params.put("nombreCompleto", nombre);
        params.put("sedeID", sedeID);
        params.put("genero", genero);
        params.put("edad", edad);
        params.put("alturaCm", altura);
        params.put("pesoKg", peso);

        client.post(URL_ACTUALIZAR_PERFIL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                int codigo = response.optInt("Codigo", 0);
                String mensaje = response.optString("Mensaje", "");

                Toast.makeText(EditarperfilActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                if (codigo == 1) {
                    // Perfil actualizado → volver al menú
                    Intent intent = new Intent(EditarperfilActivity.this, MenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(
                    int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                Log.e(TAG, "Error actualizar perfil: " + throwable.getMessage());
                Toast.makeText(EditarperfilActivity.this,
                        "Error al conectar con el servidor (actualizar)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
