package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class EditarperfilActivity extends AppCompatActivity {

    private EditText editNombre, editGenero, editEdad, editAltura, editPeso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editarperfil);

        String correoUsuario = getSharedPreferences("UserData", MODE_PRIVATE).getString("correo", "");
        String url = "http://renovaapp.atwebpages.com/Services/Obtener_usuario.php?correo=" + correoUsuario;

        // Obtener referencias a los EditText
        editNombre = findViewById(R.id.editNombre);
        editGenero = findViewById(R.id.editGenero);
        editEdad = findViewById(R.id.editEdad);
        editAltura = findViewById(R.id.editAltura);
        editPeso = findViewById(R.id.editPeso);

        obtenerDatosDesdeWebService();

        // Obtener SharedPreferences
        SharedPreferences sharedPreferencesUserData = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences sharedPreferencesUserProfile = getSharedPreferences("UserProfile", MODE_PRIVATE);

        // Recuperar datos
        String nombre = sharedPreferencesUserData.getString("nombre", "");

        // 👇 Aquí mejoramos: prioridad a UserData para el género (por GeneroActivity)
        String genero = sharedPreferencesUserData.getString("genero", sharedPreferencesUserProfile.getString("genero", ""));
        String edad = sharedPreferencesUserProfile.getString("edad", "");

        // Mostrar datos recuperados
        editNombre.setText(nombre);
        editGenero.setText(genero);
        editEdad.setText(edad);

        // Mostrar altura (prioridad a UserData que guarda desde AlturaActivity)
        String alturaUserData = sharedPreferencesUserData.getString("altura", "");
        String alturaUserProfile = sharedPreferencesUserProfile.getString("altura", "");

        if (!alturaUserData.isEmpty()) {
            try {
                float alturaMetros = Float.parseFloat(alturaUserData);
                int alturaCm = Math.round(alturaMetros * 100);
                editAltura.setText(String.valueOf(alturaCm));
            } catch (NumberFormatException e) {
                editAltura.setText(""); // Error de formato
            }
        } else if (!alturaUserProfile.isEmpty()) {
            editAltura.setText(alturaUserProfile); // Ya debería estar en cm si viene de UserProfile
        }

        // Mostrar peso (prioridad a UserData que guarda desde PesoActivity)
        String pesoUserData = sharedPreferencesUserData.getString("peso", "");
        String pesoUserProfile = sharedPreferencesUserProfile.getString("peso", "");

        if (!pesoUserData.isEmpty()) {
            editPeso.setText(pesoUserData);
        } else if (!pesoUserProfile.isEmpty()) {
            editPeso.setText(pesoUserProfile);
        }

        // Botón para guardar cambios
        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String nombreIngresado = editNombre.getText().toString();
            String generoIngresado = editGenero.getText().toString();
            String edadIngresada = editEdad.getText().toString();
            String alturaIngresada = editAltura.getText().toString();
            String pesoIngresado = editPeso.getText().toString();

            // Guardar en UserProfile
            SharedPreferences.Editor editor = sharedPreferencesUserProfile.edit();
            editor.putString("nombre", nombreIngresado);
            editor.putString("genero", generoIngresado);
            editor.putString("edad", edadIngresada);
            editor.putString("altura", alturaIngresada); // altura en cm
            editor.putString("peso", pesoIngresado);
            editor.apply();

            // Actualizar también UserData si hay datos relevantes
            SharedPreferences.Editor editorUserData = sharedPreferencesUserData.edit();
            editorUserData.putString("nombre", nombreIngresado);
            editorUserData.putString("genero", generoIngresado); // ✅ Aquí también lo guardamos
            editorUserData.putString("altura", String.valueOf(Float.parseFloat(alturaIngresada) / 100)); // Guardar en metros
            editorUserData.putString("peso", pesoIngresado);
            editorUserData.apply();

            actualizarPerfilEnWebService(
                    correoUsuario,
                    nombreIngresado,
                    generoIngresado,
                    alturaIngresada,
                    pesoIngresado
            );

            Toast.makeText(EditarperfilActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();

            // Volver a Configuración
            Intent intent = new Intent(EditarperfilActivity.this, ConfiguracionActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void obtenerDatosDesdeWebService() {
        String correoUsuario = getSharedPreferences("UserData", MODE_PRIVATE).getString("correo", "");
        String url = "http://renovaapp.atwebpages.com/Services/Obtener_usuario.php?correo=" + correoUsuario;

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONObject usuario = response.getJSONObject("usuario");

                        editNombre.setText(usuario.getString("nombres"));
                        editGenero.setText(usuario.getString("genero"));
                        //editEdad.setText(usuario.getString("edad"));
                        String edad = usuario.optString("edad", "0");  // Valor predeterminado "0" si no existe
                        editEdad.setText(edad);

                        // Manejo de la altura
                        try {
                            if (usuario.has("altura") && !usuario.isNull("altura")) {
                                String alturaString = usuario.getString("altura");

                                // Verificar que la cadena no esté vacía
                                if (!alturaString.isEmpty()) {
                                    // Intentar convertir la cadena a un número (usando el punto como separador decimal)
                                    try {
                                        double alturaMetros = Double.parseDouble(alturaString);
                                        int alturaCm = (int) Math.round(alturaMetros * 100);
                                        editAltura.setText(String.valueOf(alturaCm));
                                    } catch (NumberFormatException e) {
                                        // Si no se puede parsear, mostrar un mensaje y limpiar el campo
                                        editAltura.setText("");
                                        Log.e("EditarPerfil", "Error al convertir la altura: " + alturaString, e);
                                    }
                                } else {
                                    editAltura.setText("");  // Si la cadena está vacía, limpiar el campo
                                }
                            } else {
                                editAltura.setText("");  // Si no existe el campo o es nulo, limpiar el campo
                            }
                        } catch (Exception e) {
                            // Si ocurre un error inesperado, mostrar un mensaje de error
                            editAltura.setText("");
                            Log.e("EditarPerfil", "Error al procesar la altura", e);
                        }

                        // Manejo del peso
                        try {
                            if (usuario.has("peso") && !usuario.isNull("peso")) {
                                String pesoString = usuario.getString("peso");

                                // Verificar que la cadena no esté vacía
                                if (!pesoString.isEmpty()) {
                                    // Intentar convertir la cadena a un número (usando el punto como separador decimal)
                                    try {
                                        double peso = Double.parseDouble(pesoString);
                                        editPeso.setText(String.valueOf(peso));
                                    } catch (NumberFormatException e) {
                                        // Si no se puede parsear, mostrar un mensaje y limpiar el campo
                                        editPeso.setText("");
                                        Log.e("EditarPerfil", "Error al convertir el peso: " + pesoString, e);
                                    }
                                } else {
                                    editPeso.setText("");  // Si la cadena está vacía, limpiar el campo
                                }
                            } else {
                                editPeso.setText("");  // Si no existe el campo o es nulo, limpiar el campo
                            }
                        } catch (Exception e) {
                            // Si ocurre un error inesperado, mostrar un mensaje de error
                            editPeso.setText("");
                            Log.e("EditarPerfil", "Error al procesar el peso", e);
                        }
                    } else {
                        Toast.makeText(EditarperfilActivity.this, "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(EditarperfilActivity.this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(EditarperfilActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarPerfilEnWebService(String correo, String nombre, String genero, String altura, String peso) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("correo", correo);
        params.put("nombres", nombre);
        params.put("genero", genero);
        params.put("altura", altura);
        params.put("peso", peso);

        String url = "http://renovaapp.atwebpages.com/Services/Editar_usuario.php";

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    Toast.makeText(EditarperfilActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (success) {
                        // Puedes regresar o cerrar pantalla si deseas
                        Intent intent = new Intent(EditarperfilActivity.this, ConfiguracionActivity.class);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    Toast.makeText(EditarperfilActivity.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(EditarperfilActivity.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
