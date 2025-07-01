package com.example.edutransporte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.edutransporte.R;

import java.util.List;

public class AlunoAdapter extends BaseAdapter {
    private Context context;
    private List<com.example.edutransporte.Aluno> alunos;
    private LayoutInflater inflater;
    private boolean modoViagem = false; // Indica se está na tela de viagem
    private NotificationHelper notificationHelper;

    public AlunoAdapter(Context context, List<com.example.edutransporte.Aluno> alunos) {
        this.context = context;
        this.alunos = alunos;
        this.inflater = LayoutInflater.from(context);
        this.notificationHelper = new NotificationHelper(context);
    }

    public AlunoAdapter(Context context, List<com.example.edutransporte.Aluno> alunos, boolean modoViagem) {
        this.context = context;
        this.alunos = alunos;
        this.inflater = LayoutInflater.from(context);
        this.modoViagem = modoViagem;
        this.notificationHelper = new NotificationHelper(context);
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
            holder.txtStatus = convertView.findViewById(R.id.txt_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        com.example.edutransporte.Aluno aluno = alunos.get(position);
        holder.txtNomeAluno.setText(aluno.getNome());
        holder.txtDestino.setText("📍 " + aluno.getDestino());
        holder.txtHorario.setText("🕐 " + aluno.getHorario());

        // Atualizar status visual
        if (aluno.isConfirmado()) {
            holder.imgStatus.setImageResource(R.drawable.ic_check_green);
            holder.txtStatus.setText("✅ Confirmado");
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.imgStatus.setImageResource(R.drawable.ic_close_red);
            holder.txtStatus.setText("⏳ Aguardando");
            holder.txtStatus.setTextColor(context.getResources().getColor(android.R.color.holo_orange_dark));
        }

        // Adicionar interação se estiver no modo viagem
        if (modoViagem) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleConfirmacao(position);
                }
            });
            
            // Adicionar indicador visual de que é clicável
            convertView.setBackgroundResource(R.drawable.bg_item_aluno);
        } else {
            convertView.setOnClickListener(null);
            convertView.setBackgroundResource(android.R.color.transparent);
        }

        return convertView;
    }

    private void toggleConfirmacao(int position) {
        com.example.edutransporte.Aluno aluno = alunos.get(position);
        boolean novoStatus = !aluno.isConfirmado();
        aluno.setEmbarcou(novoStatus);
        
        // Notificar mudança
        notifyDataSetChanged();
        
        // Enviar notificação se o aluno foi confirmado
        if (novoStatus && modoViagem) {
            notificationHelper.notificarAlunoConfirmado(aluno.getNome());
        }
        
        // Feedback para o usuário
        String mensagem = novoStatus ? 
            "✅ " + aluno.getNome() + " confirmado!" : 
            "❌ Confirmação removida para " + aluno.getNome();
        Toast.makeText(context, mensagem, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder {
        TextView txtNomeAluno;
        TextView txtDestino;
        TextView txtHorario;
        ImageView imgStatus;
        TextView txtStatus;
    }
}