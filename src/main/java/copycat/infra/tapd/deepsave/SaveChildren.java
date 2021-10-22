package copycat.infra.tapd.deepsave;

import copycat.cmd.option.Option;
import copycat.infra.tapd.catalog.Catalog;

import java.util.List;

public class SaveChildren {
    public static void save(SavePage savePage, Catalog catalog, List<Option> options) {
        List<String> childrenId = catalog.getChildrenId(savePage.pageId);
        if (childrenId.isEmpty()) {
            return;
        }
        for (int i = 1; i <= childrenId.size(); i++) {
            String childId = childrenId.get(i - 1);
            String childUrl = savePage.childUrl(childId);
            System.out.printf("[GET CHILD]%n" + "%s%n%n", childUrl);
            String prefix = i < 10 ? "0" : "";
            String number = prefix + i + " ";
            SavePageAndChildren.save(savePage.pageDir + number, childUrl, catalog, options);
        }
    }
}
