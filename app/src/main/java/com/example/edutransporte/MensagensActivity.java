// MensagensActivity.java - Tela de Mensagens do Aluno
package com.example.edutransporte;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MensagensActivity extends AppCompatActivity {

    private static final String TAG = "MensagensAluno";

    private ListView listViewMensagens;
    private EditText editTextMensagem;
    private Button btnEnviarMensagem;
    private List<Mensagem> listaMensagens;
    private MensagemAdapter mensagemAdapter;
    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensagens);

        Log.d(TAG, "onCreate: Iniciando tela de mensagens do aluno");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();
        
        // Inicializar NotificationHelper
        notificationHelper = new NotificationHelper(this);

        // Verificar se está logado
        if (usuarioLogado == null || !usuarioLogado.getTipoUsuario().equals("Aluno")) {
            Toast.makeText(this, "Acesso negado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mensagens");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

        // Permitir enviar mensagem ao pressionar Enter
        editTextMensagem.setOnEditorActionListener((v, actionId, event) -> {
            enviarMensagem();
            return true;
        });
    }

    private void carregarMensagens() {
        Log.d(TAG, "Carregando mensagens salvas...");

        // Limpar lista atual
        listaMensagens.clear();

        // Carregar mensagens salvas (mesmas mensagens que o motorista vê)
        List<Mensagem> mensagensSalvas = preferencesManager.getMensagens();

        if (mensagensSalvas.isEmpty()) {
            Log.d(TAG, "Nenhuma mensagem salva encontrada");
        } else {
            Log.d(TAG, "Carregadas " + mensagensSalvas.size() + " mensagens");
            listaMensagens.addAll(mensagensSalvas);
        }

        mensagemAdapter.notifyDataSetChanged();

        // Rolar para a última mensagem se houver
        if (!listaMensagens.isEmpty()) {
            listViewMensagens.post(() -> listViewMensagens.setSelection(listaMensagens.size() - 1));
        }
    }

    private void enviarMensagem() {
        String textoMensagem = editTextMensagem.getText().toString().trim();

        if (textoMensagem.isEmpty()) {
            Toast.makeText(this, "Digite uma mensagem", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Enviando mensagem: " + textoMensagem);

        // Criar formato de hora
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String horarioAtual = sdf.format(new Date());

        // Criar nova mensagem do aluno
        Mensagem novaMensagem = new Mensagem(
                usuarioLogado.getNome(),  // Remetente = nome do aluno
                textoMensagem,            // Texto
                horarioAtual,             // Horário
                false                     // isEnviada = false para aluno
        );

        // Adicionar à lista local
        listaMensagens.add(novaMensagem);

        // Salvar no SharedPreferences
        preferencesManager.salvarMensagem(novaMensagem);
        
        // Enviar notificação para o motorista
        notificationHelper.notificarNovaMensagem(usuarioLogado.getNome(), textoMensagem);

        // Atualizar adapter
        mensagemAdapter.notifyDataSetChanged();

        // Limpar campo de texto
        editTextMensagem.setText("");

        // Scroll para a última mensagem
        listViewMensagens.post(() -> listViewMensagens.setSelection(listaMensagens.size() - 1));

        Log.d(TAG, "Mensagem enviada e salva com sucesso!");

        // Feedback visual
        Toast.makeText(this, "Mensagem enviada!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar mensagens ao voltar para a tela (para ver novas mensagens do motorista)
        carregarMensagens();
    }
}