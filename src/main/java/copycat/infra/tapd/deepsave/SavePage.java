package copycat.infra.tapd.deepsave;

import copycat.cmd.option.Option;
import copycat.common.Url;
import copycat.infra.File;
import copycat.infra.Html;
import copycat.infra.Http;
import copycat.infra.tapd.md.Md;

import java.util.List;

public class SavePage {
    static final String INDEX = "_index";
    static final String RES = "_res";

    public final String dir, pageUrl, pageId, html, title, pageDir, imageDir;

    private Md md;

    private final List<Option> options;

    public static SavePage save(String dir, String pageUrl, List<Option> options) {
        // ========== Properties ==========
        String html = Http.get(pageUrl, options);
        String title = Html.Title.fromHtml(html).trim();
        String pageId = Url.lastParam(pageUrl);
        String pageDir = dir + title + "/";
        String imageDir = pageDir;
        // ========== Object ==========
        SavePage savePage = new SavePage(dir, pageUrl, pageId, html, title, pageDir, imageDir, options);
        // ========== Actions ==========
        savePage._saveHtml();
        savePage._saveMd();
        return savePage;
    }

    private SavePage() {
        dir = pageUrl = pageId = html = title = pageDir = imageDir = null;
        md = null;
        options = null;
    }

    private SavePage(String dir, String pageUrl, String pageId, String html, String title, String pageDir, String imageDir, List<Option> options) {
        this.dir = dir;
        this.pageUrl = pageUrl;
        this.pageId = pageId;
        this.html = html;
        this.title = title;
        this.pageDir = pageDir;
        this.imageDir = imageDir;
        this.options = options;
    }

    public String childUrl(String childId) {
        return pageUrl.replace(pageId, childId);
    }

    private void _saveHtml() {
        new File(pageDir, INDEX, File.Ext.HTML).save(html);
    }

    private void _saveMd() {
        md = Md.fromHtml(html, pageUrl, options);
        _saveImages();
        md = Md.replaceImagesToLocalFile(md);
        new File(pageDir, INDEX, File.Ext.MD).save(md.text);
    }

    private void _saveImages() {
        Http.saveImages(imageDir, md.images, options);
    }
}
