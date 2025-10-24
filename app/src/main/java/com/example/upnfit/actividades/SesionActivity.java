package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
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
    private TextView lblRegistro;

    // URL del backend PHP (ajústala si usas otro servidor)
    private static final String LOGIN_URL = "http://10.0.2.2/upnfit/login.php";

    // Solo correos UPN
    private static final Pattern UPN_EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@upn\\.pe$", Pattern.CASE_INSENSITIVE);

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
        lblRegistro = findViewById(R.id.seslblRegistro);

        // Ir al registro
        lblRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(SesionActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Iniciar sesión
        btnIniciarSesion.setOnClickListener(v -> {
            String correo = normalizeEmail(txtCorreo.getText().toString());
            String clave = txtClave.getText().toString();

            if (correo.isEmpty() || clave.isEmpty()) {
                Toast.makeText(this, "Ingrese correo y contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                txtCorreo.setError("Correo inválido");
                return;
            }

            if (!isUpnEmail(correo)) {
                txtCorreo.setError("Use su correo institucional @upn.pe");
                Toast.makeText(this, "Solo se aceptan correos @upn.pe", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUsuarioRemoto(correo, clave);
        });
    }

    private static String normalizeEmail(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
    }

    private static boolean isUpnEmail(String email) {
        return UPN_EMAIL.matcher(email).matches();
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
                    int codigo = response.getInt("p_Codigo");
                    String mensaje = response.getString("p_Mensaje");

                    if (codigo == 1) {
                        int usuarioID = response.optInt("UsuarioID", 0);

                        // Guardar datos de sesión
                        SharedPreferences sp = getSharedPreferences("UserData", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("usuarioID", usuarioID);
                        editor.putString("correo", correo);
                        editor.putString("contrasena", contrasena);
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
                Toast.makeText(SesionActivity.this,
                        "Error de conexión: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }
}
