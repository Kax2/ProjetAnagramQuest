package org.example;

import java.net.Inet4Address;
import java.net.InetAddress;

public class Game {

    private final int id;
    private final InetAddress userAddr;
    private final int userPort;

    public Game(int id, InetAddress userAddr, int userPort){
        this.id = id;
        this.userAddr = userAddr;
        this.userPort = userPort;
    }

    public int getId(){
        return id;
    }

}
