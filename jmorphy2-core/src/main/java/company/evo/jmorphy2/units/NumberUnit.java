package company.evo.jmorphy2.units;

import java.util.ArrayList;
import java.util.List;


import company.evo.jmorphy2.ParsedWord;
import company.evo.jmorphy2.Tag;


public class NumberUnit extends AnalyzerUnit {
    private NumberUnit(Tag.Storage tagStorage, boolean terminate, float score) {
        super(tagStorage, terminate, score);
    }

    public static class Builder extends AnalyzerUnit.Builder {
        public Builder(boolean terminate, float score) {
            super(terminate, score);
        }

        @Override
        protected AnalyzerUnit newAnalyzerUnit(Tag.Storage tagStorage) {
            tagStorage.newGrammeme(new ArrayList<>(List.of("NUMB", "", "ЧИСЛО", "число")));
            tagStorage.newGrammeme(new ArrayList<>(List.of("intg", "", "цел", "целое")));
            tagStorage.newGrammeme(new ArrayList<>(List.of("real", "", "вещ", "вещественное")));
            tagStorage.newTag("NUMB,intg");
            tagStorage.newTag("NUMB,real");
            return new NumberUnit(tagStorage, terminate, score);
        }
    }

    @Override
    public List<ParsedWord> parse(String word, String wordLower) {
        Tag tag = null;
        if (isInteger(word)) {
            tag = tagStorage.getTag("NUMB,intg");
        }
        else if (isFloat(word)) {
            tag = tagStorage.getTag("NUMB,real");
        }

        if (tag != null) {
            List<ParsedWord> parseds = new ArrayList<>();
            parseds.add(new AnalyzerParsedWord(word, tag, word, word, score));
            return parseds;
        }
        return null;
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
