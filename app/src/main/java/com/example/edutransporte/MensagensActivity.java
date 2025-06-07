// MensagensActivity.java - Tela de Mensagens
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.edutransporte.R;

import java.util.ArrayList;
import java.util.List;

public class MensagensActivity extends AppCompatActivity {

    private ListView listViewMensagens;
    private EditText editTextMensagem;
    private Button btnEnviarMensagem;
    private List<Mensagem> listaMensagens;
    private MensagemAdapter mensagemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);

        inicializarComponentes();
        configurarEventos();
        carregarMensagens();
    }

    private void inicializarComponentes() {
        listViewMensagens = findViewById(R.id.list_view_mensagens);
        editTextMensagem = findViewById(R.id.edit_text_mensagem);
        btnEnviarMensagem = findViewById(R.id.btn_enviar_mensagem);

        listaMensagens = new ArrayList<>();
        mensagemAdapter = new MensagemAdapter(this, listaMensagens);
        listViewMensagens.setAdapter(mensagemAdapter);
    }

    private void configurarEventos() {
        btnEnviarMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensagem();
            }
        });
    }

    private void carregarMensagens() {
        // Carregar mensagens existentes
        listaMensagens.add(new Mensagem("Motorista", "Bom dia! Hoje sairemos às 7:30h", "09:00", false));
        listaMensagens.add(new Mensagem("Você", "Ok, obrigado!", "09:01", true));
        listaMensagens.add(new Mensagem("Motorista", "Haverá um pequeno atraso devido ao trânsito", "09:15", false));

        mensagemAdapter.notifyDataSetChanged();
    }

    private void enviarMensagem() {
        String textoMensagem = editTextMensagem.getText().toString().trim();

        if (textoMensagem.isEmpty()) {
            return;
        }

        // Adicionar mensagem à lista
        String horarioAtual = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date());
        Mensagem novaMensagem = new Mensagem("Você", textoMensagem, horarioAtual, true);

        listaMensagens.add(novaMensagem);
        mensagemAdapter.notifyDataSetChanged();

        // Limpar campo de texto
        editTextMensagem.setText("");

        // Scroll para a última mensagem
        listViewMensagens.smoothScrollToPosition(listaMensagens.size() - 1);
    }
}