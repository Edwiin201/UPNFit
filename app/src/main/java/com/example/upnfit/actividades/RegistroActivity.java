package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity {

    private EditText txtNombre, txtCorreo, txtClave, txtConfirmarClave;

    // Regex para validar correos institucionales @upn.pe
    private static final Pattern UPN_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@upn\\.pe$", Pattern.CASE_INSENSITIVE);

    // URL del PHP en tu servidor local
    private static final String URL_REGISTRO = "http://10.0.2.2/upnfit/usuarios.php";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        txtNombre = findViewById(R.id.regTxtNombre);
        txtCorreo = findViewById(R.id.regTxtCorreo);
        txtClave = findViewById(R.id.regTxtClave);
        txtConfirmarClave = findViewById(R.id.regTxtConfirmarClave);
        Button btnContinuar = findViewById(R.id.regBtnContinuar);

        requestQueue = Volley.newRequestQueue(this);

        btnContinuar.setOnClickListener(v -> {
            String nombre = safeTrim(txtNombre.getText().toString());
            String correoRaw = safeTrim(txtCorreo.getText().toString());
            String correo = normalizeEmail(correoRaw);
            String clave = txtClave.getText().toString();
            String confirmarClave = txtConfirmarClave.getText().toString();

            // Validaciones básicas
            if (nombre.isEmpty() || correo.isEmpty() || clave.isEmpty() || confirmarClave.isEmpty()) {
                Toast.makeText(this, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!UPN_EMAIL.matcher(correo).matches()) {
                txtCorreo.setError("Usa tu correo institucional @upn.pe");
                txtCorreo.requestFocus();
                return;
            }

            if (!clave.equals(confirmarClave)) {
                txtConfirmarClave.setError("Las contraseñas no coinciden");
                txtConfirmarClave.requestFocus();
                return;
            }

            if (clave.length() < 6) {
                txtClave.setError("La contraseña debe tener al menos 6 caracteres");
                txtClave.requestFocus();
                return;
            }

            // Enviar datos al servidor
            enviarRegistroServidor(nombre, correo, clave);
        });
    }

    private void enviarRegistroServidor(String nombre, String correo, String clave) {
        final String nombreFinal = nombre;
        final String correoFinal = correo;
        final String claveFinal = clave;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGISTRO,
                response -> {
                    Log.d("RegistroResponse", response);
                    try {
                        JSONObject json = new JSONObject(response);
                        int codigo = json.optInt("p_Codigo", 0);
                        String mensaje = json.optString("p_Mensaje", "Sin mensaje");

                        if (codigo == 1) {
                            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                            // 🧠 Extraer el ID de usuario que devuelve el servidor (si lo hay)
                            int usuarioID = json.optInt("usuarioID", 0);
                            Log.d("DEBUG_ID", "Usuario ID guardado: " + usuarioID);

                            // Guardar en SharedPreferences
                            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("nombre", nombreFinal);
                            editor.putString("correo", correoFinal);
                            editor.putInt("usuarioID", usuarioID); // ✅ GUARDAR ID DEL USUARIO
                            editor.apply();

                            // Redirigir a la siguiente pantalla
                            Intent intent = new Intent(RegistroActivity.this, GeneroActivity.class);
                            intent.putExtra("nombre", nombreFinal);
                            intent.putExtra("correo", correoFinal);
                            intent.putExtra("contrasena", claveFinal);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al interpretar la respuesta del servidor", Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    String mensajeError = (error.getMessage() != null) ? error.getMessage() : "Error desconocido";
                    Log.e("VolleyError", "Error de conexión: " + mensajeError, error);
                    Toast.makeText(this, "Error de conexión: " + mensajeError, Toast.LENGTH_LONG).show();
                }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombreCompleto", nombreFinal);
                params.put("email", correoFinal);
                params.put("password", claveFinal);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    // Métodos auxiliares
    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }
}
