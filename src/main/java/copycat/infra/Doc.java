package copycat.infra;

public class Doc {
    public static void main(String[] args) {
        new Doc("/tmp/copycat/doc", "test", Ext.MD).save("# Test");
    }

    private final String dir;

    private final String name;

    private final Ext ext;

    public Doc(String dir, String name, Ext ext) {
        this.dir = dir;
        this.name = name;
        this.ext = ext;
    }

    public void save(String content) {
        System.out.printf("Save file: %s%n", filePath());
    }

    public String filePath() {
        return String.format("%s/%s.%s", dir, name, ext.id);
    }

    public enum Ext {
        MD("md");

        private final String id;

        Ext(String id) {
            this.id = id;
        }

        public String getText() {
            return id;
        }
    }
}
