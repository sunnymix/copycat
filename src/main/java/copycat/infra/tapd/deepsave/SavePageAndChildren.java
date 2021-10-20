package copycat.infra.tapd.deepsave;

import copycat.cmd.option.Option;

import java.util.List;

public class SavePageAndChildren {
    public static void save(String dir, String pageUrl, String catalog, List<Option> options) {
        SaveChildren.save();
    }
}
