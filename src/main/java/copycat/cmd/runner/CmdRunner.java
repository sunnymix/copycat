package copycat.cmd.runner;

import copycat.catcher.HttpCatcher;
import copycat.cmd.option.Option;

import java.util.List;

public class CmdRunner {
    public static void run(List<Option> options, List<String> urls) {
        HttpCatcher.get(options, urls);
    }
}
