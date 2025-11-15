package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.upnfit.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComunidadActivity extends AppCompatActivity {

    private LinearLayout publicacionesContainer;
    private List<LinearLayout> publicacionesList;

    private final String URL_PUBLICACIONES =
            "http://upnfit.atwebpages.com/upnfit/obtener_publicaciones.php";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comunidad);

        publicacionesContainer = findViewById(R.id.publicacionesContainer);
        publicacionesList = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        // ======================
        // BOTONES INFERIORES
        // ======================
        findViewById(R.id.inicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class))
        );
        findViewById(R.id.nutricionButton).setOnClickListener(v ->
                startActivity(new Intent(this, NutricionActivity.class))
        );
        findViewById(R.id.ejercicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadfisicaActivity.class))
        );
        findViewById(R.id.mentalButton).setOnClickListener(v ->
                startActivity(new Intent(this, SaludmentalActivity.class))
        );

        // FAB NUEVA PUBLICACIÓN
        FloatingActionButton btnAgregarPublicacion = findViewById(R.id.btnAgregarPublicacion);
        btnAgregarPublicacion.setOnClickListener(v -> {
            Intent intent = new Intent(ComunidadActivity.this, NuevapublicacionActivity.class);
            startActivity(intent);
        });

        // ======================
        // CARGAR PUBLICACIONES DEL PHP
        // ======================
        cargarPublicacionesBD();

        // ======================
        // SI VIENE UNA PUBLICACIÓN NUEVA LOCAL
        // ======================
        Intent intent = getIntent();
        String mensaje = intent.getStringExtra("mensaje");
        String nombre = intent.getStringExtra("nombre");
        String hora = intent.getStringExtra("hora");
        String titulo = intent.getStringExtra("titulo");
        String categoria = intent.getStringExtra("categoria");

        if (mensaje != null && !mensaje.isEmpty()) {
            agregarPublicacion(mensaje, nombre, hora, titulo, categoria);
        }
    }

    // =====================================================
    // 🔹 CARGAR PUBLICACIONES DESDE EL PHP (VOLLEY)
    // =====================================================
    private void cargarPublicacionesBD() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_PUBLICACIONES,
                null,
                response -> {
                    try {
                        if (response.getInt("p_Codigo") != 1) return;

                        JSONArray data = response.getJSONArray("data");

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject pub = data.getJSONObject(i);

                            String titulo = pub.optString("Titulo", "");
                            String contenido = pub.optString("Contenido", "");
                            String autor = pub.optString("Autor", "Usuario");
                            String fecha = pub.optString("FechaPublicacion", "");
                            String categoria = pub.optString("Categoria", "");

                            agregarPublicacion(contenido, autor, fecha, titulo, categoria);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    // =====================================================
    // 🔹 DIBUJAR PUBLICACIÓN (REUTILIZA TU MISMO DISEÑO)
    // =====================================================
    private void agregarPublicacion(String contenido, String autor, String fecha, String titulo, String categoria) {

        // Contenedor tipo tarjeta para cada publicación
        LinearLayout publicacionView = new LinearLayout(this);
        publicacionView.setOrientation(LinearLayout.VERTICAL);
        publicacionView.setPadding(16, 16, 16, 16);
        publicacionView.setBackgroundResource(R.drawable.redondeado); // fondo redondeado

        // Márgenes entre publicaciones
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 20); // separación vertical
        publicacionView.setLayoutParams(layoutParams);

        // ======================
        // ENCABEZADO (avatar, autor, fecha)
        // ======================
        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.HORIZONTAL);
        encabezado.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Avatar
        TextView avatar = new TextView(this);
        avatar.setText(autor != null && !autor.isEmpty() ? String.valueOf(autor.charAt(0)) : "U");
        avatar.setBackgroundResource(R.drawable.circulo_azul);
        avatar.setGravity(android.view.Gravity.CENTER);
        avatar.setTextColor(getResources().getColor(android.R.color.white));
        avatar.setTextSize(16);
        avatar.setPadding(16, 16, 16, 16);

        // Nombre y fecha
        LinearLayout nombreFecha = new LinearLayout(this);
        nombreFecha.setOrientation(LinearLayout.VERTICAL);
        nombreFecha.setPadding(12, 0, 0, 0);

        TextView nombreUsuario = new TextView(this);
        nombreUsuario.setText(autor);
        nombreUsuario.setTextColor(getResources().getColor(android.R.color.black));
        nombreUsuario.setTextSize(15);
        nombreUsuario.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView tiempoPublicacion = new TextView(this);
        tiempoPublicacion.setText(fecha);
        tiempoPublicacion.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tiempoPublicacion.setTextSize(12);

        nombreFecha.addView(nombreUsuario);
        nombreFecha.addView(tiempoPublicacion);

        encabezado.addView(avatar);
        encabezado.addView(nombreFecha);

        publicacionView.addView(encabezado);

        // ======================
        // TÍTULO
        // ======================
        if (!titulo.isEmpty()) {
            TextView tvTitulo = new TextView(this);
            tvTitulo.setText(titulo);
            tvTitulo.setTextSize(16);
            tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD); // negrita
            tvTitulo.setTextColor(getResources().getColor(android.R.color.black));
            tvTitulo.setPadding(0, 12, 0, 8); // separación superior e inferior
            publicacionView.addView(tvTitulo);
        }

        // ======================
        // CONTENIDO
        // ======================
        TextView textoPublicacion = new TextView(this);
        textoPublicacion.setText(contenido);
        textoPublicacion.setTextSize(14);
        textoPublicacion.setTextColor(getResources().getColor(R.color.gris_upn)); // color similar al XML
        textoPublicacion.setPadding(0, 4, 0, 4);
        publicacionView.addView(textoPublicacion);

        // ======================
        // CATEGORÍA
        // ======================
        if (!categoria.isEmpty()) {
            TextView tvCategoria = new TextView(this);
            tvCategoria.setText("Categoría: " + categoria);
            tvCategoria.setTextSize(12);
            tvCategoria.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            tvCategoria.setPadding(0, 4, 0, 0);
            publicacionView.addView(tvCategoria);
        }

        // ======================
        // AGREGAR AL CONTENEDOR
        // ======================
        publicacionesContainer.addView(publicacionView);
        publicacionesList.add(publicacionView);
    }

}
