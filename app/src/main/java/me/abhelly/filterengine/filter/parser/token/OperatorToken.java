package me.abhelly.filterengine.filter.parser.token;

import android.content.Context;

/**
 * Token represents binary operations and brackets.
 *
 * Created by abhelly on 05.08.15.
 */
public class OperatorToken extends Token {

    public OperatorToken(@TokenType int type, String sequence) {
        super(type, sequence);
    }

    @Override
    public String getFormattedSequence(Context context) {
        return String.format(" %s ", mSequence);
    }

    public int precedence() {
        if (mType == TOKEN_LEFT_BRACKET) {
            return 1;
        } else if (mType == TOKEN_AND || mType == TOKEN_OR) {
            return 2;
        }
        return -1;
    }
}
