package me.abhelly.filterengine.filter.parser.token;

import android.content.Context;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Base query token.
 *
 * Created by abhelly on 04.08.15.
 */
public abstract class Token {

    public static final int TOKEN_LEFT_BRACKET = 0;

    public static final int TOKEN_RIGHT_BRACKET = 1;

    public static final int TOKEN_AND = 2;

    public static final int TOKEN_OR = 3;

    public static final int TOKEN_VALUE_DATE = 4;

    public static final int TOKEN_VALUE_PRIORITY = 5;

    public static final int TOKEN_VALUE_TEXT = 6;

    @TokenType
    final int mType;

    final String mSequence;

    Token(@TokenType int type, String sequence) {
        mType = type;
        mSequence = sequence;
    }

    public static Token createToken(@TokenType int type, String sequence)
            throws IllegalArgumentException {
        switch (type) {
            case TOKEN_LEFT_BRACKET:
            case TOKEN_RIGHT_BRACKET:
            case TOKEN_AND:
            case TOKEN_OR:
                return new OperatorToken(type, sequence);
            case TOKEN_VALUE_DATE:
            case TOKEN_VALUE_PRIORITY:
            case TOKEN_VALUE_TEXT:
                return new ValueToken(type, sequence);
            default:
                throw new IllegalArgumentException("Unknown token type");
        }
    }

    @TokenType
    public int getType() {
        return mType;
    }

    public String getSequence() {
        return mSequence;
    }

    /**
     * @return formatted token value
     */
    public abstract String getFormattedSequence(Context context);

    @IntDef({TOKEN_LEFT_BRACKET, TOKEN_RIGHT_BRACKET, TOKEN_AND, TOKEN_OR, TOKEN_VALUE_DATE,
            TOKEN_VALUE_PRIORITY, TOKEN_VALUE_TEXT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TokenType {

    }

}
