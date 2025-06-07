package com.example.edutransporte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.edutransporte.R;

import java.util.List;

public class AlunoAdapter extends BaseAdapter {
    private Context context;
    private List<com.example.edutransporte.Aluno> alunos;
    private LayoutInflater inflater;

    public AlunoAdapter(Context context, List<com.example.edutransporte.Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alunos.size();
    }

    @Override
    public Object getItem(int position) {
        return alunos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_aluno, parent, false);
            holder = new ViewHolder();
            holder.txtNomeAluno = convertView.findViewById(R.id.txt_nome_aluno);
            holder.txtDestino = convertView.findViewById(R.id.txt_destino);
            holder.txtHorario = convertView.findViewById(R.id.txt_horario_aluno);
            holder.imgStatus = convertView.findViewById(R.id.img_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        com.example.edutransporte.Aluno aluno = alunos.get(position);
        holder.txtNomeAluno.setText(aluno.getNome());
        holder.txtDestino.setText(aluno.getDestino());
        holder.txtHorario.setText(aluno.getHorario());

        if (aluno.isConfirmado()) {
            holder.imgStatus.setImageResource(R.drawable.ic_check_green);
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_close_red);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtNomeAluno;
        TextView txtDestino;
        TextView txtHorario;
        ImageView imgStatus;
    }
}