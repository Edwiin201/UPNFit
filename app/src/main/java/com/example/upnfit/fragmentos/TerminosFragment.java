package com.example.upnfit.fragmentos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.upnfit.R;

import java.util.Objects;

public class TerminosFragment extends DialogFragment {
    public interface OnTerminosAceptadosListener {
        void onAceptar();
    }

    private final OnTerminosAceptadosListener listener;

    public TerminosFragment(OnTerminosAceptadosListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.terminosfragment, null);

        builder.setView(view)
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    if (listener != null) {
                        listener.onAceptar(); // Se llama al cerrar
                    }
                });

        AlertDialog dialog = builder.create();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }
}
