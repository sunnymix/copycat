package copycat.infra.tapd;

import copycat.cmd.option.Option;
import copycat.infra.tapd.deepsave.SavePage;

import java.util.List;

public class DeepSave {
    public static void save(String dir, String pageUrl, String folderUrl) {
    }

    public static void savePage(String dir, String pageUrl, List<Option> options) {
        SavePage.save(dir, pageUrl, options);
    }
}
