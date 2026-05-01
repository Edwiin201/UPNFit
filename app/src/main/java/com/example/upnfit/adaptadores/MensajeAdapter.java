package com.example.upnfit.adaptadores;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Mensaje;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.ViewHolder> {

    private List<Mensaje> mensajes;

    public MensajeAdapter(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensaje, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mensaje mensaje = mensajes.get(position);
        holder.tvRemitente.setText(mensaje.getRemitente());
        holder.tvContenido.setText(mensaje.getContenido());
        holder.tvFecha.setText(mensaje.getFecha());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.cardMensaje.getLayoutParams();
        if (mensaje.isEsMio()) {
            params.gravity = Gravity.END;
            holder.cardMensaje.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.azulino_claro));
            holder.tvRemitente.setVisibility(View.GONE);
        } else {
            params.gravity = Gravity.START;
            holder.cardMensaje.setCardBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.white));
            holder.tvRemitente.setVisibility(View.VISIBLE);
        }
        holder.cardMensaje.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRemitente, tvContenido, tvFecha;
        MaterialCardView cardMensaje;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRemitente = itemView.findViewById(R.id.tvRemitente);
            tvContenido = itemView.findViewById(R.id.tvContenido);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            cardMensaje = itemView.findViewById(R.id.cardMensaje);
        }
    }
}
