package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.upnfit.MenuLateralFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuActivity extends AppCompatActivity {

    private TextView textoBienvenida;
    private AppCompatButton btnPerfil;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🌓 APLICAR modo oscuro o claro guardado
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

        // Recuperar el ID y Rol del usuario logueado
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = sharedPreferences.getInt("usuarioID", 0);
        userRole = sharedPreferences.getString("rol", "Apoderado");

        if (usuarioID == 0) {
            textoBienvenida.setText("¡Buen día, Usuario!");
        } else if (usuarioID == 999) {
            textoBienvenida.setText("¡Buen día, Familia Pérez!");
            btnPerfil.setText("FP");
        } else if (usuarioID == 888) {
            textoBienvenida.setText("¡Buen día, Prof. García!");
            btnPerfil.setText("PG");
        } else {
            obtenerPrimerNombreDesdeBD(usuarioID);
        }

        configurarMenuSegunRol();

        // 🔹 Perfil
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        }

        // 🔹 Configuración (Engranaje)
        ImageButton btnConfiguracion = findViewById(R.id.configuracion);
        if (btnConfiguracion != null) {
            btnConfiguracion.setOnClickListener(v -> startActivity(new Intent(this, ConfiguracionActivity.class)));
        }
    }

    private void configurarMenuSegunRol() {
        // Resumen Card
        TextView tvTituloResumen = findViewById(R.id.tvTituloResumen);
        TextView lblPromedio = findViewById(R.id.lblPromedio);
        TextView tvValorPromedio = findViewById(R.id.tvValorPromedio);
        TextView lblAsistencia = findViewById(R.id.lblAsistencia);
        TextView tvValorAsistencia = findViewById(R.id.tvValorAsistencia);
        TextView lblCursos = findViewById(R.id.lblCursos);
        TextView tvValorCursos = findViewById(R.id.tvValorCursos);

        // Grid Cards
        TextView tvCardNotas = findViewById(R.id.tvCardNotas);
        TextView tvSubCardNotas = findViewById(R.id.tvSubCardNotas);
        ImageView imgCardNotas = findViewById(R.id.imgCardNotas);

        TextView tvCardTareas = findViewById(R.id.tvCardTareas);
        TextView tvSubCardTareas = findViewById(R.id.tvSubCardTareas);

        TextView tvCardMensajes = findViewById(R.id.tvCardMensajes);
        TextView tvSubCardMensajes = findViewById(R.id.tvSubCardMensajes);

        TextView tvCardExamenes = findViewById(R.id.tvCardExamenes);
        TextView tvSubCardExamenes = findViewById(R.id.tvSubCardExamenes);

        TextView tvCardAsistencia = findViewById(R.id.tvCardAsistencia);
        TextView tvSubCardAsistencia = findViewById(R.id.tvSubCardAsistencia);

        View cardNotas = findViewById(R.id.cardNotas);
        View cardTareas = findViewById(R.id.cardTareas);
        View cardMensajes = findViewById(R.id.cardMensajes);
        View cardExamenes = findViewById(R.id.cardExamenes);
        View cardAsistenciaMenu = findViewById(R.id.cardAsistenciaMenu);
        View cardHorarios = findViewById(R.id.cardHorarios);

        if (userRole.equals("Profesor")) {
            // UI para PROFESOR
            tvTituloResumen.setText("Panel de Control Docente");
            lblPromedio.setText("PROM. GRUPAL");
            tvValorPromedio.setText("16.2");
            lblAsistencia.setText("ALUMNOS");
            tvValorAsistencia.setText("32");
            lblCursos.setText("SECCIONES");
            tvValorCursos.setText("04");

            tvCardNotas.setText("Gestionar Notas");
            tvSubCardNotas.setText("Subir y editar notas");
            imgCardNotas.setImageResource(android.R.drawable.ic_menu_edit);

            tvCardTareas.setText("Asignar Tareas");
            tvSubCardTareas.setText("Publicar actividades");

            tvCardMensajes.setText("Mensajes");
            tvSubCardMensajes.setText("Atención a padres");

            tvCardExamenes.setText("Programar Exámenes");
            tvSubCardExamenes.setText("Gestionar fechas");

            tvCardAsistencia.setText("Pasar Asistencia");
            tvSubCardAsistencia.setText("Registro diario");

            // Click Listeners Profesor
            cardNotas.setOnClickListener(v -> startActivity(new Intent(this, GestionarNotasActivity.class)));
            cardTareas.setOnClickListener(v -> startActivity(new Intent(this, TareasActivity.class)));
            cardMensajes.setOnClickListener(v -> startActivity(new Intent(this, MensajeriaActivity.class)));
            cardAsistenciaMenu.setOnClickListener(v -> startActivity(new Intent(this, PasarAsistenciaActivity.class)));
            cardExamenes.setOnClickListener(v -> startActivity(new Intent(this, ExamenesActivity.class)));
        } else {
            // UI para APODERADO / PADRE
            tvTituloResumen.setText("Resumen del Alumno");
            lblPromedio.setText("PROMEDIO");
            tvValorPromedio.setText("18.5");
            lblAsistencia.setText("ASISTENCIA");
            tvValorAsistencia.setText("95%");
            lblCursos.setText("CURSOS");
            tvValorCursos.setText("06");

            tvCardNotas.setText("Boleta de Notas");
            tvSubCardNotas.setText("Ver récord académico");
            imgCardNotas.setImageResource(android.R.drawable.star_on);

            tvCardTareas.setText("Tareas");
            tvSubCardTareas.setText("Actividades pendientes");

            tvCardMensajes.setText("Mensajes");
            tvSubCardMensajes.setText("Comunicación con profesores");

            tvCardExamenes.setText("Exámenes");
            tvSubCardExamenes.setText("Fechas y resultados");

            tvCardAsistencia.setText("Asistencia");
            tvSubCardAsistencia.setText("Control de faltas");

            // Click Listeners Apoderado
            cardNotas.setOnClickListener(v -> startActivity(new Intent(this, NotasActivity.class)));
            cardTareas.setOnClickListener(v -> startActivity(new Intent(this, TareasActivity.class)));
            cardMensajes.setOnClickListener(v -> startActivity(new Intent(this, MensajeriaActivity.class)));
            cardAsistenciaMenu.setOnClickListener(v -> startActivity(new Intent(this, AsistenciaActivity.class)));
            cardExamenes.setOnClickListener(v -> startActivity(new Intent(this, ExamenesActivity.class)));
        }

        if (cardHorarios != null) {
            cardHorarios.setOnClickListener(v -> startActivity(new Intent(this, CursosActivity.class)));
        }
    }

    private void obtenerPrimerNombreDesdeBD(int usuarioID) {
        String url = "http://upnfit.atwebpages.com/upnfit/obtener_datos_usuario.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                if (response == null || response.trim().startsWith("<")) {
                    Log.e("MenuActivity", "Respuesta no válida del servidor: " + response);
                    textoBienvenida.setText("¡Buen día, Alumno!");
                    return;
                }
                JSONObject json = new JSONObject(response);
                if (json.optInt("Codigo", 0) == 1) {
                    String nombreCompleto = json.optString("NombreCompleto", "Alumno");
                    String primerNombre = nombreCompleto.split("\\s+")[0];
                    textoBienvenida.setText("¡Buen día, " + primerNombre + "!");
                    btnPerfil.setText(obtenerIniciales(nombreCompleto));
                } else {
                    textoBienvenida.setText("¡Buen día, Alumno!");
                }
            } catch (JSONException e) {
                Log.e("MenuActivity", "Error al parsear JSON: " + e.getMessage());
                textoBienvenida.setText("¡Buen día, Alumno!");
            }
        }, error -> {
            Log.e("MenuActivity", "Error de Volley: " + error.toString());
            textoBienvenida.setText("¡Buen día, Alumno!");
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "ST";
        String[] partes = nombreCompleto.trim().split("\\s+");
        String inicialNombre = partes.length >= 1 ? partes[0].substring(0, 1).toUpperCase() : "";
        String inicialApellido = partes.length >= 2 ? partes[1].substring(0, 1).toUpperCase() : "";
        return inicialNombre + inicialApellido;
    }
}
