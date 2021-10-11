package copycat.cat;

import copycat.cmd.option.Option;

import java.util.List;

public class HttpCat implements Cat {
    public static void get(List<Option> options, List<String> urls) {
        for (String url : urls) {
            System.out.println("\n\n[HTT_CAT] Get: " + url + "\n\n");
        }
    }
}
