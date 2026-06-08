package company.evo.dawg;

import org.jspecify.annotations.Nullable;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class IntegerDAWG extends DAWG {
    public IntegerDAWG(InputStream stream) throws IOException {
        super(stream);
    }

    public @Nullable Integer get(String key) throws IOException {
        return get(key, null);
    }

    public @Nullable Integer get(String key, @Nullable Integer defaultValue) throws IOException {
        int res = dict.find(key.getBytes(StandardCharsets.UTF_8));
        if (res == Dict.MISSING) {
            return defaultValue;
        }
        return res;
    }
}
