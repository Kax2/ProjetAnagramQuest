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
    private final LinkedList<String> anagramSequence;
    private ArrayList<String> dictionary;
    private Map<String, Set<String>> anagramicClasses;
    public Game(InetAddress userAddr, int userPort, int finalLength){
        this.userAddr = userAddr;
        this.userPort = userPort;
        this.finalLength = finalLength;

        dictionary = loadDictionary("../french-debian.txt");

        anagramicClasses = createAnagramicClasses(dictionary);

        this.anagramSequence = findAnagramSequence(finalLength);

    }
    private String sortWord(String word){

        char wordArray[] = word.toCharArray();
        Arrays.sort(wordArray);
        String sortedWord = new String(wordArray);
        return sortedWord;

    }
    private Map<String, Set<String>> createAnagramicClasses(ArrayList<String> dictionary){

        Map<String, Set<String>> anagramicClasses = new HashMap<String, Set<String>>();

        for(String word : dictionary){

            String sortedWord = sortWord(word);


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

    public LinkedList<String> getAnagramSequence(){
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

    private LinkedList<String> findAnagramSequence(int maxLength){

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

        System.out.println("Last word : " + lastWord);

        LinkedList<String> anagramSequence = findAnagrams(lastWord);

        //System.out.println("Anagram sequence :");
        //System.out.println(anagramSequence);

        /* Delete last string if it is of length 1, it is useless */
        if(anagramSequence.getLast().length()==1){
            anagramSequence.remove(anagramSequence.size()-1);
        }

        return anagramSequence;

    }

    private LinkedList<String> findAnagrams(String initialWord){

        /* Finding all anagram with 1 less letter */
        ArrayList<String> anagramsWith1LessLetter = new ArrayList<>();

        for(var entry : anagramicClasses.entrySet()){

            if(!(entry.getKey().length() == initialWord.length()-1)){
                continue;
            }

            /* Getting all characters into a list */
            char initialWordAsArray[] = initialWord.toCharArray();
            List<Character> initialWordAsList = new ArrayList<>();
            for(char c : initialWordAsArray){
                initialWordAsList.add(c);
            }

            /* Deleting every letter in initial word from the key's letter */
            for(int i=0; i<entry.getKey().length(); i++){
                if(initialWordAsList.contains(entry.getKey().charAt(i))){
                    for(int j=0; j<initialWordAsList.size(); j++){
                        if(initialWordAsList.get(j)==entry.getKey().charAt(i)){
                            initialWordAsList.remove(j);
                            break;
                        }
                    }
                }
            }

            /* If only one letter is remaining we can use for the sequence */
            if(initialWordAsList.size()==1){
                anagramsWith1LessLetter.add(entry.getKey());
            }

        }

        //System.out.println("Anagrams with 1 less letter of " + initialWord + " :");
        //System.out.println(anagramsWith1LessLetter);

        /* If no anagrams with one less letters are found, return the initial word */
        if(anagramsWith1LessLetter.isEmpty()){
            LinkedList<String> retWord = new LinkedList<>();
            retWord.add(initialWord);

            //System.out.println("Returned Word : ");
            //System.out.println(retWord);
            return retWord;
        }

        ArrayList<LinkedList<String>> potentialSequences = new ArrayList<>();

        /* Get sequences from all potential anagrams */
        for(String anagram : anagramsWith1LessLetter){

            potentialSequences.add(findAnagrams(anagram));

        }

        /* Decide which sequence is the best by choosing the longest one */
        int bestSequence = 0;
        for(int i=1; i<potentialSequences.size(); i++){

            /* If both sequences are equal choose randomly one of the two */
            if(potentialSequences.get(i).size() == potentialSequences.get(bestSequence).size()){
                if(ThreadLocalRandom.current().nextInt(0, 2)==1){
                    bestSequence=i;
                }
            }

            if(potentialSequences.get(i).size() > potentialSequences.get(bestSequence).size()){
                bestSequence = i;
            }

        }

        LinkedList<String> sequence = new LinkedList<>();
        sequence.add(initialWord);
        sequence.addAll(potentialSequences.get(bestSequence));

        //System.out.println("Returned sequence :");
        //System.out.println(sequence);
        return sequence;

    }

}
