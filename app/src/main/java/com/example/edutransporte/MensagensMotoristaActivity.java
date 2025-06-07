// MensagensMotoristaActivity.java - Tela de Mensagens do Motorista
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MensagensMotoristaActivity extends AppCompatActivity {

    private ListView listViewMensagens;
    private EditText editTextMensagem;
    private Button btnEnviarMensagem;
    private List<Mensagem> listaMensagens;
    private MensagemAdapter mensagemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens_motorista);

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
        // Mensagens de exemplo do motorista
        listaMensagens.add(new Mensagem("Motorista", "Bom dia, pais! Hoje sairemos no horário.", "07:00", false));
        listaMensagens.add(new Mensagem("João (Pai)", "Bom dia! Obrigado pela informação.", "07:05", true));
        listaMensagens.add(new Mensagem("Maria (Mãe)", "Minha filha Ana está pronta!", "07:10", true));
        listaMensagens.add(new Mensagem("Motorista", "Perfeito! Chegando em 5 minutos.", "07:15", false));

        mensagemAdapter.notifyDataSetChanged();
    }

    private void enviarMensagem() {
        String textoMensagem = editTextMensagem.getText().toString().trim();

        if (textoMensagem.isEmpty()) {
            return;
        }

        // Adicionar mensagem à lista
        String horarioAtual = java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT).format(new java.util.Date());
        Mensagem novaMensagem = new Mensagem("Motorista", textoMensagem, horarioAtual, false);

        listaMensagens.add(novaMensagem);
        mensagemAdapter.notifyDataSetChanged();

        // Limpar campo de texto
        editTextMensagem.setText("");

        // Scroll para a última mensagem
        listViewMensagens.smoothScrollToPosition(listaMensagens.size() - 1);
    }
}