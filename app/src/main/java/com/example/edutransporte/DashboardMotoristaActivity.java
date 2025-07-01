// DashboardMotoristaActivity.java - Dashboard do Motorista
package com.example.edutransporte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;

import com.example.edutransporte.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardMotoristaActivity extends AppCompatActivity {

    private static final String TAG = "DashboardMotorista";

    private TextView txtNomeMotorista, txtEmailMotorista;
    private Button btnGerenciarViagem, btnLocalizacao, btnMensagens, btnConfiguracoes, btnLogout;
    private ListView listViewAlunos;
    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_motorista);

        Log.d(TAG, "onCreate: Iniciando Dashboard do Motorista");

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard Motorista");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Log.d(TAG, "ActionBar configurada com botão voltar");
        } else {
            Log.e(TAG, "ActionBar não encontrada");
        }

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();

        // Verificar se há usuário logado
        if (usuarioLogado == null) {
            Log.e(TAG, "Usuário não está logado! Redirecionando...");
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        Log.d(TAG, "Usuário logado: " + usuarioLogado.getEmail());

        inicializarComponentes();
        carregarDados();
        configurarEventos();
        configurarBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Recarregando dados...");
        // Recarregar dados quando voltar da tela de configurações
        carregarDados();
    }

    private void inicializarComponentes() {
        Log.d(TAG, "Inicializando componentes...");

        txtNomeMotorista = findViewById(R.id.txt_nome_motorista);
        txtEmailMotorista = findViewById(R.id.txt_email_motorista);
        btnGerenciarViagem = findViewById(R.id.btn_gerenciar_viagem);
        btnLocalizacao = findViewById(R.id.btn_localizacao);
        btnMensagens = findViewById(R.id.btn_mensagens);
        btnConfiguracoes = findViewById(R.id.btn_configuracoes);
        btnLogout = findViewById(R.id.btn_logout);

        // Verificar se os componentes foram encontrados
        if (btnGerenciarViagem == null) {
            Log.e(TAG, "ERRO: btnGerenciarViagem é NULL! Verifique o ID no XML!");
        } else {
            Log.d(TAG, "btnGerenciarViagem encontrado OK");
        }

        if (btnLocalizacao == null) {
            Log.e(TAG, "ERRO: btnLocalizacao é NULL! Verifique o ID no XML!");
        } else {
            Log.d(TAG, "btnLocalizacao encontrado OK");
        }

        if (btnMensagens == null) {
            Log.e(TAG, "ERRO: btnMensagens é NULL! Verifique o ID no XML!");
        } else {
            Log.d(TAG, "btnMensagens encontrado OK");
        }

        if (btnConfiguracoes == null) {
            Log.e(TAG, "ERRO: btnConfiguracoes é NULL! Verifique o ID no XML!");
        } else {
            Log.d(TAG, "btnConfiguracoes encontrado OK");
        }

        if (btnLogout == null) {
            Log.e(TAG, "ERRO: btnLogout é NULL! Verifique o ID no XML!");
        } else {
            Log.d(TAG, "btnLogout encontrado OK");
        }
    }

    private void carregarDados() {
        Log.d(TAG, "Carregando dados...");

        // Verificar se há usuário logado
        if (usuarioLogado == null) {
            txtNomeMotorista.setText("👤 Nome do Motorista");
            txtEmailMotorista.setText("📧 email@exemplo.com");
            return;
        }

        // Carregar dados do usuário
        txtNomeMotorista.setText("👤 " + usuarioLogado.getNome());
        txtEmailMotorista.setText("📧 " + usuarioLogado.getEmail());
    }

    private void configurarEventos() {
        Log.d(TAG, "Configurando eventos dos botões...");

        if (btnGerenciarViagem != null) {
            btnGerenciarViagem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Botão GERENCIAR VIAGEM clicado!");
                    Toast.makeText(DashboardMotoristaActivity.this, "Botão Gerenciar Viagem clicado!", Toast.LENGTH_SHORT).show();

                    // Verificar se os dados foram configurados antes de iniciar viagem
                    if (usuarioLogado == null) {
                        new AlertDialog.Builder(DashboardMotoristaActivity.this)
                                .setTitle("Erro")
                                .setMessage("Sessão expirada. Faça login novamente.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(DashboardMotoristaActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .show();
                        return;
                    }

                    String emailUsuario = usuarioLogado.getEmail();
                    String nomeMotorista = preferencesManager.getConfiguracao("nome_motorista_" + emailUsuario, "");
                    String placaVeiculo = preferencesManager.getConfiguracao("placa_veiculo_" + emailUsuario, "");

                    if (nomeMotorista.isEmpty() || placaVeiculo.isEmpty()) {
                        new AlertDialog.Builder(DashboardMotoristaActivity.this)
                                .setTitle("Configuração Necessária")
                                .setMessage("Por favor, configure seus dados antes de iniciar uma viagem.")
                                .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(DashboardMotoristaActivity.this, ConfiguracoesActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    } else {
                        try {
                            Intent intent = new Intent(DashboardMotoristaActivity.this, ViagemActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e(TAG, "Erro ao abrir ViagemActivity: " + e.getMessage());
                            Toast.makeText(DashboardMotoristaActivity.this, "Erro ao abrir tela de viagem", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        } else {
            Log.e(TAG, "btnGerenciarViagem é NULL - não foi possível configurar o listener!");
        }

        if (btnLocalizacao != null) {
            btnLocalizacao.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Botão LOCALIZAÇÃO clicado!");
                    Toast.makeText(DashboardMotoristaActivity.this, "Botão Localização clicado!", Toast.LENGTH_SHORT).show();

                    try {
                        Intent intent = new Intent(DashboardMotoristaActivity.this, LocalizacaoActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao abrir LocalizacaoActivity: " + e.getMessage());
                        Toast.makeText(DashboardMotoristaActivity.this, "Erro ao abrir localização", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "btnLocalizacao é NULL - não foi possível configurar o listener!");
        }

        if (btnMensagens != null) {
            btnMensagens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Botão MENSAGENS clicado!");
                    Toast.makeText(DashboardMotoristaActivity.this, "Botão Mensagens clicado!", Toast.LENGTH_SHORT).show();

                    try {
                        Intent intent = new Intent(DashboardMotoristaActivity.this, MensagensMotoristaActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao abrir MensagensMotoristaActivity: " + e.getMessage());
                        Toast.makeText(DashboardMotoristaActivity.this, "Erro ao abrir mensagens", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "btnMensagens é NULL - não foi possível configurar o listener!");
        }

        if (btnConfiguracoes != null) {
            btnConfiguracoes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Botão CONFIGURAÇÕES clicado!");
                    Toast.makeText(DashboardMotoristaActivity.this, "Botão Configurações clicado!", Toast.LENGTH_SHORT).show();

                    try {
                        Intent intent = new Intent(DashboardMotoristaActivity.this, ConfiguracoesActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao abrir ConfiguracoesActivity: " + e.getMessage());
                        Toast.makeText(DashboardMotoristaActivity.this, "Erro ao abrir configurações", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "btnConfiguracoes é NULL - não foi possível configurar o listener!");
        }

        if (btnLogout != null) {
            btnLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Botão LOGOUT clicado!");
                    Toast.makeText(DashboardMotoristaActivity.this, "Botão Logout clicado!", Toast.LENGTH_SHORT).show();

                    try {
                        fazerLogout();
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao fazer logout: " + e.getMessage());
                        Toast.makeText(DashboardMotoristaActivity.this, "Erro ao fazer logout", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Log.e(TAG, "btnLogout é NULL - não foi possível configurar o listener!");
        }

        Log.d(TAG, "Configuração de eventos concluída!");
    }

    private void configurarBackPressed() {
        // Configurar comportamento do botão voltar usando a API moderna
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Mostrar diálogo de confirmação
                new AlertDialog.Builder(DashboardMotoristaActivity.this)
                        .setTitle("Sair")
                        .setMessage("Deseja realmente sair do aplicativo?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Fazer logout
                                preferencesManager.logout();
                                
                                // Voltar para tela de login
                                Intent intent = new Intent(DashboardMotoristaActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Não", null)
                        .show();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // Método adicionado para tratar o botão voltar da ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: Item ID = " + item.getItemId());
        
        if (item.getItemId() == android.R.id.home) {
            Log.d(TAG, "Botão voltar pressionado - fazendo logout");
            // Botão voltar - fazer logout
            fazerLogout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fazerLogout() {
        Log.d(TAG, "Iniciando processo de logout");
        
        // Limpar dados do usuário
        preferencesManager.limparDadosUsuario();
        
        // Voltar para tela de login
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "Logout realizado com sucesso", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Logout concluído");
    }
}