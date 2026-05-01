package com.example.upnfit;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.upnfit.actividades.NotasActivity;
import com.example.upnfit.actividades.AsistenciaActivity;

public class MenuLateralFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.START);
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_lateral, container, false);

        // --- Configurar botones del Portal Institucional ---

        // 1. Mis Notas
        LinearLayout opNotas = view.findViewById(R.id.opNotas);
        if (opNotas != null) {
            opNotas.setOnClickListener(v -> {
                dismiss();
                startActivity(new Intent(getContext(), NotasActivity.class));
            });
        }

        // 2. Asistencia
        LinearLayout opAsistencia = view.findViewById(R.id.opAsistencia);
        if (opAsistencia != null) {
            opAsistencia.setOnClickListener(v -> {
                dismiss();
                startActivity(new Intent(getContext(), AsistenciaActivity.class));
            });
        }

        // 3. Perfil
        LinearLayout opPerfil = view.findViewById(R.id.opPerfil);
        if (opPerfil != null) {
            opPerfil.setOnClickListener(v -> {
                dismiss();
                Toast.makeText(getContext(), "Módulo de Perfil en mantenimiento", Toast.LENGTH_SHORT).show();
            });
        }

        // 4. Salir (Cerrar Sesión)
        LinearLayout opSalir = view.findViewById(R.id.opSalir);
        if (opSalir != null) {
            opSalir.setOnClickListener(v -> {
                dismiss();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });
        }

        return view;
    }
}
