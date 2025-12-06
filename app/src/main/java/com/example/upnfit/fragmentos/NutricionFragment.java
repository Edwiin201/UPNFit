package com.example.upnfit.fragmentos;
import com.example.upnfit.sqlite.IndicadoresDB;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ComidasFragmet;
import com.example.upnfit.sqlite.AlimentosDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class NutricionFragment extends Fragment {

    private TextView txtIMCValor, txtGrasaValor;
    private TextView tvCaloriasTotal, tvProteinasTotal, tvGrasasTotal, tvCarbsTotal;

    private Map<String, Map<String,Object>> comidasSeleccionadas = new HashMap<>();
    private double imc, grasaPct;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_nutricion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Botones
        Button btnDesayuno = view.findViewById(R.id.btnDesayuno);
        Button btnAlmuerzo = view.findViewById(R.id.btnAlmuerzo);
        Button btnCena = view.findViewById(R.id.btnCena);
        Button btnSnacks = view.findViewById(R.id.btnSnacks);

        // Indicadores
        txtIMCValor = view.findViewById(R.id.txtIMCValor);
        txtGrasaValor = view.findViewById(R.id.txtGrasaValor);

        // Resumen calórico
        tvCaloriasTotal = view.findViewById(R.id.tvCaloriasTotal);
        tvProteinasTotal = view.findViewById(R.id.tvProteinasTotal);
        tvGrasasTotal = view.findViewById(R.id.tvGrasasTotal);
        tvCarbsTotal = view.findViewById(R.id.tvCarbsTotal);

        // Obtener usuario ID
        SharedPreferences prefs = requireContext().getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = prefs.getInt("usuarioID", 0);

        if (usuarioID == 0) {
            Toast.makeText(getContext(), "No se encontró usuario", Toast.LENGTH_SHORT).show();
        } else {

            // 🛑 LÓGICA DE CACHE: 1. Carga Rápida (Local)
            cargarIndicadoresLocales(usuarioID);

            // 🛑 LÓGICA DE CACHE: 2. Sincronización (Red)
            obtenerIndicadores(usuarioID);
        }

        // Listeners
        btnDesayuno.setOnClickListener(v -> mostrarAlimentos("Desayuno"));
        btnAlmuerzo.setOnClickListener(v -> mostrarAlimentos("Almuerzo"));
        btnCena.setOnClickListener(v -> mostrarAlimentos("Cena"));
        btnSnacks.setOnClickListener(v -> mostrarAlimentos("Snacks"));
    }
    // 🛑 NUEVO MÉTODO: Leer desde SQLite para carga rápida
    private void cargarIndicadoresLocales(int usuarioID) {
        IndicadoresDB db = new IndicadoresDB(getContext());
        Cursor cursor = db.obtenerIndicadores(usuarioID);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                // Obtener datos del Cursor (usando nombres de columna de IndicadoresDB)
                double imcLocal = cursor.getDouble(cursor.getColumnIndexOrThrow(IndicadoresDB.COL_IMC));
                double grasaPctLocal = cursor.getDouble(cursor.getColumnIndexOrThrow(IndicadoresDB.COL_GRASA_PCT));

                txtIMCValor.setText(String.format("%.1f", imcLocal));
                txtGrasaValor.setText(String.format("%.1f%%", grasaPctLocal));
                Log.d("NutricionFragment", "Indicadores cargados desde SQLite.");
            } catch (IllegalArgumentException e) {
                Log.e("NutricionFragment", "Error al leer columnas del cursor", e);
                txtIMCValor.setText("Error");
                txtGrasaValor.setText("Error");
            } finally {
                cursor.close();
            }
        } else {
            // Si no hay datos locales, mostrar "Cargando" mientras llega la red
            txtIMCValor.setText("--");
            txtGrasaValor.setText("--");
            Log.d("NutricionFragment", "No se encontraron indicadores locales.");
        }
        db.close(); // Siempre cerrar la conexión a la BD
    }
    private void mostrarAlimentos(String tipo) {
        Map<String,Object> alimento;

        if(comidasSeleccionadas.containsKey(tipo)) {
            alimento = comidasSeleccionadas.get(tipo);
        } else {
            alimento = seleccionarAlimento(tipo);
            if(alimento != null) comidasSeleccionadas.put(tipo, alimento);
        }

        if(alimento != null) {
            String nombre = (String) alimento.get("nombre");
            String preparacion = (String) alimento.get("preparacion");

            ComidasFragmet dialog = new ComidasFragmet(nombre, preparacion);
            dialog.show(getParentFragmentManager(), "ComidaDialog");
        }

        actualizarResumen();
    }

    private Map<String, Object> seleccionarAlimento(String tipo) {

        AlimentosDB db = new AlimentosDB(getContext());
        Cursor cursor = db.obtenerAlimentosPorTipo(tipo);

        if (cursor == null || cursor.getCount() == 0)
            return null;

        Random rnd = new Random();
        int pos = rnd.nextInt(cursor.getCount());
        cursor.moveToPosition(pos);

        Map<String, Object> alimento = new HashMap<>();
        alimento.put("nombre", cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        alimento.put("preparacion", cursor.getString(cursor.getColumnIndexOrThrow("preparacion")));
        alimento.put("calorias", cursor.getDouble(cursor.getColumnIndexOrThrow("calorias")));
        alimento.put("proteinas", cursor.getDouble(cursor.getColumnIndexOrThrow("proteinas")));
        alimento.put("grasas", cursor.getDouble(cursor.getColumnIndexOrThrow("grasas")));
        alimento.put("carbohidratos", cursor.getDouble(cursor.getColumnIndexOrThrow("carbohidratos")));

        cursor.close();
        return alimento;
    }

    private void actualizarResumen() {
        double totalCal = 0, totalPro = 0, totalGrasas = 0, totalCarbs = 0;

        for(Map<String,Object> a : comidasSeleccionadas.values()){
            totalCal += ((Number)a.get("calorias")).doubleValue();
            totalPro += ((Number)a.get("proteinas")).doubleValue();
            totalGrasas += ((Number)a.get("grasas")).doubleValue();
            totalCarbs += ((Number)a.get("carbohidratos")).doubleValue();
        }

        tvCaloriasTotal.setText(String.format("%.0f kcal", totalCal));
        tvProteinasTotal.setText(String.format("%.0f g", totalPro));
        tvGrasasTotal.setText(String.format("%.0f g", totalGrasas));
        tvCarbsTotal.setText(String.format("%.0f g", totalCarbs));
    }

    // 🛑 MÉTODO MODIFICADO: Llamada de red + Sincronización a SQLite
    private void obtenerIndicadores(int usuarioID) {
        String url = "http://upnfit.atwebpages.com/upnfit/obtener_todas_medidas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        int codigo = json.optInt("Codigo",0);

                        if(codigo==1){
                            double imcServidor = json.optDouble("IMC",0);
                            double grasaPctServidor = json.optDouble("GrasaPct",0);

                            // 3. Actualizar la interfaz (en caso de que haya cambios)
                            txtIMCValor.setText(String.format("%.1f", imcServidor));
                            txtGrasaValor.setText(String.format("%.1f%%", grasaPctServidor));

                            // 4. Sincronizar (Guardar) los nuevos datos en la BD local
                            IndicadoresDB db = new IndicadoresDB(getContext());
                            db.guardarIndicadores(usuarioID, imcServidor, grasaPctServidor);
                            db.close();
                            Log.d("NutricionFragment", "Indicadores sincronizados con el servidor.");

                        } else {
                            Log.w("NutricionFragment", "Servidor no devolvió datos, usando cache local.");
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                    }
                },
                error -> {
                    // Si falla la red, el usuario ya vio la información del cache
                    Toast.makeText(getContext(),"Error de conexión, usando datos guardados.",Toast.LENGTH_LONG).show();
                    Log.e("NutricionFragment", "Error de conexión en red.", error);
                }
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}
