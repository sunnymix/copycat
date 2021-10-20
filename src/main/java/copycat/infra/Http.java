package copycat.infra;

import copycat.cmd.option.Option;
import copycat.cmd.option.Options;
import copycat.cmd.option.options.HeaderOption;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
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

    private static final String INDEX = "_index";
    private static final String FOLDER = "_folder";

    public static void save(List<Option> options, String url) {
        String folder = Options.getCatalog(options);
        if (folder != null) {
            _saveFolder(folder, options, url);
        } else {
            _saveHtmlAndMd(options, url);
        }
    }

    private static void _saveFolder(String folderUrl, List<Option> options, String url) {
        System.out.printf("[FOLDER]%n" + "%s%n%n", folderUrl);
        String indexHtml = _getHtml(options, url);

        String title = _getTitle(indexHtml);
        String dir = _getDir(options, title);
        _saveHtml(dir, INDEX, indexHtml);
        _saveMd(dir, INDEX, indexHtml, url, options);

        String folderHtml = _getHtml(options, folderUrl);
        _saveHtml(dir, FOLDER, folderHtml);

        String folderId = _getId(url);
        if (StringUtils.isBlank(folderId)) {
            System.out.printf("[ERROR]%n" + "folder id is blank! url: %s%n%n", url);
        } else {
            List<String> childrenId = Tapd.getChildrenId(folderId, folderHtml);
            if (!childrenId.isEmpty()) {
                for (String childId : childrenId) {
                    String childUrl = url.replace(folderId, childId);
                    System.out.printf("[GET CHILD]%n" + "%s%n%n", childUrl);
                    _saveChildHtmlAndMd(options, childUrl, dir);
                }
            }
        }
    }

    private static void _saveChildHtmlAndMd(List<Option> options, String url, String dir) throws RuntimeException {
        String html = _getHtml(options, url);
        String title = _getTitle(html);
        dir = dir + title + "/";
        _saveHtml(dir, INDEX, html);
        _saveMd(dir, INDEX, html, url, options);
    }

    private static void _saveHtmlAndMd(List<Option> options, String url) throws RuntimeException {
        String html = _getHtml(options, url);
        String title = _getTitle(html);
        String dir = _getDir(options, title);
        _saveHtml(dir, INDEX, html);
        _saveMd(dir, INDEX, html, url, options);
    }

    public static String get(String url, List<Option> options) {
        return _getHtml(options, url);
    }

    private static String _getHtml(List<Option> options, String url) {
        String html = null;
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet req = new HttpGet(url);
        _setRequestHeaders(req, options);
        CloseableHttpResponse res = null;
        try {
            System.out.printf("[HTTP GET]%n%s%n%n", url);
            res = client.execute(req);
            int statusCode = res.getStatusLine().getStatusCode();
            String body = EntityUtils.toString(res.getEntity(), "UTF-8");
            if (statusCode == HttpStatus.SC_OK) {
                html = body;
            } else {
                System.out.printf("[STATUS]%n" + "%s%n" + "body:%n" + "%s%n%n", statusCode, body);
            }
        } catch (Throwable e) {
            System.out.printf("[ERROR]%n" + "Cannot get: %s%n%n!", url);
            e.printStackTrace();
        } finally {
            try {
                if (res != null) {
                    res.close();
                }
                client.close();
            } catch (IOException e) {
                System.out.printf("[ERROR]%n" + "Client or Response cannot close!%n%n");
                e.printStackTrace();
            }
        }
        return html;
    }

    private static void _setRequestHeaders(HttpGet req, List<Option> options) {
        options.stream().filter(o -> o instanceof HeaderOption).forEach(o -> {
            String[] nameAndValue = ((HeaderOption) o).parseNameAndValue();
            if (nameAndValue.length == 2) {
                req.setHeader(nameAndValue[0], nameAndValue[1]);
            }
        });
        req.setHeader(UA, UA_CHROME);
    }

    private static void _saveHtml(String dir, String fileName, String html) {
        new File(dir, fileName, File.Ext.HTML).save(html);
    }

    private static void _saveMd(String dir, String fileName, String html, String refUrl, List<Option> options) {
        String md = Md.fromHtml(html);
        List<Image> images = _getImagesInMd(md, refUrl);
        _downloadImages(dir, images, options);
        md = _replaceImages(md, images);
        new File(dir, fileName, File.Ext.MD).save(md);
    }

    private static String _getTitle(String html) {
        return Html.Title.fromHtml(html).trim();
    }

    private static String _getId(String url) {
        if (StringUtils.isNotBlank(url)) {
            int idx = url.lastIndexOf("/");
            if (idx >= 0) {
                return url.substring(idx + 1);
            }
        }
        return null;
    }

    private static String _getDir(List<Option> options, String title) {
        String dir = Options.getDir(options);
        String baseDir = dir != null ? dir : "/tmp/copycat/doc/";
        return baseDir + title + "/";
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

    public static void saveImages(String dir, List<Image> images, List<Option> options) {
        _downloadImages(dir, images, options);
    }

    private static void _downloadImages(String dir, List<Image> images, List<Option> options) {
        if (!images.isEmpty()) {
            for (Image image : images) {
                _downloadImage(dir, image, options);
            }
        }
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
        } catch (Throwable e) {
            System.out.printf("[ERROR]%n" + "Cannot download image: %s!%n%n", image.url);
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
            if (image.file != null && !image.file.isBlank()) {
                md = md.replace(image.originalUrl, image.file);
            }
        }
        return md;
    }

    private static RequestConfig _reqConf() {
        return RequestConfig.custom()
                .setSocketTimeout(60000).setConnectTimeout(60000)
                .build();
    }

    public static void main(String[] args) {
        /*String s = String.join("\n", new String[]{
                "![](https://pubimg.xingren.com/c64c6f4b-92ed-4efc-a9b8-1cc26d4a5157.png)",
                "![num1](https://pubimg.xingren.com/6bcb5fb9-12b4-40f9-a4cf-cd1ecf59e56a.png);",
                "![num2](https://pubimg.xingren.com/ab41f8cc-dbc0-4a9b-8b77-1ce597114db8.png);",
                "![num3](https://pubimg.xingren.com/4a67d3c2-fa3b-456f-9c52-b3af2d46bb2b.png);"
        });

        List<Image> images = _getImagesInMd(s, "https://pubimg.xingren.com");
        for (Image image : images) {
            System.out.println(image);
        }*/
        _downloadImage("/tmp/copycat/doc/", new Image("![](http://img.llc687.top/uPic/截屏2020-05-31下午2.53.34.png)", "https://pubimg.xingren.com"), new ArrayList<>());
    }
}
