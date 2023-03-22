package org.example;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Game {

    private final InetAddress userAddr;
    private final int userPort;
    private final int finalLength;
    private final ArrayList<String> anagramSequence;
    private ArrayList<String> dictionary;
    private Map<String, Set<String>> anagramicClasses;
    public Game(InetAddress userAddr, int userPort, int finalLength){
        this.userAddr = userAddr;
        this.userPort = userPort;
        this.finalLength = finalLength;

        dictionary = loadDictionary("../french-debian.txt");

        this.anagramSequence = findAnagramSequence(finalLength);

        anagramicClasses = createAnagramicClasses(dictionary);

    }

    private Map<String, Set<String>> createAnagramicClasses(ArrayList<String> dictionary){

        Map<String, Set<String>> anagramicClasses = new HashMap<String, Set<String>>();

        for(String word : dictionary){
            char wordArray[] = word.toCharArray();
            Arrays.sort(wordArray);
            String sortedWord = new String(wordArray);

            Set<String> newSet;
            if(anagramicClasses.containsKey(sortedWord)){
                newSet = anagramicClasses.get(sortedWord);
            }else{

                newSet = new HashSet<>();
            }
            newSet.add(word);
            anagramicClasses.put(sortedWord, newSet);

        }

        return anagramicClasses;
    }

    public Map<String, Set<String>> getAnagramicClasses(){
        return this.anagramicClasses;
    }
    public int getUserPort(){
        return this.userPort;
    }

    public InetAddress getUserAddr(){
        return this.userAddr;
    }

    public ArrayList<String> getAnagramSequence(){
        return this.anagramSequence;
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

    private ArrayList<String> findAnagramSequence(int maxLength){
        ArrayList<String> anagramSequence = new ArrayList<>();

        if(dictionary.size()==0){

            System.err.println("Dictionary is empty -> cannot create anagram sequence");
            return anagramSequence;
        }

        ArrayList<String> potentialLastWords = new ArrayList<>();

        /* Checking if there is a word of specified max length */
        for(String word : dictionary){
            if(word.length() == maxLength){
                potentialLastWords.add(word);
            }
        }

        if(potentialLastWords.isEmpty()){
            System.err.println("Could not find a word with a max length of " + maxLength + " -> cannot create anagram sequence");
            return anagramSequence;
        }

        /* Choosing one of the last words randomly */
        int randomLastWord = ThreadLocalRandom.current().nextInt(0, potentialLastWords.size());

        String lastWord = potentialLastWords.get(randomLastWord);

        ArrayList<String> initSequence = new ArrayList<>();
        initSequence.add(lastWord);

        int length = 1;

        anagramSequence.add(lastWord);
        return anagramSequence;
    }

    private void findAnagrams(String initialWord){

        /* Finding all anagram with 1 less letter */
        ArrayList<String> anagramsWith1LessLetter = new ArrayList<>();

        for(var entry : anagramicClasses.entrySet()){

            /* Getting all characters into a list */
            char keyAsArray[] = entry.getKey().toCharArray();
            List<Character> keyAsList = new ArrayList<>();
            for(char c : keyAsArray){
                keyAsList.add(c);
            }

            List<Character> keyAsListCopy = keyAsList;

            /* Deleting every letter in initial word from the key's letter */
            for(int i=0; i<initialWord.length(); i++){
                if(keyAsListCopy.contains(initialWord.charAt(i))){
                    keyAsListCopy.remove(initialWord.charAt(i));
                }
            }

            /* If only one letter is remaining we can use for the sequence */
            if(keyAsListCopy.size()==1){
                findAnagrams(entry.getKey());
            }

        }
        

    }

}
