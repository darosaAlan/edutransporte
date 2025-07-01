// AgendamentoActivity.java - Tela de Agendamento de Dias
package com.example.edutransporte;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.edutransporte.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgendamentoActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private Button btnSalvarAgendamento, btnLimparSelecao;
    private TextView txtDiasSelecionados;
    private List<String> diasSelecionados;
    private NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento);

        inicializarComponentes();
        configurarEventos();
        diasSelecionados = new ArrayList<>();
        
        // Inicializar NotificationHelper
        notificationHelper = new NotificationHelper(this);
    }

    private void inicializarComponentes() {
        calendarView = findViewById(R.id.calendar_view);
        btnSalvarAgendamento = findViewById(R.id.btn_salvar_agendamento);
        btnLimparSelecao = findViewById(R.id.btn_limpar_selecao);
        txtDiasSelecionados = findViewById(R.id.txt_dias_selecionados);
    }

    private void configurarEventos() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String dataSelecionada = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);

                if (diasSelecionados.contains(dataSelecionada)) {
                    diasSelecionados.remove(dataSelecionada);
                    Toast.makeText(AgendamentoActivity.this, "Dia removido: " + dataSelecionada, Toast.LENGTH_SHORT).show();
                } else {
                    diasSelecionados.add(dataSelecionada);
                    Toast.makeText(AgendamentoActivity.this, "Dia selecionado: " + dataSelecionada, Toast.LENGTH_SHORT).show();
                }

                atualizarTextoDiasSelecionados();
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
                atualizarTextoDiasSelecionados();
                Toast.makeText(AgendamentoActivity.this, "Seleção limpa", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarTextoDiasSelecionados() {
        if (diasSelecionados.isEmpty()) {
            txtDiasSelecionados.setText("Nenhum dia selecionado");
            txtDiasSelecionados.setTextColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            // Ordenar as datas para exibição
            List<String> diasOrdenados = new ArrayList<>(diasSelecionados);
            Collections.sort(diasOrdenados);

            StringBuilder texto = new StringBuilder();
            for (int i = 0; i < diasOrdenados.size(); i++) {
                texto.append("• ").append(diasOrdenados.get(i));
                if (i < diasOrdenados.size() - 1) {
                    texto.append("\n");
                }
            }

            txtDiasSelecionados.setText(texto.toString());
            txtDiasSelecionados.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void salvarAgendamento() {
        if (diasSelecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um dia", Toast.LENGTH_SHORT).show();
            return;
        }

        // Salvar no SharedPreferences
        PreferencesManager prefsManager = new PreferencesManager(this);
        Usuario usuarioLogado = prefsManager.getUsuarioLogado();

        if (usuarioLogado != null) {
            prefsManager.salvarDiasAgendados(usuarioLogado.getEmail(), diasSelecionados);

            // Enviar notificação para cada dia agendado
            for (String dia : diasSelecionados) {
                notificationHelper.notificarAgendamento(dia);
            }

            // Mostrar confirmação
            StringBuilder diasTexto = new StringBuilder("Dias salvos:\n");
            for (String dia : diasSelecionados) {
                diasTexto.append("• ").append(dia).append("\n");
            }

            Toast.makeText(this, diasTexto.toString(), Toast.LENGTH_LONG).show();
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        // Adicionar título e botão voltar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Agendamento de Dias");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}