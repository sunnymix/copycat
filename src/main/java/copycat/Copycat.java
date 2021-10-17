package copycat;

import copycat.cmd.Cmd;

/**
 * Copycat command line application
 */
public class Copycat {
    public static void main(String[] args) {
        String cmdline = String.join(" ", args);
        System.out.printf("\n==========\nArgs:\n%s\n==========\n\n", cmdline);
        Cmd cmd = new Cmd(cmdline);
        cmd.printInfo();
        cmd.run();
    }
}
