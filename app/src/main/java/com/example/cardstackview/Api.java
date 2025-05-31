package com.example.cardstackview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    // IP base para todas as requisições
    private static final String BASE_IP = "192.168.1.23"; // Altere apenas aqui para mudar todos os IPs

    // Endpoints principais
    public static final String BASE_URL = "http://" + BASE_IP + "/ConecctaAPI/v1/Api.php?apicall=";
    public static final String USER_API_URL = "http://" + BASE_IP + "/CRUD_user/";
    public static final String UPDATE_API_URL = "http://" + BASE_IP + "/update/";

    // Endpoints específicos
    public static final String URL_GET_USER = USER_API_URL + "getUser.php?id=";
    public static final String URL_UPDATE_USER = UPDATE_API_URL + "update.php";
    public static final String URL_CADASTRAR_VAGA = BASE_URL + "cadastrarVaga";
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
}