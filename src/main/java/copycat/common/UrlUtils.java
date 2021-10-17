package copycat.common;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtils {
    /**
     * Get site base as protocol://domain
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
}
