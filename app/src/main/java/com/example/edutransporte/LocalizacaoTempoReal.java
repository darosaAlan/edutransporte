package com.example.edutransporte;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LocalizacaoTempoReal {
    
    private static final String TAG = "LocalizacaoTempoReal";
    private static final String NODE_LOCALIZACAO = "localizacao_van";
    
    private DatabaseReference databaseReference;
    private DatabaseReference localizacaoRef;
    private LocalizacaoListener listener;
    
    public interface LocalizacaoListener {
        void onLocalizacaoAtualizada(double latitude, double longitude, String motorista, String placa);
        void onErro(String mensagem);
    }
    
    public LocalizacaoTempoReal(Context context) {
        // Inicializar Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        localizacaoRef = databaseReference.child(NODE_LOCALIZACAO);
    }
    
    /**
     * Envia a localização atual da van para o Firebase
     * (Chamado pelo motorista)
     */
    public void enviarLocalizacao(Location location, String nomeMotorista, String placaVeiculo) {
        if (location == null) {
            Log.e(TAG, "Localização é null");
            return;
        }
        
        Map<String, Object> dadosLocalizacao = new HashMap<>();
        dadosLocalizacao.put("latitude", location.getLatitude());
        dadosLocalizacao.put("longitude", location.getLongitude());
        dadosLocalizacao.put("timestamp", System.currentTimeMillis());
        dadosLocalizacao.put("motorista", nomeMotorista);
        dadosLocalizacao.put("placa", placaVeiculo);
        dadosLocalizacao.put("velocidade", location.getSpeed());
        dadosLocalizacao.put("direcao", location.getBearing());
        
        localizacaoRef.setValue(dadosLocalizacao)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Localização enviada com sucesso: " + 
                            location.getLatitude() + ", " + location.getLongitude());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao enviar localização: " + e.getMessage());
                });
    }
    
    /**
     * Inicia a escuta das atualizações de localização
     * (Chamado pelo aluno/responsável)
     */
    public void iniciarEscutaLocalizacao(LocalizacaoListener listener) {
        this.listener = listener;
        
        localizacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                        String motorista = dataSnapshot.child("motorista").getValue(String.class);
                        String placa = dataSnapshot.child("placa").getValue(String.class);
                        
                        if (latitude != null && longitude != null && listener != null) {
                            Log.d(TAG, "Nova localização recebida: " + latitude + ", " + longitude);
                            listener.onLocalizacaoAtualizada(latitude, longitude, motorista, placa);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Erro ao processar dados: " + e.getMessage());
                        if (listener != null) {
                            listener.onErro("Erro ao processar localização");
                        }
                    }
                } else {
                    Log.d(TAG, "Nenhuma localização disponível");
                    if (listener != null) {
                        listener.onErro("Van não está em movimento");
                    }
                }
            }
            
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Erro na escuta: " + databaseError.getMessage());
                if (listener != null) {
                    listener.onErro("Erro de conexão: " + databaseError.getMessage());
                }
            }
        });
    }
    
    /**
     * Para a escuta das atualizações
     */
    public void pararEscutaLocalizacao() {
        if (localizacaoRef != null) {
            localizacaoRef.removeEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {}
                
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }
    
    /**
     * Limpa a localização (quando a viagem termina)
     */
    public void limparLocalizacao() {
        localizacaoRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Localização limpa com sucesso");
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erro ao limpar localização: " + e.getMessage());
                });
    }
} 