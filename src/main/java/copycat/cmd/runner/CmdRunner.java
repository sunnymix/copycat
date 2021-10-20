package copycat.cmd.runner;

import copycat.cmd.option.Option;
import copycat.cmd.option.Options;
import copycat.infra.tapd.DeepSave;

import java.util.List;

public class CmdRunner {
    public static void run(List<Option> options, List<String> urls) {
        if (!urls.isEmpty()) {
            DeepSave.savePage(
                    Options.getDir(options),
                    urls.get(0),
                    options
            );
            // Http.save(options, urls.get(0));
        }
    }
}
