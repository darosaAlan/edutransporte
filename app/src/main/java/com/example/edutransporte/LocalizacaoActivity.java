package com.example.edutransporte;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocalizacaoActivity extends AppCompatActivity implements OnMapReadyCallback, LocalizacaoTempoReal.LocalizacaoListener {

    private static final String TAG = "LocalizacaoActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;

    private TextView txtLocalizacao;
    private TextView txtVelocidade;
    private TextView txtMotorista;
    private TextView txtPlaca;

    private LocalizacaoTempoReal localizacaoTempoReal;
    private PreferencesManager preferencesManager;
    private Usuario usuarioLogado;
    private boolean isMotorista = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_localizacao);

        Log.d(TAG, "onCreate: Iniciando LocalizacaoActivity");

        // Inicializar PreferencesManager
        preferencesManager = new PreferencesManager(this);
        usuarioLogado = preferencesManager.getUsuarioLogado();

        // Verificar se é motorista
        if (usuarioLogado != null) {
            isMotorista = usuarioLogado.getTipoUsuario().equals("Motorista");
        }

        // Configurar ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Localização do Veículo");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar componentes
        txtLocalizacao = findViewById(R.id.txt_localizacao);
        txtVelocidade = findViewById(R.id.txt_velocidade);
        txtMotorista = findViewById(R.id.txt_motorista);
        txtPlaca = findViewById(R.id.txt_placa);

        // Inicializar Firebase
        localizacaoTempoReal = new LocalizacaoTempoReal(this);

        // Verificar Google Play Services
        verificarGooglePlayServices();

        // Inicializar cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obter o mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            Log.d(TAG, "onCreate: MapFragment encontrado, solicitando mapa");
            mapFragment.getMapAsync(this);
        } else {
            Log.e(TAG, "onCreate: MapFragment NÃO encontrado!");
            Toast.makeText(this, "Erro: Fragment do mapa não encontrado", Toast.LENGTH_LONG).show();
        }
    }

    private void verificarGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Google Play Services não está disponível. Código: " + resultCode);
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Toast.makeText(this, "Este dispositivo não suporta Google Play Services",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        } else {
            Log.d(TAG, "Google Play Services está disponível");
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: Mapa está pronto!");
        mMap = googleMap;

        // Mostrar toast para confirmar
        Toast.makeText(this, "Mapa carregado com sucesso!", Toast.LENGTH_SHORT).show();

        // Configurar o mapa
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Adicionar um marcador de teste em São Paulo
        LatLng saoPaulo = new LatLng(-23.550520, -46.633308);
        mMap.addMarker(new MarkerOptions()
                .position(saoPaulo)
                .title("Localização de Teste")
                .snippet("São Paulo - SP"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(saoPaulo, 12));

        Log.d(TAG, "onMapReady: Marcador de teste adicionado em São Paulo");

        // Se for motorista, configurar envio de localização
        if (isMotorista) {
            configurarEnvioLocalizacao();
        } else {
            // Se for aluno, escutar atualizações
            iniciarEscutaLocalizacao();
        }

        // Verificar permissões
        if (verificarPermissoes()) {
            Log.d(TAG, "onMapReady: Permissões já concedidas");
            habilitarMinhaLocalizacao();
        } else {
            Log.d(TAG, "onMapReady: Solicitando permissões");
        }
    }

    private void configurarEnvioLocalizacao() {
        Log.d(TAG, "Configurando envio de localização para motorista");
        
        // Configurar atualização periódica de localização
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            enviarLocalizacaoParaFirebase(location);
                        }
                    });
        }
    }

    private void enviarLocalizacaoParaFirebase(Location location) {
        if (usuarioLogado == null) return;

        String emailUsuario = usuarioLogado.getEmail();
        String nomeMotorista = preferencesManager.getConfiguracao("nome_motorista_" + emailUsuario, "Motorista");
        String placaVeiculo = preferencesManager.getConfiguracao("placa_veiculo_" + emailUsuario, "ABC-1234");

        localizacaoTempoReal.enviarLocalizacao(location, nomeMotorista, placaVeiculo);
        mostrarLocalizacaoNoMapa(location);
    }

    private void iniciarEscutaLocalizacao() {
        Log.d(TAG, "Iniciando escuta de localização para aluno");
        localizacaoTempoReal.iniciarEscutaLocalizacao(this);
    }

    private boolean verificarPermissoes() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "verificarPermissoes: Permissão não concedida, solicitando...");

            // Solicitar permissão
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        Log.d(TAG, "verificarPermissoes: Permissão já foi concedida");
        return true;
    }

    private void habilitarMinhaLocalizacao() {
        Log.d(TAG, "habilitarMinhaLocalizacao: Tentando habilitar localização");

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Habilitar botão de localização
            mMap.setMyLocationEnabled(true);
            Log.d(TAG, "habilitarMinhaLocalizacao: Botão MyLocation habilitado");

            // Se for motorista, obter localização atual
            if (isMotorista) {
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                Log.d(TAG, "Localização obtida: Lat=" + location.getLatitude() +
                                        ", Lng=" + location.getLongitude());
                                enviarLocalizacaoParaFirebase(location);
                            } else {
                                Log.w(TAG, "Localização é null, usando localização padrão");
                                txtLocalizacao.setText("📍 Aguardando GPS...");

                                // Tentar obter localização novamente após alguns segundos
                                new android.os.Handler().postDelayed(this::tentarObterLocalizacaoNovamente, 3000);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Erro ao obter localização: " + e.getMessage());
                            Toast.makeText(this, "Erro ao obter localização: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        });
            }
        }
    }

    private void tentarObterLocalizacaoNovamente() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            enviarLocalizacaoParaFirebase(location);
                        }
                    });
        }
    }

    private void mostrarLocalizacaoNoMapa(Location location) {
        Log.d(TAG, "mostrarLocalizacaoNoMapa: Atualizando mapa com localização");

        LatLng minhaLocalizacao = new LatLng(location.getLatitude(), location.getLongitude());

        // Limpar marcadores anteriores
        mMap.clear();

        // Adicionar novo marcador
        mMap.addMarker(new MarkerOptions()
                .position(minhaLocalizacao)
                .title("Van Escolar")
                .snippet("Motorista: João Silva"));

        // Animar câmera para a nova posição
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(minhaLocalizacao, 16));

        // Atualizar informações na tela
        txtLocalizacao.setText("📍 Lat: " + String.format("%.6f", location.getLatitude()) +
                ", Lng: " + String.format("%.6f", location.getLongitude()));

        if (location.hasSpeed()) {
            float velocidadeKmh = location.getSpeed() * 3.6f; // Converter m/s para km/h
            txtVelocidade.setText("🚗 Velocidade: " + String.format("%.1f", velocidadeKmh) + " km/h");
        } else {
            txtVelocidade.setText("🚗 Velocidade: -- km/h");
        }
    }

    // Implementação da interface LocalizacaoListener
    @Override
    public void onLocalizacaoAtualizada(double latitude, double longitude, String motorista, String placa) {
        Log.d(TAG, "Localização atualizada via Firebase: " + latitude + ", " + longitude);
        
        // Criar objeto Location
        Location location = new Location("Firebase");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        
        // Atualizar mapa na thread principal
        runOnUiThread(() -> {
            mostrarLocalizacaoNoMapa(location);
            
            // Atualizar informações do motorista
            if (motorista != null) {
                txtMotorista.setText("👨‍💼 Motorista: " + motorista);
            }
            if (placa != null) {
                txtPlaca.setText("🚐 Placa: " + placa);
            }
        });
    }

    @Override
    public void onErro(String mensagem) {
        Log.e(TAG, "Erro na localização: " + mensagem);
        runOnUiThread(() -> {
            Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();
            txtLocalizacao.setText("❌ " + mensagem);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permissão de localização concedida");
                habilitarMinhaLocalizacao();
            } else {
                Log.e(TAG, "Permissão de localização negada");
                Toast.makeText(this, "Permissão de localização necessária para usar esta funcionalidade",
                        Toast.LENGTH_LONG).show();
            }
        }
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
        Log.d(TAG, "onResume: Atividade retomada");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Atividade pausada");
        
        // Se for aluno, parar escuta quando sair da tela
        if (!isMotorista) {
            localizacaoTempoReal.pararEscutaLocalizacao();
        }
    }
}