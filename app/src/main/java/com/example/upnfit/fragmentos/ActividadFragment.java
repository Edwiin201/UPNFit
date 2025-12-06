package com.example.upnfit.fragmentos;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.upnfit.R;
import com.example.upnfit.actividades.GraficoPasosActivity;
import com.example.upnfit.actividades.MapaActivity;
import com.example.upnfit.notificaciones.NotificationHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Random;

import cz.msebera.android.httpclient.Header;

public class ActividadFragment extends Fragment {

    private SensorManager sensorManager;
    private Sensor stepSensor;

    private int initialSteps = -1;
    private int stepsToday = 0;

    private TextView txtPasos, txtCalorias;

    private static final String URL_ACTIVIDAD =
            "http://renovaapp.atwebpages.com/Services/Actividad_fisica.php";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_actividad, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NotificationHelper.createNotificationChannel(requireContext());

        // Permiso para notificaciones Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 200);
            }
        }

        txtPasos = view.findViewById(R.id.txtPasos);
        txtCalorias = view.findViewById(R.id.txtCalorias);

        sensorManager = (SensorManager) requireContext().getSystemService(
                requireContext().SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        SharedPreferences prefs = requireContext().getSharedPreferences("pasos", getContext().MODE_PRIVATE);

        // Reseteo diario
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

        // Botón: ubicación
        view.findViewById(R.id.btnVerUbicacion).setOnClickListener(v ->
                startActivity(new Intent(requireContext(), MapaActivity.class)));

        // Botón: actividad del día
        view.findViewById(R.id.btnVerActividad).setOnClickListener(v ->
                obtenerActividadDelDia());

        // Botón: gráfico semanal
        Button btnGrafico = view.findViewById(R.id.btnGrafico);
        btnGrafico.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), GraficoPasosActivity.class))
        );
    }


    // ==================== NOTIFICACIÓN =====================
    private void mostrarNotificacion(String titulo, String mensaje) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(requireContext(), NotificationHelper.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(titulo)
                        .setContentText(mensaje)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(requireContext());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }


    // ==================== SENSOR LISTENER =====================
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

            SharedPreferences prefs = requireContext().getSharedPreferences("pasos", getContext().MODE_PRIVATE);
            prefs.edit().putInt("hoy", stepsToday).apply();

            // Notificaciones motivacionales
            SharedPreferences prefsApp = requireContext().getSharedPreferences("AppSettings", getContext().MODE_PRIVATE);
            boolean notiActiva = prefsApp.getBoolean("noti_motivacionales", false);

            if (notiActiva) {

                String[] mensajes = {
                        "💪 ¡Sigue así!",
                        "🔥 Estás avanzando increíble",
                        "🏃‍♂️ Cada paso cuenta",
                        "⭐ La constancia te hace fuerte",
                        "✨ ¡Vamos, tú puedes!"
                };

                int randomMsg = new Random().nextInt(mensajes.length);

                if (stepsToday > 0 && stepsToday % 1500 == 0) {
                    mostrarNotificacion("Motivación UPN FIT", mensajes[randomMsg]);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };


    // ==================== RESUME =====================
    @Override
    public void onResume() {
        super.onResume();

        if (stepSensor != null) {
            sensorManager.registerListener(
                    stepListener,
                    stepSensor,
                    SensorManager.SENSOR_DELAY_NORMAL
            );
        }

        SharedPreferences prefs = requireContext().getSharedPreferences("pasos", getContext().MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        int index = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        int hoy = prefs.getInt("hoy", 0);
        prefs.edit().putInt("dia" + index, hoy).apply();
    }


    // ==================== PAUSE =====================
    @Override
    public void onPause() {
        super.onPause();
        sensorManager.unregisterListener(stepListener);

        SharedPreferences prefs = requireContext().getSharedPreferences("pasos", getContext().MODE_PRIVATE);
        prefs.edit().putInt("initial", initialSteps).apply();
    }


    // ==================== API ACTIVIDAD =====================
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

                        dialog.show(getParentFragmentManager(), "actividadFisica");

                    } else {
                        Toast.makeText(requireContext(),
                                response.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(requireContext(),
                            "Error procesando respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String s, Throwable throwable) {
                Toast.makeText(requireContext(),
                        "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
