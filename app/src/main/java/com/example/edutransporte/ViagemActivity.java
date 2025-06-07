// ViagemActivity.java - Tela de Gerenciamento de Viagens
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViagemActivity extends AppCompatActivity {

    private TextView textViewStatusViagem;
    private Button btnIniciarViagem;
    private Button btnFinalizarViagem;
    private ListView listViewAlunos;
    private List<Aluno> listaAlunos;
    private AlunoAdapter alunoAdapter;
    private boolean viagemIniciada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viagem);

        inicializarComponentes();
        configurarEventos();
        carregarAlunos();
        atualizarStatusViagem();
    }

    private void inicializarComponentes() {
        textViewStatusViagem = findViewById(R.id.text_view_status_viagem);
        btnIniciarViagem = findViewById(R.id.btn_iniciar_viagem);
        btnFinalizarViagem = findViewById(R.id.btn_finalizar_viagem);
        listViewAlunos = findViewById(R.id.list_view_alunos_viagem);

        listaAlunos = new ArrayList<>();
        alunoAdapter = new AlunoAdapter(this, listaAlunos);
        listViewAlunos.setAdapter(alunoAdapter);
    }

    private void configurarEventos() {
        btnIniciarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarViagem();
            }
        });

        btnFinalizarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarViagem();
            }
        });
    }

    private void carregarAlunos() {
        // Dados de exemplo - substitua por dados reais do seu banco/API
        listaAlunos.add(new Aluno("João Silva", "Rua das Flores, 123", false, "07:30"));
        listaAlunos.add(new Aluno("Maria Santos", "Av. Principal, 456", false, "07:35"));
        listaAlunos.add(new Aluno("Pedro Costa", "Rua do Centro, 789", false, "07:40"));
        listaAlunos.add(new Aluno("Ana Oliveira", "Rua da Escola, 321", false, "07:45"));

        alunoAdapter.notifyDataSetChanged();
    }

    private void iniciarViagem() {
        if (!viagemIniciada) {
            viagemIniciada = true;
            atualizarStatusViagem();
            Toast.makeText(this, "Viagem iniciada!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Viagem já está em andamento", Toast.LENGTH_SHORT).show();
        }
    }

    private void finalizarViagem() {
        if (viagemIniciada) {
            viagemIniciada = false;
            atualizarStatusViagem();
            Toast.makeText(this, "Viagem finalizada!", Toast.LENGTH_SHORT).show();

            // Reset status dos alunos
            for (Aluno aluno : listaAlunos) {
                aluno.setEmbarcou(false);
            }
            alunoAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "Nenhuma viagem em andamento", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarStatusViagem() {
        if (viagemIniciada) {
            textViewStatusViagem.setText("Status: Viagem em Andamento");
            textViewStatusViagem.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            btnIniciarViagem.setEnabled(false);
            btnFinalizarViagem.setEnabled(true);
        } else {
            textViewStatusViagem.setText("Status: Aguardando Início");
            textViewStatusViagem.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            btnIniciarViagem.setEnabled(true);
            btnFinalizarViagem.setEnabled(false);
        }
    }
}