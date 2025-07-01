package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText editEmail, editSenha;
    private RadioGroup radioGroupTipoUsuario;
    private Button btnEntrar, btnCadastrar;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: Iniciando MainActivity");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);

        // Verificar se já há usuário logado
        Usuario usuarioLogado = preferencesManager.getUsuarioLogado();
        if (usuarioLogado != null) {
            Log.d(TAG, "Usuário já logado: " + usuarioLogado.getNome());
            redirecionarParaDashboard(usuarioLogado);
            return;
        }

        inicializarComponentes();
        configurarEventos();

        // Criar usuários de teste
        criarUsuariosDeTeste();
    }

    private void inicializarComponentes() {
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        radioGroupTipoUsuario = findViewById(R.id.radio_group_tipo_usuario);
        btnEntrar = findViewById(R.id.btn_entrar);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
    }

    private void configurarEventos() {
        btnEntrar.setOnClickListener(v -> fazerLogin());
        btnCadastrar.setOnClickListener(v -> abrirCadastro());
    }

    private void fazerLogin() {
        String email = editEmail.getText().toString().trim();
        String senha = editSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int tipoUsuarioId = radioGroupTipoUsuario.getCheckedRadioButtonId();
        if (tipoUsuarioId == -1) {
            Toast.makeText(this, "Selecione o tipo de usuário", Toast.LENGTH_SHORT).show();
            return;
        }

        RadioButton radioButton = findViewById(tipoUsuarioId);
        String tipoUsuario = radioButton.getText().toString();

        Log.d(TAG, "Tipo de usuário selecionado: " + tipoUsuario);

        // Buscar usuário no banco local
        Usuario usuario = preferencesManager.buscarUsuario(email);

        if (usuario != null) {
            Log.d(TAG, "Usuário encontrado: " + usuario.getNome());

            // Verificar senha
            if (usuario.getSenha().equals(senha)) {
                Log.d(TAG, "Senha correta, verificando tipo...");

                // Verificar tipo de usuário
                String tipoSalvo = usuario.getTipoUsuario();
                if ((tipoUsuario.equals("Motorista") && tipoSalvo.equals("Motorista")) ||
                        (tipoUsuario.equals("Aluno") && tipoSalvo.equals("Aluno"))) {

                    Log.d(TAG, "Tipo de usuário correto, fazendo login...");

                    // Salvar usuário logado
                    preferencesManager.salvarUsuarioLogado(usuario);

                    // Redirecionar para dashboard apropriado
                    redirecionarParaDashboard(usuario);

                    Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    Log.w(TAG, "Tipo de usuário incorreto. Esperado: " + tipoSalvo + ", Selecionado: " + tipoUsuario);
                    Toast.makeText(this, "Tipo de usuário incorreto", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.w(TAG, "Senha incorreta");
                Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.w(TAG, "Usuário não encontrado: " + email);
            Toast.makeText(this, "Usuário não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void redirecionarParaDashboard(Usuario usuario) {
        Log.d(TAG, "Navegando para dashboard do tipo: " + usuario.getTipoUsuario());

        Intent intent;
        if (usuario.getTipoUsuario().equals("Motorista")) {
            intent = new Intent(MainActivity.this, DashboardMotoristaActivity.class);
        } else {
            intent = new Intent(MainActivity.this, DashboardAlunoActivity.class);
        }

        // NÃO usar flags que limpam a task
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish(); // Apenas finaliza a MainActivity
    }

    private void abrirCadastro() {
        Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    private void criarUsuariosDeTeste() {
        // Verificar se já existem usuários
        if (preferencesManager.getUsuarios().isEmpty()) {
            Log.d(TAG, "Criando usuários de teste...");

            // Criar aluno de teste
            Usuario aluno = new Usuario("Ana Silva", "aluno@teste.com", "123456", "Aluno");
            preferencesManager.salvarUsuario(aluno);

            // Criar motorista de teste
            Usuario motorista = new Usuario("João Santos", "motorista@teste.com", "123456", "Motorista");
            preferencesManager.salvarUsuario(motorista);

            Toast.makeText(this, "Usuários de teste criados!", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "Usuários criados com sucesso!");
        } else {
            Log.d(TAG, "Usuários já existem, total: " + preferencesManager.getUsuarios().size());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: MainActivity retomada");

        // Verificar novamente se há usuário logado
        Usuario usuarioLogado = preferencesManager.getUsuarioLogado();
        if (usuarioLogado != null) {
            Log.d(TAG, "onResume: Usuário ainda logado, redirecionando...");
            redirecionarParaDashboard(usuarioLogado);
        }
    }
}