package com.alehee.wannaplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Text;

public class Index extends AppCompatActivity {

    private TextView t_view;
    private LinearLayout linearLayout;
    private Button b_play;
    private Button b_refresh;
    private Button b_logout;

    private String nick = "";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private static final String ROOT_URL = "https://riverlakestudios.pl/wp/";
    private static final String URL_VIEW = ROOT_URL+"view.php";
    private static final String URL_CHANGE = ROOT_URL+"insStat.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        t_view = findViewById(R.id.your_nick);
        b_play = findViewById(R.id.wpButt);
        b_refresh = findViewById(R.id.refButt);
        b_logout = findViewById(R.id.logoutButt);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        String getNick = intent.getStringExtra("nick");
        nick = getNick;

        t_view.setText(nick);

        refresh();

        b_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_status();
            }
        });

        b_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });

        b_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }

    @Override
    public void onBackPressed(){
        // NIC NIE RÓB
    }

    private void refresh(){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, URL_VIEW, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    linearLayout = findViewById(R.id.layout_nick);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    linearLayout.removeAllViews();

                    for(int i=0; i<response.length(); i++){
                        JSONObject jsonObject = response.getJSONObject(i);
                        String nick_var = jsonObject.getString("nick");
                        String color_var = jsonObject.getString("status");

                        TextView textView = new TextView(getApplicationContext());
                        textView.setText(nick_var);
                        textView.setGravity(Gravity.CENTER);
                        if(color_var.equals("1"))
                            textView.setTextColor(Color.parseColor("#00FF00"));
                        else
                            textView.setTextColor(Color.parseColor("#FF0000"));
                        textView.setTextSize(36);
                        linearLayout.addView(textView);

                        if(nick_var.equals(nick) && color_var.equals("1")) {
                            b_play.setText("LAME!");
                            b_play.setTextColor(Color.parseColor("#FF0000"));
                        }
                        else if(nick_var.equals(nick) && color_var.equals("0")){
                            b_play.setText("PLAY!");
                            b_play.setTextColor(Color.parseColor("#00FF00"));
                        }
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
        });

        // WYSYLKA QUEUE
        RequestQueue requestQueue = Volley.newRequestQueue(this );
        requestQueue.add(jsonArrayRequest);
    }

    private void change_status(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CHANGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    refresh();
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
                params.put("nick", nick);
                return params;
            }
        };

        // WYSYLKA QUEUE
        RequestQueue requestQueue = Volley.newRequestQueue(this );
        requestQueue.add(stringRequest);
    }

    private void logout(){

        // USUWA DANE LOGOWANIA
        editor = sharedPreferences.edit();
        editor.putString("login", "");
        editor.putString("password", "");
        editor.commit();
        // -----

        Intent intent = new Intent(Index.this, MainActivity.class);
        Index.this.startActivity(intent);
    }
}
