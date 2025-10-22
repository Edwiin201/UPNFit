package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

public class HorameditacionActivity extends AppCompatActivity {

    private TextView tvTime; // Para mostrar el tiempo
    private ImageButton btnPause; // Botón para pausar/reanudar
    private CountDownTimer countDownTimer; // Temporizador para contar los minutos
    private long timeLeftInMillis = 5 * 60 * 1000; // 5 minutos en milisegundos
    private boolean isTimerRunning = false; // Controla si el temporizador está corriendo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horameditacion);

        // Inicializar los elementos
        tvTime = findViewById(R.id.tvTime);
        btnPause = findViewById(R.id.btnPause);

        // Mostrar el tiempo inicial (5 minutos)
        updateTimeText();

        // Establecer el evento OnClickListener para el botón de pausa/reanudación
        btnPause.setOnClickListener(v -> {
            if (isTimerRunning) {
                // Pausar el temporizador
                pauseTimer();
            } else {
                // Iniciar o reanudar el temporizador
                startTimer();
            }
        });

        // Establecer el evento OnClickListener para el botón "X" de regreso
        ImageButton btnRegresoMenu = findViewById(R.id.regresomenu);

        btnRegresoMenu.setOnClickListener(v -> {
            // Crear una nueva intención para abrir SaludmentalActivity
            Intent intent = new Intent(HorameditacionActivity.this, SaludmentalActivity.class);
            startActivity(intent);  // Inicia la actividad SaludmentalActivity
            finish();  // Finaliza la actividad actual (HorameditacionActivity)
        });
    }

    // Método para iniciar el temporizador
    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimeText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateTimeText();
                // Aquí puedes poner lo que debería pasar cuando el tiempo se acabe (por ejemplo, mostrar un mensaje o reiniciar el contador)
            }
        }.start();

        isTimerRunning = true;
        btnPause.setImageResource(android.R.drawable.ic_media_pause); // Cambiar icono a "Pausa"
    }

    // Método para pausar el temporizador
    private void pauseTimer() {
        countDownTimer.cancel();
        isTimerRunning = false;
        btnPause.setImageResource(android.R.drawable.ic_media_play); // Cambiar icono a "Reproducir"
    }

    // Actualiza el texto de la hora en pantalla (formato minutos:segundos)
    private void updateTimeText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String time = String.format("%02d:%02d", minutes, seconds);
        tvTime.setText(time);
    }
}
