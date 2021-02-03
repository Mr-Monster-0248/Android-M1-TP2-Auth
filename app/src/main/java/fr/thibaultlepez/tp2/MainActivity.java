package fr.thibaultlepez.tp2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText _loginForm;
    private EditText _passwordForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _loginForm = (EditText) findViewById(R.id.loginForm);
        _passwordForm = (EditText) findViewById(R.id.passwordForm);
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

    private static void authenticateHTTPBin(String login, String password) {
        try {
            URL url = new URL("https://httpbin.org/basic-auth/bob/sympa");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            String credential = login + ":" + password;
            String basicAuth = "Basic " + Base64.encodeToString(credential.getBytes(), Base64.NO_WRAP);
            urlConnection.setRequestProperty("Authorization", basicAuth);


            Thread thread = new Thread(() -> {
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String s = readStream(in);
                    Log.i("LEPEZ", s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            thread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authenticate(View view) {
        String login = _loginForm.getText().toString();
        String password = _passwordForm.getText().toString();
        authenticateHTTPBin(login, password);
    }
}