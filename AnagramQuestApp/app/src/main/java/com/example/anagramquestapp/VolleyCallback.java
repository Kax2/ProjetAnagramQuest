package com.example.anagramquestapp;

import java.util.ArrayList;

public interface VolleyCallback {

    void onError(String message);

    void onResponse(ArrayList<String> response);
}
