package company.evo.jmorphy2;

import java.io.IOException;
import java.io.InputStream;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;


public class JSONUtils {
    // Integral numbers are read as Long (not Integer) to preserve the behaviour
    // of the previous noggit-based parser, which the dictionary metadata relies on.
    private static final JsonMapper MAPPER = JsonMapper.builder()
        .enable(DeserializationFeature.USE_LONG_FOR_INTS)
        .build();

    public static Object parseJSON(InputStream stream) throws IOException {
        return MAPPER.readValue(stream, Object.class);
    }
}
