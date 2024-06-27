package esgi.damien.whowroteit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText bookInput;
    private TextView titleText;
    private TextView authorText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bookInput = findViewById(R.id.bookInput);
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
    }

    /**
     * Cette méthode récupère le texte de la vue EditText, convertie en String et l'affecte à une variable appelée queryString.
     *
     * @param view
     */
    public void searchBooks(View view) {
        String queryString = bookInput.getText().toString();

        InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputManager != null) {
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        new FetchBook(titleText, authorText).execute(queryString);
        authorText.setText("");
        titleText.setText(R.string.loading);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected() && !queryString.isEmpty()) {
            new FetchBook(titleText, authorText).execute(queryString);
            authorText.setText("");
            titleText.setText(R.string.loading);
        } else {
            if (queryString.isEmpty()) {
                authorText.setText("");
                titleText.setText(R.string.no_search_term);
            } else {
                authorText.setText("");
                titleText.setText(R.string.no_network);
            }
        }
    }
}