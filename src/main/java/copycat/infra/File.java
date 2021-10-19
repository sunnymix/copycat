package copycat.infra;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class File {
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
        this.dir = dir.endsWith("/") ? dir : dir + "/";
        this.name = name;
        this.ext = ext;
    }

    public void save(String content) {
        System.out.printf("[%S]%n%s%n%n", ext.id.toUpperCase(Locale.ROOT), filePath());
        _resetFile();
        try (PrintWriter writer = new PrintWriter(filePath(), StandardCharsets.UTF_8)) {
            writer.println(content);
        } catch (Exception e) {
        }
    }

    public String filePath() {
        return String.format("%s%s.%s", dir, name, ext.id);
    }

    private void _createDir() {
        FileProxy f = new FileProxy(dir);
        f.mkdirs();
    }

    private void _resetFile() {
        _createDir();
        FileProxy f = new FileProxy(filePath());
        f.createNewFile();
    }

    private static class FileProxy {
        private final java.io.File file;

        public FileProxy(String path) {
            file = new java.io.File(path);
        }

        public void createNewFile() {
            if (!file.exists()) {
                try {
                    boolean ok = file.createNewFile();
                } catch (IOException e) {
                    System.out.println("Cannot create file");
                }
            }
        }

        public void mkdirs() {
            if (!file.exists()) {
                boolean ok = file.mkdirs();
            }
        }
    }

    public static void main(String[] args) {
        new File("/tmp/copycat/doc/", "test", Ext.MD).save("# Test");
    }
}
