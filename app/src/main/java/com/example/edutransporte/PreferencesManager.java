package com.example.edutransporte;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PreferencesManager {
    private static final String PREF_NAME = "EduTransportePrefs";
    private static final String KEY_USUARIOS = "usuarios";
    private static final String KEY_USUARIO_LOGADO = "usuario_logado";
    private static final String KEY_MENSAGENS = "mensagens";
    private static final String KEY_DIAS_AGENDADOS = "dias_agendados";
    private static final String KEY_CONFIGURACOES = "configuracoes";
    // Novas chaves para salvar últimos dados de login
    private static final String KEY_ULTIMO_EMAIL = "ultimo_email";
    private static final String KEY_ULTIMO_TIPO_USUARIO = "ultimo_tipo_usuario";

    private SharedPreferences sharedPreferences;
    private Gson gson;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    // ===== USUÁRIOS =====

    public void salvarUsuario(Usuario usuario) {
        List<Usuario> usuarios = getUsuarios();

        // Verificar se usuário já existe
        boolean existe = false;
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equals(usuario.getEmail())) {
                usuarios.set(i, usuario); // Atualizar usuário existente
                existe = true;
                break;
            }
        }

        if (!existe) {
            usuarios.add(usuario); // Adicionar novo usuário
        }

        String json = gson.toJson(usuarios);
        sharedPreferences.edit().putString(KEY_USUARIOS, json).apply();
    }

    public List<Usuario> getUsuarios() {
        String json = sharedPreferences.getString(KEY_USUARIOS, "[]");
        Type type = new TypeToken<List<Usuario>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public Usuario getUsuario(String email, String senha) {
        List<Usuario> usuarios = getUsuarios();
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }

    public Usuario buscarUsuario(String email) {
        List<Usuario> usuarios = getUsuarios();
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                return u;
            }
        }
        return null;
    }

    public void salvarUsuarioLogado(Usuario usuario) {
        String json = gson.toJson(usuario);
        sharedPreferences.edit().putString(KEY_USUARIO_LOGADO, json).apply();
    }

    public Usuario getUsuarioLogado() {
        String json = sharedPreferences.getString(KEY_USUARIO_LOGADO, null);
        if (json != null) {
            return gson.fromJson(json, Usuario.class);
        }
        return null;
    }

    public void logout() {
        sharedPreferences.edit().remove(KEY_USUARIO_LOGADO).apply();
    }

    // ===== ÚLTIMOS DADOS DE LOGIN (NOVOS MÉTODOS) =====

    public void salvarUltimoEmail(String email) {
        sharedPreferences.edit().putString(KEY_ULTIMO_EMAIL, email).apply();
    }

    public String getUltimoEmail() {
        return sharedPreferences.getString(KEY_ULTIMO_EMAIL, "");
    }

    public void salvarUltimoTipoUsuario(String tipo) {
        sharedPreferences.edit().putString(KEY_ULTIMO_TIPO_USUARIO, tipo).apply();
    }

    public String getUltimoTipoUsuario() {
        return sharedPreferences.getString(KEY_ULTIMO_TIPO_USUARIO, "");
    }

    // Método para limpar dados de login (útil para testes)
    public void limparDadosLogin() {
        sharedPreferences.edit()
                .remove(KEY_ULTIMO_EMAIL)
                .remove(KEY_ULTIMO_TIPO_USUARIO)
                .remove(KEY_USUARIO_LOGADO)
                .apply();
    }

    // ===== MENSAGENS =====

    public void salvarMensagem(Mensagem mensagem) {
        List<Mensagem> mensagens = getMensagens();
        mensagens.add(mensagem);

        String json = gson.toJson(mensagens);
        sharedPreferences.edit().putString(KEY_MENSAGENS, json).apply();
    }

    public List<Mensagem> getMensagens() {
        String json = sharedPreferences.getString(KEY_MENSAGENS, "[]");
        Type type = new TypeToken<List<Mensagem>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // ===== AGENDAMENTO =====

    public void salvarDiasAgendados(String emailUsuario, List<String> dias) {
        String key = KEY_DIAS_AGENDADOS + "_" + emailUsuario;
        String json = gson.toJson(dias);
        sharedPreferences.edit().putString(key, json).apply();
    }

    public List<String> getDiasAgendados(String emailUsuario) {
        String key = KEY_DIAS_AGENDADOS + "_" + emailUsuario;
        String json = sharedPreferences.getString(key, "[]");
        Type type = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(json, type);
    }

    // ===== CONFIGURAÇÕES =====

    public void salvarConfiguracao(String chave, String valor) {
        sharedPreferences.edit().putString(chave, valor).apply();
    }

    public String getConfiguracao(String chave, String valorPadrao) {
        return sharedPreferences.getString(chave, valorPadrao);
    }

    public void salvarConfiguracaoBoolean(String chave, boolean valor) {
        sharedPreferences.edit().putBoolean(chave, valor).apply();
    }

    public boolean getConfiguracaoBoolean(String chave, boolean valorPadrao) {
        return sharedPreferences.getBoolean(chave, valorPadrao);
    }

    /**
     * Limpa todos os dados do usuário logado
     */
    public void limparDadosUsuario() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        // Limpar dados do usuário
        editor.remove("usuario_logado");
        editor.remove("email_usuario");
        editor.remove("nome_usuario");
        editor.remove("tipo_usuario");
        
        // Limpar dados de viagem
        editor.remove("viagem_iniciada");
        editor.remove("horario_inicio");
        editor.remove("viagem_pausada");
        
        // Limpar dados de agendamento
        editor.remove("dias_agendados");
        
        // Limpar configurações específicas do usuário
        Usuario usuario = getUsuarioLogado();
        if (usuario != null) {
            String emailUsuario = usuario.getEmail();
            editor.remove("viagem_iniciada_" + emailUsuario);
            editor.remove("horario_inicio_" + emailUsuario);
            editor.remove("viagem_pausada_" + emailUsuario);
            editor.remove("dias_agendados_" + emailUsuario);
            editor.remove("nome_motorista_" + emailUsuario);
            editor.remove("placa_veiculo_" + emailUsuario);
        }
        
        editor.apply();
    }
}