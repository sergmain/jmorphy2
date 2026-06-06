package company.evo.jmorphy2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@RunWith(JUnit4.class)
public class JSONUtilsTest {
    private static Object parse(String json) throws IOException {
        InputStream stream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        return JSONUtils.parseJSON(stream);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testScalarTypes() throws IOException {
        Map<String,Object> obj = (Map<String,Object>) parse(
            "{\"i\": 5, \"big\": 10000000000, \"d\": 1.5, \"s\": \"text\", \"b\": true, \"n\": null}");

        // Integral numbers must come back as Long (not Integer) so that the
        // dictionary metadata casts like (long) map.get(...) keep working.
        assertTrue(obj.get("i") instanceof Long);
        assertEquals(5L, obj.get("i"));
        assertEquals(10000000000L, obj.get("big"));
        long unboxed = (long) obj.get("i");
        assertEquals(5L, unboxed);

        assertTrue(obj.get("d") instanceof Double);
        assertEquals(1.5, (Double) obj.get("d"), 0.0);

        assertEquals("text", obj.get("s"));
        assertEquals(Boolean.TRUE, obj.get("b"));
        assertNull(obj.get("n"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testContainerTypes() throws IOException {
        Object arr = parse("[1, 2, 3]");
        assertTrue(arr instanceof List);
        List<Object> list = (List<Object>) arr;
        assertEquals(3, list.size());
        assertEquals(1L, list.get(0));

        Object obj = parse("{\"k\": \"v\"}");
        assertTrue(obj instanceof Map);
        assertEquals("v", ((Map<String,Object>) obj).get("k"));
    }
}
