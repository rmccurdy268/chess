package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.util.concurrent.ConcurrentHashMap;

public class Library {
    public final ConcurrentHashMap<Integer, Lobby> lobbies = new ConcurrentHashMap<>();

    public Library(){}

    public void addLobby(int num, Lobby lobby){
        lobbies.put(num, lobby);
    }
    public void add(String visitorName, Session session, int gameID) {
        Lobby currentLobby = lobbies.get(gameID);
        currentLobby.add(visitorName, session);
    }
    public Lobby getLobby(Integer num){
        return lobbies.get(num);
    }
}
