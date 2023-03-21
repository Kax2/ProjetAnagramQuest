package org.example;

import java.net.Inet4Address;

public class Game {

    private final int id;
    private final Inet4Address user_addr;

    public Game(int id, Inet4Address user_addr){
        this.id = id;
        this.user_addr = user_addr;
    }

}
