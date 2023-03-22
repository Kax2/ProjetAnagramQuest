package org.example;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Game {

    private final InetAddress userAddr;
    private final int userPort;
    private final int finalLength;
    //public final ArrayList<String> anagramSequence;
    private ArrayList<String> dictionary;
    public Game(InetAddress userAddr, int userPort, int finalLength){
        this.userAddr = userAddr;
        this.userPort = userPort;
        this.finalLength = finalLength;

        dictionary = loadDictionary("../french-debian.txt");

    }

    public int getUserPort(){
        return this.userPort;
    }

    public InetAddress getUserAddr(){
        return this.userAddr;
    }

    private ArrayList<String> loadDictionary(String path){

        ArrayList<String> dictionary = new ArrayList<>();

        try {
            dictionary = (ArrayList<String>) Files.readAllLines(Paths.get(path));
        } catch (IOException e) {
            System.err.println("Could not load the dictionary");
        }

        return dictionary;
    }

    public void printDict(){
        for(String word : dictionary){
            System.out.println(word);
        }
    }

}
