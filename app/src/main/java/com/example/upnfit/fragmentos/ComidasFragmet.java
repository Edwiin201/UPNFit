package com.example.upnfit.fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.upnfit.R;

import java.util.Objects;


public class ComidasFragmet extends DialogFragment{
    private String comida, preparacion;

    public ComidasFragmet(String comida, String preparacion) {
        this.comida = comida;
        this.preparacion = preparacion;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_fragmentos, null);

        TextView txtTitulo = view.findViewById(R.id.txtTituloComida);
        TextView txtPreparacion = view.findViewById(R.id.txtPreparacionComida);

        txtTitulo.setText(comida);
        txtPreparacion.setText(preparacion);

        builder.setView(view)
                .setPositiveButton("Cerrar", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();

        // Asegura que el fondo sea transparente para permitir las esquinas redondeadas
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        return builder.create();
    }
}
