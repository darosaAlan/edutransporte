package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardAlunoActivity extends AppCompatActivity implements LocalizacaoTempoReal.LocalizacaoListener {

    private static final String TAG = "DashboardAlunoActivity";

    private TextView txtNomeAluno;
    private TextView txtEmailAluno;
    private TextView txtStatusLocalizacao;
    private TextView txtInfoVan;
    private CardView cardLocalizacao;
    private Button btnLocalizacao;
    private Button btnVerLocalizacao;
    private Button btnMensagens;
    private Button btnAgendamento;
    private Button btnConfiguracoes;
    private Button btnLogout;

    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;
    private LocalizacaoTempoReal localizacaoTempoReal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_aluno);

        Log.d(TAG, "onCreate: Iniciando DashboardAlunoActivity");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();

        // Verificar se há usuário logado
        if (usuarioLogado == null) {
            Log.e(TAG, "Nenhum usuário logado encontrado");
            Toast.makeText(this, "Erro: Usuário não encontrado", Toast.LENGTH_LONG).show();
            voltarParaLogin();
            return;
        }

        // Inicializar Firebase para localização
        localizacaoTempoReal = new LocalizacaoTempoReal(this);

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard do Aluno");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Log.d(TAG, "ActionBar configurada com botão voltar");
        } else {
            Log.e(TAG, "ActionBar não encontrada");
        }

        inicializarViews();
        configurarEventos();
        carregarDadosUsuario();
        iniciarEscutaLocalizacao();
    }

    private void inicializarViews() {
        txtNomeAluno = findViewById(R.id.txt_nome_aluno);
        txtEmailAluno = findViewById(R.id.txt_email_aluno);
        txtStatusLocalizacao = findViewById(R.id.txt_status_localizacao);
        txtInfoVan = findViewById(R.id.txt_info_van);
        cardLocalizacao = findViewById(R.id.card_localizacao);
        btnLocalizacao = findViewById(R.id.btn_localizacao);
        btnVerLocalizacao = findViewById(R.id.btn_ver_localizacao);
        btnMensagens = findViewById(R.id.btn_mensagens);
        btnAgendamento = findViewById(R.id.btn_agendamento);
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void configurarEventos() {
        btnLocalizacao.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocalizacaoActivity.class);
            startActivity(intent);
        });

        btnVerLocalizacao.setOnClickListener(v -> {
            Intent intent = new Intent(this, LocalizacaoActivity.class);
            startActivity(intent);
        });

        btnMensagens.setOnClickListener(v -> {
            Intent intent = new Intent(this, MensagensActivity.class);
            startActivity(intent);
        });

        btnAgendamento.setOnClickListener(v -> {
            Intent intent = new Intent(this, AgendamentoActivity.class);
            startActivity(intent);
        });

        btnConfiguracoes.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConfiguracoesActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            fazerLogout();
        });
    }

    private void carregarDadosUsuario() {
        if (usuarioLogado != null) {
            txtNomeAluno.setText("👤 " + usuarioLogado.getNome());
            txtEmailAluno.setText("📧 " + usuarioLogado.getEmail());
        }
    }

    private void iniciarEscutaLocalizacao() {
        Log.d(TAG, "Iniciando escuta de localização em tempo real");
        localizacaoTempoReal.iniciarEscutaLocalizacao(this);
    }

    // Implementação da interface LocalizacaoListener
    @Override
    public void onLocalizacaoAtualizada(double latitude, double longitude, String motorista, String placa) {
        Log.d(TAG, "Localização atualizada: " + latitude + ", " + longitude);
        
        runOnUiThread(() -> {
            // Atualizar status
            txtStatusLocalizacao.setText("🟢 Van em movimento");
            txtStatusLocalizacao.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            
            // Atualizar informações da van
            StringBuilder infoVan = new StringBuilder();
            if (motorista != null && !motorista.isEmpty()) {
                infoVan.append("👨‍💼 ").append(motorista).append("\n");
            }
            if (placa != null && !placa.isEmpty()) {
                infoVan.append("🚐 ").append(placa);
            }
            
            if (infoVan.length() > 0) {
                txtInfoVan.setText(infoVan.toString());
            }
            
            // Mostrar card de localização
            cardLocalizacao.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void onErro(String mensagem) {
        Log.e(TAG, "Erro na localização: " + mensagem);
        
        runOnUiThread(() -> {
            txtStatusLocalizacao.setText("🔴 " + mensagem);
            txtStatusLocalizacao.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            txtInfoVan.setText("Van não está em movimento");
            
            // Ocultar card de localização se não há van em movimento
            if (mensagem.contains("não está em movimento")) {
                cardLocalizacao.setVisibility(View.GONE);
            }
        });
    }

    private void voltarParaLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: Item ID = " + item.getItemId());
        
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Botão voltar pressionado - fazendo logout");
            // Botão voltar - fazer logout
            fazerLogout();
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            Log.d(TAG, "Menu logout pressionado - fazendo logout");
            fazerLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fazerLogout() {
        Log.d(TAG, "Iniciando processo de logout");
        
        // Limpar dados do usuário
        preferencesManager.limparDadosUsuario();
        
        // Parar escuta de localização
        if (localizacaoTempoReal != null) {
            localizacaoTempoReal.pararEscutaLocalizacao();
        }
        
        // Voltar para tela de login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Logout concluído");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Dashboard do aluno retomado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Dashboard do aluno pausado");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Parar escuta de localização
        localizacaoTempoReal.pararEscutaLocalizacao();
    }
}