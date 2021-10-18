package copycat.infra;

import copycat.cmd.option.Option;

import java.util.Arrays;
import java.util.List;

public class Tapd {
    private static final String[] PRE_TOKENS = new String[]{
            ""
    }

    public static List<String> getChildren(String folderId, List<Option> options, String html) {
        System.out.printf("[FOLDER CHILDREN]%n  folderId: %s%n", folderId);
        String[] preTokens = new String[]{
                "baseZNodesForLoadMoreNodes", "id:\"" + folderId + "\""
        };
        int idIdx = html.indexOf("baseZNodesForLoadMoreNodes");
        if (idIdx > 0) {
            idIdx = html.indexOf("id:\"" + folderId + "\"", idIdx);
        }
        if (idIdx > 0) {

        }

        return Arrays.asList("null");
    }

    public static String _findJsonObj(String html, int idx) {
        char startToken = '{', endToken = '}';
        int start = -1, end = -1;
        // find start
        int next = idx;
        while (next >= 0) {
            if (html.charAt(next) == startToken) {
                break;
            }
            next--;
        }
        if (next < 0) {

        }
        // find end
    }
}
