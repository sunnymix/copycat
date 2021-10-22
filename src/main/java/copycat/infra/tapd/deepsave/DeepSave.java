package copycat.infra.tapd.deepsave;

import copycat.cmd.option.Option;
import copycat.infra.tapd.catalog.Catalog;

import java.util.List;

public class DeepSave {
    public static void save(String dir, String pageUrl, String catalogUrl, List<Option> options) {
        Catalog catalog = Catalog.of(dir, catalogUrl, options);
        SavePageAndChildren.save(dir, pageUrl, catalog, options);
    }
}
