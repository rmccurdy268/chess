package webSocketMessages.userCommands;

public class ResignCommand extends UserGameCommand{
    public ResignCommand(UserGameCommand command) {
        super(command.getAuthString());
        this.commandType = command.getCommandType();
    }
}
