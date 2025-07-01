// ConfiguracoesActivity.java - Tela de Configurações
package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracoesActivity extends AppCompatActivity {

    private static final String TAG = "ConfiguracoesActivity";

    private EditText editTextNomeMotorista;
    private EditText editTextTelefone;
    private EditText editTextPlacaVeiculo;
    private Switch switchNotificacoes;
    private Switch switchLocalizacao;
    private Button btnSalvarConfiguracoes;

    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // REDIRECIONAMENTO TEMPORÁRIO - FORÇA IR PARA LOGIN
        // Verifica se veio do launcher (não de outra activity)
        if (getIntent().getCategories() != null &&
                getIntent().getCategories().contains(Intent.CATEGORY_LAUNCHER)) {

            Log.d(TAG, "App iniciado direto em ConfiguracoesActivity - Redirecionando para MainActivity");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_configuracoes);

        Log.d(TAG, "onCreate: Iniciando ConfiguracoesActivity normalmente");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();

        // Verificar se há usuário logado
        if (usuarioLogado == null) {
            Log.e(TAG, "Nenhum usuário logado! Redirecionando para login...");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "Usuário logado: " + usuarioLogado.getEmail() + " - Tipo: " + usuarioLogado.getTipoUsuario());

        // Verificar se é motorista
        if (!usuarioLogado.getTipoUsuario().equals("Motorista")) {
            Toast.makeText(this, "Acesso restrito a motoristas", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        inicializarComponentes();
        configurarEventos();
        carregarConfiguracoes();

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Configurações");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void inicializarComponentes() {
        editTextNomeMotorista = findViewById(R.id.edit_text_nome_motorista);
        editTextTelefone = findViewById(R.id.edit_text_telefone);
        editTextPlacaVeiculo = findViewById(R.id.edit_text_placa_veiculo);
        switchNotificacoes = findViewById(R.id.switch_notificacoes);
        switchLocalizacao = findViewById(R.id.switch_localizacao);
        btnSalvarConfiguracoes = findViewById(R.id.btn_salvar_configuracoes);
    }

    private void configurarEventos() {
        btnSalvarConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarConfiguracoes();
            }
        });
    }

    private void carregarConfiguracoes() {
        Log.d(TAG, "carregarConfiguracoes: Carregando dados salvos...");

        String emailUsuario = usuarioLogado.getEmail();

        // Carregar dados salvos específicos para este usuário
        String nomeSalvo = preferencesManager.getConfiguracao("nome_motorista_" + emailUsuario, "");
        String telefoneSalvo = preferencesManager.getConfiguracao("telefone_motorista_" + emailUsuario, "");
        String placaSalva = preferencesManager.getConfiguracao("placa_veiculo_" + emailUsuario, "");

        // Se não há dados salvos, usar dados básicos do usuário (se disponíveis)
        if (nomeSalvo.isEmpty() && usuarioLogado.getNome() != null && !usuarioLogado.getNome().isEmpty()) {
            nomeSalvo = usuarioLogado.getNome();
        }
        if (telefoneSalvo.isEmpty() && usuarioLogado.getTelefone() != null && !usuarioLogado.getTelefone().isEmpty()) {
            telefoneSalvo = usuarioLogado.getTelefone();
        }

        // Preencher campos (deixar vazio se não há dados)
        editTextNomeMotorista.setText(nomeSalvo);
        editTextTelefone.setText(telefoneSalvo);
        editTextPlacaVeiculo.setText(placaSalva);

        Log.d(TAG, "Dados carregados - Nome: '" + nomeSalvo + "', Telefone: '" + telefoneSalvo + "', Placa: '" + placaSalva + "'");

        // Carregar estado dos switches (padrão: desligado para não incomodar)
        boolean notificacoesAtivas = preferencesManager.getConfiguracaoBoolean(
                "notificacoes_" + emailUsuario, false);
        boolean localizacaoAtiva = preferencesManager.getConfiguracaoBoolean(
                "localizacao_" + emailUsuario, false);

        switchNotificacoes.setChecked(notificacoesAtivas);
        switchLocalizacao.setChecked(localizacaoAtiva);
    }

    private void salvarConfiguracoes() {
        Log.d(TAG, "salvarConfiguracoes: Iniciando...");

        String nome = editTextNomeMotorista.getText().toString().trim();
        String telefone = editTextTelefone.getText().toString().trim();
        String placa = editTextPlacaVeiculo.getText().toString().trim().toUpperCase();

        // Validações básicas
        if (nome.isEmpty()) {
            Toast.makeText(this, "Por favor, informe o nome do motorista", Toast.LENGTH_SHORT).show();
            editTextNomeMotorista.requestFocus();
            return;
        }

        if (telefone.isEmpty()) {
            Toast.makeText(this, "Por favor, informe o telefone", Toast.LENGTH_SHORT).show();
            editTextTelefone.requestFocus();
            return;
        }

        if (placa.isEmpty()) {
            Toast.makeText(this, "Por favor, informe a placa do veículo", Toast.LENGTH_SHORT).show();
            editTextPlacaVeiculo.requestFocus();
            return;
        }

        // Validação simples de placa (formato brasileiro)
        if (placa.length() < 7 || placa.length() > 8) {
            Toast.makeText(this, "Formato de placa inválido. Use ABC-1234 ou ABC1D23", Toast.LENGTH_SHORT).show();
            editTextPlacaVeiculo.requestFocus();
            return;
        }

        Log.d(TAG, "Salvando - Nome: '" + nome + "', Telefone: '" + telefone + "', Placa: '" + placa + "'");

        try {
            String emailUsuario = usuarioLogado.getEmail();

            // Salvar configurações específicas do usuário
            preferencesManager.salvarConfiguracao("nome_motorista_" + emailUsuario, nome);
            preferencesManager.salvarConfiguracao("telefone_motorista_" + emailUsuario, telefone);
            preferencesManager.salvarConfiguracao("placa_veiculo_" + emailUsuario, placa);

            // Atualizar também o objeto usuário
            usuarioLogado.setNome(nome);
            usuarioLogado.setTelefone(telefone);
            preferencesManager.salvarUsuario(usuarioLogado);
            preferencesManager.salvarUsuarioLogado(usuarioLogado);

            // Salvar estado dos switches
            preferencesManager.salvarConfiguracaoBoolean("notificacoes_" + emailUsuario, switchNotificacoes.isChecked());
            preferencesManager.salvarConfiguracaoBoolean("localizacao_" + emailUsuario, switchLocalizacao.isChecked());

            Log.d(TAG, "Todas as configurações foram salvas com sucesso!");

            // Mostrar mensagem de sucesso
            Toast.makeText(this, "Configurações salvas com sucesso!", Toast.LENGTH_LONG).show();

            // Voltar para a tela anterior após salvar
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Erro ao salvar configurações: " + e.getMessage());
            Toast.makeText(this, "Erro ao salvar configurações. Tente novamente.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}