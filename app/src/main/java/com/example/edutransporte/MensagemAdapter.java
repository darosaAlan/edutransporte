package com.example.edutransporte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.edutransporte.R;

import java.util.List;

public class MensagemAdapter extends BaseAdapter {
    private Context context;
    private List<Mensagem> mensagens;
    private LayoutInflater inflater;

    public MensagemAdapter(Context context, List<Mensagem> mensagens) {
        this.context = context;
        this.mensagens = mensagens;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mensagens.size();
    }

    @Override
    public Object getItem(int position) {
        return mensagens.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_mensagem, parent, false);
            holder = new ViewHolder();
            holder.txtRemetente = convertView.findViewById(R.id.txt_remetente);
            holder.txtMensagem = convertView.findViewById(R.id.txt_mensagem);
            holder.txtHorario = convertView.findViewById(R.id.txt_horario);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Mensagem mensagem = mensagens.get(position);
        holder.txtRemetente.setText(mensagem.getRemetente());
        holder.txtMensagem.setText(mensagem.getTexto());
        holder.txtHorario.setText(mensagem.getHorario());

        // Configurar estilo baseado no remetente
        if (mensagem.isEnviada()) {
            convertView.setBackgroundResource(R.drawable.bg_mensagem_enviada);
        } else {
            convertView.setBackgroundResource(R.drawable.bg_mensagem_recebida);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView txtRemetente;
        TextView txtMensagem;
        TextView txtHorario;
    }
}