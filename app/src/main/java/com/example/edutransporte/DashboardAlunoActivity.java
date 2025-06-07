package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class DashboardAlunoActivity extends AppCompatActivity {

    private TextView txtNomeUsuario, txtHorario, txtDestino, txtMotorista;
    private Button btnMarcarDias, btnMensagens, btnLocalizacao;
    private CardView cardProximaViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_aluno);

        inicializarComponentes();
        carregarDados();
        configurarEventos();
    }

    private void inicializarComponentes() {
        txtNomeUsuario = findViewById(R.id.txt_nome_usuario);
        txtHorario = findViewById(R.id.txt_horario);
        txtDestino = findViewById(R.id.txt_destino);
        txtMotorista = findViewById(R.id.txt_motorista);
        btnMarcarDias = findViewById(R.id.btn_marcar_dias);
        btnMensagens = findViewById(R.id.btn_mensagens);
        btnLocalizacao = findViewById(R.id.btn_localizacao);
        cardProximaViagem = findViewById(R.id.card_proxima_viagem);
    }

    private void carregarDados() {
        // Carregar dados do usuário e próxima viagem
        txtNomeUsuario.setText("Olá, Ana! 👋");
        txtHorario.setText("Horário: 07:30");
        txtDestino.setText("Destino: Colégio São José");
        txtMotorista.setText("📍 Motorista: João Silva");
    }

    private void configurarEventos() {
        btnMarcarDias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajuste o pacote e nome da Activity abaixo conforme o seu projeto
                Intent intent = new Intent(DashboardAlunoActivity.this, AgendamentoActivity.class);
                startActivity(intent);
            }
        });

        btnMensagens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajuste o pacote e nome da Activity abaixo conforme o seu projeto
                Intent intent = new Intent(DashboardAlunoActivity.this, MensagensActivity.class);
                startActivity(intent);
            }
        });

        btnLocalizacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardAlunoActivity.this, LocalizacaoActivity.class);
                startActivity(intent);
            }
        });
    }
}
