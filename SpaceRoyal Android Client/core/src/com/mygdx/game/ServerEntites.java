package com.mygdx.game;

public interface ServerEntites {

    public static final String SERVER_ADDR ="http://ec2-34-242-217-217.eu-west-1.compute.amazonaws.com:8080";
    public static final String SHOOT_ACTION ="shoot";
    public static final String DISCONNECT_EVENT="playerDisconnected";
    public static final String PLAYER_CONNECTED="newPlayer";
    public static final String SOCKET_ID="socketID";
    public static final String PLAYER_MOVED="playerMoved";
    public static final String GET_PLAYERS="getPlayers";
}
