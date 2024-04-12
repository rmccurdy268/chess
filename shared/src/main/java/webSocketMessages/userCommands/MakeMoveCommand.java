package webSocketMessages.userCommands;

public class MakeMoveCommand extends UserGameCommand{
    public MakeMoveCommand(UserGameCommand command) {
        super(command.getAuthString());
        this.commandType = command.getCommandType();
    }
}
