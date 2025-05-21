package com.example.cardstackview;

import android.content.Context;

import java.net.HttpURLConnection;
import java.net.URL;

public class Api {
    private static final String ROOT_URL = "http://192.168.1.23/ConecctaAPI/v1/Api.php?apicall=";
    public static final String URL_CADASTRAR_VAGA = ROOT_URL + "cadastrarVaga";
    public static final String URL_GET_VAGAS = ROOT_URL + "getVagas";
    public static final String URL_REGISTRAR_INTERESSE = ROOT_URL + "registrarinteresse";

    public static final String URL_EXCLUIR_VAGA = ROOT_URL + "excluirVaga";

    // Adicione este método para verificar a conexão
    public static boolean isURLReachable(Context context) {
        try {
            URL url = new URL(ROOT_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            return (responseCode == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            return false;
        }
    }
}
