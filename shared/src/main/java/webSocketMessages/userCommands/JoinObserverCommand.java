package webSocketMessages.userCommands;

public class JoinObserverCommand extends UserGameCommand{
    public JoinObserverCommand(UserGameCommand command) {
        super(command.getAuthString());
        this.commandType = command.getCommandType();
    }
}
