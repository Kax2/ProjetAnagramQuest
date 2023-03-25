package com.example.anagramquestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener{

    private String selectedDictionary;

    private ArrayList<String> availableDictionaries = new ArrayList<>();
    private String URLtoDictionaries = "https://localhost:8888/dictionaries";
    private String URLtoGenerateAnagramSequence = "https://localhost:8888//game/:dictionary/:n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        availableDictionaries.add("english");
        availableDictionaries.add("french");
         */

        Spinner dictionarySelect = findViewById(R.id.dictionarySelect);
        dictionarySelect.setOnItemSelectedListener(this);

        ArrayAdapter<String> dictAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,availableDictionaries);

        dictionarySelect.setAdapter(dictAdapter);

        //Log.d("MyAPP","dictionary selected :");

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        Log.d("MyAPP","dictionary selected : "+ availableDictionaries.get(i));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /*
    private void fetchDictionaryList(){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLtoDictionaries,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray dictionaryListJSON = new JSONArray(response);
                            for(int i=0; i<dictionaryListJSON.length(); i++){

                            }
                        }catch (JSONException e){
                            Log.e(getClass().getName(), "Problem creating JSON Array", e);
                        }
                    }
                }
    }*/
}