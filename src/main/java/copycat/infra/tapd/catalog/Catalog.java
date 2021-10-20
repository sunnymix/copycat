package copycat.infra.tapd.catalog;

import copycat.cmd.option.Option;
import copycat.infra.File;
import copycat.infra.Http;
import copycat.infra.Tapd;

import java.util.List;

public class Catalog {
    static final String CATALOG = "_catalog";

    final String dir, catalogUrl, html;

    public static Catalog save(String dir, String catalogUrl, List<Option> options) {
        // ========== Properties ==========
        String html = Http.get(catalogUrl, options);
        // ========== Object ==========
        Catalog catalog = new Catalog(dir, catalogUrl, html);
        // ========== Actions ==========
        catalog._saveHtml();
        return catalog;
    }

    private Catalog(String dir, String catalogUrl, String html) {
        this.dir = dir;
        this.catalogUrl = catalogUrl;
        this.html = html;
    }

    private Catalog() {
        dir = catalogUrl = html = null;
    }

    public List<String> getChildrenId(String parentId) {
        return Tapd.getChildrenId(parentId, html);
    }

    private void _saveHtml() {
        new File(dir, CATALOG, File.Ext.HTML).save(html);
    }
}
