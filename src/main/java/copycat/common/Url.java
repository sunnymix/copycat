package copycat.common;

import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Url {
    /**
     * Get site base as protocol://domain
     *
     * @param u original url
     * @return site of the url
     */
    public static String getSite(String u) {
        String site = null;
        try {
            URL url = new URL(u);
            site = String.format("%s://%s", url.getProtocol(), url.getHost());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return site;
    }

    public static boolean sameSite(String u1, String u2) {
        return getSite(u1).equals(getSite(u2));
    }

    public static String decode(String s) {
        try {
            if (s != null && !s.isBlank()) {
                s = URLDecoder.decode(s, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
        }
        return s;
    }

    public static String lastParam(String url) {
        String param = null;
        if (StringUtils.isNotBlank(url)) {
            int idx = url.lastIndexOf("/");
            if (idx >= 0) {
                param = url.substring(idx + 1);
            }
        }
        return param;
    }
}
