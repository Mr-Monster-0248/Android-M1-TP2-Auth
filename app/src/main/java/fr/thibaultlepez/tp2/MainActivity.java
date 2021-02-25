package fr.thibaultlepez.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private EditText _loginForm;
    private EditText _passwordForm;
    private TextView _resultText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _loginForm = (EditText) findViewById(R.id.loginForm);
        _passwordForm = (EditText) findViewById(R.id.passwordForm);
        _resultText = (TextView) findViewById(R.id.resultText);
    }

    private static String readStream(InputStream in) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            int i = in.read();
            while (i != -1) {
                bo.write(i);
                i = in.read();
            }
            return bo.toString();
        } catch (IOException e) {
            return "";
        }
    }


    public void authenticate(View view) throws InterruptedException {
        String login = _loginForm.getText().toString();
        String password = _passwordForm.getText().toString();

        Authenticate auth = new Authenticate(login, password);
        Thread authenticateHttpBin = new Thread(auth);
        authenticateHttpBin.start();
        authenticateHttpBin.join();

        String res = auth.getResult();
        this._resultText.setText(res);
        // Log.d("LEPEZ", auth.getResult());
    }

    class Authenticate implements Runnable {
        private String _password;
        private String _login;

        private String result;

        Authenticate(String login, String password) {
            this._login = login;
            this._password = password;
        }

        public String getResult() {
            return result;
        }

        @Override
        public void run() {
            try {
                URL url = new URL("https://httpbin.org/basic-auth/bob/sympa");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                String credential = this._login + ":" + this._password;
                String basicAuth = "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);
                urlConnection.setRequestProperty("Authorization", basicAuth);

                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = readStream(in);
                    Log.i("LEPEZ", s);
                    this.result = s;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}