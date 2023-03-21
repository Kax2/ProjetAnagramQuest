package org.example;

import java.net.Inet4Address;
import java.net.InetAddress;

public class Game {

    private final int id;
    private final InetAddress userAddr;
    private final int userPort;
    private final int finalLength;
    public Game(int id, InetAddress userAddr, int userPort, int finalLength){
        this.id = id;
        this.userAddr = userAddr;
        this.userPort = userPort;
        this.finalLength = finalLength;
    }

    public int getId(){
        return id;
    }

}
