package com.example.upnfit.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.upnfit.R;

import java.util.Random;

public class SaludmentalActivity extends AppCompatActivity {

    private LinearLayout layoutEmojis;
    private FrameLayout floatingContainer;
    private int[] emojiIds = {R.drawable.feliz, R.drawable.tranquilo, R.drawable.neutral, R.drawable.estresado};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_saludmental);

        // Botones de navegación
        Button btnMenu = findViewById(R.id.inicioButton);
        Button btnNutricion = findViewById(R.id.nutricionButton);
        Button btnActividad = findViewById(R.id.ejercicioButton);
        Button btnComunidad = findViewById(R.id.comunidadButton);
        Button btnIniciar = findViewById(R.id.iniciarButton);

        btnMenu.setOnClickListener(v -> startActivity(new Intent(this, MenuActivity.class)));
        btnNutricion.setOnClickListener(v -> startActivity(new Intent(this, NutricionActivity.class)));
        btnActividad.setOnClickListener(v -> startActivity(new Intent(this, ActividadfisicaActivity.class)));
        btnComunidad.setOnClickListener(v -> startActivity(new Intent(this, ComunidadActivity.class)));
        btnIniciar.setOnClickListener(v -> startActivity(new Intent(this, HorameditacionActivity.class)));

        // Emoji animación lógica
        layoutEmojis = findViewById(R.id.layoutEmojis);
        floatingContainer = findViewById(R.id.emojiOverlay);

        for (int i = 0; i < layoutEmojis.getChildCount(); i++) {
            ImageView emoji = (ImageView) layoutEmojis.getChildAt(i);
            int finalI = i;
            emoji.setOnClickListener(v -> {
                ocultarOtrosEmojisExcepto(finalI);
                mostrarEmojisFlotantes(emojiIds[finalI]);
            });
        }
    }

    private void ocultarOtrosEmojisExcepto(int index) {
        for (int i = 0; i < layoutEmojis.getChildCount(); i++) {
            layoutEmojis.getChildAt(i).setVisibility(i == index ? View.VISIBLE : View.GONE);
        }
    }

    private void mostrarEmojisFlotantes(int emojiDrawable) {
        floatingContainer.removeAllViews();
        floatingContainer.setVisibility(View.VISIBLE);

        int cantidad = 25;
        int ancho = floatingContainer.getWidth();
        int alto = floatingContainer.getHeight();
        if (ancho == 0) ancho = 1080; // fallback
        if (alto == 0) alto = 1920;

        Random random = new Random();
        for (int i = 0; i < cantidad; i++) {
            ImageView emojiView = new ImageView(this);
            emojiView.setImageResource(emojiDrawable);

            int size = random.nextInt(80) + 60;
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
            params.leftMargin = random.nextInt(ancho - size);
            params.topMargin = random.nextInt(alto - size);
            emojiView.setLayoutParams(params);

            AlphaAnimation animation = new AlphaAnimation(0f, 1f);
            animation.setDuration(500);
            emojiView.startAnimation(animation);

            floatingContainer.addView(emojiView);
        }

        new Handler().postDelayed(() -> {
            floatingContainer.removeAllViews();
            floatingContainer.setVisibility(View.GONE);
            layoutEmojis.setVisibility(View.VISIBLE);

            // Restaurar todos los emojis visibles
            for (int i = 0; i < layoutEmojis.getChildCount(); i++) {
                layoutEmojis.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }, 5000);
    }
}
