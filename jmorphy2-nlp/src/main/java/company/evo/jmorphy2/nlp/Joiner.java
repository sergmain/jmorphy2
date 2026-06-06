package company.evo.jmorphy2.nlp;


// Minimal replacement for the subset of Guava's Joiner used by jmorphy2-nlp.
final class Joiner {
    private final String separator;

    private Joiner(String separator) {
        this.separator = separator;
    }

    static Joiner on(String separator) {
        return new Joiner(separator);
    }

    String join(Iterable<?> parts) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object part : parts) {
            if (!first) {
                sb.append(separator);
            }
            sb.append(part);
            first = false;
        }
        return sb.toString();
    }
}
