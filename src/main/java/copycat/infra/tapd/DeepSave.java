package copycat.infra.tapd;

import copycat.cmd.option.Option;
import copycat.infra.tapd.catalog.Catalog;
import copycat.infra.tapd.deepsave.SavePage;

import java.util.List;

public class DeepSave {
    public static void save(String dir, String pageUrl, String catalogUrl, List<Option> options) {
        Catalog catalog = Catalog.save(dir, catalogUrl, options);
        _savePageAndChildren(dir, pageUrl, catalog, options);
    }

    private static void _savePageAndChildren(String dir, String pageUrl, Catalog catalog, List<Option> options) {
        SavePage savePage = _savePage(dir, pageUrl, options);
        _saveChildren(savePage, catalog, options);
    }

    private static SavePage _savePage(String dir, String pageUrl, List<Option> options) {
        return SavePage.save(dir, pageUrl, options);
    }

    private static void _saveChildren(SavePage savePage, Catalog catalog, List<Option> options) {
        List<String> childrenId = catalog.getChildrenId(savePage.pageId);
        if (!childrenId.isEmpty()) {
            for (String childId : childrenId) {
                String childUrl = savePage.childUrl(childId);
                System.out.printf("[GET CHILD]%n" + "%s%n%n", childUrl);
                _savePageAndChildren(savePage.pageDir, childUrl, catalog, options);
            }
        }
    }
}
