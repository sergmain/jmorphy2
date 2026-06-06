package company.evo.jmorphy2.nlp;


// Minimal replacement for the subset of Guava's CharMatcher used by jmorphy2-nlp.
final class CharMatcher {
    private final String chars;

    private CharMatcher(String chars) {
        this.chars = chars;
    }

    static CharMatcher anyOf(String chars) {
        return new CharMatcher(chars);
    }

    private boolean matches(char c) {
        return chars.indexOf(c) >= 0;
    }

    String trimFrom(String s) {
        int start = 0;
        int end = s.length();
        while (start < end && matches(s.charAt(start))) {
            start++;
        }
        while (end > start && matches(s.charAt(end - 1))) {
            end--;
        }
        return s.substring(start, end);
    }
}
