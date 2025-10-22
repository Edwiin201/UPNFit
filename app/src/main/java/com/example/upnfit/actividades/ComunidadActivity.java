package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.upnfit.R;

import java.util.ArrayList;
import java.util.List;

public class ComunidadActivity extends AppCompatActivity {

    private LinearLayout publicacionesContainer; // Contenedor donde se mostrarán las publicaciones
    private List<LinearLayout> publicacionesList; // Lista para almacenar las publicaciones dinámicas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_comunidad);

        publicacionesContainer = findViewById(R.id.publicacionesContainer); // Asegúrate de tener un contenedor en el XML
        publicacionesList = new ArrayList<>();

        // Botón para ir a MenuActivity
        Button btnMenu = findViewById(R.id.inicioButton);
        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(ComunidadActivity.this, MenuActivity.class);
            startActivity(intent);
        });

        // Botón para ir a NutricionActivity
        Button btnNutricion = findViewById(R.id.nutricionButton);
        btnNutricion.setOnClickListener(v -> {
            Intent intent = new Intent(ComunidadActivity.this, NutricionActivity.class);
            startActivity(intent);
        });

        // Botón para ir a ActividadfisicaActivity
        Button btnActividad = findViewById(R.id.ejercicioButton);
        btnActividad.setOnClickListener(v -> {
            Intent intent = new Intent(ComunidadActivity.this, ActividadfisicaActivity.class);
            startActivity(intent);
        });

        // Botón para ir a SaludmentalActivity
        Button btnMental = findViewById(R.id.mentalButton);
        btnMental.setOnClickListener(v -> {
            Intent intent = new Intent(ComunidadActivity.this, SaludmentalActivity.class);
            startActivity(intent);
        });

        // Botón flotante para agregar publicación
        FloatingActionButton btnAgregarPublicacion = findViewById(R.id.btnAgregarPublicacion);
        btnAgregarPublicacion.setOnClickListener(v -> {
            // Crear una nueva intención para abrir NuevapublicacionActivity
            Intent intent = new Intent(ComunidadActivity.this, NuevapublicacionActivity.class);
            startActivity(intent);
        });

        // Verificar si se ha recibido un mensaje de la actividad de nueva publicación
        Intent intent = getIntent();
        String mensaje = intent.getStringExtra("mensaje");
        String nombre = intent.getStringExtra("nombre");
        String hora = intent.getStringExtra("hora");

        if (mensaje != null && !mensaje.isEmpty()) {
            // Crear una nueva vista para mostrar la publicación
            agregarPublicacion(mensaje, nombre, hora);
        }
    }

    private void agregarPublicacion(String mensaje, String nombre, String hora) {
        // Crear una nueva vista de publicación
        LinearLayout publicacionView = new LinearLayout(this);
        publicacionView.setOrientation(LinearLayout.VERTICAL);
        publicacionView.setPadding(12, 12, 12, 12);
        publicacionView.setBackgroundResource(R.drawable.redondeado); // Aplicar estilo al fondo

        // Encabezado de la publicación con nombre y hora
        LinearLayout encabezado = new LinearLayout(this);
        encabezado.setOrientation(LinearLayout.HORIZONTAL);
        encabezado.setPadding(8, 8, 8, 8);

        // Avatar (nombre iniciales)
        TextView avatar = new TextView(this);
        avatar.setText(nombre != null && !nombre.isEmpty() ? String.valueOf(nombre.charAt(0)) : "U"); // Primer letra del nombre
        avatar.setBackgroundResource(R.drawable.circulo_azul);
        avatar.setGravity(android.view.Gravity.CENTER);
        avatar.setTextColor(getResources().getColor(android.R.color.white));
        avatar.setTextSize(16);
        avatar.setPadding(12, 12, 12, 12);

        // Nombre del usuario
        TextView nombreUsuario = new TextView(this);
        nombreUsuario.setText(nombre); // Mostrar nombre del usuario
        nombreUsuario.setTextSize(14);
        nombreUsuario.setTextColor(getResources().getColor(android.R.color.black));
        nombreUsuario.setPadding(8, 0, 0, 0);

        // Hora de la publicación
        TextView tiempoPublicacion = new TextView(this);
        tiempoPublicacion.setText(hora); // Mostrar la hora de la publicación
        tiempoPublicacion.setTextSize(12);
        tiempoPublicacion.setTextColor(getResources().getColor(android.R.color.darker_gray));
        tiempoPublicacion.setPadding(8, 0, 0, 0);

        // Agregar todos los elementos al encabezado
        encabezado.addView(avatar);
        encabezado.addView(nombreUsuario);
        encabezado.addView(tiempoPublicacion);

        // Agregar el encabezado a la vista de la publicación
        publicacionView.addView(encabezado);

        // Mostrar el mensaje de la publicación
        TextView textoPublicacion = new TextView(this);
        textoPublicacion.setText(mensaje); // El texto recibido
        textoPublicacion.setTextSize(14);
        textoPublicacion.setTextColor(getResources().getColor(android.R.color.black));
        publicacionView.addView(textoPublicacion);

        // Botón de eliminación
        Button btnEliminar = new Button(this);
        btnEliminar.setText("Eliminar");
        btnEliminar.setOnClickListener(v -> {
            // Eliminar la publicación
            publicacionesContainer.removeView(publicacionView);
            publicacionesList.remove(publicacionView); // Eliminar de la lista de publicaciones
            Toast.makeText(this, "Publicación eliminada", Toast.LENGTH_SHORT).show();
        });

        // Agregar el botón de eliminación
        publicacionView.addView(btnEliminar);

        // Agregar la publicación al contenedor principal
        publicacionesContainer.addView(publicacionView);
        publicacionesList.add(publicacionView); // Agregar a la lista de publicaciones
    }
}
