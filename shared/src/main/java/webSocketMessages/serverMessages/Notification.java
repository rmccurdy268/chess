package webSocketMessages.serverMessages;

import com.google.gson.Gson;

public class Notification extends ServerMessage{
    public Notification(ServerMessageType type) {
        super(type);
    }

    public String toString() {
        return new Gson().toJson(this);
    }

}
