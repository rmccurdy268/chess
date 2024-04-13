package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    public LeaveCommand(UserGameCommand command) {
        super(CommandType.LEAVE,command.getAuthString());
    }
}
