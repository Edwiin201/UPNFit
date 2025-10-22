package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;
import com.example.upnfit.fragmentos.TerminosFragment;
import com.example.upnfit.sqlite.Renovaapp;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ContratoActivity extends AppCompatActivity {

    final boolean[] yaVioTerminos = {false};

    private ImageButton btnContrato;
    private CheckBox checkBoxContrato;
    private Renovaapp dbHelper;

    // Datos recibidos
    private String nombre, correo, contrasena, genero, objetivo1, objetivo2;
    private float altura;
    private int peso;
    private static final String URL_REGISTRO = "http://renovaapp.atwebpages.com/Services/Registrar_usuario.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrato);

        btnContrato = findViewById(R.id.btncontrato);
        checkBoxContrato = findViewById(R.id.checkBoxContrato);
        dbHelper = new Renovaapp(this);

        // Obtener datos del intent
        nombre = getIntent().getStringExtra("nombre");
        correo = getIntent().getStringExtra("correo");
        contrasena = getIntent().getStringExtra("contrasena");
        genero = getIntent().getStringExtra("genero");
        altura = getIntent().getFloatExtra("altura", 0f);  // ¡esto ya es seguro!
        peso = getIntent().getIntExtra("peso", 0);
        objetivo1 = getIntent().getStringExtra("objetivo1");
        objetivo2 = getIntent().getStringExtra("objetivo2");

        // Mostrar contrato personalizado
        mostrarContratoConNombreYObjetivos(nombre);

        checkBoxContrato.setOnClickListener(v -> {
            if (!yaVioTerminos[0]) {
                TerminosFragment terminosFragment = new TerminosFragment(() -> {
                    yaVioTerminos[0] = true;
                    checkBoxContrato.setChecked(true);
                });
                terminosFragment.show(getSupportFragmentManager(), "Terminos");
                checkBoxContrato.setChecked(false);
            }
        });

        btnContrato.setOnClickListener(v -> {
            if (!checkBoxContrato.isChecked()) {
                Toast.makeText(this, "Debes aceptar el contrato para continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obtener objetivos desde SharedPreferences
            SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
            String objetivo1 = prefs.getString("objetivo1", "");
            String objetivo2 = prefs.getString("objetivo2", "");

            // Guardar usuario localmente con objetivos
            boolean agregado = dbHelper.agregarUsuarioCompleto(nombre, correo, contrasena, genero, altura, peso);

            // También puedes registrarlo en la nube (opcional)
            registrarUsuarioEnNube(nombre, correo, contrasena, genero, altura, peso, objetivo1, objetivo2);

            if (agregado) {
                // Guardar el correo en SharedPreferences para validación en bienvenida
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("correo", correo);
                editor.apply();

                // Ir directamente a BienvenidaActivity y limpiar la pila
                Intent intent = new Intent(ContratoActivity.this, BienvenidaActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar usuario localmente. Intenta de nuevo.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registrarUsuarioEnNube(String nombre, String correo, String clave,
                                        String genero, float altura, int peso,
                                        String objetivo1, String objetivo2) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("nombres", nombre);
        params.put("correo", correo);
        params.put("contrasena", clave);
        params.put("genero", genero);
        params.put("altura", altura);
        params.put("peso", peso);
        params.put("objetivo1", objetivo1);
        params.put("objetivo2", objetivo2);
        params.put("foto_perfil","");

        client.post(URL_REGISTRO, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Toast.makeText(ContratoActivity.this,
                            response.getString("message"),
                            Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    Toast.makeText(ContratoActivity.this,
                            "Error al procesar respuesta.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Toast.makeText(ContratoActivity.this,
                        "Error: " + throwable.getMessage(),
                        Toast.LENGTH_LONG).show();
                throwable.printStackTrace();
            }
        });
    }

    private void mostrarContratoConNombreYObjetivos(String nombreCompleto) {
        TextView textoContrato = findViewById(R.id.contract_text);

        // Obtener primer nombre
        String primerNombre = obtenerPrimerNombre(nombreCompleto);

        // Obtener objetivos desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String objetivo1 = prefs.getString("objetivo1", "");
        String objetivo2 = prefs.getString("objetivo2", "");

        String objetivosTexto;
        if (!objetivo1.isEmpty() && !objetivo2.isEmpty()) {
            objetivosTexto = objetivo1 + " y " + objetivo2;
        } else if (!objetivo1.isEmpty()) {
            objetivosTexto = objetivo1;
        } else {
            objetivosTexto = "mis objetivos";
        }

        String texto = "Yo, [ Carlos ], me comprometo a seguir mi programa de Renova, para alcanzar mis objetivos... \n" +
                "Al firmar este compromiso, declaro que:\n" +
                "-Asumo la responsabilidad de mi bienestar y progreso.\n" +
                "-Me comprometo a cumplir con las recomendaciones y rutinas propuestas en la aplicación.\n" +
                "-Entiendo que los resultados dependen de mi constancia y dedicación.\n" +
                "-Buscaré ayuda profesional si presento dudas o dificultades durante el proceso.\n" +
                "-Seré honesto conmigo mismo sobre mis avances y desafíos.\n" +
                "-Celebraré mis logros y aprenderé de los obstáculos.\n" +
                "-Reconozco que este compromiso es conmigo mismo y mi salud futura.";

        texto = texto.replace("[ Carlos ]", primerNombre);
        texto = texto.replace("mis objetivos", objetivosTexto);

        textoContrato.setText(texto);
    }

    private String obtenerPrimerNombre(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "Usuario";
        String[] partes = nombreCompleto.trim().split("\\s+");
        return partes.length >= 1 ? partes[0] : nombreCompleto;
    }
}
