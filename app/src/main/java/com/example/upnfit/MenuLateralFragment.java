package com.example.upnfit; // 🟢 Asegúrate de que el package sea correcto (puede ser .fragments)

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment; // 🟢 Usamos DialogFragment estándar

import com.example.upnfit.actividades.NutricionActivity;
// import com.example.upnfit.actividades.PerfilActivity; // Descomenta cuando exista

public class MenuLateralFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        // 🟢 CONFIGURACIÓN PARA QUE SALGA A LA IZQUIERDA
        if (getDialog() != null && getDialog().getWindow() != null) {
            Window window = getDialog().getWindow();

            // Alineamos el diálogo a la izquierda (START)
            window.setGravity(Gravity.START);

            // Ancho y Alto: Usamos lo que diga el XML (wrap_content) para el ancho, y match_parent para el alto
            window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

            // Fondo transparente para que se vean los bordes redondeados si los usas, o para quitar sombras raras
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Animación (Opcional: para que entre suavemente, aunque por defecto hará un fade)
            // window.setWindowAnimations(android.R.style.Animation_Dialog);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu_lateral, container, false);

        // --- Configurar botones ---

        // 1. Nutrición
        LinearLayout opNutricion = view.findViewById(R.id.opNutricion);
        if (opNutricion != null) {
            opNutricion.setOnClickListener(v -> {
                dismiss();
                startActivity(new Intent(getContext(), NutricionActivity.class));
            });
        }

        // 2. Perfil
        LinearLayout opPerfil = view.findViewById(R.id.opPerfil);
        if (opPerfil != null) {
            opPerfil.setOnClickListener(v -> {
                dismiss();
                Toast.makeText(getContext(), "Ir a Perfil", Toast.LENGTH_SHORT).show();
                // startActivity(new Intent(getContext(), PerfilActivity.class));
            });
        }

        // 3. Salir
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
