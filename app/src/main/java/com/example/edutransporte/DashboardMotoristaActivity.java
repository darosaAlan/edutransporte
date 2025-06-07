// DashboardMotoristaActivity.java - Dashboard do Motorista
package com.example.edutransporte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.edutransporte.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardMotoristaActivity extends AppCompatActivity {

    private TextView txtNomeMotorista, txtVeiculoInfo, txtTotalAlunos;
    private ListView listViewAlunos;
    private Button btnIniciarViagem, btnMensagens, btnConfiguracoes;
    private List<Aluno> listaAlunos;
    private AlunoAdapter alunoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_motorista);

        inicializarComponentes();
        carregarDados();
        configurarEventos();
    }

    private void inicializarComponentes() {
        txtNomeMotorista = findViewById(R.id.txt_nome_motorista);
        txtVeiculoInfo = findViewById(R.id.txt_veiculo_info);
        txtTotalAlunos = findViewById(R.id.txt_total_alunos);
        listViewAlunos = findViewById(R.id.list_view_alunos);
        btnIniciarViagem = findViewById(R.id.btn_iniciar_viagem);
        btnMensagens = findViewById(R.id.btn_mensagens_motorista);
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);

        listaAlunos = new ArrayList<>();
        alunoAdapter = new AlunoAdapter(this, listaAlunos);
        listViewAlunos.setAdapter(alunoAdapter);
    }

    private void carregarDados() {
        txtNomeMotorista.setText("Motorista: João Silva");
        txtVeiculoInfo.setText("Veículo: Van Escolar - ABC-1234");

        // Carregar lista de alunos
        listaAlunos.add(new com.example.edutransporte.Aluno("Ana Silva", "Colégio São José", true, "07:30"));
        listaAlunos.add(new Aluno("Carlos Santos", "Faculdade Tech", true, "07:35"));
        listaAlunos.add(new Aluno("Maria Oliveira", "Colégio São José", false, "07:40"));
        listaAlunos.add(new Aluno("Pedro Costa", "Faculdade Tech", true, "07:45"));

        txtTotalAlunos.setText("Total de alunos hoje: " + listaAlunos.stream().mapToInt(aluno -> aluno.isConfirmado() ? 1 : 0).sum());
        alunoAdapter.notifyDataSetChanged();
    }

    private void configurarEventos() {
        btnIniciarViagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardMotoristaActivity.this, ViagemActivity.class);
                startActivity(intent);
            }
        });

        btnMensagens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardMotoristaActivity.this, MensagensMotoristaActivity.class);
                startActivity(intent);
            }
        });

        btnConfiguracoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardMotoristaActivity.this, ConfiguracoesActivity.class);
                startActivity(intent);
            }
        });
    }
}