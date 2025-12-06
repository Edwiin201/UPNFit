package com.example.upnfit.fragmentos;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.upnfit.R;
import com.example.upnfit.actividades.HorameditacionActivity;

import java.util.Random;

public class SaludMentalFragment extends Fragment {

    private LinearLayout layoutEmojis;
    private FrameLayout floatingContainer;

    private int[] emojiIds = {
            R.drawable.feliz,
            R.drawable.tranquilo,
            R.drawable.neutral,
            R.drawable.estresado
    };

    public SaludMentalFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_salud_mental, container, false);

        // Referencias
        layoutEmojis = view.findViewById(R.id.layoutEmojis);


        Button btnIniciar = view.findViewById(R.id.iniciarButton);

        // → Lógica: abrir pantalla HorameditacionActivity
        btnIniciar.setOnClickListener(v -> {
            if (getActivity() != null) {
                startActivity(new android.content.Intent(
                        getActivity(),
                        HorameditacionActivity.class
                ));
            }
        });

        // → Lógica: animación emojis
        for (int i = 0; i < layoutEmojis.getChildCount(); i++) {
            ImageView emoji = (ImageView) layoutEmojis.getChildAt(i);
            int finalI = i;
            emoji.setOnClickListener(v -> {
                ocultarOtrosEmojisExcepto(finalI);
                mostrarEmojisFlotantes(emojiIds[finalI]);
            });
        }

        return view;
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

        if (ancho == 0) ancho = 1080;
        if (alto == 0) alto = 1920;

        Random random = new Random();

        for (int i = 0; i < cantidad; i++) {

            ImageView emojiView = new ImageView(getContext());
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

            // Restaurar todos los emojis visibles
            for (int i = 0; i < layoutEmojis.getChildCount(); i++) {
                layoutEmojis.getChildAt(i).setVisibility(View.VISIBLE);
            }

        }, 5000);
    }
}
