package com.alehee.wannaplay;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String ver = "0.2.1";

    private String nick = "Error";

    private Button b_log;
    private EditText t_pass;
    private EditText t_log;
    private ImageView i_pass;
    private ImageView i_log;
    private TextView v_ver;
    private TextView v_err;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String ROOT_URL = "https://riverlakestudios.pl/wp/";
    private static final String URL_LOGIN = ROOT_URL+"login.php";
    private static final String URL_VER = ROOT_URL+"verCheck.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        v_err = findViewById(R.id.errorView);
        v_ver = findViewById(R.id.verView);
        v_ver.setText("Version: "+ver);

        t_pass = findViewById(R.id.passText);
        t_log = findViewById(R.id.loginText);

        i_pass = findViewById(R.id.passImg);
        i_log = findViewById(R.id.loginImg);

        b_log = findViewById(R.id.logButt);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        checkVersion();

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void loginMySQL(String pass, String log){
        final String password = pass;
        final String login = log;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    nick = jsonObject.getString("message");

                    if(!nick.equals("Error")) {
                        v_err.setVisibility(View.INVISIBLE);

                        // ZAPISUJE LOGOWANIE
                        editor.putString("login", login);
                        editor.putString("password", password);
                        editor.commit();
                        // -----

                        Intent intent = new Intent(MainActivity.this, Index.class);
                        intent.putExtra("nick", nick);
                        MainActivity.this.startActivity(intent);
                    }
                    else{
                        v_err.setText("Błąd logowania!");
                        v_err.setVisibility(View.VISIBLE);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
                // TUTAJ MUSI BYC HASH MAPA ŻEBY WKLEJAC POST
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // NAZWA POST I ZMIENNA KTÓRA MA BYC WYSLANA
                params.put("password", password);
                params.put("login", login);
                return params;
            }
        };

        // WYSYLKA QUEUE
        RequestQueue requestQueue = Volley.newRequestQueue(this );
        requestQueue.add(stringRequest);
    }

    private void checkVersion(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_VER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String string = jsonObject.getString("message");
                    if(string.equals("OK")){

                        String log_login = null;
                        String log_password = null;

                        log_login = sharedPreferences.getString("login", "");
                        log_password = sharedPreferences.getString("password", "");

                        if(!log_login.equals("") && !log_password.equals("")){
                            loginMySQL(log_password, log_login);
                        }

                        b_log.setVisibility(View.VISIBLE);
                        t_pass.setVisibility(View.VISIBLE);
                        t_log.setVisibility(View.VISIBLE);
                        i_pass.setVisibility(View.VISIBLE);
                        i_log.setVisibility(View.VISIBLE);

                        b_log.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                String password = t_pass.getText().toString();
                                String login = t_log.getText().toString();

                                loginMySQL(password, login);
                            }
                        });
                    }
                    else if(string.equals("MAINTENANCE")){
                        v_err.setText("Aplikacja jest teraz niedostępna!");
                        v_err.setVisibility(View.VISIBLE);

                        b_log.setVisibility(View.INVISIBLE);
                        t_pass.setVisibility(View.INVISIBLE);
                        t_log.setVisibility(View.INVISIBLE);
                        i_pass.setVisibility(View.INVISIBLE);
                        i_log.setVisibility(View.INVISIBLE);
                    }
                    else{
                        v_err.setText("Masz nieaktualną wersję aplikacji! \n Nowa wersja aplikacji ściąga się na Twój telefon! \n Zainstaluj ją!");
                        v_err.setVisibility(View.VISIBLE);

                        b_log.setVisibility(View.INVISIBLE);
                        t_pass.setVisibility(View.INVISIBLE);
                        t_log.setVisibility(View.INVISIBLE);
                        i_pass.setVisibility(View.INVISIBLE);
                        i_log.setVisibility(View.INVISIBLE);

                        DownloadManager downloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(string);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        Long reference = downloadManager.enqueue(request);
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("ver", ver);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this );
        requestQueue.add(stringRequest);
    }
}
