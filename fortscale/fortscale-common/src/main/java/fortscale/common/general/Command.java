package fortscale.common.general;


public enum Command {

    ENRICH, CLEAN, CLEAN_ALL;

    public static Command createCommand(String commandName) throws Exception {
        return Command.valueOf(commandName.toUpperCase());
    }
}
