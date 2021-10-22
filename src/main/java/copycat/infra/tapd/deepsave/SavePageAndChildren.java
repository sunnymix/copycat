package copycat.infra.tapd.deepsave;

import copycat.cmd.option.Option;
import copycat.infra.tapd.catalog.Catalog;

import java.util.List;

public class SavePageAndChildren {
    public static void save(String dir, String pageUrl, Catalog catalog, List<Option> options) {
        SavePage savePage = SavePage.save(dir, pageUrl, options);
        SaveChildren.save(savePage, catalog, options);
    }
}
