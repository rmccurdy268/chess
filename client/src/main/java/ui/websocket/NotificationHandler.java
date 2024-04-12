package ui.websocket;

import webSocketMessages.serverMessages.Notification;

public interface NotificationHandler {
    void notify(Notification notification);
}

