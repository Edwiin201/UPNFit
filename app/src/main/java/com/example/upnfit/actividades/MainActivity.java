package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest; // 🛑 Usaremos JsonObjectRequest para manejar el array
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ActividadFragment;
import com.example.upnfit.fragmentos.ComunidadFragment;
import com.example.upnfit.fragmentos.MenuFragment;
import com.example.upnfit.fragmentos.NutricionFragment;
import com.example.upnfit.fragmentos.SaludMentalFragment;
import com.example.upnfit.sqlite.IndicadoresDB;
import com.example.upnfit.sqlite.PerfilDB;
import com.example.upnfit.sqlite.PublicacionesDB; // 🛑 NUEVA IMPORTACIÓN

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private LinearLayout btnInicio, btnNutricion, btnActividad, btnMental, btnComunidad;
    private LinearLayout btnConfiguracion;
    private LinearLayout btnSalir;

    private ImageButton btnMenuToggle;

    private DrawerLayout drawerLayout;

    private static final String TAG = "MainActivity";


    private static final String URL_DATOS_USUARIO =
            "http://upnfit.atwebpages.com/upnfit/obtener_datos_usuario.php";

    private static final String URL_PUBLICACIONES = // 🛑 NUEVA CONSTANTE
            "http://upnfit.atwebpages.com/upnfit/obtener_publicaciones.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // modo oscuro o claro guardado antes de crear la UI
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        boolean darkModeEnabled = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                darkModeEnabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);

        // 1. Obtener usuarioID
        SharedPreferences userPrefs = getSharedPreferences("UserData", MODE_PRIVATE);
        int usuarioID = userPrefs.getInt("usuarioID", 0);

        // 2. SINCRONIZACIÓN INICIAL: Solo si tenemos un usuario válido
        if (usuarioID != 0) {
            // Sincroniza indicadores (IMC, Grasa)
            sincronizarIndicadoresInicial(usuarioID);

            // Sincroniza datos de Perfil (Nombre, Sede)
            sincronizarPerfilInicial(usuarioID);

            // 🛑 Sincroniza Publicaciones (Caché para ComunidadFragment)
            sincronizarPublicacionesInicial();
        }

        //  INICIO DE LA LÓGICA DE CARGA DEL FRAGMENTO INICIAL
        if (savedInstanceState == null) {
            String fragmentToOpen = getIntent().getStringExtra("openFragment");

            if ("comunidad".equals(fragmentToOpen)) {
                loadFragment(new ComunidadFragment());
            } else {
                loadFragment(new MenuFragment());
            }
        }
        //  FIN DE LA LÓGICA DE CARGA DEL FRAGMENTO INICIAL


        // 3. Configurar navegación (Solo menú lateral)
        setupNavigationButtons();
    }

    //  AGREGAMOS onNewIntent para manejar la navegación si la Activity ya estaba abierta
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        String fragmentToOpen = intent.getStringExtra("openFragment");

        if ("comunidad".equals(fragmentToOpen)) {
            loadFragment(new ComunidadFragment());
        }
    }


    private void setupNavigationButtons() {
        // 1. Opciones del menú lateral (LinearLayouts)
        btnInicio = findViewById(R.id.inicioButton);
        btnNutricion = findViewById(R.id.nutricionButton);
        btnActividad = findViewById(R.id.ejercicioButton);
        btnMental = findViewById(R.id.mentalButton);
        btnComunidad = findViewById(R.id.comunidadButton);

        // Opciones de Configuración y Salir
        btnConfiguracion = findViewById(R.id.configuracion);
        btnSalir = findViewById(R.id.opSalir);

        // Inicializa y configura el botón de apertura
        btnMenuToggle = findViewById(R.id.menuToggle);
        if (btnMenuToggle != null) {
            btnMenuToggle.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }


        // 3. Listeners para Fragments
        btnInicio.setOnClickListener(v -> loadFragment(new MenuFragment()));
        btnNutricion.setOnClickListener(v -> loadFragment(new NutricionFragment()));
        btnActividad.setOnClickListener(v -> loadFragment(new ActividadFragment()));
        btnMental.setOnClickListener(v -> loadFragment(new SaludMentalFragment()));
        btnComunidad.setOnClickListener(v -> loadFragment(new ComunidadFragment()));

        // 4. Listener para Configuración del Menú
        btnConfiguracion.setOnClickListener(v -> {
            startActivity(new Intent(this, ConfiguracionActivity.class));
            // También cerramos el cajón al ir a otra Activity
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });

        // 5. Listener para Cerrar Sesión
        btnSalir.setOnClickListener(v -> {
            // Lógica para cerrar sesión
            Intent intent = new Intent(this, SesionActivity.class);
            startActivity(intent);
            finish();
            if (drawerLayout != null) {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }


    // Método genérico para cargar Fragments en el contenedor
    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Usar R.id.fragment_container del activity_main.xml
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        //  Cierra el DrawerLayout después de cargar el fragmento
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    // =========================================================================
    // 🛑 NUEVOS MÉTODOS DE SINCRONIZACIÓN DE CACHÉ
    // =========================================================================


    /**
     * 🛑 NUEVO: Obtiene todas las publicaciones del servidor y actualiza PublicacionesDB.
     */
    private void sincronizarPublicacionesInicial() {
        PublicacionesDB db = new PublicacionesDB(this);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_PUBLICACIONES,
                null,
                response -> {
                    try {
                        if (response.optInt("p_Codigo", 0) != 1) return;

                        JSONArray data = response.getJSONArray("data");

                        // 1. Limpiar cache antes de guardar los nuevos datos
                        db.limpiarCache();

                        for (int i = 0; i < data.length(); i++) {
                            JSONObject pub = data.getJSONObject(i);

                            // Usamos "PublicacionID" basado en la confirmación previa
                            int publicacionID = pub.optInt("PublicacionID", 0);
                            String titulo = pub.optString("Titulo", "");
                            String contenido = pub.optString("Contenido", "");
                            String autor = pub.optString("Autor", "Usuario");
                            String fecha = pub.optString("FechaPublicacion", "");
                            String categoria = pub.optString("Categoria", "");

                            // 2. Guardar en la caché SQLite
                            if (publicacionID != 0) {
                                db.guardarPublicacion(publicacionID, titulo, contenido, autor, fecha, categoria);
                            }
                        }
                        Log.d(TAG, "Cache de publicaciones actualizada con éxito.");

                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar publicaciones en MainActivity: " + e.getMessage());
                    } finally {
                        // Siempre cerrar la DB
                        db.close();
                    }
                },
                error -> {
                    Log.e(TAG, "Error de red al sincronizar publicaciones: " + error.toString());
                    db.close();
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    /**
     * Obtiene los indicadores del servidor y los guarda en la caché SQLite (IndicadoresDB).
     */
    private void sincronizarIndicadoresInicial(int usuarioID) {
        String url = "http://upnfit.atwebpages.com/upnfit/obtener_todas_medidas.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optInt("Codigo", 0) == 1) {
                            double imcServidor = json.optDouble("IMC", 0);
                            double grasaPctServidor = json.optDouble("GrasaPct", 0);

                            // 💾 Guardar en SQLite (IndicadoresDB)
                            IndicadoresDB db = new IndicadoresDB(this);
                            db.guardarIndicadores(usuarioID, imcServidor, grasaPctServidor);
                            db.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e(TAG, "Error sincronizando indicadores.", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    /**
     * Obtiene los datos personales del servidor y los guarda en PerfilDB, preservando medidas locales.
     */
    private void sincronizarPerfilInicial(int usuarioID) {
        StringRequest request = new StringRequest(Request.Method.POST, URL_DATOS_USUARIO,
                response -> {
                    PerfilDB db = new PerfilDB(MainActivity.this);
                    Cursor cursor = db.obtenerPerfil(usuarioID);

                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.optInt("Codigo", 0) == 1) {

                            String nombre = json.optString("NombreCompleto", "");
                            String sede = json.optString("SedeID", "");

                            // Recopilar medidas existentes de la caché para no perderlas
                            String genero = "";
                            int edad = 0;
                            double altura = 0.0;
                            double peso = 0.0;

                            if (cursor != null && cursor.moveToFirst()) {
                                genero = cursor.getString(cursor.getColumnIndexOrThrow(PerfilDB.COL_GENERO));
                                edad = cursor.getInt(cursor.getColumnIndexOrThrow(PerfilDB.COL_EDAD));
                                altura = cursor.getDouble(cursor.getColumnIndexOrThrow(PerfilDB.COL_ALTURA));
                                peso = cursor.getDouble(cursor.getColumnIndexOrThrow(PerfilDB.COL_PESO));
                            }

                            // Guardar el perfil completo (Nombre/Sede del servidor + Medidas locales)
                            db.guardarPerfil(usuarioID, nombre, sede, genero, edad, altura, peso);

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error sincronizando perfil: " + e.getMessage());
                    } finally {
                        if (cursor != null) cursor.close();
                        db.close();
                    }
                },
                error -> {
                    Log.e(TAG, "Error de red sincronizando perfil.", error);
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("usuarioID", String.valueOf(usuarioID));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}