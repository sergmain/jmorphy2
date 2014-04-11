package net.uaprom.jmorphy2.nlp;

import java.lang.Math;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.CharMatcher;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableList;


public class Rules {
    private final List<Rule> rules = new ArrayList<Rule>();
    private final Map<Integer,List<Rule>> rulesBySize = new HashMap<Integer,List<Rule>>();
    private int maxRightSize;

    private Cache<String,List<Rule>> cache = null;
    
    private static final int DEFAULT_CACHE_SIZE = 10000;
    private static final Splitter rhsSplitter = Splitter.on("|").trimResults().omitEmptyStrings();

    public Rules() {
        this(DEFAULT_CACHE_SIZE);
    }

    public Rules(int cacheSize) {
        if (cacheSize > 0) {
            cache = CacheBuilder.newBuilder().maximumSize(cacheSize).build();
        }
    }
    
    public void add(String left, String right) {
        add(left, right, 1.0f);
    }

    public void add(String left, String right, float weight) {
        for (String rightPart : rhsSplitter.split(right)) {
            Rule r = new Rule(left, rightPart, weight);
            rules.add(r);
            List<Rule> bySize = rulesBySize.get(r.rightSize);
            if (bySize == null) {
                bySize = new ArrayList<Rule>();
                rulesBySize.put(r.rightSize, bySize);
            }
            bySize.add(r);
            if (r.rightSize > maxRightSize) {
                maxRightSize = r.rightSize;
            }
        }
    }

    public int getMaxRightSize() {
        return maxRightSize;
    }

    public Rule match(List<Node> nodes) {
        for (Rule rule : rules) {
            if (rule.match(nodes)) {
                return rule;
            }
        }
        return null;
    }

    public List<Rule> matchAll(List<Node> nodes) {
        String cacheKey =
            Joiner.on(" ").join(Lists.transform(nodes, Node.cacheKeyFunc()));
        List<Rule> matchedRules = cache.getIfPresent(cacheKey);
        // System.out.println(cacheKey);
        // System.out.println(matchedRules);
        if (matchedRules == null) {
            matchedRules = matchAllNC(nodes);
            cache.put(cacheKey, matchedRules);
        }
        return matchedRules;
        // return matchAllNC(nodes);
    }

    private List<Rule> matchAllNC(List<Node> nodes) {
        List<Rule> matchedRules = new ArrayList<Rule>();
        List<Rule> testRules = rulesBySize.get(nodes.size());
        if (testRules == null) {
            return matchedRules;
        }
        for (Rule rule : testRules) {
            if (rule.match(nodes)) {
                matchedRules.add(rule);
            }
        }
        return matchedRules;
    }

    @Override
    public String toString() {
        return Joiner.on("\n").join(rules);
    }

    public static class Rule {
        public final String leftStr;
        public final String rightStr;
        public final ImmutableSet<String> left;
        public final ImmutableList<NodeMatcher> right;
        public final int rightSize;
        public final float weight;

        private static Splitter grammemeSplitter = Splitter.on(",");
        private static Splitter rhsSplitter = Splitter.on(" ").trimResults();
        private static CharMatcher wordMatcher = CharMatcher.anyOf("'\"");

        public Rule(String left, String right, float weight) {
            this.leftStr = left;
            this.rightStr = right;
            this.left = parseLeft(left);
            this.right = parseRight(right);
            this.rightSize = this.right.size();
            this.weight = weight;
        }

        protected ImmutableSet<String> parseLeft(String left) {
            return ImmutableSet.copyOf(grammemeSplitter.split(left));
        }

        protected ImmutableList<NodeMatcher> parseRight(String right) {
            ImmutableList.Builder<NodeMatcher> listBuilder = ImmutableList.builder();
            for (String part : rhsSplitter.split(right)) {
                if ((part.startsWith("'") && part.endsWith("'")) ||
                    (part.startsWith("\"") && part.endsWith("\""))) {
                    listBuilder.add(new NodeMatcher(null, wordMatcher.trimFrom(part)));
                } else {
                    listBuilder.add(new NodeMatcher(ImmutableSet.copyOf(grammemeSplitter.split(part)), null));
                }
            }
            return listBuilder.build();
        }

        public boolean match(List<Node> nodes) {
            int n = right.size();
            if (nodes.size() < n) {
                return false;
            }

            for (int i = 0; i < n; i++) {
                if (!right.get(i).match(nodes.get(i))) {
                    return false;
                }
            }
            return true;
        }

        public Node apply(ImmutableList<Node> nodes) {
            ImmutableList<Node> reducedNodes = nodes.subList(0, rightSize);
            return new Node(left, reducedNodes, Node.calcScore(reducedNodes));
        }

        @Override
        public String toString() {
            return String.format("%s -> %s", leftStr, rightStr);
        }

        public static class NodeMatcher {
            private final ImmutableSet<String> grammemeValues;
            private final String word;

            public NodeMatcher(ImmutableSet<String> grammemeValues, String word) {
                this.grammemeValues = grammemeValues;
                this.word = word;
            }

            public boolean match(Node node) {
                return node.match(grammemeValues, word);
            }
        };
    };
}