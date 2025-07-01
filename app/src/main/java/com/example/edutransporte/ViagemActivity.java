// ViagemActivity.java - Tela de Gerenciamento de Viagens
package com.example.edutransporte;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ViagemActivity extends AppCompatActivity {

    private static final String TAG = "ViagemActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int UPDATE_INTERVAL = 10000; // 10 segundos

    private TextView txtStatusViagem, txtHorarioInicio, txtTotalAlunos, txtAlunosConfirmados;
    private Button btnIniciarViagem, btnPausarViagem, btnFinalizarViagem;

    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;
    private AlunoAdapter alunoAdapter;
    private List<Aluno> listaAlunos;

    private boolean viagemIniciada = false;
    private boolean viagemPausada = false;
    private long horarioInicio = 0;
    private String statusAtual = "Aguardando início";

    private FusedLocationProviderClient fusedLocationClient;
    private LocalizacaoTempoReal localizacaoTempoReal;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable tempoRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viagem);

        Log.d(TAG, "onCreate: Iniciando ViagemActivity");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();

        // Verificar se há usuário logado
        if (usuarioLogado == null) {
            Log.e(TAG, "Nenhum usuário logado encontrado");
            Toast.makeText(this, "Erro: Usuário não encontrado", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Inicializar componentes
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        localizacaoTempoReal = new LocalizacaoTempoReal(this);

        inicializarComponentes();
        carregarEstadoViagem();
        carregarAlunos();
        configurarEventos();
        atualizarInterface();

        // Iniciar atualização de tempo
        iniciarAtualizacaoTempo();

        // Verificar permissões
        if (!verificarPermissoes()) {
            solicitarPermissoes();
        }
    }

    private void inicializarComponentes() {
        txtStatusViagem = findViewById(R.id.text_view_status_viagem);
        txtHorarioInicio = findViewById(R.id.txt_horario_inicio);
        txtTotalAlunos = findViewById(R.id.txt_total_alunos);
        txtAlunosConfirmados = findViewById(R.id.txt_alunos_confirmados);
        btnIniciarViagem = findViewById(R.id.btn_iniciar_viagem);
        btnPausarViagem = findViewById(R.id.btn_pausar_viagem);
        btnFinalizarViagem = findViewById(R.id.btn_finalizar_viagem);
        
        // Usar ListView em vez de RecyclerView
        ListView listViewAlunos = findViewById(R.id.list_view_alunos_viagem);

        // Configurar ListView
        listaAlunos = new ArrayList<>();
        alunoAdapter = new AlunoAdapter(this, listaAlunos, true); // true = modo viagem
        listViewAlunos.setAdapter(alunoAdapter);
    }

    private void carregarEstadoViagem() {
        String viagemIniciadaStr = preferencesManager.getConfiguracao("viagem_iniciada_" + usuarioLogado.getEmail(), "false");
        viagemIniciada = viagemIniciadaStr.equals("true");

        if (viagemIniciada) {
            String horarioInicioStr = preferencesManager.getConfiguracao("horario_inicio_" + usuarioLogado.getEmail(), "0");
            try {
                horarioInicio = Long.parseLong(horarioInicioStr);
            } catch (NumberFormatException e) {
                horarioInicio = 0;
            }
        }
    }

    private void carregarAlunos() {
        // Simular lista de alunos (em um app real, viria do banco de dados)
        listaAlunos.clear();
        listaAlunos.add(new Aluno("João Silva", "Colégio São José", false, "07:30"));
        listaAlunos.add(new Aluno("Maria Santos", "Faculdade Tech", false, "07:35"));
        listaAlunos.add(new Aluno("Pedro Costa", "Colégio São José", false, "07:40"));
        listaAlunos.add(new Aluno("Ana Oliveira", "Faculdade Tech", false, "07:45"));
        listaAlunos.add(new Aluno("Lucas Ferreira", "Escola Municipal", false, "07:50"));
        alunoAdapter.notifyDataSetChanged();
    }

    private void configurarEventos() {
        btnIniciarViagem.setOnClickListener(v -> iniciarViagem());
        btnPausarViagem.setOnClickListener(v -> pausarViagem());
        btnFinalizarViagem.setOnClickListener(v -> finalizarViagem());
    }

    private void iniciarViagem() {
        Log.d(TAG, "Iniciando viagem");
        
        if (!verificarPermissoes()) {
            Toast.makeText(this, "Permissão de localização necessária", Toast.LENGTH_SHORT).show();
            solicitarPermissoes();
            return;
        }

        viagemIniciada = true;
        viagemPausada = false;
        horarioInicio = System.currentTimeMillis();
        statusAtual = "Viagem em andamento";

        salvarEstadoViagem();
        atualizarInterface();
        
        // Iniciar envio de localização
        iniciarEnvioLocalizacao();
        
        Toast.makeText(this, "Viagem iniciada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void pausarViagem() {
        Log.d(TAG, "Pausando viagem");
        
        viagemPausada = !viagemPausada;
        statusAtual = viagemPausada ? "Viagem pausada" : "Viagem em andamento";
        
        salvarEstadoViagem();
        atualizarInterface();
        
        if (viagemPausada) {
            pararEnvioLocalizacao();
            Toast.makeText(this, "Viagem pausada", Toast.LENGTH_SHORT).show();
        } else {
            // Retomar viagem
            long tempoPausa = System.currentTimeMillis() - horarioInicio;
            horarioInicio += tempoPausa;
            iniciarEnvioLocalizacao();
            Toast.makeText(this, "Viagem retomada", Toast.LENGTH_SHORT).show();
        }
    }

    private void finalizarViagem() {
        Log.d(TAG, "Finalizando viagem");
        
        new AlertDialog.Builder(this)
                .setTitle("Finalizar Viagem")
                .setMessage("Tem certeza que deseja finalizar a viagem?")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viagemIniciada = false;
                        viagemPausada = false;
                        statusAtual = "Viagem finalizada";
                        horarioInicio = 0;

                        salvarEstadoViagem();
                        atualizarInterface();
                        
                        // Limpar localização do Firebase
                        localizacaoTempoReal.limparLocalizacao();
                        
                        // Salvar estatísticas da viagem
                        salvarEstatisticasViagem();
                        
                        Toast.makeText(ViagemActivity.this, "Viagem finalizada com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void salvarEstadoViagem() {
        preferencesManager.salvarConfiguracao("viagem_iniciada_" + usuarioLogado.getEmail(), String.valueOf(viagemIniciada));
        preferencesManager.salvarConfiguracao("horario_inicio_" + usuarioLogado.getEmail(), String.valueOf(horarioInicio));
    }

    private void iniciarEnvioLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            // Enviar localização inicial
            enviarLocalizacaoAtual();
            
            // Configurar envio periódico
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (viagemIniciada && !viagemPausada) {
                        enviarLocalizacaoAtual();
                        handler.postDelayed(this, UPDATE_INTERVAL);
                    }
                }
            }, UPDATE_INTERVAL);
        }
    }

    private void pararEnvioLocalizacao() {
        // O handler será parado automaticamente na próxima verificação
    }

    private void enviarLocalizacaoAtual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            
            CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
            
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                    .addOnSuccessListener(this, location -> {
                        if (location != null && viagemIniciada && !viagemPausada) {
                            enviarLocalizacaoParaFirebase(location);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Erro ao obter localização: " + e.getMessage());
                    });
        }
    }

    private void enviarLocalizacaoParaFirebase(Location location) {
        if (usuarioLogado == null) return;

        String emailUsuario = usuarioLogado.getEmail();
        String nomeMotorista = preferencesManager.getConfiguracao("nome_motorista_" + emailUsuario, "Motorista");
        String placaVeiculo = preferencesManager.getConfiguracao("placa_veiculo_" + emailUsuario, "ABC-1234");

        localizacaoTempoReal.enviarLocalizacao(location, nomeMotorista, placaVeiculo);
        
        Log.d(TAG, "Localização enviada: " + location.getLatitude() + ", " + location.getLongitude());
    }

    private void iniciarAtualizacaoTempo() {
        tempoRunnable = new Runnable() {
            @Override
            public void run() {
                atualizarTempo();
                handler.postDelayed(this, 1000); // Atualizar a cada segundo
            }
        };
        handler.post(tempoRunnable);
    }

    private void atualizarTempo() {
        if (viagemIniciada && !viagemPausada) {
            long tempoAtual = System.currentTimeMillis();
            long tempoDecorrido = tempoAtual - horarioInicio;
            
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String tempoFormatado = sdf.format(new Date(tempoDecorrido));
            
            txtHorarioInicio.setText("⏱️ Tempo: " + tempoFormatado);
        }
    }

    private void salvarEstatisticasViagem() {
        if (horarioInicio > 0) {
            long tempoTotal = System.currentTimeMillis() - horarioInicio;
            long tempoSegundos = tempoTotal / 1000;
            
            // Salvar estatísticas
            preferencesManager.salvarConfiguracao("ultima_viagem_duracao", String.valueOf(tempoSegundos));
            preferencesManager.salvarConfiguracao("ultima_viagem_alunos", String.valueOf(listaAlunos.size()));
            preferencesManager.salvarConfiguracao("ultima_viagem_data", new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()));
            
            Log.d(TAG, "Estatísticas salvas: Duração=" + tempoSegundos + "s, Alunos=" + listaAlunos.size());
        }
    }

    private boolean verificarPermissoes() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermissoes() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão de localização concedida");
                Toast.makeText(this, "Permissão concedida!", Toast.LENGTH_SHORT).show();
            } else {
                Log.e(TAG, "Permissão de localização negada");
                Toast.makeText(this, "Permissão necessária para rastreamento", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void atualizarInterface() {
        txtStatusViagem.setText("🚌 Status: " + statusAtual);
        
        if (viagemIniciada) {
            btnIniciarViagem.setEnabled(false);
            btnPausarViagem.setEnabled(true);
            btnFinalizarViagem.setEnabled(true);
            
            if (viagemPausada) {
                btnPausarViagem.setText("Retomar Viagem");
            } else {
                btnPausarViagem.setText("Pausar Viagem");
            }
        } else {
            txtHorarioInicio.setText("⏱️ Tempo: 00:00:00");
            btnIniciarViagem.setEnabled(true);
            btnPausarViagem.setEnabled(false);
            btnFinalizarViagem.setEnabled(false);
            btnPausarViagem.setText("Pausar Viagem");
        }
        
        int totalAlunos = listaAlunos.size();
        int alunosConfirmados = 0;
        for (Aluno aluno : listaAlunos) {
            if (aluno.isConfirmado()) {
                alunosConfirmados++;
            }
        }
        
        txtTotalAlunos.setText("Total: " + totalAlunos);
        txtAlunosConfirmados.setText("Confirmados: " + alunosConfirmados);
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
        Log.d(TAG, "onResume: Verificando estado da viagem");
        
        // Verificar se há viagem em andamento
        String viagemIniciadaStr = preferencesManager.getConfiguracao("viagem_iniciada_" + usuarioLogado.getEmail(), "false");
        if (viagemIniciadaStr.equals("true")) {
            String horarioInicioStr = preferencesManager.getConfiguracao("horario_inicio_" + usuarioLogado.getEmail(), "0");
            try {
                horarioInicio = Long.parseLong(horarioInicioStr);
                viagemIniciada = true;
                viagemPausada = false;
                atualizarInterface();
                Log.d(TAG, "Viagem em andamento restaurada");
            } catch (NumberFormatException e) {
                Log.e(TAG, "Erro ao restaurar horário de início: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Salvando estado da viagem");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null && tempoRunnable != null) {
            handler.removeCallbacks(tempoRunnable);
        }
    }
}