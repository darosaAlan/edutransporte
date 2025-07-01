package com.example.edutransporte;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class CadastroActivity extends AppCompatActivity {

    private EditText editNome, editEmail, editSenha, editConfirmarSenha, editTelefone;
    private RadioGroup radioGroupTipo;
    private Button btnCadastrar;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        preferencesManager = new PreferencesManager(this);

        inicializarComponentes();
        configurarEventos();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Cadastro");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void inicializarComponentes() {
        editNome = findViewById(R.id.edit_nome_cadastro);
        editEmail = findViewById(R.id.edit_email_cadastro);
        editSenha = findViewById(R.id.edit_senha_cadastro);
        editConfirmarSenha = findViewById(R.id.edit_confirmar_senha);
        editTelefone = findViewById(R.id.edit_telefone_cadastro);
        radioGroupTipo = findViewById(R.id.radio_group_tipo_cadastro);
        btnCadastrar = findViewById(R.id.btn_confirmar_cadastro);
    }

    private void configurarEventos() {
        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarCadastro();
            }
        });
    }

    private void realizarCadastro() {
        String nome = editNome.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();
        String confirmarSenha = editConfirmarSenha.getText().toString().trim();
        String telefone = editTelefone.getText().toString().trim();

        // Validações
        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || telefone.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!senha.equals(confirmarSenha)) {
            Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
            return;
        }

        if (senha.length() < 6) {
            Toast.makeText(this, "A senha deve ter no mínimo 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoId = radioGroupTipo.getCheckedRadioButtonId();
        if (tipoId == -1) {
            Toast.makeText(this, "Selecione o tipo de usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioTipo = findViewById(tipoId);
        String tipoUsuario = radioTipo.getText().toString().contains("Motorista") ? "Motorista" : "Aluno";

        // Verificar se email já existe
        List<Usuario> usuarios = preferencesManager.getUsuarios();
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                Toast.makeText(this, "Este email já está cadastrado", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Criar novo usuário
        Usuario novoUsuario = new Usuario(nome, email, senha, tipoUsuario);
        novoUsuario.setTelefone(telefone);

        // Salvar usuário
        preferencesManager.salvarUsuario(novoUsuario);

        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_LONG).show();

        // Voltar para login
        finish();
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