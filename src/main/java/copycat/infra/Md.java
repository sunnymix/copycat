package copycat.infra;

import org.apache.commons.lang3.StringEscapeUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Md {
    private static final String[] PRE_TOKENS = new String[]{
            "wiki_dom = {\"dome\"",
            ":",
            "\""
    };

    private static final String TAIL_TOKEN = "\"";

    public static String fromHtml(String html) {
        String original = _getOriginal(html);
        String decode = _decode(original);
        return decode;
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

    private static String _decode(String s) {
        s = _decodeEOL(s);
        s = _decodeUnicode(s);
        s = _decodeUrl(s);
        return s;
    }

    private static String _decodeEOL(String s) {
        return s.replace("\\r\\n", "\n");
    }

    private static String _decodeUnicode(String s) {
        return StringEscapeUtils.unescapeJava(s);
    }

    private static String _decodeUrl(String s) {
        try {
            s = URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
        }
        return s;
    }
}
