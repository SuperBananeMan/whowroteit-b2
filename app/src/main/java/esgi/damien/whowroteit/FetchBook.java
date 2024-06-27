package esgi.damien.whowroteit;

import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String, Void, String> {

    private WeakReference<TextView> titleText;
    private WeakReference<TextView> authorText;

    FetchBook(TextView titleText, TextView authorText) {
        this.titleText = new WeakReference<>(titleText);
        this.authorText = new WeakReference<>(authorText);
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            // Convertit la réponse en objet JSON.
            JSONObject jsonObject = new JSONObject(s);
            // Récupère le JSONArray des éléments du livre.
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            // Initialise les champs de l'itérateur et des résultats.
            int i = 0;
            String title = null;
            String authors = null;
            // Recherche les résultats dans le tableau des éléments, en quittant
            // lorsque le titre et l'auteur sont trouvés ou
            // lorsque tous les éléments ont été vérifiés.
            while (i < itemsArray.length() &&
                    (authors == null && title == null)) {
                // Récupère les informations sur l'élément actuel.
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");
                // Essayez d'obtenir l'auteur et le titre de l'élément actuel,
                // détectez si l'un des champs est vide et passez à autre chose.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // Passe à l'élément suivant.
                i++;
            }
            // Si les deux sont trouvés, affiche le résultat.
            if (title != null && authors != null) {
                titleText.get().setText(title);
                authorText.get().setText(authors);
            } else {
                // Si aucun n'est trouvé, mettez à jour l'interface utilisateur
                // pour afficher les résultats ayant échoué.
                titleText.get().setText(R.string.no_results);
                authorText.get().setText("");
            }
        } catch (Exception e) {
            // Si onPostExecute ne reçoit pas une chaîne JSON appropriée,
            // mettez à jour l'interface utilisateur pour afficher les résultats ayant échoué.
            titleText.get().setText(R.string.no_results);
            authorText.get().setText("");
            e.printStackTrace();
        }
        super.onPostExecute(s);
    }
}
