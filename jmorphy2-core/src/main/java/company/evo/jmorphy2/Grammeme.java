package company.evo.jmorphy2;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;


public class Grammeme {
    public final String key;
    public final String value;
    public final @Nullable String parentValue;
    public final String russianValue;
    public final String description;

    private final Tag.Storage storage;

    public Grammeme(List<String> grammemeInfo, Tag.Storage storage) {
        this(grammemeInfo.get(0),
             grammemeInfo.get(1),
             grammemeInfo.get(2),
             grammemeInfo.get(3),
             storage);
    }

    public Grammeme(String value,
                    String parentValue,
                    String russianValue,
                    String description,
                    Tag.Storage storage)
    {
        this.key = Tag.Storage.normalizeGrammemeValue(value);
        this.value = value;
        this.parentValue = stringOrNull(parentValue);
        this.russianValue = russianValue;
        this.description = description;
        this.storage = storage;
    }

    private static @Nullable String stringOrNull(@Nullable String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        return s;
    }

    public @Nullable Grammeme getParent() {
        return storage.getGrammeme(parentValue);
    }

    public @Nullable Grammeme getRoot() {
        Grammeme grammeme = this;
        Grammeme parentGrammeme = grammeme.getParent();
        if (parentGrammeme == null) {
            return null;
        }
        while (parentGrammeme != null) {
            grammeme = parentGrammeme;
            parentGrammeme = grammeme.getParent();
        }
        return grammeme;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Grammeme) {
            Grammeme other = (Grammeme) obj;
            return key.equals(other.key) &&
                    storage == other.storage;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return value;
    }

    public String info() {
        return String.format("<%s, %s, %s, %s>", value, parentValue, russianValue, description);
    }
}
