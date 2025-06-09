package com.example.cardstackview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Api {


    private static final String BASE_IP = "192.168.1.9"; // Filippie
    //private static final String BASE_IP = "192.168.22.160"; // Lula
    //private static final String BASE_IP = "10.67.97.83"; // IPs aleatórios

    // Endpoints principais
    public static final String BASE_URL = "http://" + BASE_IP + "/ConecctaAPI/v1/";
    public static final String USER_API_URL = "http://" + BASE_IP + "/CRUD_user/";
    public static final String UPDATE_API_URL = "http://" + BASE_IP + "/update/";
    public static final String SYNC_API_URL = "http://" + BASE_IP + "/sync/";

    // Endpoints de autenticação e usuário
    public static final String URL_LOGAR_USUARIO = BASE_URL + "Api.php?apicall=logarUsuario";
    public static final String URL_GET_USER_DATA = BASE_URL + "get_user_id.php";
    public static final String URL_SYNC_USER = SYNC_API_URL + "sync.php";
    public static final String URL_GET_USER_BY_UID = BASE_URL + "Api.php?apicall=getUserByFirebaseUid";

    // Endpoints de usuário (CRUD)
    public static final String URL_GET_USER = USER_API_URL + "getUser.php?id=";
    public static final String URL_UPDATE_USER = UPDATE_API_URL + "update.php";

    // Endpoints de vagas
    public static final String URL_GET_VAGAS = BASE_URL + "Api.php?apicall=getVagas";
    public static final String URL_GET_VAGAS_BY_USER = BASE_URL + "Api.php?apicall=getVagasByUserId";
    public static final String URL_CADASTRAR_VAGA = BASE_URL + "Api.php?apicall=cadastrarVaga";
    public static final String URL_EXCLUIR_VAGA = BASE_URL + "Api.php?apicall=excluirVaga";
    public static final String URL_ATUALIZAR_VAGA = BASE_URL + "Api.php?apicall=atualizarVaga";

    // Endpoints de candidaturas
    public static final String URL_CANDIDATAR_VAGA = BASE_URL + "Api.php?apicall=candidatarVaga";
    public static final String URL_VERIFICAR_CANDIDATURA = BASE_URL + "Api.php?apicall=verificarCandidatura";
    public static final String URL_LISTAR_CANDIDATURAS = BASE_URL + "Api.php?apicall=listarCandidaturas";
    public static final String URL_ATUALIZAR_STATUS_CANDIDATURA = BASE_URL + "Api.php?apicall=atualizarStatusCandidatura";

    // Endpoints de interesse
    public static final String URL_REGISTRAR_INTERESSE = BASE_URL + "Api.php?apicall=registrarinteresse";

    // Configurações de timeout
    public static final int CONNECT_TIMEOUT = 15000; // 15 segundos
    public static final int READ_TIMEOUT = 10000;    // 10 segundos

    // Método para verificar conexão com a internet
    public static boolean isURLReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Log.d("API", "Sem conexão com a internet");
            return false;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(BASE_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            int responseCode = connection.getResponseCode();
            boolean isReachable = (responseCode == HttpURLConnection.HTTP_OK);
            Log.d("API", "Base URL reachable: " + isReachable);
            return isReachable;
        } catch (Exception e) {
            Log.e("API", "Erro ao verificar conexão com base URL", e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }



    // Método auxiliar para verificar endpoint específico
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
            boolean isReachable = (responseCode == HttpURLConnection.HTTP_OK);
            Log.d("API", "Endpoint " + urlString + " reachable: " + isReachable);
            return isReachable;
        } catch (Exception e) {
            Log.e("API", "Erro ao verificar endpoint: " + urlString, e);
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método para construir URL com parâmetros
    public static String buildUrl(String baseUrl, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return baseUrl;
        }

        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        boolean firstParam = !baseUrl.contains("?");

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (firstParam) {
                urlBuilder.append("?");
                firstParam = false;
            } else {
                urlBuilder.append("&");
            }
            urlBuilder.append(entry.getKey()).append("=").append(entry.getValue());
        }

        return urlBuilder.toString();
    }

    public static void checkApiReachability(Context context, ApiReachabilityCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
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
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    int responseCode = connection.getResponseCode();
                    return (responseCode == HttpURLConnection.HTTP_OK);
                } catch (Exception e) {
                    Log.e("API", "Erro ao verificar conexão com base URL", e);
                    return false;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(Boolean isReachable) {
                callback.onResult(isReachable);
            }
        }.execute();
    }

    public interface ApiReachabilityCallback {
        void onResult(boolean isReachable);
    }
}
