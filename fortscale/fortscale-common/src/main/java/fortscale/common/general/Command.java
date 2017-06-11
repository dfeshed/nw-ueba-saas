package fortscale.common.general;


public enum Command {

    ENRICH, CLEAN;

    public static Command createCommand(String commandName) throws Exception {
        return Command.valueOf(commandName.toUpperCase());
    }
}
