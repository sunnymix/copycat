package copycat.infra;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

public class File {
    public static void main(String[] args) {
        new File("/tmp/copycat/doc", "test", Ext.MD).save("# Test");
    }

    public final String dir;

    public final String name;

    public final Ext ext;

    public enum Ext {
        HTML("html"),
        TXT("txt"),
        MD("md");

        public final String id;

        Ext(String id) {
            this.id = id;
        }
    }

    public File(String dir, String name, Ext ext) {
        this.dir = dir;
        this.name = name;
        this.ext = ext;
    }

    public void save(String content) {
        System.out.printf("Save file: %s%n", filePath());
        _resetFile();
        try (PrintWriter writer = new PrintWriter(filePath(), StandardCharsets.UTF_8)) {
            writer.println(content);
        } catch (Exception e) {
        }
    }

    public String dirPath() {
        String path = dir;
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    public String filePath() {
        return String.format("%s/%s.%s", dirPath(), name, ext.id);
    }

    private void _createDir() {
        F f = new F(dirPath());
        f.mkdirs();
    }

    private void _resetFile() {
        _createDir();
        F f = new F(filePath());
        f.createNewFile();
    }
}
