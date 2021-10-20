package copycat.infra;

import copycat.common.Url;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Image {
    private final static String[] TOKENS = new String[]{
            "![", "](", ")"
    };

    public final int start;

    public final int end;

    public final String str;

    public final String title;

    public final String originalUrl;

    public final String url;

    public final String ref;

    public final boolean attachInRef;

    public String file;

    public Image(String s, String ref) {
        int start = -1, idx = -1, end = -1;
        List<String> values = new ArrayList<>();
        for (int i = 0; i < TOKENS.length; i++) {
            String token = TOKENS[i];
            idx = s.indexOf(token, end);
            if (idx >= 0) {
                if (end > 0 && idx >= end) {
                    values.add(s.substring(end, idx));
                }
                end = idx + token.length();
                if (i == 0) {
                    start = idx;
                }
            }
        }
        // init properties:
        this.ref = Url.decode(ref);
        this.file = null;
        if (start >= 0 && idx >= 0 && end > start && values.size() == 2) {
            this.start = start;
            this.end = end;
            this.str = s.substring(this.start, this.end);
            this.title = values.get(0);
            this.originalUrl = Url.decode(values.get(1));
            this.url = _fillUrlSite(this.originalUrl, ref).trim();
            this.attachInRef = Url.sameSite(this.url, ref);
        } else {
            this.start = -1;
            this.end = -1;
            this.str = null;
            this.title = null;
            this.originalUrl = null;
            this.url = null;
            this.attachInRef = false;
        }
    }

    public String fileName() {
        String name = null;
        try {
            URL url = new URL(this.url);
            name = String.format("%s-%s",
                    url.getHost().replace(".", "-"),
                    url.getPath().replace("/", "-"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (name == null) {
            name = UUID.randomUUID().toString();
        }
        return "_res/" + name;
    }

    @Override
    public String toString() {
        return "Image{" +
                "str='" + str + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", file='" + file + '\'' +
                ", attachInRef='" + attachInRef + '\'' +
                '}';
    }

    private String _fillUrlSite(String url, String ref) {
        if (!url.startsWith("http") || url.startsWith("/")) {
            return Url.getSite(ref) + url;
        }
        return url;
    }

    public static void main(String[] args) {
        Image image = new Image("![](http://img.llc687.top/uPic/%E6%88%AA%E5%B1%8F2020-05-26%E4%B8%8A%E5%8D%889.55.06.png)", "https://pubimg.xingren.com");
        System.out.println(image);
        System.out.println(image.fileName());
    }
}
