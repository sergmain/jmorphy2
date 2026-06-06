package company.evo.jmorphy2.nlp;

import java.util.ArrayList;
import java.util.List;


// Minimal replacement for the subset of Guava's Splitter used by jmorphy2-nlp.
// Splits on a literal separator (not a regex) and, like Guava, keeps trailing
// empty strings unless omitEmptyStrings() is requested.
final class Splitter {
    private final String separator;
    private final int limit;
    private final boolean trim;
    private final CharMatcher trimMatcher;
    private final boolean omitEmpty;

    private Splitter(String separator, int limit, boolean trim, CharMatcher trimMatcher, boolean omitEmpty) {
        this.separator = separator;
        this.limit = limit;
        this.trim = trim;
        this.trimMatcher = trimMatcher;
        this.omitEmpty = omitEmpty;
    }

    static Splitter on(String separator) {
        return new Splitter(separator, 0, false, null, false);
    }

    Splitter limit(int maxItems) {
        return new Splitter(separator, maxItems, trim, trimMatcher, omitEmpty);
    }

    Splitter trimResults() {
        return new Splitter(separator, limit, true, null, omitEmpty);
    }

    Splitter trimResults(CharMatcher matcher) {
        return new Splitter(separator, limit, true, matcher, omitEmpty);
    }

    Splitter omitEmptyStrings() {
        return new Splitter(separator, limit, trim, trimMatcher, true);
    }

    List<String> split(String input) {
        List<String> raw = new ArrayList<>();
        int pos = 0;
        while (true) {
            if (limit > 0 && raw.size() == limit - 1) {
                raw.add(input.substring(pos));
                break;
            }
            int idx = input.indexOf(separator, pos);
            if (idx < 0) {
                raw.add(input.substring(pos));
                break;
            }
            raw.add(input.substring(pos, idx));
            pos = idx + separator.length();
        }
        List<String> result = new ArrayList<>(raw.size());
        for (String piece : raw) {
            if (trim) {
                piece = (trimMatcher == null) ? piece.strip() : trimMatcher.trimFrom(piece);
            }
            if (omitEmpty && piece.isEmpty()) {
                continue;
            }
            result.add(piece);
        }
        return result;
    }
}
