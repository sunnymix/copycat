package copycat.infra;

import java.io.IOException;

public class F {
    private final java.io.File f;

    public F(String path) {
        f = new java.io.File(path);
    }

    public void createNewFile() {
        if (!f.exists()) {
            try {
                boolean ok = f.createNewFile();
            } catch (IOException e) {
                System.out.println("Cannot create file");
            }
        }
    }

    public void mkdirs() {
        if (!f.exists()) {
            boolean ok = f.mkdirs();
        }
    }
}
