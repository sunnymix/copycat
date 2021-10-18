package copycat.catcher;

import copycat.cmd.option.Option;
import copycat.infra.Http;

import java.util.List;

public class HttpCatcher implements Catcher {
    public static void get(List<Option> options, List<String> urls) {
        for (String url : urls) {
            get(options, url);
        }
    }

    public static void get(List<Option> options, String url) {
        Http.getHtmlAndMd(options, url);
    }
}
