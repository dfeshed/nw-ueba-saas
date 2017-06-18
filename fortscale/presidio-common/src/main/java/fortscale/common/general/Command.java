package fortscale.common.general;


public enum Command {

    RUN, CLEAN, CLEAN_ALL;

    public static Command createCommand(String commandName) throws Exception {
        return Command.valueOf(commandName.toUpperCase());
    }

    public boolean equals(Command command) {
        return this.name().equals(command.name());
    }
}
