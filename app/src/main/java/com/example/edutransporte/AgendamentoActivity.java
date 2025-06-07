// AgendamentoActivity.java - Tela de Agendamento de Dias
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.edutransporte.R;

import java.util.ArrayList;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnSalvarAgendamento, btnLimparSelecao;
    private List<String> diasSelecionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        inicializarComponentes();
        configurarEventos();
        diasSelecionados = new ArrayList<>();
    }

    private void inicializarComponentes() {
        calendarView = findViewById(R.id.calendar_view);
        btnSalvarAgendamento = findViewById(R.id.btn_salvar_agendamento);
        btnLimparSelecao = findViewById(R.id.btn_limpar_selecao);
    }

    private void configurarEventos() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String dataSelecionada = dayOfMonth + "/" + (month + 1) + "/" + year;

                if (diasSelecionados.contains(dataSelecionada)) {
                    diasSelecionados.remove(dataSelecionada);
                    Toast.makeText(AgendamentoActivity.this, "Dia removido: " + dataSelecionada, Toast.LENGTH_SHORT).show();
                } else {
                    diasSelecionados.add(dataSelecionada);
                    Toast.makeText(AgendamentoActivity.this, "Dia selecionado: " + dataSelecionada, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnSalvarAgendamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salvarAgendamento();
            }
        });

        btnLimparSelecao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diasSelecionados.clear();
                Toast.makeText(AgendamentoActivity.this, "Seleção limpa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void salvarAgendamento() {
        if (diasSelecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um dia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Implementar salvamento no banco de dados ou API
        StringBuilder diasTexto = new StringBuilder("Dias salvos:\n");
        for (String dia : diasSelecionados) {
            diasTexto.append("• ").append(dia).append("\n");
        }

        Toast.makeText(this, diasTexto.toString(), Toast.LENGTH_LONG).show();
        finish();
    }
}