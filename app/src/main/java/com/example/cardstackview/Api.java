package com.example.cardstackview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    private static final String ROOT_URL = "http://192.168.56.1/ConecctaAPI/v1/Api.php?apicall=";
    public static final String URL_CADASTRAR_VAGA = ROOT_URL + "cadastrarVaga";
    public static final String URL_GET_VAGAS = ROOT_URL + "getVagas";
    public static final String URL_REGISTRAR_INTERESSE = ROOT_URL + "registrarinteresse";

    public static final String URL_EXCLUIR_VAGA = ROOT_URL + "excluirVaga";

    // Adicione este método para verificar a conexão
    public static boolean isURLReachable(Context context) {
        // 1. Verifica se há conexão com internet
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            return false;
        }

        // 2. Tenta conectar ao servidor
        HttpURLConnection connection = null;
        try {
            URL url = new URL(ROOT_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // 3 segundos
            connection.setReadTimeout(3000);

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
