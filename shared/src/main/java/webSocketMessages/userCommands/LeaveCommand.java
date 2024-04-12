package webSocketMessages.userCommands;

public class LeaveCommand extends UserGameCommand{
    public LeaveCommand(UserGameCommand command) {

        this.commandType = command.getCommandType();
    }
}
