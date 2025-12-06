package com.example.upnfit.fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuFragment extends Fragment {

    private TextView textoBienvenida;
    private AppCompatButton btnPerfil;
    private TextView txtObjetivos;
    private TextView tvConsejoDia;
    private RequestQueue requestQueue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla el layout del fragmento
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa la cola de Volley
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(requireContext());
        }

        // 1. Referencias UI (Desde la vista inflada 'view')
        textoBienvenida = view.findViewById(R.id.textoBienvenida);
        btnPerfil = view.findViewById(R.id.btnmenuPerfil);
        txtObjetivos = view.findViewById(R.id.txtObjetivosMenu);
        tvConsejoDia = view.findViewById(R.id.tvConsejoDia);

        //  2. Lógica de Bienvenida y Perfil
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
        int usuarioID = sharedPreferences.getInt("usuarioID", 0);

        if (usuarioID == 0) {
            textoBienvenida.setText("¡Buen día, Usuario!");
            btnPerfil.setText("US");
        } else {
            obtenerPrimerNombreDesdeBD(usuarioID);
        }

        //  3. Mostrar objetivos guardados
        String objetivo1 = sharedPreferences.getString("objetivo1", "");
        String objetivo2 = sharedPreferences.getString("objetivo2", "");

        if (!objetivo1.isEmpty() && !objetivo2.isEmpty()) {
            txtObjetivos.setText("Tus objetivos: " + objetivo1 + ", " + objetivo2);
        } else if (!objetivo1.isEmpty()) {
            txtObjetivos.setText("Tu objetivo: " + objetivo1);
        } else {
            txtObjetivos.setText("Tus objetivos: (no seleccionados)");
        }

        // --- 4. Cargar consejo del día ---
        cargarConsejoDelDia();


    }

    // El resto de los métodos se trasladan directamente:

    // Obtener nombre desde la BD
    private void obtenerPrimerNombreDesdeBD(int usuarioID) {
        String url = "http://upnfit.atwebpages.com/upnfit/obtener_datos_usuario.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONObject json = new JSONObject(response);
                int codigo = json.optInt("Codigo", 0);

                if (codigo == 1) {
                    String nombreCompleto = json.optString("NombreCompleto", "Usuario");
                    String primerNombre = nombreCompleto.split("\\s+")[0];

                    textoBienvenida.setText("¡Buen día, " + primerNombre + "!");
                    String iniciales = obtenerIniciales(nombreCompleto);
                    btnPerfil.setText(iniciales);
                } else {
                    textoBienvenida.setText("¡Buen día, Usuario!");
                }

            } catch (JSONException e) {
                textoBienvenida.setText("¡Buen día, Usuario!");
                Log.e("MenuFragment", "JSON Parsing Error: " + e.getMessage());
            }

        }, error -> {
            Log.e("MenuFragment", "Error de conexión: " + error.toString());
            textoBienvenida.setText("¡Buen día, Usuario!");
            Toast.makeText(requireContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        requestQueue.add(request);
    }

    // Obtener iniciales
    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "US";

        String[] partes = nombreCompleto.trim().split("\\s+");
        String inicialNombre = partes.length >= 1 ? partes[0].substring(0, 1).toUpperCase() : "";
        String inicialApellido = partes.length >= 2 ? partes[1].substring(0, 1).toUpperCase() : "";

        return inicialNombre + inicialApellido;
    }

    // Consejo del día
    private void cargarConsejoDelDia() {
        String url = "http://renovaapp.atwebpages.com/Services/Consejo_diario.php";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject consejo = response.getJSONObject("data");
                            String titulo = consejo.getString("titulo");
                            String contenido = consejo.getString("contenido");
                            tvConsejoDia.setText("💡 " + titulo + ":\n" + contenido);
                        } else {
                            tvConsejoDia.setText("💡 Hoy es un buen día para empezar algo nuevo.");
                        }
                    } catch (JSONException e) {
                        tvConsejoDia.setText("💡 Consejo no disponible por el momento.");
                    }
                },
                error -> {
                    Log.e("API_ERROR", "Error al obtener el consejo");
                    tvConsejoDia.setText("💡 Error al obtener el consejo.");
                });

        requestQueue.add(request);
    }
}