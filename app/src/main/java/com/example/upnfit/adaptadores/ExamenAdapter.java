package com.example.upnfit.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Examen;
import java.util.List;

public class ExamenAdapter extends RecyclerView.Adapter<ExamenAdapter.ViewHolder> {

    private List<Examen> examenes;

    public ExamenAdapter(List<Examen> examenes) {
        this.examenes = examenes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_examen, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Examen examen = examenes.get(position);
        holder.tvCurso.setText(examen.getCurso());
        holder.tvTipo.setText(examen.getTipo());
        holder.tvFecha.setText(examen.getFecha());
        holder.tvHora.setText(examen.getHora());
        holder.tvTema.setText("Temario: " + examen.getTema());
    }

    @Override
    public int getItemCount() {
        return examenes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCurso, tvTipo, tvFecha, tvHora, tvTema;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCurso = itemView.findViewById(R.id.tvCursoExamen);
            tvTipo = itemView.findViewById(R.id.tvTipoExamen);
            tvFecha = itemView.findViewById(R.id.tvFechaExamen);
            tvHora = itemView.findViewById(R.id.tvHoraExamen);
            tvTema = itemView.findViewById(R.id.tvTemaExamen);
        }
    }
}
