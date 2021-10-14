package copycat.infra;

public class Html {
    public static class Title {
        private static final String[] PRE_TOKENS = new String[]{
                "id=\"wikiName\"",
                "title=\""
        };

        private static final String TAIL_TOKEN = "\"";

        private static final String[] TITLE_PRE_REMOVE = new String[]{};

        private static final String[] TITLE_TAIL_REMOVE = new String[]{};

        public static String fromHtml(String html) {
            String title = "";
            int start = _getStart(html), end = -1;
            if (start >= 0) {
                end = html.indexOf(TAIL_TOKEN, start);
            }
            if (end > start) {
                title = html.substring(start, end);
            }
            title = _removeTitlePre(TITLE_PRE_REMOVE, title, true);
            title = _removeTitlePre(TITLE_TAIL_REMOVE, title, false);
            return title.trim();
        }

        private static int _getStart(String s) {
            int idx, from = -1;
            for (String token : PRE_TOKENS) {
                idx = s.indexOf(token, from);
                if (idx >= 0) {
                    from = idx + token.length();
                } else {
                    break;
                }
            }
            return from;
        }

        private static String _removeTitlePre(String[] filters, String title, boolean preOrTail) {
            title = title.trim();
            if (title.isBlank()) {
                return title;
            }
            for (String filter : filters) {
                if (preOrTail) {
                    if (title.startsWith(filter)) {
                        return title.substring(filter.length());
                    }
                } else {
                    if (title.endsWith(filter)) {
                        return title.substring(0, title.lastIndexOf(filter));
                    }
                }
            }
            return title.trim();
        }
    }
}
