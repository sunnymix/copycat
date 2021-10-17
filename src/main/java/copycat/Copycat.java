package copycat;

import copycat.cmd.Cmd;

/**
 * Copycat command line application
 * <p>
 * TODO:
 * - Command line, use --option pattern instead of space pattern to split the args
 */
public class Copycat {
    public static void main(String[] args) {
        String cmdline = String.join(" ", args);
        Cmd cmd = new Cmd(cmdline);
        cmd.print();
        cmd.run();
    }
}
