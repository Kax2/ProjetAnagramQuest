package org.example;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class Serveur_UDP {

    public static final int PORT = 20000;
    public static final String INTERFACE = "localhost";
    private static ArrayList<Game> gameInstances = new ArrayList<>();
    private static ArrayList<ArrayList<String>> datagramToCommandList(DatagramPacket packet){

        /* Converting data to String */
        String dataString = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

        dataString = dataString.trim();

        /*
        Converting the data to an array of commands as Strings
        each command must be separated with commas as such : "cmd1,cmd2,cmd3"
        */
        var commands = dataString.split(",");

        /*
        Separate each command into a command with a value if it is provided
        the command and value must be separated by ":"
        for example the command "start:10" will become an ArrayList with "start" and "10"
        */
        ArrayList<ArrayList<String>> commandList = new ArrayList<ArrayList<String>>();

        for(int i=0; i<commands.length;i++){

            String[] tempCommand = commands[i].split(":");
            ArrayList<String> tempCommandArray = new ArrayList<>(Arrays.asList(tempCommand));
            commandList.add(tempCommandArray);
        }

        return commandList;
    }
    private static boolean isGameAlreadyStarted(InetAddress addr, int port){

        for (Game game : gameInstances){
            if((game.getUserAddr()==addr) && (game.getUserPort()==port)){
                return true;
            }
        }
        return false;
    }

    private static void sendErrorPacket(DatagramSocket socket, InetAddress destAddr, int destPort, String errorMsg){

        byte[] errorMsgBytes = ("error:" + errorMsg).getBytes(StandardCharsets.UTF_8);

        var errorMsgPacket = new DatagramPacket(errorMsgBytes, 0, errorMsgBytes.length);

        errorMsgPacket.setAddress(destAddr);
        errorMsgPacket.setPort(destPort);

        try {
            socket.send(errorMsgPacket);
        } catch (IOException e) {
            System.err.println("Could not send packet");
        }
    }

    private static boolean parseStart(ArrayList<ArrayList<String>> commandList, DatagramPacket receivedPacket, DatagramSocket socket){



        String errorMsg;

        for(ArrayList<String> command : commandList){

            if(command.get(0).equals("start")){

                /* Checking if user did not send a value of a max length for the anagram */
                if((command.size()==1)){

                    errorMsg = "No max size of anagram specified -> canceling game instantiation";

                    /* printing error in console and sending error message to user's client */
                    System.err.println("Received start command by : " + receivedPacket.getAddress() + "/" + receivedPacket.getPort() + " : " + errorMsg);

                    sendErrorPacket(socket,receivedPacket.getAddress(), receivedPacket.getPort(), errorMsg);

                    return false;
                }

                /* Checking if more than one value has been sent */
                if(command.size()>2){
                    System.out.println("Several values were given -> using only the first value");
                }

                /* initializing final length of an anagram */
                int finalLength;

                /* Checking if the value sent is a number */
                try{
                    finalLength = Integer.parseInt(command.get(1));
                }catch (NumberFormatException e){

                    errorMsg = "Value parsed is not a Number -> canceling game instantiation";

                    /* printing error in console and sending error message to user's client */
                    System.err.println("Received start command by : " + receivedPacket.getAddress() + "/" + receivedPacket.getPort() + " : " + errorMsg);

                    sendErrorPacket(socket,receivedPacket.getAddress(), receivedPacket.getPort(), errorMsg);

                    return false;
                }

                /* Check if game is already started for the user*/
                if(isGameAlreadyStarted(receivedPacket.getAddress(),receivedPacket.getPort())){

                    errorMsg = "A game has already been started for this client -> canceling game instantiation";

                    /* printing error in console and sending error message to user's client */
                    System.err.println("Received start command by : " + receivedPacket.getAddress() + "/" + receivedPacket.getPort() + " : " + errorMsg);

                    sendErrorPacket(socket,receivedPacket.getAddress(), receivedPacket.getPort(), errorMsg);

                    return false;
                }

                Game game = new Game(receivedPacket.getAddress(), receivedPacket.getPort(), finalLength);

                /* Checking if we could generate a anagram sequence for the specified max length of a word */
                if(game.getAnagramSequence().isEmpty()){

                    errorMsg = "Could not find a anagram sequence -> canceling game instantiation";

                    /* printing error in console and sending error message to user's client */
                    System.err.println("Received start command by : " + receivedPacket.getAddress() + "/" + receivedPacket.getPort() + " : " + errorMsg);

                    sendErrorPacket(socket,receivedPacket.getAddress(), receivedPacket.getPort(), errorMsg);

                    return false;
                }

                /* All other checks are valid, we add the game to instances for the player and do not check other commands */
                gameInstances.add(game);
                return true;
            }
        }
        /* No start command in command list, we do nothing, and check for other commands*/
        return false;
    }
    public static void main(String[] args) throws UnknownHostException, SocketException {

        ///, InetAddress.getByName(INTERFACE)

        /* Initialization of a socket */
        var socket = new DatagramSocket(PORT);

        /* Setting the timeout of the socket to 10s */
        socket.setSoTimeout(10000);

        /* Buffer for our received packets */
        var buffer = new byte[128];

        /* Initialization of our receiving packet */
        var receivedPacket = new DatagramPacket(buffer, buffer.length);

        /* Initialization of our sending packet */
        var sentPacket = new DatagramPacket(new byte[0], 0);

        /* Main loop */
        while(true){

            /* Testing if we have received a packet */
            try {
                socket.receive(receivedPacket);
            } catch (IOException e) {
                System.err.println("Packet not received");
                continue;
            }

            String dataString = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), StandardCharsets.UTF_8);
            System.out.println("Received Message from : " + receivedPacket.getAddress() + "/" + receivedPacket.getPort() + ": " + dataString);

            /* Converting the data in the received packet to a List of commands */
            ArrayList<ArrayList<String>> commandList = datagramToCommandList(receivedPacket);

            /* Checking if user sent start command */
            if(parseStart(commandList, receivedPacket, socket)){
                continue;
            }

            System.out.println("Game Instances :");
            if(gameInstances.size()!=0){
                for(Game game : gameInstances){
                    System.out.println("ID : " + game.getUserAddr() + "/" + game.getUserPort());

                    Map<String, Set<String>> anagramicClasses = game.getAnagramicClasses();
                    for(var anagramicClasse :  anagramicClasses.entrySet()){
                        System.out.print(anagramicClasse.getKey() + " -> ");
                        for(var value : anagramicClasse.getValue()){
                            System.out.print(value + " ");
                            System.out.println();
                        }
                    }
                }
            }
        }
    }
}
