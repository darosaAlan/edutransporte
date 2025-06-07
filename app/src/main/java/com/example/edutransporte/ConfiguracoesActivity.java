// ConfiguracoesActivity.java - Tela de Configurações
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracoesActivity extends AppCompatActivity {

    private EditText editTextNomeMotorista;
    private EditText editTextTelefone;
    private EditText editTextPlacaVeiculo;
    private Switch switchNotificacoes;
    private Switch switchLocalizacao;
    private Button btnSalvarConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        inicializarComponentes();
        configurarEventos();
        carregarConfiguracoes();
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
        // Carregar configurações salvas (exemplo com dados padrão)
        editTextNomeMotorista.setText("João Silva");
        editTextTelefone.setText("(11) 99999-9999");
        editTextPlacaVeiculo.setText("ABC-1234");
        switchNotificacoes.setChecked(true);
        switchLocalizacao.setChecked(true);
    }

    private void salvarConfiguracoes() {
        String nome = editTextNomeMotorista.getText().toString().trim();
        String telefone = editTextTelefone.getText().toString().trim();
        String placa = editTextPlacaVeiculo.getText().toString().trim();

        if (nome.isEmpty() || telefone.isEmpty() || placa.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Aqui você salvaria as configurações (SharedPreferences, banco, etc.)
        boolean notificacoesAtivas = switchNotificacoes.isChecked();
        boolean localizacaoAtiva = switchLocalizacao.isChecked();

        // Simular salvamento
        Toast.makeText(this, "Configurações salvas com sucesso!", Toast.LENGTH_SHORT).show();

        // Opcional: voltar para tela anterior
        finish();
    }
}