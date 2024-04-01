package server;

import java.util.Collection;

public record ListArray(Collection<GameList> games) {

    public String makeString(){
        StringBuilder giantString = new StringBuilder();
        for (GameList game:games){
            String miniString = "";
            miniString = String.format("[ gameName: %s - gameID: %d - whiteUser: %s - blackUser: %s ]\n", game.gameName(), game.gameID(), game.whiteUsername(), game.blackUsername());
            giantString.append(miniString);
        }
        return giantString.toString();
    }
}


