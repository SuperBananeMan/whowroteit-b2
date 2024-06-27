package esgi.damien.whowroteit;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.InputStreamReader;

public class NetworkUtils {

    // Tag pour les messages de log
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    // URL de base pour l'API Books.
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    // Paramètre pour la chaîne de recherche.
    private static final String QUERY_PARAM = "q";
    // Paramètre qui limite les résultats de la recherche.
    private static final String MAX_RESULTS = "maxResults";
    // Paramètre pour filtrer par type de recherche.
    private static final String PRINT_TYPE = "printType";

    static String getBookInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;

        Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, queryString)
                .appendQueryParameter(MAX_RESULTS, "10")
                .appendQueryParameter(PRINT_TYPE, "books")
                .build();

        try {
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Récupère le InputStream.
            InputStream inputStream = urlConnection.getInputStream();
            // Crée un buffer pour le InputStream.
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Utilise un StringBuilder pour contenir la réponse.
            StringBuilder builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                // Ajoute une nouvelle ligne à la réponse.
                builder.append(line);
                builder.append("\n");
            }

            if (builder.length() == 0) {
                // Stream était vide. Aucun besoin de parser.
                return null;
            }

            bookJSONString = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Ferme la connexion et le BufferedReader.
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Écrit le JSON dans le log pour déboguer.
        Log.d(LOG_TAG, bookJSONString);

        return bookJSONString;
    }
}
