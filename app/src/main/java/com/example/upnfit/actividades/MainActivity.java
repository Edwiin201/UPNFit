package com.example.upnfit.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ImageButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.upnfit.fragmentos.MenuFragment;
import com.example.upnfit.fragmentos.NutricionFragment;
import com.example.upnfit.fragmentos.ActividadFragment;
import com.example.upnfit.fragmentos.SaludMentalFragment;
import com.example.upnfit.fragmentos.ComunidadFragment;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import com.example.upnfit.R;


public class MainActivity extends AppCompatActivity {

    private LinearLayout btnInicio, btnNutricion, btnActividad, btnMental, btnComunidad;
    private LinearLayout btnConfiguracion;
    private LinearLayout btnSalir;

    private ImageButton btnMenuToggle;

    private DrawerLayout drawerLayout;

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

        //  INICIO DE LA LÓGICA DE CARGA DEL FRAGMENTO INICIAL
        if (savedInstanceState == null) {

            // 1. Verificar si hay una solicitud específica (por ejemplo, desde NuevapublicacionActivity)
            String fragmentToOpen = getIntent().getStringExtra("openFragment");

            if ("comunidad".equals(fragmentToOpen)) {
                // Si la señal es "comunidad", cargamos ComunidadFragment
                loadFragment(new ComunidadFragment());
            } else {
                // Si no hay señal (inicio normal), cargamos el fragmento de menú
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
            // Si la Activity ya estaba en memoria y recibe un nuevo Intent con la instrucción, la cargamos.
            loadFragment(new ComunidadFragment());
        }
        // Nota: Si se envía otro Intent sin "comunidad", no hacemos nada para evitar recargar el fragmento actual.
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
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
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
}