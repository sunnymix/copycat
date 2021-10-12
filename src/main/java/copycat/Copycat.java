package copycat;

import copycat.cmd.Cmd;

/**
 * Copycat command line application
 */
public class Copycat {
    public static void main(String[] args) {
        Cmd cmd = new Cmd(args);
        cmd.printInfo();
        cmd.run();
    }
}
