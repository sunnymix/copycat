package copycat.cmd;

import copycat.cmd.option.Option;

public class CmdException extends RuntimeException {
    public CmdException(String msg) {
        super(msg);
    }

    public static void EmptyCommand() {
        throw new CmdException("Empty command!");
    }

    public static void BadCommandName() {
        throw new CmdException("Bad command name, should be 'copycat'!");
    }

    public static void EmptyOptionName() {
        throw new CmdException("Empty option name!");
    }

    public static void HasNoUrl() {
        throw new CmdException("Has no url!");
    }

    public static void CannotCreateOption(Option option) {
        throw new CmdException(String.format("Cannot create option, Class('%s')!", option));
    }
}
