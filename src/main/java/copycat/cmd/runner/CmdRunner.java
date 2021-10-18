package copycat.cmd.runner;

import copycat.cmd.option.Option;
import copycat.infra.Http;

import java.util.List;

public class CmdRunner {
    public static void run(List<Option> options, List<String> urls) {
        if (!urls.isEmpty()) {
            Http.save(options, urls.get(0));
        }
    }
}
