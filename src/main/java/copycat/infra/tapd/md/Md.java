package copycat.infra.tapd.md;

import copycat.cmd.option.Option;
import copycat.common.Url;
import copycat.infra.Image;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Focus on Markdown content
 */
public class Md {
    private static final String[] PRE_TOKENS = new String[]{
            "wiki_dom = {\"dome\"", ":", "\""
    };

    private static final String TAIL_TOKEN = "\"";

    public final String text;

    public final List<Image> images;

    public static Md fromHtml(String html, String pageUrl, List<Option> options) {
        // ========== Properties ==========
        String text = null;
        text = _getOriginal(html);
        text = _decode(text);
        List<Image> images = _seekImages(text, pageUrl);
        // ========== Object ==========
        Md md = new Md(text, images);
        // ========== Actions ==========
        return md;
    }

    public static Md replaceImagesToLocalFile(Md fromMd) {
        // ========== Properties ==========
        String text = fromMd.text;
        if (fromMd.images != null && !fromMd.images.isEmpty()) {
            for (Image image : fromMd.images) {
                if (image.file != null && !image.file.isBlank()) {
                    text = text.replace(image.originalUrl, image.file);
                }
            }
        }
        // ========== Object ==========
        Md md = new Md(text, fromMd.images);
        // ========== Actions ==========
        return md;
    }

    private Md() {
        this.text = null;
        this.images = null;
    }

    private Md(String text, List<Image> images) {
        this.text = text;
        this.images = images;
    }

    private static String _getOriginal(String html) {
        String res = "";
        int start = _getStartFrom(html);
        if (start > 0) {
            int end = html.indexOf(TAIL_TOKEN, start);
            if (end > start) {
                res = html.substring(start, end);
            }
        }
        return res;
    }

    private static int _getStartFrom(String html) {
        int start, from = -1;
        for (String token : PRE_TOKENS) {
            start = html.indexOf(token, from);
            if (start > 0) {
                from = start + token.length();
            } else {
                break;
            }
        }
        return from;
    }

    private static String _decode(String text) {
        text = _decodeEOL(text);
        text = _decodeUnicode(text);
        text = Url.decode(text);
        return text;
    }

    private static String _decodeEOL(String text) {
        return text.replace("\\r\\n", "\n");
    }

    private static String _decodeUnicode(String text) {
        return StringEscapeUtils.unescapeJava(text);
    }

    private static List<Image> _seekImages(String text, String pageUrl) {
        List<Image> images = new ArrayList<>();
        int idx = 0;
        while (idx < text.length()) {
            Image image = new Image(text, pageUrl);
            if (image.end > 0) {
                images.add(image);
                text = text.substring(image.end);
            } else {
                break;
            }
        }
        return images;
    }
}
