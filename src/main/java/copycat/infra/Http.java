package copycat.infra;

import copycat.cmd.option.Option;
import copycat.cmd.option.Options;
import copycat.cmd.option.options.DirOption;
import copycat.cmd.option.options.HeaderOption;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Http {
    public static final String UA = "User-Agent";
    public static final String UA_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36";

    public static void getHtmlAndMd(List<Option> options, String url) throws RuntimeException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        _setRequestHeaders(get, options);
        CloseableHttpResponse res = null;
        try {
            res = client.execute(get);
            int statusCode = res.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(res.getEntity(), "UTF-8");
            System.out.println("Stats: " + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                _saveHtmlAndMd(options, body, url);
            }
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

    private static void _setRequestHeaders(HttpGet get, List<Option> options) {
        options.stream().filter(o -> o instanceof HeaderOption).forEach(o -> {
            String[] nameAndValue = ((HeaderOption) o).parseNameAndValue();
            if (nameAndValue.length == 2) {
                get.setHeader(nameAndValue[0], nameAndValue[1]);
            }
        });
    }

    private static void _saveHtmlAndMd(List<Option> options, String body, String url) {
        Option dirOption = Options.get(options, DirOption.NAME);
        String baseDir = dirOption != null ? dirOption.value() : "/tmp/copycat/doc/";
        // get dir, name:
        String name = Html.Title.fromHtml(body.substring(0, 1000)).trim();
        String dir = baseDir + name + "/";
        // save html file:
        new File(dir, name, File.Ext.HTML).save(body);
        // get md:
        String md = Md.fromHtml(body);
        // get images in md:
        List<Image> images = _getImagesInMd(md, url);
        // save images files:
        md = _downloadImages(dir, md, images, options);
        // replace images:
        md = _replaceImages(md, images);
        // save md file:
        new File(dir, "_index", File.Ext.MD).save(md);
    }

    private static List<Image> _getImagesInMd(String md, String ref) {
        List<Image> images = new ArrayList<>();
        int idx = 0;
        while (idx < md.length()) {
            Image image = new Image(md, ref);
            if (image.end > 0) {
                images.add(image);
                md = md.substring(image.end);
            } else {
                break;
            }
        }
        return images;
    }

    private static String _downloadImages(String dir, String md, List<Image> images, List<Option> options) {
        if (!images.isEmpty()) {
            for (Image image : images) {
                _downloadImage(dir, image, options);
            }
        }
        return md;
    }

    private static void _downloadImage(String dir, Image image, List<Option> options) {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet req = new HttpGet(image.url);
        req.setHeader(UA, UA_CHROME);
        if (image.attachInRef) {
            _setRequestHeaders(req, options);
        }
        req.setConfig(_reqConf());
        CloseableHttpResponse res = null;
        try {
            res = client.execute(req);
            int statusCode = res.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                InputStream in = entity.getContent();
                String filePath = dir + image.fileName();
                FileUtils.copyInputStreamToFile(in, new java.io.File(filePath));
                in.close();
                image.file = image.fileName();
            }
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

    private static String _replaceImages(String md, List<Image> images) {
        for (Image image : images) {
            md = md.replace(image.url, image.file);
        }
        return md;
    }

    private static RequestConfig _reqConf() {
        return RequestConfig.custom()
                .setSocketTimeout(60000).setConnectTimeout(60000)
                .build();
    }

    public static void main(String[] args) {
        String s = String.join("\n", new String[]{
                "![](https://pubimg.xingren.com/c64c6f4b-92ed-4efc-a9b8-1cc26d4a5157.png)",
                "![num1](https://pubimg.xingren.com/6bcb5fb9-12b4-40f9-a4cf-cd1ecf59e56a.png);",
                "![num2](https://pubimg.xingren.com/ab41f8cc-dbc0-4a9b-8b77-1ce597114db8.png);",
                "![num3](https://pubimg.xingren.com/4a67d3c2-fa3b-456f-9c52-b3af2d46bb2b.png);"
        });

        List<Image> images = _getImagesInMd(s, "https://pubimg.xingren.com");
        for (Image image : images) {
            System.out.println(image);
        }

        _downloadImage("/tmp/copycat/doc/", new Image("![](/c64c6f4b-92ed-4efc-a9b8-1cc26d4a5157.png)", "https://pubimg.xingren.com"), new ArrayList<>());
    }
}
