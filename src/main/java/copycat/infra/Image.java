package copycat.infra;

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

    public final String url;

    public Image(String s) {
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
        if (start >= 0 && idx >= 0 && end > start && values.size() == 2) {
            this.start = start;
            this.end = end;
            this.str = s.substring(this.start, this.end);
            this.title = values.get(0);
            this.url = values.get(1);
        } else {
            this.start = -1;
            this.end = -1;
            this.str = null;
            this.title = null;
            this.url = null;
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
        return name;
    }

    @Override
    public String toString() {
        return "Image{" +
                "str='" + str + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    public static void main(String[] args) {
        Image image = new Image("![](https://pubimg.xingren.com/c64c6f4b-92ed-4efc-a9b8-1cc26d4a5157.png)");
        System.out.println(image);
        System.out.println(image.fileName());
    }
}
