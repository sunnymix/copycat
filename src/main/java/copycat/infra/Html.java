package copycat.infra;

public class Html {
    public static final String HEAD_TAG = "<head>";
    public static final String TITLE_TAG = "<title>";
    public static final String TITLE_TAG_END = "</title>";

    public static class Title {
        private static final String[] TITLE_PRE_REMOVE = new String[]{
                "编辑wiki-文档_"
        };

        private static final String[] TITLE_TAIL_REMOVE = new String[]{
                "-TAPD平台"
        };

        public static String fromHtml(String html) {
            String title = "";
            int headTag = html.indexOf(HEAD_TAG);
            int titleTag = html.indexOf(TITLE_TAG, headTag);
            if (titleTag > 0) {
                int titleTagEnd = html.indexOf(TITLE_TAG_END, titleTag);
                if (titleTagEnd > headTag) {
                    title = html.substring(titleTag + TITLE_TAG.length(), titleTagEnd);
                }
            }
            title = _removeTitlePre(TITLE_PRE_REMOVE, title, true);
            title = _removeTitlePre(TITLE_TAIL_REMOVE, title, false);
            return title.trim();
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
