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

public class ActividadfisicaFragment extends DialogFragment {

    private final String actividad;
    private final String tipo;
    private final String indicaciones;

    public ActividadfisicaFragment(String actividad, String tipo, String indicaciones) {
        this.actividad = actividad;
        this.tipo = tipo;
        this.indicaciones = indicaciones;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.actividadfragment, null);

        TextView txtNombre = view.findViewById(R.id.txtNombreActividad);
        TextView txtTipo = view.findViewById(R.id.txtTipoActividad);
        TextView txtIndicaciones = view.findViewById(R.id.txtIndicacionesActividad);

        txtNombre.setText(actividad);
        txtTipo.setText(tipo);
        txtIndicaciones.setText(indicaciones);

        builder.setView(view)
                .setPositiveButton("Cerrar", (dialog, id) -> dismiss());

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}
