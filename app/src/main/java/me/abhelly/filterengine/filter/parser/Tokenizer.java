package me.abhelly.filterengine.filter.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.abhelly.filterengine.filter.parser.token.Token;

/**
 * Filter query tokenizer.
 *
 * Created by abhelly on 04.08.15.
 */
public class Tokenizer {

    private static final String LEFT_BRACKET = "\\(";

    private static final String RIGHT_BRACKET = "\\)";

    private static final String AND = "&";

    private static final String OR = "\\|";

    private static final String PRIORITY = "p[1-4]";

    // accepted format is: yyyy-MM-dd
    private static final String DATE
            = "((19|20)\\d\\d)-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])";

    // FIXME: 05.08.15 it doesn't match any [1-4] in text
    private static final String TEXT = "^[.[^"
            + LEFT_BRACKET + RIGHT_BRACKET + AND + OR + PRIORITY
            + "]]+";

    private String sentence;

    private ArrayList<PatternRule> rules;

    private Tokenizer(String sentence) {
        this.sentence = sentence;
        this.rules = new ArrayList<>();

        rules.add(new PatternRule(Token.TOKEN_LEFT_BRACKET, LEFT_BRACKET));
        rules.add(new PatternRule(Token.TOKEN_RIGHT_BRACKET, RIGHT_BRACKET));
        rules.add(new PatternRule(Token.TOKEN_AND, AND));
        rules.add(new PatternRule(Token.TOKEN_OR, OR));
        rules.add(new PatternRule(Token.TOKEN_VALUE_PRIORITY, PRIORITY));
        rules.add(new PatternRule(Token.TOKEN_VALUE_DATE, DATE));
        rules.add(new PatternRule(Token.TOKEN_VALUE_TEXT, TEXT));
    }

    public static LinkedList<Token> tokenize(String sentence) {
        return new Tokenizer(sentence).tokenize();
    }

    public LinkedList<Token> tokenize() {
        sentence = sentence.trim();
        LinkedList<Token> tokens = new LinkedList<>();
        int pos = 0;
        final int end = sentence.length();
        Matcher matcher = Pattern.compile("dummy").matcher(sentence);
        while (pos < end) {
            matcher.region(pos, end);
            lookingAt:
            {
                for (PatternRule rule : rules) {
                    if (matcher.usePattern(rule.pattern).lookingAt()) {
                        String string = sentence.substring(matcher.start(), matcher.end()).trim();
                        if (string.length() > 0) {
                            tokens.addFirst(Token.createToken(rule.tokenType, string));
                            pos = matcher.end();
                            break lookingAt;
                        }
                    }
                }
                pos++;
            }
        }
        return tokens;
    }

    static class PatternRule {

        private Pattern pattern;

        @Token.TokenType
        private int tokenType;

        PatternRule(@Token.TokenType int type, String regex) {
            tokenType = type;
            pattern = Pattern.compile(regex);
        }
    }
}
