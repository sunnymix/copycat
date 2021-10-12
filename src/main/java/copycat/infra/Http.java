package copycat.infra;

import copycat.cmd.option.Option;
import copycat.cmd.option.options.HeaderOption;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class Http {
    public static void get(List<Option> options, String url) throws RuntimeException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        _setHeaders(get, options);
        CloseableHttpResponse res = null;
        try {
            res = client.execute(get);
            System.out.println(res.getStatusLine().getStatusCode());
            String body = EntityUtils.toString(res.getEntity(), "UTF-8");
            System.out.println(body);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (res != null) res.close();
                client.close();
            } catch (IOException e) {
            }
        }
    }

    private static void _setHeaders(HttpGet get, List<Option> options) {
        options.stream().filter(o -> o instanceof HeaderOption).forEach(o -> {
            String[] nameAndValue = ((HeaderOption) o).parseNameAndValue();
            if (nameAndValue.length == 2) {
                get.setHeader(nameAndValue[0], nameAndValue[1]);
            }
        });
    }
}