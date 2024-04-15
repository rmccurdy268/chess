package ui.websocket;

import webSocketMessages.serverMessages.ErrorMessage;

public interface ErrorHandler {
    public void getError(ErrorMessage message);
}
