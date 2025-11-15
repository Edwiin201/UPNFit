package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ComidasFragmet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NutricionActivity extends AppCompatActivity {

    private TextView txtIMCValor, txtGrasaValor;
    private TextView tvCaloriasTotal, tvProteinasTotal, tvGrasasTotal, tvCarbsTotal;

    private double imc, grasaPct;

    // 🔹 Guardar alimento seleccionado por tipo
    private Map<String, Map<String,Object>> comidasSeleccionadas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutricion);

        // Botones de comida
        Button btnDesayuno = findViewById(R.id.btnDesayuno);
        Button btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        Button btnCena = findViewById(R.id.btnCena);
        Button btnSnacks = findViewById(R.id.btnSnacks);

        // Indicadores
        txtIMCValor = findViewById(R.id.txtIMCValor);
        txtGrasaValor = findViewById(R.id.txtGrasaValor);

        // Resumen calórico
        tvCaloriasTotal = findViewById(R.id.tvCaloriasTotal);
        tvProteinasTotal = findViewById(R.id.tvProteinasTotal);
        tvGrasasTotal = findViewById(R.id.tvGrasasTotal);
        tvCarbsTotal = findViewById(R.id.tvCarbsTotal);

        // Obtener usuarioID
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = prefs.getInt("usuarioID", 0);

        if (usuarioID == 0) {
            Toast.makeText(this, "⚠️ No se encontró el ID del usuario logueado", Toast.LENGTH_SHORT).show();
        } else {
            obtenerIndicadores(usuarioID);
        }

        // Listeners botones comida
        btnDesayuno.setOnClickListener(v -> mostrarAlimentos("Desayuno"));
        btnAlmuerzo.setOnClickListener(v -> mostrarAlimentos("Almuerzo"));
        btnCena.setOnClickListener(v -> mostrarAlimentos("Cena"));
        btnSnacks.setOnClickListener(v -> mostrarAlimentos("Snacks"));

        // Navegación inferior
        findViewById(R.id.inicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, MenuActivity.class)));
        findViewById(R.id.ejercicioButton).setOnClickListener(v ->
                startActivity(new Intent(this, ActividadfisicaActivity.class)));
        findViewById(R.id.mentalButton).setOnClickListener(v ->
                startActivity(new Intent(this, SaludmentalActivity.class)));
        findViewById(R.id.comunidadButton).setOnClickListener(v ->
                startActivity(new Intent(this, ComunidadActivity.class)));
    }

    // Mostrar alimentos por tipo
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
            dialog.show(getSupportFragmentManager(), "ComidaDialog");
        }

        actualizarResumen();
    }
    // Selección aleatoria de alimento según tipo y perfil
    private Map<String, Object> seleccionarAlimento(String tipo) {
        String perfil;
        if (imc < 18.5) perfil = "ganancia";
        else if (imc < 25) perfil = "mantenimiento";
        else if (imc < 30) perfil = "reduccion_ligera";
        else perfil = "reduccion_agresiva";

        List<Map<String,Object>> lista = new ArrayList<>();
        Random rnd = new Random();

        // 🔹 DESAYUNO
        if(tipo.equals("Desayuno")) {
            if(perfil.equals("ganancia")) {
                lista.add(crearAlimento("Avena con leche y banana","Cocer avena con leche y añadir banana",350,12,8,60));
                lista.add(crearAlimento("Huevos revueltos con pan integral","2 huevos + 2 rebanadas de pan integral",400,20,15,40));
                lista.add(crearAlimento("Batido de proteína y avena","Mezclar proteína en polvo, avena y leche",450,25,10,55));
                lista.add(crearAlimento("Tostadas con aguacate y huevo","Pan integral con aguacate y huevo pochado",380,18,14,42));
                lista.add(crearAlimento("Yogur con frutos secos y miel","Yogur natural + frutos secos + miel",320,15,12,40));
                lista.add(crearAlimento("Pan integral con mantequilla de maní","2 rebanadas con mantequilla de maní",370,14,16,38));
                lista.add(crearAlimento("Smoothie bowl con frutas y avena","Frutas + avena + yogur",360,10,8,65));
            } else if(perfil.equals("mantenimiento")) {
                lista.add(crearAlimento("Yogur con fruta y avena","Mezclar yogur natural con fruta y avena",250,10,5,40));
                lista.add(crearAlimento("Tostadas integrales con aguacate","2 rebanadas con aguacate",300,8,10,40));
                lista.add(crearAlimento("Huevos duros con pan integral","2 huevos + pan integral",280,12,10,35));
                lista.add(crearAlimento("Batido de fruta natural","Licuar fruta con leche",240,8,5,45));
                lista.add(crearAlimento("Pan integral con queso bajo en grasa","2 rebanadas de pan con queso",260,10,6,38));
                lista.add(crearAlimento("Avena cocida con leche y miel","Cocer avena con leche y un poco de miel",270,9,6,42));
                lista.add(crearAlimento("Fruta picada con yogur","Fruta fresca + yogur natural",220,6,3,50));
            } else { // reduccion_ligera o agresiva
                lista.add(crearAlimento("Yogur con frutas y avena","Mezclar yogur natural con fruta y avena",200,8,3,35));
                lista.add(crearAlimento("Tostadas integrales con tomate","2 rebanadas con tomate y aguacate",220,7,4,30));
                lista.add(crearAlimento("Batido verde","Espinaca, pepino y manzana",180,5,2,30));
                lista.add(crearAlimento("Avena ligera con leche de almendra","Cocer avena con leche de almendra",210,6,3,32));
                lista.add(crearAlimento("Huevos revueltos con verduras","2 huevos + vegetales salteados",230,12,10,15));
                lista.add(crearAlimento("Pan integral con aguacate","1 rebanada de pan integral + aguacate",200,5,5,28));
                lista.add(crearAlimento("Fruta fresca","1 manzana o plátano",90,1,0,22));
            }
        }

        // 🔹 ALMUERZO (7 alimentos)
        if(tipo.equals("Almuerzo")) {
            lista.add(crearAlimento("Pechuga de pollo con arroz integral","150g de pollo + 100g arroz integral + vegetales",500,35,10,55));
            lista.add(crearAlimento("Ensalada de atún","Atún + lechuga + tomate + aceite de oliva",400,30,12,35));
            lista.add(crearAlimento("Salmón al horno con quinoa","150g salmón + 80g quinoa + vegetales",480,32,15,45));
            lista.add(crearAlimento("Pasta integral con verduras y pollo","100g pasta integral + pollo + verduras",520,28,12,60));
            lista.add(crearAlimento("Wrap de pollo con vegetales","Tortilla integral + pollo + verduras",450,25,10,50));
            lista.add(crearAlimento("Arroz con huevo y vegetales","100g arroz + 2 huevos + vegetales",470,22,14,55));
            lista.add(crearAlimento("Lentejas con arroz y verduras","100g lentejas + arroz + verduras",430,20,8,60));
        }

        // 🔹 CENA (7 alimentos)
        if(tipo.equals("Cena")) {
            lista.add(crearAlimento("Sopa de verduras con pollo","Sopa de verduras + 100g pollo",350,20,6,45));
            lista.add(crearAlimento("Ensalada de salmón y aguacate","Salmón + aguacate + lechuga",400,25,18,35));
            lista.add(crearAlimento("Tortilla de claras con vegetales","4 claras + vegetales salteados",300,22,5,40));
            lista.add(crearAlimento("Pescado al horno con vegetales","150g pescado + verduras",380,28,8,42));
            lista.add(crearAlimento("Pollo a la plancha con ensalada","150g pollo + ensalada",360,30,10,40));
            lista.add(crearAlimento("Quinoa con verduras","80g quinoa + verduras",340,12,6,50));
            lista.add(crearAlimento("Wrap integral con pavo y vegetales","Tortilla integral + pavo + verduras",370,25,8,45));
        }

        // 🔹 SNACKS (7 alimentos)
        if(tipo.equals("Snacks")) {
            lista.add(crearAlimento("Frutos secos","30g de frutos secos variados",180,6,16,6));
            lista.add(crearAlimento("Yogur natural con miel","100g yogur + 1 cucharadita de miel",120,5,3,15));
            lista.add(crearAlimento("Barra de proteína","1 barra comercial",200,20,8,20));
            lista.add(crearAlimento("Fruta fresca","1 manzana o plátano",90,1,0,22));
            lista.add(crearAlimento("Palitos de zanahoria con hummus","100g zanahoria + hummus",150,4,7,18));
            lista.add(crearAlimento("Galletas integrales","2 galletas integrales",110,3,4,20));
            lista.add(crearAlimento("Batido de frutas pequeño","Licuar fruta con agua o leche",130,2,2,28));
        }

        // Retornar un alimento aleatorio
        if(lista.isEmpty()) return null;
        return lista.get(rnd.nextInt(lista.size()));
    }


    private Map<String,Object> crearAlimento(String nombre, String preparacion, double calorias, double proteinas, double grasas, double carbohidratos){
        Map<String,Object> alimento = new HashMap<>();
        alimento.put("nombre", nombre);
        alimento.put("preparacion", preparacion);
        alimento.put("calorias", calorias);
        alimento.put("proteinas", proteinas);
        alimento.put("grasas", grasas);
        alimento.put("carbohidratos", carbohidratos);
        return alimento;
    }

    // Actualizar resumen calórico con suma total
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

    // Obtener IMC y grasa corporal desde servidor
    private void obtenerIndicadores(int usuarioID) {
        String url = "http://upnfit.atwebpages.com/upnfit/obtener_todas_medidas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        Log.d("NutricionActivity","Respuesta servidor: "+response);
                        JSONObject json = new JSONObject(response);

                        int codigo = json.optInt("Codigo",0);
                        String mensaje = json.optString("Mensaje","");

                        if(codigo==1){
                            imc = json.optDouble("IMC",0);
                            grasaPct = json.optDouble("GrasaPct",0);

                            txtIMCValor.setText(String.format("%.1f",imc));
                            txtGrasaValor.setText(String.format("%.1f%%",grasaPct));

                        } else {
                            txtIMCValor.setText("--");
                            txtGrasaValor.setText("--");
                            Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e){
                        e.printStackTrace();
                        Toast.makeText(this,"Error al leer los datos del servidor",Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this,"Error de conexión con el servidor de indicadores",Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
