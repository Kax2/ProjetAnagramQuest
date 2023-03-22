package org.example;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;

public class Game {

    private final InetAddress userAddr;
    private final int userPort;
    private final int finalLength;
    //public final ArrayList<String> anagramSequence;
    public Game(InetAddress userAddr, int userPort, int finalLength){
        this.userAddr = userAddr;
        this.userPort = userPort;
        this.finalLength = finalLength;
    }

    public int getUserPort(){
        return this.userPort;
    }

    public InetAddress getUserAddr(){
        return this.userAddr;
    }

}
