package webSocketMessages.userCommands;

public class MakeMoveCommand extends UserGameCommand{
    public MakeMoveCommand(UserGameCommand command) {
        super(CommandType.MAKE_MOVE,command.getAuthString());
    }
}
