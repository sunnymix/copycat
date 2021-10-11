package copycat;

import copycat.cmd.Cmd;

/**
 * Copycat command line application
 */
public class Copycat {
    public static void main(String[] args) {
        Cmd.help();

        Cmd cmd = new Cmd(args);
        System.out.println(cmd.toString());

        cmd.run();
    }
}
