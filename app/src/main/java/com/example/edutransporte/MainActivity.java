package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.edutransporte.R;

public class MainActivity extends AppCompatActivity {

    private EditText editEmail, editSenha;
    private RadioGroup radioGroupTipoUsuario;
    private Button btnEntrar, btnCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarComponentes();
        configurarEventos();
    }

    private void inicializarComponentes() {
        editEmail = findViewById(R.id.edit_email);
        editSenha = findViewById(R.id.edit_senha);
        radioGroupTipoUsuario = findViewById(R.id.radio_group_tipo_usuario);
        btnEntrar = findViewById(R.id.btn_entrar);
        btnCadastrar = findViewById(R.id.btn_cadastrar);
    }

    private void configurarEventos() {
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.edutransporte.AgendamentoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void realizarLogin() {
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

        // Simular autenticação
        if (validarCredenciais(email, senha)) {
            Intent intent;
            if (tipoUsuario.contains("Motorista")) {
                intent = new Intent(MainActivity.this, com.example.edutransporte.DashboardMotoristaActivity.class);
            } else {
                intent = new Intent(MainActivity.this, com.example.edutransporte.DashboardAlunoActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validarCredenciais(String email, String senha) {
        // Implementar validação real aqui
        return !email.isEmpty() && !senha.isEmpty();
    }
}
