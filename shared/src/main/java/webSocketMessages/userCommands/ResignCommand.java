package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(UserGameCommand command) {
        super(CommandType.RESIGN,command.getAuthString());
    }
}
