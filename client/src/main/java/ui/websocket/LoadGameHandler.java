package ui.websocket;

import webSocketMessages.serverMessages.LoadMessage;

public interface LoadGameHandler {

    public void loadGame(LoadMessage message);
}
