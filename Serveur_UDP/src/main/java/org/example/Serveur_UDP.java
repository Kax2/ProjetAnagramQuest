package org.example;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Serveur_UDP {

    public static final int PORT = 20000;
    public static final String INTERFACE = "localhost";
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
            String s = new String(receivedPacket.getData(), 0, receivedPacket.getLength(), StandardCharsets.UTF_8);
            s = s.trim();
            System.out.println(receivedPacket.getAddress() + "/" + receivedPacket.getPort() + ": " + s);



        }
    }
}
