package com.example.upnfit.fragmentos;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ComunidadFragment extends Fragment {

    private LinearLayout publicacionesContainer;
    private List<LinearLayout> publicacionesList;

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

        // Botón agregar publicación
        FloatingActionButton btnAgregar = view.findViewById(R.id.btnAgregarPublicacion);
        btnAgregar.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), NuevapublicacionActivity.class));
        });

        // Cargar publicaciones
        cargarPublicacionesBD();

        return view;
    }

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
                        Toast.makeText(requireContext(), "Error al cargar publicaciones", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
        );

        requestQueue.add(request);
    }

    private void agregarPublicacion(String contenido, String autor, String fecha, String titulo, String categoria) {

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
        avatar.setTextColor(getResources().getColor(android.R.color.white));

        LinearLayout datos = new LinearLayout(requireContext());
        datos.setOrientation(LinearLayout.VERTICAL);
        datos.setPadding(12, 0, 0, 0);

        TextView autorTxt = new TextView(requireContext());
        autorTxt.setText(autor);
        autorTxt.setTextSize(16);

        TextView fechaTxt = new TextView(requireContext());
        fechaTxt.setText(fecha);
        fechaTxt.setTextSize(12);

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
            publicacionView.addView(tvTitulo);
        }

        // Contenido
        TextView tvContenido = new TextView(requireContext());
        tvContenido.setText(contenido);
        tvContenido.setTextSize(14);
        publicacionView.addView(tvContenido);

        // Categoría
        if (!categoria.isEmpty()) {
            TextView tvCat = new TextView(requireContext());
            tvCat.setText("Categoría: " + categoria);
            tvCat.setTextSize(12);
            publicacionView.addView(tvCat);
        }

        publicacionesContainer.addView(publicacionView);
        publicacionesList.add(publicacionView);
    }
}
