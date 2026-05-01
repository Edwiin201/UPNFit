package com.example.upnfit.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.upnfit.R;
import com.example.upnfit.modelos.Curso;
import java.util.List;

public class CursoAdapter extends RecyclerView.Adapter<CursoAdapter.ViewHolder> {

    private List<Curso> cursos;

    public CursoAdapter(List<Curso> cursos) {
        this.cursos = cursos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curso, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Curso curso = cursos.get(position);
        holder.tvNombre.setText(curso.getNombre());
        holder.tvProfesor.setText(curso.getProfesor());
        holder.tvHorario.setText(curso.getHorario());
        holder.imgCurso.setImageResource(curso.getImagenResId());
    }

    @Override
    public int getItemCount() {
        return cursos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvProfesor, tvHorario;
        ImageView imgCurso;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreCurso);
            tvProfesor = itemView.findViewById(R.id.tvProfesorCurso);
            tvHorario = itemView.findViewById(R.id.tvHorarioCurso);
            imgCurso = itemView.findViewById(R.id.imgCurso);
        }
    }
}
