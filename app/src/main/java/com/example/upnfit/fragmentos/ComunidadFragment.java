package com.example.upnfit.fragmentos;

import android.database.Cursor;
import android.graphics.Color;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;
import com.example.upnfit.actividades.NuevapublicacionActivity;
import com.example.upnfit.sqlite.PublicacionesDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComunidadFragment extends Fragment {

    private LinearLayout publicacionesContainer;
    private List<LinearLayout> publicacionesList;
    private PublicacionesDB db; // Instancia de la base de datos

    private final String URL_PUBLICACIONES =
            "http://upnfit.atwebpages.com/upnfit/obtener_publicaciones.php";

    RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comunidad, container, false);

        publicacionesContainer = view.findViewById(R.id.publicacionesContainer);
        publicacionesList = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(requireContext());
        db = new PublicacionesDB(requireContext()); // Inicializamos la DB

        // Botón agregar publicación
        FloatingActionButton btnAgregar = view.findViewById(R.id.btnAgregarPublicacion);
        btnAgregar.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), NuevapublicacionActivity.class));
        });

        // 🛑 ESTRATEGIA DE CACHE: 1. Carga Rápida Local
        cargarPublicacionesLocal();

        // 🛑 ESTRATEGIA DE CACHE: 2. Sincronización en segundo plano (refresca la cache)
        cargarPublicacionesBD();

        return view;
    }

    /**
     * Carga publicaciones desde la caché SQLite (carga instantánea).
     */
    private void cargarPublicacionesLocal() {
        Cursor cursor = db.obtenerPublicaciones();
        if (cursor != null && cursor.moveToFirst()) {
            publicacionesContainer.removeAllViews(); // Limpia la vista antes de cargar

            do {
                try {
                    // Extraer datos de la caché SQLite
                    // Usamos la constante de la columna, no el nombre del JSON
                    int idPub = cursor.getInt(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_ID_PUBLICACION));
                    String titulo = cursor.getString(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_TITULO));
                    String contenido = cursor.getString(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_CONTENIDO));
                    String autor = cursor.getString(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_AUTOR));
                    String fecha = cursor.getString(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_FECHA));
                    String categoria = cursor.getString(cursor.getColumnIndexOrThrow(PublicacionesDB.COL_CATEGORIA));

                    agregarPublicacion(contenido, autor, fecha, titulo, categoria);
                } catch (Exception e) {
                    Log.e("ComunidadFragment", "Error al leer datos de la cache: " + e.getMessage());
                }
            } while (cursor.moveToNext());

            cursor.close();
            Log.d("ComunidadFragment", "Publicaciones cargadas desde SQLite.");
        } else {
            Log.d("ComunidadFragment", "Cache de publicaciones vacía. Cargando desde red.");
        }
    }


    /**
     * Carga publicaciones desde el servidor (red) y las guarda en la caché.
     */
    private void cargarPublicacionesBD() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_PUBLICACIONES,
                null,
                response -> {
                    try {
                        if (response.getInt("p_Codigo") != 1) return;

                        JSONArray data = response.getJSONArray("data");

                        // 1. Limpiar cache y vista antes de refrescar con los nuevos datos
                        db.limpiarCache();
                        publicacionesContainer.removeAllViews();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject pub = data.getJSONObject(i);

                            // 🛑 CORRECCIÓN: Usamos "PublicacionID" como clave del JSON
                            int publicacionID = pub.optInt("PublicacionID", 0);
                            String titulo = pub.optString("Titulo", "");
                            String contenido = pub.optString("Contenido", "");
                            String autor = pub.optString("Autor", "Usuario");
                            String fecha = pub.optString("FechaPublicacion", "");
                            String categoria = pub.optString("Categoria", "");

                            // 2. Agregar a la caché SQLite
                            db.guardarPublicacion(publicacionID, titulo, contenido, autor, fecha, categoria);

                            // 3. Agregar a la vista (UI)
                            agregarPublicacion(contenido, autor, fecha, titulo, categoria);
                        }
                        Log.d("ComunidadFragment", "Publicaciones cargadas y cache actualizada desde red.");

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error al procesar publicaciones", Toast.LENGTH_SHORT).show();
                    } finally {
                        // 4. Cerramos la conexión a la base de datos local
                        db.close();
                    }
                },
                error -> {
                    error.printStackTrace();
                    // Si falla la red, ya tenemos los datos de la caché (si existen)
                    Toast.makeText(requireContext(), "Error de conexión. Mostrando datos sin conexión.", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void agregarPublicacion(String contenido, String autor, String fecha, String titulo, String categoria) {
        // --- El método de construcción de la UI permanece sin cambios ---

        LinearLayout publicacionView = new LinearLayout(requireContext());
        publicacionView.setOrientation(LinearLayout.VERTICAL);
        publicacionView.setPadding(16, 16, 16, 16);
        publicacionView.setBackgroundResource(R.drawable.redondeado);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 20);
        publicacionView.setLayoutParams(lp);

        // Encabezado
        LinearLayout encabezado = new LinearLayout(requireContext());
        encabezado.setOrientation(LinearLayout.HORIZONTAL);

        TextView avatar = new TextView(requireContext());
        avatar.setText(autor.isEmpty() ? "U" : autor.substring(0, 1));
        avatar.setBackgroundResource(R.drawable.circulo_azul);
        avatar.setGravity(android.view.Gravity.CENTER);
        avatar.setPadding(16, 16, 16, 16);
        avatar.setTextColor(getResources().getColor(android.R.color.white)); // Blanco OK

        LinearLayout datos = new LinearLayout(requireContext());
        datos.setOrientation(LinearLayout.VERTICAL);
        datos.setPadding(12, 0, 0, 0);

        TextView autorTxt = new TextView(requireContext());
        autorTxt.setText(autor);
        autorTxt.setTextSize(16);
        autorTxt.setTextColor(Color.parseColor("#000000"));

        TextView fechaTxt = new TextView(requireContext());
        fechaTxt.setText(fecha);
        fechaTxt.setTextSize(12);
        fechaTxt.setTextColor(Color.parseColor("#000000"));

        datos.addView(autorTxt);
        datos.addView(fechaTxt);

        encabezado.addView(avatar);
        encabezado.addView(datos);

        publicacionView.addView(encabezado);

        // Título
        if (!titulo.isEmpty()) {
            TextView tvTitulo = new TextView(requireContext());
            tvTitulo.setText(titulo);
            tvTitulo.setTextSize(17);
            tvTitulo.setPadding(0, 10, 0, 10);
            tvTitulo.setTextColor(Color.parseColor("#000000"));
            publicacionView.addView(tvTitulo);
        }

        // Contenido
        TextView tvContenido = new TextView(requireContext());
        tvContenido.setText(contenido);
        tvContenido.setTextSize(14);
        tvContenido.setTextColor(Color.parseColor("#000000"));
        publicacionView.addView(tvContenido);

        // Categoría
        if (!categoria.isEmpty()) {
            TextView tvCat = new TextView(requireContext());
            tvCat.setText("Categoría: " + categoria);
            tvCat.setTextSize(12);
            tvCat.setTextColor(Color.parseColor("#000000"));
            publicacionView.addView(tvCat);
        }

        publicacionesContainer.addView(publicacionView);
        publicacionesList.add(publicacionView);
    }
}