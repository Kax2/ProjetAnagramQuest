package org.example;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Serveur_UDP {

    public static final int PORT = 20000;
    public static final String INTERFACE = "localhost";
    private static LinkedList<Game> gameInstances;
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
    private static int getNewIdForGameInstance(){

        /* Get the ID of the last Game Instance */
        int id = gameInstances.get(gameInstances.size()-1).getId();


        /* Get the ids of all game instances */
        ArrayList<Integer> ids = new ArrayList<>();
        for(Game game : gameInstances){
            ids.add(game.getId());
        }

        /* Check if ID is already in the list of IDs, increase ID by one if it is already in the list of IDs*/
        while(ids.contains(id)){
            id++;
        }

        return id;

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

            /* Converting the data in the received packet to a List of commands */
            ArrayList<ArrayList<String>> commandList = datagramToCommandList(receivedPacket);

            /* create new game instance if start command is received */
            for(ArrayList<String> command : commandList){
                if(command.get(0)=="start"){
                    gameInstances.add(new Game(getNewIdForGameInstance(),receivedPacket.getAddress(), receivedPacket.getPort()));
                }
            }


            //System.out.println(receivedPacket.getAddress() + "/" + receivedPacket.getPort() + ": " + receivedPacketString);


            /*  Affichage pour voir si ça marche, ne pas prendre en compte
            for(int i=0; i<receivedPacketCommands.length; i++) {
                if(commandList.get(i).size()==1){
                    System.out.println("Elem " + i + " : " + commandList.get(i).get(0));
                } else if (commandList.get(i).size()==2) {
                    System.out.println("Elem " + i + " : " + commandList.get(i).get(0) + " -> "+commandList.get(i).get(1));
                }
            }
            */


        }
    }
}
