package com.example.edutransporte;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    
    private static final String TAG = "NotificationHelper";
    
    // IDs dos canais de notificação
    public static final String CHANNEL_VIAGEM = "viagem_channel";
    public static final String CHANNEL_AGENDAMENTO = "agendamento_channel";
    public static final String CHANNEL_MENSAGEM = "mensagem_channel";
    public static final String CHANNEL_GERAL = "geral_channel";
    
    // IDs das notificações
    public static final int NOTIFICATION_VIAGEM_INICIADA = 1001;
    public static final int NOTIFICATION_VIAGEM_FINALIZADA = 1002;
    public static final int NOTIFICATION_ALUNO_CONFIRMADO = 1003;
    public static final int NOTIFICATION_AGENDAMENTO = 1004;
    public static final int NOTIFICATION_MENSAGEM = 1005;
    public static final int NOTIFICATION_LEMBRETE_HORARIO = 1006;
    public static final int NOTIFICATION_VAN_CHEGANDO = 1007;
    
    private Context context;
    private NotificationManagerCompat notificationManager;
    
    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = NotificationManagerCompat.from(context);
        criarCanaisNotificacao();
    }
    
    private void criarCanaisNotificacao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel viagemChannel = new NotificationChannel(
                    CHANNEL_VIAGEM, "Viagens", NotificationManager.IMPORTANCE_HIGH);
            viagemChannel.setDescription("Notificações sobre viagens em andamento");
            viagemChannel.enableVibration(true);
            
            NotificationChannel agendamentoChannel = new NotificationChannel(
                    CHANNEL_AGENDAMENTO, "Agendamentos", NotificationManager.IMPORTANCE_DEFAULT);
            agendamentoChannel.setDescription("Lembretes de agendamentos e horários");
            
            NotificationChannel mensagemChannel = new NotificationChannel(
                    CHANNEL_MENSAGEM, "Mensagens", NotificationManager.IMPORTANCE_HIGH);
            mensagemChannel.setDescription("Novas mensagens do chat");
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(viagemChannel);
            notificationManager.createNotificationChannel(agendamentoChannel);
            notificationManager.createNotificationChannel(mensagemChannel);
        }
    }
    
    public void notificarViagemIniciada(String nomeMotorista, String placaVeiculo) {
        Intent intent = new Intent(context, ViagemActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_VIAGEM)
                .setSmallIcon(R.drawable.ic_bus_logo)
                .setContentTitle("🚌 Viagem Iniciada!")
                .setContentText("Motorista " + nomeMotorista + " iniciou a viagem")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        try {
            notificationManager.notify(NOTIFICATION_VIAGEM_INICIADA, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    public void notificarAlunoConfirmado(String nomeAluno) {
        Intent intent = new Intent(context, ViagemActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_VIAGEM)
                .setSmallIcon(R.drawable.ic_check_green)
                .setContentTitle("✅ Aluno Confirmado")
                .setContentText(nomeAluno + " confirmou presença")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        try {
            notificationManager.notify(NOTIFICATION_ALUNO_CONFIRMADO, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    public void notificarAgendamento(String data) {
        Intent intent = new Intent(context, AgendamentoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_AGENDAMENTO)
                .setSmallIcon(R.drawable.ic_bus_logo)
                .setContentTitle("📅 Agendamento Confirmado")
                .setContentText("Viagem agendada para " + data)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        try {
            notificationManager.notify(NOTIFICATION_AGENDAMENTO, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    public void notificarNovaMensagem(String remetente, String mensagem) {
        Intent intent = new Intent(context, MensagensActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_MENSAGEM)
                .setSmallIcon(R.drawable.ic_bus_logo)
                .setContentTitle("💬 Nova Mensagem de " + remetente)
                .setContentText(mensagem)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        try {
            notificationManager.notify(NOTIFICATION_MENSAGEM, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    public void notificarViagemFinalizada(String nomeMotorista) {
        Intent intent = new Intent(context, DashboardAlunoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_VIAGEM)
                .setSmallIcon(R.drawable.ic_bus_logo)
                .setContentTitle("✅ Viagem Finalizada")
                .setContentText("A viagem foi concluída com sucesso")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);
        
        try {
            notificationManager.notify(NOTIFICATION_VIAGEM_FINALIZADA, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Erro ao enviar notificação: " + e.getMessage());
        }
    }
    
    public void cancelarNotificacao(int notificationId) {
        notificationManager.cancel(notificationId);
    }
    
    public void cancelarTodasNotificacoes() {
        notificationManager.cancelAll();
    }
}
