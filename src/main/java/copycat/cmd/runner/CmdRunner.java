package copycat.cmd.runner;

import copycat.cat.HttpCat;
import copycat.cmd.option.Option;

import java.util.List;

public class CmdRunner {
    public static void run(List<Option> options, List<String> urls) {
        HttpCat.get(options, urls);
    }
}
