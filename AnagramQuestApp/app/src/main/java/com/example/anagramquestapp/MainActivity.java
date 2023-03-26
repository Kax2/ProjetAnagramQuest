package com.example.anagramquestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity
    implements AdapterView.OnItemSelectedListener{

    private String selectedDictionary;

    private Game game;

    private ArrayList<String> availableDictionaries = new ArrayList<>();
    /* On emulator use 10.0.2.2 ip address to access host machine address ip  */
    private String URLtoDictionaries = "http://192.168.1.76:8888/dictionaries";
    private String URLtoGenerateAnagramSequence = "https://localhost:8888/game/:dictionary/:n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* For testing purposes */
        /*
        availableDictionaries.add("english");
        availableDictionaries.add("french");
         */

        fetchDictionaryList();

        Spinner dictionarySelect = findViewById(R.id.dictionarySelect);
        dictionarySelect.setOnItemSelectedListener(this);

        ArrayAdapter<String> dictAdapter = new ArrayAdapter(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                availableDictionaries);

        dictionarySelect.setAdapter(dictAdapter);

        Button generateAnagramButton = findViewById(R.id.generateAnagramButton);
        generateAnagramButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditText maxNumberOfLettersInput = (EditText) findViewById(R.id.maxNumberOfLettersInput);
                        String maxNumberOfLettersString = maxNumberOfLettersInput.getText().toString();

                        int maxNumberOfLetters=0;

                        try {
                            maxNumberOfLetters = Integer.parseInt(maxNumberOfLettersString);
                            Log.i("maxNumberOfLetters",Integer.toString(maxNumberOfLetters));
                        }catch (NumberFormatException e){
                            Toast.makeText(MainActivity.this, "Please enter a number for the max number of letters", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(maxNumberOfLetters<2){
                            Toast.makeText(MainActivity.this, "Please selected a number higher than 2", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ArrayList<ArrayList<String>> anagramSequence = requestSequence(maxNumberOfLetters);

                        if(anagramSequence.isEmpty()){
                            Toast.makeText(MainActivity.this,"Could not generate a anagram sequence",Toast.LENGTH_LONG).show();
                            return;
                        }

                        game = new Game(anagramSequence);

                        showAnagramToGuess();

                        Log.i("Sequence", anagramSequence.toString());


                        Log.i("dictSelected",selectedDictionary);

                    }
                }
        );


        Button guessButton = findViewById(R.id.guessButton);
        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText guessInput = findViewById(R.id.guessInput);
                String guessString = guessInput.getText().toString();

                Log.i("Debug", "HERE");

                if(game==null){
                    return;
                }
                if(!game.isGameStarted()){
                    return;
                }

                if(game.answerIsCorrect(guessString)){
                    Toast.makeText(MainActivity.this, "Correct answer !" ,Toast.LENGTH_LONG).show();
                    if(!game.setNextAnagramToGuess()){
                        Toast.makeText(MainActivity.this, "You WIN !" ,Toast.LENGTH_LONG).show();
                        TextView anagramTextView = findViewById(R.id.anagramToGuess);
                        anagramTextView.setText("");
                    }

                }

            }
            });



    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        selectedDictionary = availableDictionaries.get(i);
        Log.d("MyAPP","dictionary selected : "+ availableDictionaries.get(i));

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private ArrayList<ArrayList<String>> requestSequence(int maxNumberOfLetters){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URLtoGenerateAnagramSequence.replace(":dictionary",selectedDictionary);

        ArrayList<ArrayList<String>> returnSequence = new ArrayList<>();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONArray sequenceJA = new JSONArray(response);

                            for(int i=0; i<sequenceJA.length(); i++){
                                JSONObject obj = sequenceJA.getJSONObject(i);
                                ArrayList<String> words = (ArrayList<String>) Arrays.asList(obj.getString("words").split(","));
                                returnSequence.add(words);
                            }

                        }catch (JSONException e){
                            Log.e("requestedJSON", "Problem with JSON");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

        }
        );
        queue.add(stringRequest);
        return returnSequence;
    }

    private void fetchDictionaryList(){

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLtoDictionaries,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray dictionaryListJSON = new JSONArray(response);
                            for (int i = 0; i < dictionaryListJSON.length(); i++) {
                                JSONObject obj = dictionaryListJSON.getJSONObject(i);
                                availableDictionaries.add((String) obj.get("language"));
                            }
                        } catch (JSONException e) {
                            Log.e(getClass().getName(), "Problem creating JSON Array", e);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(getClass().getName(), "Error when retrieving data", error);
                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(stringRequest);
    }

    private void showAnagramToGuess(){

        TextView anagramTextView = findViewById(R.id.anagramToGuess);
        anagramTextView.setText(game.getCurrentAnagramToGuess());

    }
}