package company.evo.jmorphy2;

import org.jspecify.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;


public class ResourceFileLoader extends FileLoader {
    private final String basePath;

    public ResourceFileLoader(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public InputStream newStream(String filename) {
        InputStream is = getClass().getResourceAsStream(basePath + "/" + filename);
        if (is==null) {
            throw new IllegalStateException("Resource not found: " + filename);
        }
        return is;
    }
}
