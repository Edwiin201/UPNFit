package com.example.upnfit.actividades;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.upnfit.R;
import com.example.upnfit.fragmentos.ActividadfisicaFragment;
import com.example.upnfit.notificaciones.NotificationHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class ActividadfisicaActivity extends AppCompatActivity {

    // SENSOR
    private SensorManager sensorManager;
    private Sensor stepSensor;

    private int initialSteps = -1;
    private int stepsToday = 0;

    private TextView txtPasos, txtCalorias;

    private static final String URL_ACTIVIDAD =
            "http://renovaapp.atwebpages.com/Services/Actividad_fisica.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_actividadfisica);

        // 🔔 Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this);

        // 🔔 Permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            }
        }

        // REFERENCIAS UI
        txtPasos = findViewById(R.id.txtPasos);
        txtCalorias = findViewById(R.id.txtCalorias);

        // SENSOR
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);

        // ============ RESETEO DE PASOS POR DÍA ===============
        Calendar calendar = Calendar.getInstance();
        String today = String.format("%04d-%02d-%02d",
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH));

        String lastDate = prefs.getString("fecha", "");

        if (!today.equals(lastDate)) {
            initialSteps = -1;
            prefs.edit().putString("fecha", today).apply();
            prefs.edit().putInt("initial", -1).apply();
        } else {
            initialSteps = prefs.getInt("initial", -1);
        }

        // BOTONES NAVEGACIÓN
        findViewById(R.id.inicioButton).setOnClickListener(
                v -> startActivity(new Intent(this, MenuActivity.class)));

        findViewById(R.id.nutricionButton).setOnClickListener(
                v -> startActivity(new Intent(this, NutricionActivity.class)));

        findViewById(R.id.mentalButton).setOnClickListener(
                v -> startActivity(new Intent(this, SaludmentalActivity.class)));

        findViewById(R.id.comunidadButton).setOnClickListener(
                v -> startActivity(new Intent(this, ComunidadActivity.class)));

        findViewById(R.id.btnVerUbicacion).setOnClickListener(
                v -> startActivity(new Intent(this, MapaActivity.class)));

        findViewById(R.id.btnVerActividad).setOnClickListener(v -> obtenerActividadDelDia());

        // Gráfico semanal
        Button btnGrafico = findViewById(R.id.btnGrafico);
        btnGrafico.setOnClickListener(
                v -> startActivity(new Intent(this, GraficoPasosActivity.class)));
    }



    // ======================= NOTIFICACIONES ===========================
    private void mostrarNotificacion(String titulo, String mensaje) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher) // O usa ic_walk si lo tienes
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
                return;
            }
        }

        try {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }



    // ======================= SENSOR LISTENER ===========================
    private final SensorEventListener stepListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            int totalSteps = (int) event.values[0];

            if (initialSteps == -1) {
                initialSteps = totalSteps;
            }

            stepsToday = totalSteps - initialSteps;

            double calorias = stepsToday * 0.04;

            txtPasos.setText("Pasos de hoy: " + stepsToday);
            txtCalorias.setText("Calorías quemadas: " +
                    String.format(java.util.Locale.US, "%.2f", calorias));

            SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);
            prefs.edit().putInt("hoy", stepsToday).apply();


            // 🔥 MOTIVACIÓN (Opción C) — Cada 1500 pasos
            SharedPreferences prefsApp = getSharedPreferences("AppSettings", MODE_PRIVATE);
            boolean notiActiva = prefsApp.getBoolean("noti_motivacionales", false);

            if (notiActiva) {

                String[] mensajes = {
                        "💪 ¡Sigue así!",
                        "🔥 Estás avanzando increíble",
                        "🏃‍♂️ Cada paso cuenta",
                        "⭐ La constancia te hace fuerte",
                        "✨ ¡Vamos, tú puedes!"
                };

                int randomMsg = new java.util.Random().nextInt(mensajes.length);

                if (stepsToday > 0 && stepsToday % 1500 == 0) {
                    mostrarNotificacion("Motivación UPN FIT", mensajes[randomMsg]);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };



    // ======================= ACTIVAR SENSOR ===========================
    @Override
    protected void onResume() {
        super.onResume();

        if (stepSensor != null) {
            sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int hoyPasos = prefs.getInt("hoy", 0);
        prefs.edit().putInt("dia" + index, hoyPasos).apply();
    }


    // ======================= DESACTIVAR SENSOR ===========================
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(stepListener);

        SharedPreferences prefs = getSharedPreferences("pasos", MODE_PRIVATE);
        prefs.edit().putInt("initial", initialSteps).apply();
    }



    // ======================= API ACTIVIDAD ===========================
    private void obtenerActividadDelDia() {

        AsyncHttpClient client = new AsyncHttpClient();

        client.post(URL_ACTIVIDAD, null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (response.getBoolean("success")) {

                        ActividadfisicaFragment dialog = new ActividadfisicaFragment(
                                response.getString("actividad"),
                                response.getString("tipo"),
                                response.getString("indicaciones")
                        );

                        dialog.show(getSupportFragmentManager(), "actividadFisica");

                    } else {
                        Toast.makeText(ActividadfisicaActivity.this,
                                response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(ActividadfisicaActivity.this,
                            "Error al procesar respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(ActividadfisicaActivity.this,
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
