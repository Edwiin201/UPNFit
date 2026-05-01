package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.upnfit.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class SesionActivity extends AppCompatActivity {

    private EditText txtCorreo, txtClave;
    private Button btnIniciarSesion;
    private CheckBox chkRecordar;

    // URL del backend PHP
    private static final String LOGIN_URL = "http://upnfit.atwebpages.com/upnfit/login.php";

    // Validación de correo general para colegios
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion);

        // Ajuste de bordes
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias UI
        txtCorreo = findViewById(R.id.sexTxtCorreo);
        txtClave = findViewById(R.id.sextxtClave);
        btnIniciarSesion = findViewById(R.id.sesBtnIniciarSesion);
        chkRecordar = findViewById(R.id.chkRecordarUsuario);

        // Cargar correo guardado si existe
        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
        if (sp.getBoolean("recordar", false)) {
            txtCorreo.setText(sp.getString("correo_guardado", ""));
            chkRecordar.setChecked(true);
        }

        // Iniciar sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = normalizeEmail(txtCorreo.getText().toString());
            String clave = txtClave.getText().toString();

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar o borrar correo según el CheckBox
            SharedPreferences.Editor editor = sp.edit();
            if (chkRecordar.isChecked()) {
                editor.putString("correo_guardado", correo);
                editor.putBoolean("recordar", true);
            } else {
                editor.remove("correo_guardado");
                editor.putBoolean("recordar", false);
            }
            editor.apply();

            if (!EMAIL_PATTERN.matcher(correo).matches()) {
                txtCorreo.setError("Ingrese un correo electrónico válido");
                return;
            }

            // --- MODO DEMO PARA COLEGIOS ---
            if (correo.equals("familia@colegio.edu.pe") && clave.equals("123456")) {
                SharedPreferences.Editor editorLocal = sp.edit();
                editorLocal.putInt("usuarioID", 999); // ID de prueba
                editorLocal.putString("correo", correo);
                editorLocal.putString("rol", "Apoderado");
                editorLocal.apply();

                Toast.makeText(this, "Modo Apoderado: Acceso concedido", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SesionActivity.this, MenuActivity.class));
                finish();
                return;
            }

            if (correo.equals("profesor@colegio.edu.pe") && clave.equals("123456")) {
                SharedPreferences.Editor editorLocal = sp.edit();
                editorLocal.putInt("usuarioID", 888); // ID de prueba Profesor
                editorLocal.putString("correo", correo);
                editorLocal.putString("rol", "Profesor");
                editorLocal.apply();

                Toast.makeText(this, "Modo Profesor: Acceso concedido", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SesionActivity.this, MenuActivity.class));
                finish();
                return;
            }
            // -------------------------------

            loginUsuarioRemoto(correo, clave);
        });
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isUpnEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    private void loginUsuarioRemoto(String correo, String contrasena) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", correo);
        params.put("password", contrasena);

        client.post(LOGIN_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response == null) {
                        Toast.makeText(SesionActivity.this, "Respuesta vacía del servidor", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int codigo = response.getInt("p_Codigo");
                    String mensaje = response.getString("p_Mensaje");

                    if (codigo == 1) {
                        int usuarioID = response.getInt("UsuarioID");
                        String rol = response.optString("Rol", "Apoderado"); // Por defecto apoderado

                        // Guardar datos de sesión
                        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("usuarioID", usuarioID);
                        editor.putString("correo", correo);
                        editor.putString("contrasena", contrasena);
                        editor.putString("rol", rol);
                        editor.apply();

                        Toast.makeText(SesionActivity.this,
                                "Inicio de sesión exitoso (ID: " + usuarioID + ")",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(SesionActivity.this, MenuActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SesionActivity.this, mensaje, Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SesionActivity.this,
                            "Error al procesar la respuesta del servidor",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                String errorMsg = "Error de conexión";
                if (responseString != null && responseString.contains("<br />")) {
                    errorMsg = "Error en el servidor (PHP)";
                } else if (throwable != null) {
                    errorMsg += ": " + throwable.getMessage();
                }
                Toast.makeText(SesionActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
}
