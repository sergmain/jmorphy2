package company.evo.jmorphy2;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Regression guard for the NPE introduced by the Guava-removal / Jackson-3 migration
 * (commits 62a5148 / 0507490) and fixed in d089aa8.
 * <br/>
 * When {@code Tag.grammemes} was backed by an immutable {@code Set.copyOf(...)}, the JDK
 * immutable-set implementations reject {@code null} arguments to {@code contains}
 * ({@code Objects.requireNonNull}), so any query method receiving a {@code null} grammeme
 * (e.g. {@code ParsedWord.inflect} forwarding a {@code storage.getGrammeme(unknown)} == null
 * into {@code containsAll}) threw NullPointerException instead of returning a boolean.
 * <br/>
 * Guava's ImmutableSet tolerated {@code contains(null)} (returned false); a HashSet-backed
 * {@code Collections.unmodifiableSet} restores that tolerance. These tests pin the
 * null-tolerant contract for the query methods so the immutable-set form cannot silently
 * creep back in.
 */
@RunWith(JUnit4.class)
public class TagNullToleranceTest {

    private static Tag.Storage storageWith(String... grammemeValues) {
        Tag.Storage storage = new Tag.Storage();
        for (String v : grammemeValues) {
            // value, parentValue, russianValue, description
            storage.newGrammeme(List.of(v, "", v, v));
        }
        return storage;
    }

    @Test
    public void containsAll_argumentContainingNullGrammeme_returnsFalseAndDoesNotThrow() {
        Tag.Storage storage = storageWith("NOUN");
        Tag tag = storage.newTag("NOUN");
        Grammeme noun = storage.getGrammeme("NOUN");
        Grammeme unknown = storage.getGrammeme("UNKN"); // not registered -> null, mirrors Morph

        List<Grammeme> required = Arrays.asList(noun, unknown);
        // Pre-fix Set.copyOf form threw NPE here; null-tolerant form returns false.
        assertFalse(tag.containsAll(required));
    }

    @Test
    public void contains_nullGrammeme_returnsFalseAndDoesNotThrow() {
        Tag.Storage storage = storageWith("NOUN");
        Tag tag = storage.newTag("NOUN");

        assertFalse(tag.contains((Grammeme) null));
    }

    @Test
    public void containsAny_argumentContainingNullGrammeme_returnsFalseAndDoesNotThrow() {
        Tag.Storage storage = storageWith("NOUN", "VERB");
        Tag tag = storage.newTag("NOUN");
        Grammeme verb = storage.getGrammeme("VERB");      // registered but NOT on this tag
        Grammeme unknown = storage.getGrammeme("UNKN");   // null

        assertFalse(tag.containsAny(Arrays.asList(verb, unknown)));
    }

    @Test
    public void containsAll_subsetOfActualGrammemes_stillReturnsTrue() {
        // Sanity: the null-tolerance fix must not break the positive path.
        Tag.Storage storage = storageWith("NOUN");
        Tag tag = storage.newTag("NOUN");
        Grammeme noun = storage.getGrammeme("NOUN");

        assertTrue(tag.containsAll(List.of(noun)));
    }
}
