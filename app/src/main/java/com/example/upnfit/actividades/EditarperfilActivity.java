package com.example.upnfit.actividades;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.example.upnfit.sqlite.PerfilDB;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class EditarperfilActivity extends AppCompatActivity {

    private EditText editNombre, editSede, editGenero, editEdad, editAltura, editPeso;
    private int usuarioID; // Hacemos usuarioID accesible a todos los métodos

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

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        editNombre = findViewById(R.id.editNombre);
        editSede   = findViewById(R.id.editSede);
        editGenero = findViewById(R.id.editGenero);
        editEdad   = findViewById(R.id.editEdad);
        editAltura = findViewById(R.id.editAltura);
        editPeso   = findViewById(R.id.editPeso);

        usuarioID = getSharedPreferences("UserData", MODE_PRIVATE)
                .getInt("usuarioID", 0);

        if (usuarioID == 0) {
            Toast.makeText(this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🛑 NUEVA ESTRATEGIA DE CACHE
        // 1. Carga Rápida Local
        cargarPerfilLocal(usuarioID);

        // 2. Sincronización en segundo plano (para obtener datos más recientes)
        obtenerDatosDesdeServidor(usuarioID);

        // El listener de guardar permanece igual
        findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> actualizarPerfil(usuarioID));
    }

    // 🛑 NUEVO MÉTODO: Carga los datos guardados en SQLite
    private void cargarPerfilLocal(int usuarioID) {
        PerfilDB db = new PerfilDB(this);
        Cursor cursor = db.obtenerPerfil(usuarioID);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                editNombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(PerfilDB.COL_NOMBRE)));
                editSede.setText(cursor.getString(cursor.getColumnIndexOrThrow(PerfilDB.COL_SEDE)));
                editGenero.setText(cursor.getString(cursor.getColumnIndexOrThrow(PerfilDB.COL_GENERO)));
                editEdad.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(PerfilDB.COL_EDAD))));
                editAltura.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(PerfilDB.COL_ALTURA))));
                editPeso.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(PerfilDB.COL_PESO))));
                Log.d(TAG, "Perfil cargado desde SQLite.");
            } catch (Exception e) {
                Log.e(TAG, "Error al leer datos del cursor local: " + e.getMessage());
            } finally {
                cursor.close();
            }
        } else {
            // Mostrar estado inicial si no hay cache
            editNombre.setText("");
            editSede.setText("");
            editGenero.setText("");
            editEdad.setText("");
            editAltura.setText("");
            editPeso.setText("");
            Log.d(TAG, "No hay cache local. Esperando servidor.");
        }
        db.close();
    }

    // 🛑 MÉTODO UNIFICADO: Obtiene nombre y medidas y guarda en cache
    private void obtenerDatosDesdeServidor(int usuarioID) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("usuarioID", usuarioID);

        // 1. Obtener Datos Personales (Nombre, Sede)
        client.post(URL_DATOS_USUARIO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.optInt("Codigo", 0) == 1) {
                    // 3. Actualizar UI y Cache
                    String nombre = response.optString("NombreCompleto", "");
                    String sede = response.optString("SedeID", "");
                    editNombre.setText(nombre);
                    editSede.setText(sede);

                    // Continuamos con la obtención de medidas (Medidas se sincronizan al final)
                    obtenerYGuardarMedidas(usuarioID, nombre, sede);
                } else {
                    Log.w(TAG, "Error de servidor al obtener datos personales.");
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Error de red al obtener datos: " + throwable.getMessage());
                // No mostrar Toast aquí si ya se cargó la cache, solo si la cache estaba vacía.
            }
        });
    }

    // 🛑 NUEVO MÉTODO: Obtiene medidas y realiza la sincronización final en SQLite
    private void obtenerYGuardarMedidas(int usuarioID, String nombre, String sede) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("usuarioID", usuarioID);

        client.post(URL_MEDIDAS_USUARIO, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response.optInt("Codigo", 0) == 1) {

                    // Obtener valores del servidor
                    String genero = response.optString("Genero", "");
                    String edadStr = response.optString("Edad", "0");
                    String alturaStr = response.optString("AlturaCm", "0");
                    String pesoStr = response.optString("PesoKg", "0");

                    // Actualizar UI
                    editGenero.setText(genero);
                    editEdad.setText(edadStr);
                    editAltura.setText(alturaStr);
                    editPeso.setText(pesoStr);

                    // Convertir para guardar en la BD local
                    int edad = Integer.parseInt(edadStr.isEmpty() ? "0" : edadStr);
                    double altura = Double.parseDouble(alturaStr.isEmpty() ? "0" : alturaStr);
                    double peso = Double.parseDouble(pesoStr.isEmpty() ? "0" : pesoStr);

                    // 💾 4. GUARDAR EN CACHE SQLITE
                    PerfilDB db = new PerfilDB(EditarperfilActivity.this);
                    db.guardarPerfil(usuarioID, nombre, sede, genero, edad, altura, peso);
                    db.close();
                    Log.d(TAG, "Cache SQLite de perfil actualizada con éxito.");

                } else {
                    Log.w(TAG, "Error de servidor al obtener medidas.");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Error de red al obtener medidas: " + throwable.getMessage());
            }
        });
    }


    private void actualizarPerfil(int usuarioID) {
        // ... (Tu validación de campos sigue igual) ...
        String nombre = editNombre.getText().toString().trim();
        String sedeID = editSede.getText().toString().trim();
        String genero = editGenero.getText().toString().trim();
        String edadStr = editEdad.getText().toString().trim();
        String alturaStr = editAltura.getText().toString().trim();
        String pesoStr = editPeso.getText().toString().trim();

        if (nombre.isEmpty() || sedeID.isEmpty() || genero.isEmpty()
                || edadStr.isEmpty() || alturaStr.isEmpty() || pesoStr.isEmpty()) {

            Toast.makeText(this,
                    "Completa todos los campos antes de guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        // ... (Tu código de AsyncHttpClient y RequestParams sigue igual) ...
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("usuarioID", usuarioID);
        params.put("nombreCompleto", nombre);
        params.put("sedeID", sedeID);
        params.put("genero", genero);
        params.put("edad", edadStr);
        params.put("alturaCm", alturaStr);
        params.put("pesoKg", pesoStr);

        client.post(URL_ACTUALIZAR_PERFIL, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                int codigo = response.optInt("Codigo", 0);
                String mensaje = response.optString("Mensaje", "");

                Toast.makeText(EditarperfilActivity.this, mensaje, Toast.LENGTH_SHORT).show();

                if (codigo == 1) {

                    // 🛑 5. Sincronización Inmediata después de guardar exitosamente
                    try {
                        PerfilDB db = new PerfilDB(EditarperfilActivity.this);
                        db.guardarPerfil(
                                usuarioID,
                                nombre,
                                sedeID,
                                genero,
                                Integer.parseInt(edadStr),
                                Double.parseDouble(alturaStr),
                                Double.parseDouble(pesoStr)
                        );
                        db.close();
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Error al guardar el perfil en SQLite después de actualizar: " + e.getMessage());
                    }


                    //  Volver a MainActivity
                    Intent intent = new Intent(EditarperfilActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "Error actualizar perfil: " + throwable.getMessage());
                Toast.makeText(EditarperfilActivity.this,
                        "Error al conectar con el servidor (actualizar)", Toast.LENGTH_SHORT).show();
            }
        });
    }
}