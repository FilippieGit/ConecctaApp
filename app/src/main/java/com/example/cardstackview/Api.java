package com.example.cardstackview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    // IP base para todas as requisições
    private static final String BASE_IP = "192.168.22.160"; // Filippie
//    private static final String BASE_IP = "192.168.22.160"; // Lula
//    private static final String BASE_IP = "192.168.22.160"; // IPs aleatórios

    // Endpoints principais
    public static final String BASE_URL = "http://" + BASE_IP + "/ConecctaAPI/v1/Api.php?apicall=";
    public static final String USER_API_URL = "http://" + BASE_IP + "/CRUD_user/";
    public static final String UPDATE_API_URL = "http://" + BASE_IP + "/update/";

    // Endpoints específicos
    public static final String URL_GET_USER = USER_API_URL + "getUser.php?id=";
    public static final String URL_UPDATE_USER = UPDATE_API_URL + "update.php";
    public static final String URL_CADASTRAR_VAGA = BASE_URL + "cadastrarVaga";

    public static final String URL_LISTAR_CANDIDATURAS = BASE_URL + "listarCandidaturas";
    public static final String URL_GET_VAGAS = BASE_URL + "getVagas";
    public static final String URL_REGISTRAR_INTERESSE = BASE_URL + "registrarinteresse";
    public static final String URL_CANDIDATAR_VAGA = BASE_URL + "candidatarVaga";
    public static final String URL_EXCLUIR_VAGA = BASE_URL + "excluirVaga";
    public static final String URL_VERIFICAR_CANDIDATURA = BASE_URL + "verificarCandidatura";


    // Método para verificar conexão
    public static boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            return false;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static final int CONNECT_TIMEOUT = 15000; // 15 segundos
    public static final int READ_TIMEOUT = 10000;    // 10 segundos

    // Método auxiliar para verificar URL
    public static boolean isEndpointReachable(String urlString, Context context) {
        if (!isURLReachable(context)) {
            return false;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Log.e("API", "Erro ao verificar endpoint: " + urlString, e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}